package bard.util;

import java.io.Writer;
import java.net.URL;
import java.sql.Clob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.balusc.util.ObjectConverter;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.scripps.fl.pubchem.PubChemDB;
import edu.scripps.fl.pubchem.db.PCAssay;

public class AidInserter extends SqlInserter {

	private static final Logger log = LoggerFactory.getLogger(AidInserter.class);
	
	private ResultSet assayRs, extRefRs, assayDocRs, exptRs;
	private Clob clob;
//	private PreparedStatement assayPs, extRefPs, assayDocPs, exptPs, measurePs;
	
//	@Override public boolean passFilter(Long aid) {
//		return aid >= 463095 ? true : false;
//	}

	void processAid(Long aid) throws Exception {
//		Long assay_id = Util.getAssayIdFromAid(aid);
//		if( assay_id != null ) {
//			if( extRefPs == null )
//				extRefPs = Util.getConnection().prepareStatement("delete from external_reference ext_reference_id in (select ext_reference_id from assay a, experiment e, external_reference er where a.assay_id = e.assay_id and e.experiment_id = er.experiment_id and er.ext_assay_ref = ?)");
//			extRefPs.setLong(1, assay_id);
//			extRefPs.executeUpdate();
//			
//			if( assayDocPs == null )
//				assayDocPs = Util.getConnection().prepareStatement("delete from assay_document where assay_id = ?");
//			assayDocPs.setLong(1, assay_id);
//			assayDocPs.executeUpdate();
//			
//			if( exptPs == null )
//				exptPs = Util.getConnection().prepareStatement("delete from experiment where assay_id = ?");
//			exptPs.setLong(1, assay_id);
//			exptPs.executeUpdate();
//			
//			if( measurePs == null )
//				measurePs = Util.getConnection().prepareStatement("delete from measure_context_item where assay_id = ?");
//			measurePs.setLong(1, assay_id);
//			measurePs.executeUpdate();
//			
//			if( assayPs == null )
//				assayPs = Util.getConnection().prepareStatement("delete from assay where assay_id = ?");
//			assayPs.setLong(1, assay_id);
//			assayPs.executeUpdate();
//		}
		
		
		
		log.debug("Inserting aid: " + aid);
		PCAssay pcassay = Util.getPCAssay(aid);
		String version = String.format("%s.%s", pcassay.getVersion(), pcassay.getRevision());
		java.sql.Date createDate = new java.sql.Date(pcassay.getDepositDate().getTime());
		java.sql.Date holdDate = pcassay.getHoldUntilDate() != null ? new java.sql.Date(pcassay.getHoldUntilDate().getTime()) : null;

		Long assay_id = Util.nextVal("assay");
		if (assayRs == null)
			assayRs = Util.getTable("assay");
		assayRs.moveToInsertRow();
		assayRs.updateLong("assay_id", assay_id);
		assayRs.updateString("assay_status", "Active");
		assayRs.updateString("assay_short_name", pcassay.getExtRegId());
		assayRs.updateString("assay_name", pcassay.getName());
		assayRs.updateString("assay_version", version);
		assayRs.updateString("assay_type", "Regular");
		assayRs.updateString("designed_by", pcassay.getSourceName());
		assayRs.updateString("ready_for_extraction", "Ready");
		assayRs.updateLong("version", 1L);
		assayRs.updateDate("date_created", createDate);
		assayRs.insertRow();

		Long expt_id = Util.nextVal("experiment");
		if (exptRs == null)
			exptRs = Util.getTable("experiment");
		exptRs.moveToInsertRow();
		exptRs.updateLong("experiment_id", expt_id);
		exptRs.updateString("experiment_name", pcassay.getName());
		exptRs.updateString("experiment_status", "Pending");
		exptRs.updateString("ready_for_extraction", "Ready");
		exptRs.updateLong("assay_id", assay_id);
		if( holdDate != null )
			exptRs.updateDate("hold_until_date", holdDate);
		exptRs.updateLong("version", 1L);
		exptRs.updateDate("date_created", createDate);
		exptRs.insertRow();

		if (extRefRs == null)
			extRefRs = Util.getTable("external_reference");
		extRefRs.moveToInsertRow();
		extRefRs.updateLong("external_reference_id", Util.nextVal("external_reference"));
		extRefRs.updateLong("external_system_id", 1L);
		extRefRs.updateLong("experiment_id", expt_id);
		extRefRs.updateString("ext_assay_ref", "aid=" + pcassay.getAID());
		extRefRs.updateString("version", version);
		extRefRs.updateDate("date_created", createDate);
		extRefRs.insertRow();

		if( pcassay.getProtocol() != null )
			insertDocument(assay_id, createDate, "Protocol", pcassay.getProtocol());
		insertDocument(assay_id, createDate, "Description", pcassay.getDescription());
		if( pcassay.getComment() != null )
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
//		Set<Long> mlpAids = PubChemFactory.getInstance().getAIDs("\"NIH Molecular Libraries Program\"[SourceCategory]");
//		List<Long> aidsInBard = Util.getAidsInBard();
//		System.out.println(String.format("there are %s aids already in bard", aidsInBard.size()));
//		mlpAids.removeAll(aidsInBard);
//		System.out.println(String.format("processing %s aids", mlpAids.size()));
//		return mlpAids;
		return Util.getPCAssayAidsNotInBard();
	}

	public static void main(String[] args) throws Exception {
		URL url = AidInserter.class.getClassLoader().getResource("log4j.config.xml");
		DOMConfigurator.configure(url);

		url = AidInserter.class.getClassLoader().getResource("hibernate.broad.cfg.xml");
		PubChemDB.setUp(url);
		
		SqlInserter ins = new AidInserter();
		// ins.insertAid(504814L);
		if (args.length > 0) {
			List list = new ArrayList(Arrays.asList(args));
			ObjectConverter.convertList(list, Long.class);
			ins.process(list);
		} else
			ins.process(ins.getAids());
		
		XrefInserter xins = new XrefInserter();
		xins.process(ins.successes);

		Set<Long> set = new TreeSet();
		set.addAll(ins.successes);
		set.addAll(xins.successes);
		System.out.println("Bard Import Successes: " + set);
		set.clear();
		set.addAll(ins.failures);
		set.addAll(xins.failures);
		System.out.println("Bard Import Failures: " + set);
	}
}
