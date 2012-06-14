package bard.util;

import java.io.InputStream;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import edu.scripps.fl.pubchem.PubChemFactory;
import edu.scripps.fl.pubchem.PubChemXMLParserFactory;
import edu.scripps.fl.pubchem.db.PCAssay;

public class SqlInserter {
//	private static final Logger log = LoggerFactory.getLogger(SqlInserter.class);
		
	private Connection conn;
	private ResultSet assayRs, extAssayRs, assayDocRs;
	private Clob clob;
	
    void init() throws Exception {
//    	OracleDataSource ds new OracleDataSource();
//    	ds.set
    	if( null == conn) 
    		conn = DriverManager.getConnection("jdbc:oracle:thin:@barddb:1521:BARDQA", "southern", "SOUTHERN");
    }

    void insertAid(Long aid) throws Exception {
    	System.out.println("Inserting aid: " + aid);
    	PCAssay pcassay = getPCAssay(aid);
    	String version =  String.format("%s.%s", pcassay.getVersion(), pcassay.getRevision());
    	java.sql.Date createDate = new java.sql.Date(pcassay.getDepositDate().getTime());
    	
    	Long id = nextVal(conn,"assay");
    	if( assayRs == null) 
    			assayRs = getTable(conn, "assay");
    	assayRs.moveToInsertRow();
    	assayRs.updateLong("assay_id", id);
    	assayRs.updateString("assay_name", pcassay.getName());
    	assayRs.updateString("assay_version", version);
    	assayRs.updateLong("version", 1L);
    	assayRs.updateDate("date_created", createDate);
    	assayRs.updateString("ready_for_extraction", "Ready");
    	assayRs.updateString("assay_status", "Active");
    	assayRs.insertRow();
    	
    	if(extAssayRs == null)
    		extAssayRs = getTable(conn, "external_assay");
    	extAssayRs.moveToInsertRow();
    	extAssayRs.updateLong("assay_id", id);
    	extAssayRs.updateString("version", version);
    	extAssayRs.updateDate("date_created", createDate);
    	extAssayRs.updateString("ext_assay_id", "aid=" + pcassay.getAID());
    	extAssayRs.updateLong("external_system_id", 1L);
    	extAssayRs.insertRow();
    	
    	insertDocument(id, createDate, "Protocol", pcassay.getProtocol());
    	insertDocument(id, createDate, "Description", pcassay.getDescription());
    	insertDocument(id, createDate, "Comments", pcassay.getComment());
    	
    }
    
    void insertDocument(Long id, java.sql.Date createDate, String type, String contents) throws Exception {
    	if( assayDocRs == null )
    		assayDocRs = getTable(conn, "assay_document");
    	assayDocRs.moveToInsertRow();
    	assayDocRs.updateLong("assay_document_id", nextVal(conn,"assay_document"));
    	assayDocRs.updateLong("assay_id", id);
    	assayDocRs.updateString("document_name", type);
    	assayDocRs.updateString("document_type", type);
    	
    	if( clob == null )
    		clob = oracle.sql.CLOB.createTemporary(conn, false, oracle.sql.CLOB.DURATION_SESSION);
    	clob.truncate(0);
    	Writer writer = clob.setCharacterStream(1);
    	writer.write(contents);
    	writer.close();
    	assayDocRs.updateClob("document_content", clob);
    	
    	assayDocRs.updateLong("version", 1L);
    	assayDocRs.updateDate("date_created", createDate);
    	assayDocRs.insertRow();
    }
    
    ResultSet getTable(Connection conn, String table) throws Exception {
    	PreparedStatement ps = conn.prepareStatement(String.format("SELECT %1$s.* FROM %1$s", table)
                , ResultSet.TYPE_SCROLL_SENSITIVE
                , ResultSet.CONCUR_UPDATABLE);
    	ResultSet rs = ps.executeQuery();
    	return rs;
    }

    Long nextVal(Connection conn, String table) throws SQLException {
        String sql = String.format("select %s_id_seq.nextval from dual", table);
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        rs.next();
        Long lng = rs.getLong(1);
        rs.close();
        ps.close();
        return lng;
    }
    
    void runMLPInsert() throws Exception {
        Set<Long> mlpAids = PubChemFactory.getInstance().getMolecularLibrariesOrBeforeAIDs();
        for(Long aid: mlpAids)
        	try {
       			insertAid(aid);
        	}
        	catch(Exception ex) {
        		ex.printStackTrace();
        		if( ex instanceof SQLException )
        			throw ex;
        	}
    }
    
    PCAssay getPCAssay(long assayId) throws Exception {
        InputStream is = PubChemFactory.getInstance().getXmlDescr(assayId);
        List<PCAssay> assays = PubChemXMLParserFactory.getInstance().populateAssayFromXML(is, false);
        PCAssay pcassay = assays.get(0);
        PubChemFactory.getInstance().populateAssayFromSummary(pcassay);
        return pcassay;
    }

    public static void main(String[] args) throws Exception {
//    	BasicConfigurator.configure();
        SqlInserter ins = new SqlInserter();
        ins.init();
        ins.insertAid(504814L);
//        ins.runMLPInsert();
    }
}
