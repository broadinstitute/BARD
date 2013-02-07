package bard.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Restrictions;

import bard.util.dbutil.ColumnConverterHandler;
import edu.scripps.fl.pubchem.PubChemDB;
import edu.scripps.fl.pubchem.db.PCAssay;
import edu.scripps.fl.pubchem.db.PCAssayColumn;

public class Util {

	private static Connection conn;

	public static void closeConnection() throws SQLException {
		if( conn != null) {
			DbUtils.close(conn);
			conn = null;
		}
	}
	
	public static Connection getConnection() throws SQLException {
		if( conn == null ) {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@VMBARDDEV:1521:BARDDEV", "southern", "Cr0wb0r0!");
			conn.setAutoCommit(false);
		}
		return conn;
	}


	public static List<Long> getAidsInBard() throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Long> aids = (List<Long>) qr.query(getConnection(),
						"select distinct to_number(replace(ext_assay_ref,'aid=','')) from external_reference where external_system_id = 1 order by to_number(replace(ext_assay_ref,'aid=',''))",
						new ColumnConverterHandler(1, Long.class));
		return aids;
	}
	
	public static List<Long> getPCAssayAidsNotInBard() throws SQLException {
		List<Long> aids = (List<Long>) new QueryRunner()
				.query(getConnection(),
						"select assay_aid from pcassay where assay_aid not in ( select to_number(substr(ext_assay_ref, 5, length(ext_assay_ref) - 4)) + 0 as aid from external_reference where external_system_id = (select external_system_id from external_system where system_name = 'PubChem' ) ) and assay_activity_outcome_method != 'summary' order by assay_aid ",
						new ColumnConverterHandler(1, Long.class));
		return aids;
	}

	public static Long getAssayIdFromAid(Long aid) throws Exception {
		Number nbr = new QueryRunner().query(getConnection(),
				"select e.assay_id from external_reference er, experiment e where e.experiment_id = er.experiment_id and er.ext_assay_ref = ?",
				new ScalarHandler<Number>(), String.format("aid=%s", aid));
		if (nbr == null)
			throw new Exception("Cannot find assay_id for AID: " + aid);
		return nbr.longValue();
	}

	public static Long getExperimentIdFromAid(Long aid) throws Exception {
		Number nbr = new QueryRunner().query(getConnection(), "select experiment_id from external_reference er where er.ext_assay_ref = ?",
				new ScalarHandler<Number>(), String.format("aid=%s", aid));
		if (nbr == null)
			throw new Exception("Cannot find experiment_id for AID: " + aid);
		return nbr.longValue();
	}

	public static Long nextVal(String table) throws SQLException {
		return new QueryRunner().query(getConnection(), String.format("select %s_id_seq.nextval from dual", table), new ScalarHandler<Number>()).longValue();
	}

	public static Long getElementIdByLabel(String label) throws Exception {
		Number num = new QueryRunner().query(getConnection(), "select element_id from element where lower(label) = ?", new ScalarHandler<Number>(),
				label.toLowerCase());
		return num == null ? null : num.longValue();
	}

	public static ResultSet getTable(String table) throws SQLException {
		PreparedStatement ps = getConnection().prepareStatement(String.format("SELECT %1$s.* FROM %1$s", table), ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	public static ResultSet getSequenceNumbers(String sequenceName, int prefetchSize) throws SQLException {
		PreparedStatement ps = getConnection().prepareStatement(
				String.format("select %s.nextval from dual connect by level <= %s", sequenceName, prefetchSize), ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY);
		ps.setFetchSize(prefetchSize);
		ResultSet srs = ps.executeQuery();
		return srs;
	}

	public static void verifyBatch(int[] count) throws SQLException {
		for (int ii = 0; ii < count.length; ii++) {
			if (count[ii] < 0 & count[ii] != Statement.SUCCESS_NO_INFO) {
				throw new SQLException(String.format("Statement %s in batch did not succeed", ii));
			}
		}
	}

	public static PCAssay getPCAssay(Long assayId) throws Exception {
		 return (PCAssay) PubChemDB.getSession().createCriteria(PCAssay.class).add(Restrictions.eq("AID",assayId.intValue())).list().get(0);

//		InputStream is = PubChemFactory.getInstance().getXmlDescr(assayId);
//		List<PCAssay> assays = PubChemXMLParserFactory.getInstance().populateAssayFromXML(is, false);
//		PCAssay pcassay = assays.get(0);
//		PubChemFactory.getInstance().populateAssayFromSummary(pcassay);
//		return pcassay;
	}

	public static String preparePlaceHolders(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length;) {
			builder.append("?");
			if (++i < length)
				builder.append(",");
		}
		return builder.toString();
	}

	public static void setValues(PreparedStatement preparedStatement, Object... values) throws SQLException {
		for (int i = 0; i < values.length; i++)
			preparedStatement.setObject(i + 1, values[i]);
	}

	public static void printResultSetRow(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		for (int ii = 1; ii < meta.getColumnCount(); ii++)
			System.out.println(String.format("%s = %s", meta.getColumnLabel(ii), rs.getObject(ii)));
	}

	public static Double getConcentrationFromColumn(PCAssayColumn col) {
		Double dbl = null;
		// Pattern pattern =
		// Pattern.compile("^.*(at|@)[_ ]([-+]?[0-9]*\\.?[0-9]+).*$");
		Pattern pattern = Pattern.compile("[\\s@_]([0-9]*\\.?[0-9]+)[\\s@_]?(u|m|n)M");
		Matcher matcher = pattern.matcher(col.getName());
		if (matcher.find()) {
			dbl = Double.valueOf(matcher.group(1));
			if ("m".equals(matcher.group(2)))
				dbl = dbl * 1E3;
			if ("n".equals(matcher.group(2)))
				dbl = dbl * 1E-3;
		}
		else if (!StringUtils.isBlank(col.getDescription())) {
			matcher = pattern.matcher(col.getDescription());
			if (matcher.find()) {
				dbl = Double.valueOf(matcher.group(1));
				if ("m".equals(matcher.group(2)))
					dbl = dbl * 1E3;
				if ("n".equals(matcher.group(2)))
					dbl = dbl * 1E-3;
			}
		}
		return dbl;
	}

	// public static <K,V> Map<K,V> toMap(Map<K,V> map, Object...objects) {
	//
	// }
}