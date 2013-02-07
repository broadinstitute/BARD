package bard.util;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.balusc.util.ObjectConverter;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bard.util.db.ResultBean;
import bard.util.dbutil.OracleSequenceIterator;
import bard.util.dbutil.SimpleMapHandler;
import edu.scripps.fl.pubchem.PubChemCsvIterator;
import edu.scripps.fl.pubchem.PubChemDB;
import edu.scripps.fl.pubchem.db.PCAssay;
import edu.scripps.fl.pubchem.db.PCAssayColumn;
import edu.scripps.fl.pubchem.db.PCAssayResult;

public class ResultMapInserter extends SqlInserter {

	Logger log = LoggerFactory.getLogger(ResultMapInserter.class);

	private PreparedStatement resultDelete, resultContextDelete;
	private PreparedStatement resultInsert, contextInsert, hierInsert, elementInsert, elemHierInsert;
	private Map<String, Number> resultTypeIdMap;
	private Long concValueId, resultEndpointId;
	private java.sql.Date createDate = new java.sql.Date(new java.util.Date().getTime());
	OracleSequenceIterator resultIds;
	OracleSequenceIterator contextIds;

	@Override
	Collection<Long> getAids() throws Exception {
		Set set = new HashSet();
//		set.add(348L);
		return set;
	}

	@Override
	public void process(Collection<Long> aids) throws Exception {
		resultIds = new OracleSequenceIterator(Util.getConnection(), "result_id_seq", 20000);
		contextIds = new OracleSequenceIterator(Util.getConnection(), "result_context_item_id_seq", 20000);
		
		concValueId = Util.getElementIdByLabel("concentration value");
		resultEndpointId = Util.getElementIdByLabel("result endpoint");
		
//		resultTypeIdMap = new QueryRunner().query(Util.getConnection(), "select e.element_id, lower(e.label) from element e, element_hierarchy eh where eh.child_element_id = e.element_id and eh.relationship_type in ( 'is_a', 'has') start with eh.parent_element_id = ? connect by prior eh.child_element_id = eh.parent_element_id",
//			new GenerifiedMapHandler<String, Number>(), resultEndpointId);
		resultTypeIdMap = (Map) new QueryRunner().query(Util.getConnection(), "select lower(e.label), e.element_id from element e",		
				new SimpleMapHandler());
//		for(String key: resultTypeIdMap.keySet())
//			System.out.println(key);
		
		resultDelete = Util.getConnection().prepareStatement("delete from result where experiment_id = ?");
		resultContextDelete = Util.getConnection().prepareStatement("delete from result_context_item where experiment_id = ?");
		resultInsert = Util
				.getConnection()
				.prepareStatement(
						"insert into result(RESULT_ID,RESULT_STATUS,READY_FOR_EXTRACTION,VALUE_DISPLAY,VALUE_NUM,EXPERIMENT_ID,SUBSTANCE_ID,RESULT_TYPE_ID,VERSION,DATE_CREATED,MODIFIED_BY) values(?,?,?,?,?,?,?,?,1,?,'southern')");
		contextInsert = Util
				.getConnection()
				.prepareStatement(
						"insert into result_context_item(RESULT_CONTEXT_ITEM_ID,GROUP_RESULT_CONTEXT_ID,EXPERIMENT_ID,RESULT_ID,ATTRIBUTE_ID,VALUE_ID,EXT_VALUE_ID,QUALIFIER,VALUE_NUM,VALUE_MIN,VALUE_MAX,VALUE_DISPLAY,VERSION,DATE_CREATED,MODIFIED_BY) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,1,?,'southern')");

		hierInsert = Util.getConnection().prepareStatement(
				"insert into result_hierarchy(result_id, parent_result_id, hierarchy_type, version, date_created, modified_by) VALUES(?,?,?,1,?,'southern')");

		elementInsert = Util.getConnection().prepareStatement("insert into element(element_id, element_status, label, ready_for_extraction, version, date_created, modified_by) values(?,'Published',?,'Ready',1,?,'southern')");
		elemHierInsert = Util.getConnection().prepareStatement("insert into element_hierarchy(element_hierarchy_id, parent_element_id, child_element_id, relationship_type, version, date_created, modified_by) values(element_hierarchy_id_seq.nextval,?,?,'is_a',1,?,'southern')");
		super.process(aids);
	}

	public Long getResultTypeId(String type) throws Exception {
		Number nbr = resultTypeIdMap.get(type.toLowerCase());
		if( nbr == null ) {
			nbr = Util.nextVal("element");
			elementInsert.setLong(1, nbr.longValue());
			elementInsert.setString(2, type.toLowerCase());
			elementInsert.setDate(3, createDate);
			elementInsert.executeUpdate();
			elemHierInsert.setLong(1, resultEndpointId);
			elemHierInsert.setLong(2, nbr.longValue());
			elemHierInsert.setDate(3, createDate);
			elemHierInsert.executeUpdate();
			resultTypeIdMap.put(type.toLowerCase(), nbr.longValue());
		}
		return nbr.longValue();
	}

	@Override
	void processAid(Long aid) throws Exception {
		ProgressWriter pw = new ProgressWriter("AID " + aid);
		
		PCAssay assay = Util.getPCAssay(aid);
		if (assay.getPanels().size() > 0) {
			System.err.println(String.format("AID %s is a panel assay, skipping", aid));
			return;
		}

		Long exptId = Util.getExperimentIdFromAid(aid);

		// delete existing results
		log.debug("Deleting existing results for aid " + aid);
		resultContextDelete.setLong(1, exptId);
		resultContextDelete.executeUpdate();
		resultDelete.setLong(1, exptId);
		resultDelete.executeUpdate();

		

		List<ResultBean> resultbeans = new QueryRunner().query(Util.getConnection(), "select * from result_map where aid = ? order by tid",
				new BeanListHandler<ResultBean>(ResultBean.class), aid);

		// work out what is needed for each column then we don't need lookups per row

		Map<Integer, ResultBean> tidMap = new HashMap();
		
		for (int ii = 0; ii < resultbeans.size(); ii++) {
			ResultBean bean = resultbeans.get(ii);
			tidMap.put(bean.tid, bean);
			if (bean.resultType != null)
				bean.resultTypeId = getResultTypeId(bean.resultType);
		}
		
		// insert contexts
		for (int ii = 0; ii < resultbeans.size(); ii++) {
			ResultBean bean = resultbeans.get(ii);
			if (bean.contextItem != null ) {
					bean.elementId = Util.getElementIdByLabel(bean.contextItem);
				if( bean.elementId == null ) {
					bean.elementId = Util.nextVal("element");
					elementInsert.setLong(1, bean.elementId);
					elementInsert.setString(2, bean.contextItem.toLowerCase());
					elementInsert.setDate(3, createDate);
					elementInsert.executeUpdate();
				}
			}
		}

		
//		// work out ouw many rows we have.
//		Iterator<PCAssayResult> iter = new PubChemCsvIterator(assay);
//		int counter = 0;
//		while (iter.hasNext()) {
//			iter.next();
//			counter++;
//		}
//		log.debug(counter + " ids required for AID " + assay.getAID());

		
		
		// walk through file a second time so we can insert records
		Iterator<PCAssayResult> iter = new PubChemCsvIterator(assay);
		int counter = 0;
		while (iter.hasNext()) {
			pw.increment();
			if( ++counter % 20000 == 0 )
				doCommit();
			PCAssayResult result = iter.next();

			for (int ii = 0; ii < resultbeans.size(); ii++) {
				ResultBean bean = resultbeans.get(ii);
				PCAssayColumn col = assay.getColumns().get(ii);
				
				if( col.getTID().equals(bean.qualifierTid))
					continue;
				
				if( bean.resultType == null && bean.contextItem == null )
					continue;
				
				if (bean.resultTypeId != null) {
					
					String display = "";
					if( bean.qualifierTid != null ) {
						display = result.getAllValues().get(bean.qualifierTid);
					}
					
					Object obj;
					if( bean.tid == -1)
						obj = result.getOutcome();
					else if ( bean.tid == 0 )
						obj = result.getRankScore();
					else
						obj = result.getAllValues().get(ii-2);
					
					display += obj == null ? "" : obj.toString();
					
//					resultIds.next();
					bean.resultId = resultIds.next();
					resultInsert.setLong(1, bean.resultId);
					resultInsert.setString(2, "Pending"); // "RESULT_STATUS"
					resultInsert.setString(3, "Ready"); // "READY_FOR_EXTRACTION"
					resultInsert.setString(4, display); // "VALUE_DISPLAY"
					if (col.getTypeClass().equals(Double.class) & obj != null & ! StringUtils.isBlank(obj.toString()))
						resultInsert.setDouble(5, new Double(obj.toString())); // "VALUE_NUM"
					else
						resultInsert.setNull(5, java.sql.Types.DOUBLE);
					resultInsert.setLong(6, exptId); // "EXPERIMENT_ID"
					resultInsert.setLong(7, result.getSID()); // "SUBSTANCE_ID"
					resultInsert.setLong(8, bean.resultTypeId); // "RESULT_TYPE_ID"
					resultInsert.setDate(9, createDate); // "DATE_CREATED"
					resultInsert.addBatch();

					if (bean.concentrationUnit != null & bean.concentration != null) {
						contextInsert.setLong(1, contextIds.next()); // RESULT_CONTEXT_ITEM_ID
						contextInsert.setNull(2, Types.NUMERIC); // GROUP_RESULT_CONTEXT_ID
						contextInsert.setLong(3, exptId); // EXPERIMENT_ID
						contextInsert.setLong(4, bean.resultId); // RESULT_ID
						contextInsert.setLong(5, concValueId); // ATTRIBUTE_ID
						contextInsert.setNull(6, Types.NUMERIC); // VALUE_ID
						contextInsert.setNull(7, Types.NUMERIC); // EXT_VALUE_ID
						contextInsert.setNull(8, Types.VARCHAR); // QUALIFIER
						contextInsert.setDouble(9, bean.concentration); // VALUE_NUM
						contextInsert.setString(12, String.format("%s %s", bean.concentration, bean.concentrationUnit)); // VALUE_DISPLAY
						contextInsert.setNull(10, Types.NUMERIC); // VALUE_MIN
						contextInsert.setNull(11, Types.NUMERIC); // VALUE_MAX
						contextInsert.setDate(13, createDate); // DATE_CREATED
						contextInsert.addBatch();
					}
				} 
			}
			
			// insert contexts
			for (int ii = 0; ii < resultbeans.size(); ii++) {
				ResultBean bean = resultbeans.get(ii);
				PCAssayColumn col = assay.getColumns().get(ii);
				if (bean.elementId != null ) {
					contextInsert.setLong(1, contextIds.next());
					contextInsert.setNull(2, Types.NUMERIC); // GROUP_RESULT_CONTEXT_ID
					contextInsert.setLong(3, exptId); // EXPERIMENT_ID
					if (bean.contextTid != null) {
						ResultBean parent = tidMap.get(bean.contextTid);
						contextInsert.setLong(4, parent.resultId); // RESULT_ID
					}
					
					contextInsert.setLong(5, bean.elementId); // ATTRIBUTE_ID
					contextInsert.setNull(6, Types.NUMERIC); // VALUE_ID
					contextInsert.setNull(7, Types.NUMERIC); // EXT_VALUE_ID
					contextInsert.setNull(8, Types.VARCHAR); // QUALIFIER
					
					Object obj = result.getAllValues().get(ii-2);
					if (col.getTypeClass().equals(Double.class) & obj != null & ! StringUtils.isBlank(obj.toString()))
						contextInsert.setDouble(9, new Double(obj.toString())); // "VALUE_NUM"
					else 
						contextInsert.setNull(9, Types.NUMERIC);
					
					contextInsert.setString(12, obj.toString()); // VALUE_DISPLAY
					contextInsert.setNull(10, Types.NUMERIC); // VALUE_MIN
					contextInsert.setNull(11, Types.NUMERIC); // VALUE_MAX
					contextInsert.setDate(13, createDate); // DATE_CREATED
					contextInsert.addBatch();
				}
			}
			
//			insert hierarchy
			for (int ii = 0; ii < resultbeans.size(); ii++) {
				ResultBean bean = resultbeans.get(ii);
				if (bean.parentTid != null) {
					ResultBean parent = tidMap.get(bean.tid);
					hierInsert.setLong(1, bean.resultId);
					hierInsert.setLong(2, parent.resultId);
					hierInsert.setString(3, bean.relationship);
					hierInsert.setDate(4, createDate);
					hierInsert.addBatch();
				}
			}
		} // end while
		
		doCommit();
	}
	
	public void doCommit() throws SQLException {
		log.debug("Starting commit");
		Util.verifyBatch(resultInsert.executeBatch());
		Util.verifyBatch(contextInsert.executeBatch());
		Util.verifyBatch(hierInsert.executeBatch());
		Util.getConnection().commit();
		log.debug("finished commit");
		System.gc();
	}

	public static void main(String[] args) throws Exception {
		URL url = ResultMapInserter.class.getClassLoader().getResource("log4j.config.xml");
		DOMConfigurator.configure(url);
		
		url = ResultMapInserter.class.getClassLoader().getResource("hibernate.broad.cfg.xml");
		PubChemDB.setUp(url);
		
		SqlInserter ins = new ResultMapInserter();
		// ins.insertAid(504814L);
		if (args.length > 0) {
			List list = new ArrayList(Arrays.asList(args));
			ObjectConverter.convertList(list, Long.class);
			ins.process(list);
		} else
			ins.process(ins.getAids());
	}
}