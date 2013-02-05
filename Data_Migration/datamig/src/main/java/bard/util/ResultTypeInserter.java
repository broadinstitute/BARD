package bard.util;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import bard.util.dbutil.GenerifiedMapHandler;

import edu.scripps.fl.pubchem.PubChemCsvIterator;
import edu.scripps.fl.pubchem.PubChemDB;
import edu.scripps.fl.pubchem.db.PCAssay;
import edu.scripps.fl.pubchem.db.PCAssayColumn;
import edu.scripps.fl.pubchem.db.PCAssayResult;

public class ResultTypeInserter extends SqlInserter {
	
	private ResultSet measureRs;
	private ResultSet measureContextRs;
	private ResultSet resultRs;
	private PreparedStatement measureDelete, measureContextDelete, resultTypeLookup, resultDelete;
//	private MultiKeyMap resultTypeMap;
	private Map<String, Number> resultTypeIdMap;

	@Override Collection<Long> getAids() throws Exception {
		return Util.getAidsInBard();
	}
	
	@Override public void process(Collection<Long> aids) throws Exception {
		resultTypeIdMap = (Map<String, Number>) new QueryRunner().query(Util.getConnection(), "select result_type_name, result_type_id from result_type", new GenerifiedMapHandler<String, Number>());
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
		super.process(aids);
	}
	
	public Long getResultTypeId(String type) throws Exception {
		Number nbr = resultTypeIdMap.get(type);
		if( nbr == null )
			throw new Exception ("Cannot determine id for result type: " + type);
		return nbr.longValue();
	}
	
	
	@Override void processAid(Long aid) throws Exception {
		System.out.println("processing aid: " + aid);
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
		if( assay.getColumns().size() == 3 ) { // outcome, score, plus one other
			
			measureDelete.setLong(1, assay_id);
			measureDelete.executeUpdate();
			measureContextDelete.setLong(1, assay_id);
			measureContextDelete.executeUpdate();
			
			PCAssayColumn col = assay.getColumns().get( assay.getColumns().size() - 1);
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
			
			String unit = col.getTestedConcentrationUnit();
			if( unit == null ) {
				if( "percent inhibition".equals(type) || "percent activation".equals(type))
					unit = "uM";
			}
			if( "um".equals(unit))
				unit = "uM";
			
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
			
			
			resultDelete.setLong(1, exptId);
			resultDelete.executeUpdate();
			Iterator<PCAssayResult> iter = new PubChemCsvIterator(assay);
			while(iter.hasNext()) {
				PCAssayResult result = iter.next();
				long resultId = Util.nextVal("result");
				resultRs.moveToInsertRow();
				resultRs.updateLong("RESULT_ID", resultId); 
				resultRs.updateString("RESULT_STATUS", "Pending");
				resultRs.updateString("READY_FOR_EXTRACTION", "Ready");
				resultRs.updateString("VALUE_DISPLAY", new String(result.getValue(col).toString()));
				if( col.getTypeClass().isAssignableFrom(Number.class) )
					resultRs.updateDouble("VALUE_NUM", new Double((result.getValue(col).toString())));
				else
					resultRs.updateNull("VALUE_NUM");
				resultRs.updateLong("EXPERIMENT_ID", exptId);
				resultRs.updateLong("SUBSTANCE_ID", result.getSID());
				resultRs.updateLong("RESULT_TYPE_ID", resultTypeId); 
				resultRs.updateLong("VERSION",1L); 
				resultRs.updateDate("DATE_CREATED", createDate);
				resultRs.updateString("MODIFIED_BY", "southern");
				resultRs.insertRow();
			}
			
			
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
		
		URL url = ResultTypeInserter.class.getClassLoader().getResource("hibernate.broad.cfg.xml");
		PubChemDB.setUp(url);
		
		SqlInserter ins = new ResultTypeInserter();
		// ins.insertAid(504814L);
		if (args.length > 0) {
			List list = new ArrayList(Arrays.asList(args));
			ObjectConverter.convertList(list, Long.class);
			ins.process(list);
		} else
			ins.process(ins.getAids());
	}
}