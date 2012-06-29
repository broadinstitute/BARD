package bard.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import edu.scripps.fl.pubchem.PubChemFactory;
import edu.scripps.fl.pubchem.PubChemXMLParserFactory;
import edu.scripps.fl.pubchem.db.PCAssay;

public class Util {
	
	private static Connection conn;
	private static Connection conn2;
	
	public static Connection getConnection() throws SQLException {
		if (conn == null) {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@barddb:1521:BARDQA", "bard_dev", "bard_dev");
			conn.setAutoCommit(false);
		}
		return conn;
	}
	
	public static Connection getConnection2() throws SQLException {
		if (conn == null) {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@orpheus:1521:FLPROD", "southern", "SOUTHERN");
			conn.setAutoCommit(false);
		}
		return conn;
	}
	
	public static List<Long> getAidsInBard() throws SQLException {
    	List<Long> aids = new QueryRunner().query(getConnection(),"select distinct to_number(replace(ext_assay_ref,'aid=','')) from external_reference where external_system_id = 1 order by to_number(replace(ext_assay_ref,'aid=',''))", new ColumnConverterHandler(1, Long.class));
    	return aids;
	}
	
	public static Long getAssayIdFromAid(Long aid) throws Exception {
		return new QueryRunner().query(getConnection(), "select e.assay_id from external_reference er, experiment e where e.experiment_id = er.experiment_id and er.ext_assay_ref = ?", new ScalarHandler<Number>(), String.format("aid=%s",aid)).longValue();
	}
	
	public static Long nextVal(String table) throws SQLException, ClassNotFoundException {
		return new QueryRunner().query(getConnection(), String.format("select %s_id_seq.nextval from dual", table), new ScalarHandler<Number>()).longValue();
    }
	
	public static Long getElementIdByLabel(String label) throws Exception {
		return new QueryRunner().query(getConnection(), "select element_id from element where label = ?", new ScalarHandler<Number>(), label).longValue();
	}
	
	public static ResultSet getTable(String table) throws Exception {
    	PreparedStatement ps = getConnection().prepareStatement(String.format("SELECT %1$s.* FROM %1$s", table)
                , ResultSet.TYPE_SCROLL_SENSITIVE
                , ResultSet.CONCUR_UPDATABLE);
    	ResultSet rs = ps.executeQuery();
    	return rs;
    }
	
	public static PCAssay getPCAssay(long assayId) throws Exception {
        InputStream is = PubChemFactory.getInstance().getXmlDescr(assayId);
        List<PCAssay> assays = PubChemXMLParserFactory.getInstance().populateAssayFromXML(is, false);
        PCAssay pcassay = assays.get(0);
        PubChemFactory.getInstance().populateAssayFromSummary(pcassay);
        return pcassay;
    }
	
	
	public static void printResultSetRow(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		for(int ii = 1; ii < meta.getColumnCount(); ii++) {
			System.out.println(String.format("%s = %s", meta.getColumnLabel(ii), rs.getObject(ii)));
		}
	}
	
//	public static <K,V> Map<K,V> toMap(Map<K,V> map, Object...objects) {
//		
//	}
}