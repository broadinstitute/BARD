package bard.util;

import java.io.Writer;
import java.sql.Clob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.balusc.util.ObjectConverter;
import edu.scripps.fl.pubchem.PubChemFactory;
import edu.scripps.fl.pubchem.db.PCAssay;

public class AidInserter extends SqlInserter {

	private ResultSet assayRs, extRefRs, assayDocRs, exptRs;
	private Clob clob;
	
	@Override public boolean passFilter(Long aid) {
		return aid >= 463095 ? true : false;
	}

	void processAid(Long aid) throws Exception {
		System.out.println("Inserting aid: " + aid);
		PCAssay pcassay = Util.getPCAssay(aid);
		String version = String.format("%s.%s", pcassay.getVersion(), pcassay.getRevision());
		java.sql.Date createDate = new java.sql.Date(pcassay.getDepositDate().getTime());

		Long assay_id = Util.nextVal("assay");
		if (assayRs == null)
			assayRs = Util.getTable("assay");
		assayRs.moveToInsertRow();
		assayRs.updateLong("assay_id", assay_id);
		assayRs.updateString("assay_name", pcassay.getName());
		assayRs.updateString("assay_version", version);
		assayRs.updateLong("version", 1L);
		assayRs.updateString("designed_by", pcassay.getSourceName());
		assayRs.updateDate("date_created", createDate);
		assayRs.updateString("ready_for_extraction", "Ready");
		assayRs.updateString("assay_status", "Active");
		assayRs.insertRow();

		Long expt_id = Util.nextVal("experiment");
		if (exptRs == null)
			exptRs = Util.getTable("experiment");
		exptRs.moveToInsertRow();
		exptRs.updateLong("experiment_id", expt_id);
		exptRs.updateLong("version", 1L);
		exptRs.updateLong("assay_id", assay_id);
		exptRs.updateDate("date_created", createDate);
		exptRs.updateString("experiment_name", pcassay.getName());
		exptRs.updateString("experiment_status", "Pending");
		exptRs.updateString("ready_for_extraction", "Ready");
		exptRs.insertRow();

		if (extRefRs == null)
			extRefRs = Util.getTable("external_reference");
		extRefRs.moveToInsertRow();
		extRefRs.updateLong("external_reference_id", Util.nextVal("external_reference"));
		extRefRs.updateLong("experiment_id", expt_id);
		extRefRs.updateString("version", version);
		extRefRs.updateDate("date_created", createDate);
		extRefRs.updateString("ext_assay_ref", "aid=" + pcassay.getAID());
		extRefRs.updateLong("external_system_id", 1L);
		extRefRs.insertRow();

		insertDocument(assay_id, createDate, "Protocol", pcassay.getProtocol());
		insertDocument(assay_id, createDate, "Description", pcassay.getDescription());
		insertDocument(assay_id, createDate, "Comments", pcassay.getComment());

	}

	void insertDocument(Long id, java.sql.Date createDate, String type, String contents) throws Exception {
		if (assayDocRs == null)
			assayDocRs = Util.getTable("assay_document");
		assayDocRs.moveToInsertRow();
		assayDocRs.updateLong("assay_document_id", Util.nextVal("assay_document"));
		assayDocRs.updateLong("assay_id", id);
		assayDocRs.updateString("document_name", type);
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

	public Collection<Long> getAids() throws Exception {
		Set<Long> mlpAids = PubChemFactory.getInstance().getMolecularLibrariesOrBeforeAIDs();
		mlpAids.removeAll(PubChemFactory.getInstance().getAIDs("\"hasonhold\"[filt]"));
		// remove existing in table
		mlpAids.removeAll(Util.getAidsInBard());
		return mlpAids;
	}

	public static void main(String[] args) throws Exception {
		SqlInserter ins = new AidInserter();
		// ins.insertAid(504814L);
		if (args.length > 0) {
			List list = new ArrayList(Arrays.asList(args));
			ObjectConverter.convertList(list, Long.class);
			ins.process(list);
		} else
			ins.process(ins.getAids());
	}
}
