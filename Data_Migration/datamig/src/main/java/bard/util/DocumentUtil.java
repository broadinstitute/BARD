package bard.util;

import java.io.IOException;
import java.io.Writer;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentUtil {

	private static ResultSet assayDocRs;
	private static Clob clob;

//	static {
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			@Override
//			public void run() {
//				try {
//					assayDocRs.close();
//					clob.free();
//				}
//				catch(SQLException ex) {
//					ex.printStackTrace();
//				}
//			}
//		});
//	}

	public static void insertDocument(Long assay_id, java.sql.Date createDate, String name, String type, String contents) throws SQLException, IOException {
		if (assayDocRs == null)
			assayDocRs = Util.getTable("assay_document");
		assayDocRs.moveToInsertRow();
		assayDocRs.updateLong("assay_document_id", Util.nextVal("assay_document"));
		assayDocRs.updateLong("assay_id", assay_id);
		assayDocRs.updateString("document_name", name);
		assayDocRs.updateString("document_type", type);
		if (clob == null)
			clob = oracle.sql.CLOB.createTemporary(Util.getConnection(), false, oracle.sql.CLOB.DURATION_SESSION);
		clob.truncate(0);
		Writer writer = clob.setCharacterStream(1);
		writer.write(contents);
		writer.close();
		assayDocRs.updateClob("document_content", clob);
		assayDocRs.updateLong("version", 1L);
		assayDocRs.updateDate("date_created", createDate);
		assayDocRs.insertRow();
	}
}
