package bard.util.app;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.balusc.util.ObjectConverter;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bard.util.SqlInserter;
import bard.util.Util;
import bard.util.db.ResultBean;
import bard.util.dbutil.GenerifiedMapHandler;
import edu.scripps.fl.pubchem.PubChemCsvIterator;
import edu.scripps.fl.pubchem.PubChemDB;
import edu.scripps.fl.pubchem.db.PCAssay;
import edu.scripps.fl.pubchem.db.PCAssayColumn;
import edu.scripps.fl.pubchem.db.PCAssayResult;
import gov.ncgc.PubChemAssayParser;

public class BatchResultTypeInserter extends SqlInserter {
	
	Logger log = LoggerFactory.getLogger(BatchResultTypeInserter.class);
	
	private ResultSet measureRs;
	private ResultSet measureContextRs;
	private ResultSet resultRs;
	private PreparedStatement measureDelete, measureContextDelete, resultTypeLookup, resultDelete, resultContextDelete;
	private PreparedStatement resultInsert, contextInsert;
//	private MultiKeyMap resultTypeMap;
	private Map<String, Number> resultTypeIdMap;
	private Long concId, pubchemScore, activityOutcome;

	@Override Collection<Long> getAids() throws Exception {
		return Util.getAidsInBard();
	}
	
	@Override public void process(Collection<Long> aids) throws Exception {
		resultTypeIdMap = new QueryRunner().query(Util.getConnection(), "select result_type_name, result_type_id from result_type", new GenerifiedMapHandler<String, Number>());
//		resultTypeMap = new QueryRunner().query(Util.getConnection(), "select column_name, column_description, result_type from column_result_type", new MultiKeyMapHandler());
//		for(Map.Entry e: (Set<Map.Entry>) resultTypeMap.entrySet())
//			System.out.println(e.getKey() + "\t" + e.getValue());
		measureRs = Util.getTable("measure");
		measureContextRs = Util.getTable("measure_context");
		resultRs = Util.getTable("result");
		measureDelete = Util.getConnection().prepareStatement("delete from measure where assay_id = ?");
		measureContextDelete = Util.getConnection().prepareStatement("delete from measure_context where assay_id = ?");
		resultTypeLookup = Util.getConnection().prepareStatement("select result_type from column_result_type where column_name = ? and column_description = ?");
		resultDelete = Util.getConnection().prepareStatement("delete from result where experiment_id = ?");
		resultContextDelete = Util.getConnection().prepareStatement("delete from result_context_item where experiment_id = ?");
//		resultInsert = Util.getConnection().prepareCall("insert into result(RESULT_ID,RESULT_STATUS,READY_FOR_EXTRACTION,VALUE_DISPLAY,VALUE_NUM,EXPERIMENT_ID,SUBSTANCE_ID,RESULT_TYPE_ID,VERSION,DATE_CREATED,MODIFIED_BY) values(RESULT_ID_SEQ.NEXTVAL,:RESULT_STATUS,:READY_FOR_EXTRACTION,:VALUE_DISPLAY,:VALUE_NUM,:EXPERIMENT_ID,:SUBSTANCE_ID,:RESULT_TYPE_ID,:VERSION,:DATE_CREATED,:MODIFIED_BY)");
		resultInsert = Util.getConnection().prepareStatement("insert into result(RESULT_ID,RESULT_STATUS,READY_FOR_EXTRACTION,VALUE_DISPLAY,VALUE_NUM,EXPERIMENT_ID,SUBSTANCE_ID,RESULT_TYPE_ID,VERSION,DATE_CREATED,MODIFIED_BY) values(?,?,?,?,?,?,?,?,1,?,'southern')");
		contextInsert = Util.getConnection().prepareStatement("insert into result_context_item(RESULT_CONTEXT_ITEM_ID,GROUP_RESULT_CONTEXT_ID,EXPERIMENT_ID,RESULT_ID,ATTRIBUTE_ID,VALUE_ID,EXT_VALUE_ID,QUALIFIER,VALUE_NUM,VALUE_MIN,VALUE_MAX,VALUE_DISPLAY,VERSION,DATE_CREATED,MODIFIED_BY) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,1,?,'southern')");

		concId = Util.getElementIdByLabel("assay concentration");
		pubchemScore = Util.getElementIdByLabel("pubchem score");
		activityOutcome = Util.getElementIdByLabel("activity outcome");
		super.process(aids);
	}
	
	public Long getResultTypeId(String type) throws Exception {
		Number nbr = resultTypeIdMap.get(type);
		if( nbr == null )
			throw new Exception ("Cannot determine id for result type: " + type);
		return nbr.longValue();
	}
	
	@Override void processAid(Long aid) throws Exception {
		log.info("processing aid: " + aid);
		PCAssay assay = Util.getPCAssay(aid);
		if( assay.getPanels().size() > 0 ) {
			System.err.println(String.format("AID %s is a panel assay, skipping", aid));
			return;
		}			
		MultiValueMap curveCols = getCurveColumns(assay);
		Map<String, PCAssayColumn> colsMap = getColumnNameMap(assay);
		Map<String, PCAssayColumn> actColsMap = getActiveColumnNameMap(assay);
		Map<String, PCAssayColumn> testedColsMap = getTestedColumnNameMap(assay);
		// try to find ac or tc in result types mapping table.

		Long assay_id = Util.getAssayIdFromAid(aid);
		Long exptId = Util.getExperimentIdFromAid(aid);
		
		java.sql.Date createDate = new java.sql.Date(new java.util.Date().getTime());
		Long measure_id = Util.nextVal("measure");
		
		
		if( "confirmatory".equals(assay.getActivityOutcomeMethod()) & actColsMap.size() == 1 ) {
		
			List<ResultBean> list = new ArrayList();
			for(PCAssayColumn col: assay.getColumns()) {
				ResultBean bean = new ResultBean();
				bean.aid = col.getAssay().getAID();
				bean.tid = col.getTID();
				if( bean.tid <= 0 || col.isActiveConcentration())
					bean.parentTid = bean.tid;
				
				if( col.getTestedConcentration() != null )
					bean.parentTid = actColsMap.values().iterator().next().getTID();
				
				if( col.getPanel() == null )
					bean.groupNo = 1;
				else
					bean.groupNo = col.getPanel().getPanelNumber();
				
				if( col.getName().toLowerCase().contains("qualifier") || col.getName().toLowerCase().contains("modifier"))
					bean.ignorable = true;
				
				bean.resultType = getResultType(col);
				bean.groupNo = col.getCurvePlotLabel() == null ? actColsMap.size() + 1 : col.getCurvePlotLabel();
				if( bean.resultType == null ) {
					bean.contextItem = getResultContextItem(col);
					if( bean.contextItem == null )
						bean.externalContextItem = col.getName();
				}
				
				list.add(bean);
			}
			
			PreparedStatement ps = Util.getConnection().prepareStatement("insert into column_result_mapping(aid, tid, parentTid, resultType, group, contextItem, externalContextItem, ignore) values(?,?,?,?,?,?,?,?)");
			for(ResultBean bean: list) {
				ps.setInt(1, bean.aid);
				ps.setInt(2, bean.tid);
				ps.setString(3, bean.resultType);
				ps.setInt(4, bean.groupNo);
				if( bean.contextItem == null)
					ps.setNull(5, Types.VARCHAR);
				else
					ps.setObject(5, bean.contextItem);
				if( bean.externalContextItem == null)
					ps.setNull(6, Types.VARCHAR);
				else
					ps.setObject(6, bean.externalContextItem);
				ps.setBoolean(7, bean.ignorable);
				ps.executeUpdate();
			}
			
		}		
		
		if( assay.getColumns().size() == 3 ) { // outcome, score, plus one other
			
			measureDelete.setLong(1, assay_id);
			measureDelete.executeUpdate();
			measureContextDelete.setLong(1, assay_id);
			measureContextDelete.executeUpdate();
			
			// delete existing results
			resultContextDelete.setLong(1, exptId);
			resultContextDelete.executeUpdate();
			resultDelete.setLong(1, exptId);
			resultDelete.executeUpdate();
			
			PCAssayColumn col = assay.getColumns().get( 2 ); // score and outcome come first
			String type = getResultType(col);
			
			Long resultTypeId = getResultTypeId(type);
			
			Long measureContextId = Util.nextVal("measure_context");
			measureContextRs.moveToInsertRow();
			measureContextRs.updateLong("MEASURE_CONTEXT_ID", measureContextId);
			measureContextRs.updateLong("ASSAY_ID", assay_id);
			measureContextRs.updateString("CONTEXT_NAME", "Context for " + type);
			measureContextRs.updateLong("VERSION", 1L);
			measureContextRs.updateDate("DATE_CREATED", createDate);
			measureContextRs.updateNull("LAST_UPDATED");
			measureContextRs.updateString("MODIFIED_BY", "southern");
			measureContextRs.insertRow();			
			
			Double conc = null;
			if ( null != col.getTestedConcentration() ) {
				conc = col.getTestedConcentration();
			}
			else {
				conc = Util.getConcentrationFromColumn(col);
			}
			
			if( null == conc )
				log.error(String.format("Could not determine concentration for AID %s: %s", assay.getAID(), col.getName()));
			
			String unit = col.getTestedConcentrationUnit();
			if( "um".equals(unit))
				unit = "uM";
			else if( unit == null & conc != null)
				unit = "uM";
			else {
				log.error(String.format("Could not determine concentration unit for AID %s: %s", assay.getAID(), col.getName()));
			}
//			else if( unit == null ) {
//				if( type.startsWith("percent") )
//					unit = "uM";
//				else
//					unit = JOptionPane.showInputDialog(null, "", String.format("Conc unit for AID %s: %s", assay.getAID(), col.getName()), JOptionPane.INFORMATION_MESSAGE);
//			}
			
			if( conc != null ) {
				contextInsert.setLong(1, Util.nextVal("result_context_item")); // RESULT_CONTEXT_ITEM_ID	
				contextInsert.setNull(2, Types.NUMERIC); // GROUP_RESULT_CONTEXT_ID
				contextInsert.setLong(3, exptId); // EXPERIMENT_ID
				contextInsert.setNull(4, Types.NUMERIC); // RESULT_ID
				contextInsert.setLong(5, concId); // ATTRIBUTE_ID // conc
				contextInsert.setNull(6, Types.NUMERIC); // VALUE_ID
				contextInsert.setNull(7, Types.NUMERIC); // EXT_VALUE_ID
				contextInsert.setNull(8, Types.VARCHAR); // QUALIFIER
				contextInsert.setDouble(9, conc); // VALUE_NUM
				contextInsert.setNull(10, Types.NUMERIC); // VALUE_MIN
				contextInsert.setNull(11, Types.NUMERIC); // VALUE_MAX
				contextInsert.setString(12, String.format("%s %s", conc, unit)); // VALUE_DISPLAY
				contextInsert.setDate(13, createDate); // DATE_CREATED
			}
			

			measureRs.moveToInsertRow();
			measureRs.updateLong("MEASURE_ID", measure_id);
			measureRs.updateLong("ASSAY_ID", assay_id);
			measureRs.updateLong("MEASURE_CONTEXT_ID", measureContextId);
			measureRs.updateNull("PARENT_MEASURE_ID");
			measureRs.updateLong("RESULT_TYPE_ID", resultTypeId);
			measureRs.updateString("ENTRY_UNIT", unit);
			measureRs.updateLong("VERSION", 1L);
			measureRs.updateDate("DATE_CREATED", createDate);
			measureRs.updateString("MODIFIED_BY", "southern");
			measureRs.insertRow();
			
			// determine how many sequence numbers to prefetch
			log.debug("Fetching CSV for AID " + assay.getAID());
			Iterator<PCAssayResult> iter = new PubChemCsvIterator(assay);
			int counter = 0;
			while(iter.hasNext()) {
				iter.next();
				counter++;
			}
			log.debug(counter + " ids required for AID " + assay.getAID());
			// get sequence numbers
			ResultSet resultIds = Util.getSequenceNumbers("result_id_seq", counter * 3); // result type plus outcome and score
			// walk through file a second time so we can insert records
			iter = new PubChemCsvIterator(assay);
			counter = 0;
			while(iter.hasNext()) {
				PCAssayResult result = iter.next();

				resultIds.next();
				Long resultId = resultIds.getLong(1);
				resultInsert.setLong(1, resultId);
				resultInsert.setString(2, "Pending"); // "RESULT_STATUS"
				resultInsert.setString(3, "Ready"); // "READY_FOR_EXTRACTION"
				resultInsert.setString(4, new String(result.getValue(col).toString())); // "VALUE_DISPLAY"
				if( col.getTypeClass().equals(Double.class) )
					resultInsert.setDouble(5, new Double((result.getValue(col).toString()))); // "VALUE_NUM"
				else
					resultInsert.setNull(5, java.sql.Types.DOUBLE);
				resultInsert.setLong(6, exptId); // "EXPERIMENT_ID"
				resultInsert.setLong(7, result.getSID()); // "SUBSTANCE_ID"
				resultInsert.setLong(8, resultTypeId); // "RESULT_TYPE_ID" 
//				resultInsert.setLong(8, 1L); // "VERSION" 
				resultInsert.setDate(9, createDate); // "DATE_CREATED"
//				resultInsert.setString(10, "southern"); // "MODIFIED_BY"
				resultInsert.addBatch();
				
				resultIds.next();
				resultId = resultIds.getLong(1);
				resultInsert.setLong(1, resultId);
				resultInsert.setString(2, "Pending"); // "RESULT_STATUS"
				resultInsert.setString(3, "Ready"); // "READY_FOR_EXTRACTION"
				resultInsert.setString(4, "" + result.getRankScore()); // "VALUE_DISPLAY"
				if( null == result.getRankScore() )
					resultInsert.setNull(5, Types.NUMERIC); // "VALUE_NUM"
				else
					resultInsert.setDouble(5, result.getRankScore().doubleValue()); // "VALUE_NUM"
				resultInsert.setLong(6, exptId); // "EXPERIMENT_ID"
				resultInsert.setLong(7, result.getSID()); // "SUBSTANCE_ID"
				resultInsert.setLong(8, pubchemScore); // "RESULT_TYPE_ID" 
//				resultInsert.setLong(8, 1L); // "VERSION" 
				resultInsert.setDate(9, createDate); // "DATE_CREATED"
//				resultInsert.setString(10, "southern"); // "MODIFIED_BY"
				resultInsert.addBatch();
				
				resultIds.next();
				resultId = resultIds.getLong(1);
				resultInsert.setLong(1, resultId);
				resultInsert.setString(2, "Pending"); // "RESULT_STATUS"
				resultInsert.setString(3, "Ready"); // "READY_FOR_EXTRACTION"
				resultInsert.setString(4, result.getOutcome()); // "VALUE_DISPLAY"
				resultInsert.setNull(5, Types.NUMERIC); // "VALUE_NUM"
				resultInsert.setLong(6, exptId); // "EXPERIMENT_ID"
				resultInsert.setLong(7, result.getSID()); // "SUBSTANCE_ID"
				resultInsert.setLong(8, activityOutcome); // "RESULT_TYPE_ID" 
//				resultInsert.setLong(8, 1L); // "VERSION" 
				resultInsert.setDate(9, createDate); // "DATE_CREATED"
//				resultInsert.setString(10, "southern"); // "MODIFIED_BY"
				resultInsert.addBatch();
			}
			log.debug("Records batched");
			Util.verifyBatch(resultInsert.executeBatch());
			log.debug("batches inserted");
		}
		else if( actColsMap.size() ==1 ) {
			
			
		}
//		System.out.println(curveCols.keySet());
//		System.out.println(colsMap.keySet());
//		System.out.println(actColsMap.keySet());
//		
//		for(PCAssayColumn col: actColsMap.values()) {
//			System.out.println(getResultType(resultTypeMap, col));
//		}
	}
	
	public String getResultType(PCAssayColumn col) throws Exception {
//		boolean test = resultTypeMap.containsKey(col.getName(), col.getDescription());
//		return (String) resultTypeMap.get(col.getName(), col.getDescription());
		String desc = StringUtils.isEmpty(col.getDescription()) ? "null" : col.getDescription();
		String type = new QueryRunner().query(Util.getConnection(),"select result_type from column_result_type where column_name = ? and nvl(column_description,'null') = ?", new ScalarHandler<String>(), col.getName(), desc);
		if( StringUtils.isBlank(type) )
			throw new Exception(String.format("Could not determine result type for aid %s\t%s\t%s", col.getAssay().getAID(), col.getName(), col.getDescription()));
		return type;
	}
	
	public String getResultContextItem(PCAssayColumn col) throws Exception {
		String type = null;
		if( PubChemAssayParser.isHillSlope(col.getName()))
			type = "Hill Slope";
		else if ( PubChemAssayParser.isHillInf(col.getName()))
			type = "Hill Inf";
		else if ( PubChemAssayParser.isHillZero(col.getName()))
			type = "Hill Zero";
		return type;
	}
	
	MultiValueMap getCurveColumns(PCAssay assay) {
		MultiValueMap curveCols = new MultiValueMap();
		for(PCAssayColumn col: assay.getColumns()) {
			if( null != col.getCurvePlotLabel())
				curveCols.put(col.getCurvePlotLabel(), col);
		}
		return curveCols;
	}
	
	Map<String, PCAssayColumn> getColumnNameMap(PCAssay assay) {
		Map<String, PCAssayColumn> colsMap = new HashMap();
		for (PCAssayColumn col : assay.getColumns())
			colsMap.put(col.getName(), col);
		return colsMap;
	}
	
	
	Map<String, PCAssayColumn> getActiveColumnNameMap(PCAssay assay) {
		Map<String, PCAssayColumn> colsMap = new HashMap();
		for (PCAssayColumn col : assay.getColumns())
			if( col.isActiveConcentration() )
				colsMap.put(col.getName(), col);
		return colsMap;
	}
	
	Map<String, PCAssayColumn> getTestedColumnNameMap(PCAssay assay) {
		Map<String, PCAssayColumn> colsMap = new HashMap();
		for (PCAssayColumn col : assay.getColumns())
			if( col.getTestedConcentration() != null )
				colsMap.put(col.getName(), col);
		return colsMap;
	}
	
	public static void main(String[] args) throws Exception {
		
		URL url = BatchResultTypeInserter.class.getClassLoader().getResource("log4j.config.xml");
		DOMConfigurator.configure(url);
		
		url = BatchResultTypeInserter.class.getClassLoader().getResource("hibernate.broad.cfg.xml");
		PubChemDB.setUp(url);
		
		SqlInserter ins = new BatchResultTypeInserter();
		// ins.insertAid(504814L);
		if (args.length > 0) {
			List list = new ArrayList(Arrays.asList(args));
			ObjectConverter.convertList(list, Long.class);
			ins.process(list);
		} else
			ins.process(ins.getAids());
	}
}