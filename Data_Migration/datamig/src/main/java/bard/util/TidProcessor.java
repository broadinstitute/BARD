package bard.util;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bard.util.db.ColumnResultType;
import bard.util.db.ResultBean;
import edu.scripps.fl.pubchem.db.PCAssay;
import edu.scripps.fl.pubchem.db.PCAssayColumn;
import gov.ncgc.PubChemAssayParser;

public class TidProcessor {

	private static final Logger log = LoggerFactory.getLogger(TidProcessor.class);

	public void process(Long aid) throws Exception {
		PCAssay assay = Util.getPCAssay(aid);
		if (assay.getPanels().size() > 0) {
			System.err.println(String.format("AID %s is a panel assay, skipping", aid));
			return;
		}
		MultiValueMap curveCols = getCurveColumns(assay);
		Map<String, PCAssayColumn> colsMap = getColumnNameMap(assay);
		Map<String, PCAssayColumn> actColsMap = getActiveColumnNameMap(assay);
		Map<String, PCAssayColumn> testedColsMap = getTestedColumnNameMap(assay);
		// try to find ac or tc in result types mapping table.

		
		// first create beans for all rows and stick them in a map
		Map<Integer, ResultBean> beans = new TreeMap();
		for (PCAssayColumn col : assay.getColumns()) {
			ResultBean bean = new ResultBean();
			bean.aid = col.getAssay().getAID();
			bean.tid = col.getTID();
			beans.put(bean.getTid(), bean);
		}

		
		//then walk threw each row
		for (PCAssayColumn col : assay.getColumns()) {
			ResultBean bean = beans.get(col.getTID());
			// if (bean.tid <= 0 ) {
			// bean.parentTid = bean.tid;
			// // bean = bean.tid;
			// }

			// if ( col.isActiveConcentration()) {
			// bean.parentTid = bean.tid;
			// bean.groupNo = 1;
			// }
			// else
			// bean.groupNo = col.getCurvePlotLabel() == null ?
			// actColsMap.size() + 1 : col.getCurvePlotLabel();

			bean.tidName = col.getName();

			if (col.getTestedConcentration() != null) {
				Integer tid = null;
				if (actColsMap.size() > 0)
					tid = actColsMap.values().iterator().next().getTID();
				bean.parentTid = tid;
				bean.relationship = "Derives";
			}

			// if (col.getPanel() == null)
			// bean.groupNo = 1;
			// else
			// bean.groupNo = col.getPanel().getPanelNumber();

			if (col.getName().toLowerCase().contains("qualifier") || col.getName().toLowerCase().contains("modifier")) {
				bean.qualifierTid = col.getTID();
				beans.get(bean.getTid() + 1).qualifierTid = col.getTID();
			}
			
			bean.seriesNo = col.getCurvePlotLabel();

			ColumnResultType crt = getColumnResultType(col);
			if (crt == null)
				bean.contextItem = col.getName();
			else {
				if (crt.result_type != null)
					bean.resultType = crt.result_type;
				else {
					if (crt.attribute_1 != null)
						bean.contextItem = crt.attribute_1;
					else {
						bean.contextItem = getResultContextItem(col.getName());
						if (bean.contextItem == null)
							bean.contextItem = col.getName();
					}

					Integer tid = null;
					if (actColsMap.size() > 0)
						tid = actColsMap.values().iterator().next().getTID();
					bean.contextTid = tid;
				}
				bean.stats_modifier = crt.stats_modifier;
			}

			// String resultType = getResultType(col);
			// String contextItem = getResultContextItem(resultType);
			// if( contextItem == null )
			// bean.resultType = resultType;
			// else
			// bean.contextItem = contextItem;

			// if (bean.resultType == null) {
			// bean.contextItem = getResultContextItem(col.getName());
			// if (bean.contextItem == null)
			// bean.contextItem = col.getName();
			// Integer tid = null;
			// if (actColsMap.size() > 0)
			// tid = actColsMap.values().iterator().next().getTID();
			// bean.contextTid = tid;
			// }

			Double conc = null;
			if (null != col.getTestedConcentration()) {
				conc = col.getTestedConcentration();
			} else {
				conc = Util.getConcentrationFromColumn(col);
			}

			if (null == conc)
				log.error(String.format("Could not determine concentration for AID %s: %s", assay.getAID(), col.getName()));

			String unit = null;
			if (col.isActiveConcentration())
				unit = col.getUnit();
			else {
				unit = col.getTestedConcentrationUnit();
				if (unit == null & conc != null)
					unit = "uM";
				// else
				// log.error(String.format("Could not determine concentration unit for AID %s: %s",
				// assay.getAID(), col.getName()));
			}
			if ("um".equals(unit))
				unit = "uM";

			bean.concentration = conc;
			bean.concentrationUnit = unit;
		}

		Util.getConnection().prepareStatement("delete from result_map where aid = " + assay.getAID()).executeUpdate();

		PreparedStatement ps = Util
				.getConnection()
				.prepareStatement(
						"insert into result_map(aid, tid, tidname, seriesNo, parentTid, relationship, resultType, stats_modifier, contextTid, contextItem, qualifierTid, concentration, concentrationUnit) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		for (ResultBean bean : beans.values()) {
			ps.setInt(1, bean.aid);
			ps.setInt(2, bean.tid);
			ps.setString(3, bean.tidName);
			
			if (bean.seriesNo == null)
				ps.setNull(4, Types.NUMERIC);
			else
				ps.setInt(4, bean.seriesNo);

			if (bean.parentTid == null)
				ps.setNull(5, Types.NUMERIC);
			else
				ps.setInt(5, bean.parentTid);

			if (bean.relationship != null)
				ps.setString(6, bean.relationship);
			else
				ps.setNull(6, Types.VARCHAR);

			ps.setString(7, bean.resultType);
			
			ps.setString(8, bean.stats_modifier);
			
			if (bean.contextTid == null)
				ps.setNull(9, Types.NUMERIC);
			else
				ps.setInt(9, bean.contextTid);
			
			if (bean.contextItem == null)
				ps.setNull(10, Types.VARCHAR);
			else
				ps.setObject(10, bean.contextItem);

			if (bean.qualifierTid == null)
				ps.setNull(11, Types.NUMERIC);
			else
				ps.setInt(11, bean.qualifierTid);

			if (bean.concentration == null)
				ps.setNull(12, Types.NUMERIC);
			else
				ps.setDouble(12, bean.concentration);

			if (bean.concentrationUnit == null)
				ps.setNull(13, Types.VARCHAR);
			else
				ps.setString(13, bean.concentrationUnit);

			ps.addBatch();
		}
		ps.executeBatch();
	}

	public ColumnResultType getColumnResultType(PCAssayColumn col) throws Exception {
		String desc = StringUtils.isEmpty(col.getDescription()) ? "null" : col.getDescription();
		ColumnResultType crt = new QueryRunner().query(Util.getConnection(),
				"select * from column_result_type where column_name = ? and nvl(column_description,'null') = ?", new BeanHandler<ColumnResultType>(
						ColumnResultType.class), col.getName(), desc);
		return crt;
	}

	public String getResultType(PCAssayColumn col) throws Exception {
		// boolean test = resultTypeMap.containsKey(col.getName(),
		// col.getDescription());
		// return (String) resultTypeMap.get(col.getName(),
		// col.getDescription());
		String desc = StringUtils.isEmpty(col.getDescription()) ? "null" : col.getDescription();
		String type = new QueryRunner().query(Util.getConnection(),
				"select result_type from column_result_type where column_name = ? and nvl(column_description,'null') = ?", new ScalarHandler<String>(),
				col.getName(), desc);
		System.out.println(String.format(">>>%s<<<", col.getName()));
		System.out.println(String.format(">>>%s<<<", desc));
		if (StringUtils.isBlank(type)) {
			log.error(String.format("Could not determine result type for aid %s\t%s\t%s", col.getAssay().getAID(), col.getName(), col.getDescription()));
			return null;
		}
		return type;
	}

	public String getResultContextItem(String name) throws Exception {
		String type = null;
		if (PubChemAssayParser.isHillSlope(name))
			type = "Hill coefficient";
		else if (PubChemAssayParser.isHillInf(name))
			type = "Hill sinf";
		else if (PubChemAssayParser.isHillZero(name))
			type = "Hill s0";
		else if (isExcludedPoints(name))
			type = "excluded points";
		return type;
	}

	static final String[] EXCLUDED_POINTS = { "Excluded Points", "Excluded_Points" };

	public static boolean isExcludedPoints(String text) {
		if (text == null)
			return false;
		for (int i = 0; i < EXCLUDED_POINTS.length; ++i) {
			if (EXCLUDED_POINTS[i].equals(text) || text.indexOf(EXCLUDED_POINTS[i]) >= 0) {
				return true;
			}
		}
		return false;
	}

	MultiValueMap getCurveColumns(PCAssay assay) {
		MultiValueMap curveCols = new MultiValueMap();
		for (PCAssayColumn col : assay.getColumns()) {
			if (null != col.getCurvePlotLabel())
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
			if (col.isActiveConcentration())
				colsMap.put(col.getName(), col);
		return colsMap;
	}

	Map<String, PCAssayColumn> getTestedColumnNameMap(PCAssay assay) {
		Map<String, PCAssayColumn> colsMap = new HashMap();
		for (PCAssayColumn col : assay.getColumns())
			if (col.getTestedConcentration() != null)
				colsMap.put(col.getName(), col);
		return colsMap;
	}
}
