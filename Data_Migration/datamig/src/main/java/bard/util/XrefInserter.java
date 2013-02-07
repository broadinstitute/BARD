package bard.util;

import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.balusc.util.ObjectConverter;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.xml.DOMConfigurator;

import bard.util.app.XRefLabelFetch;
import bard.util.dbutil.SimpleMapHandler;
import edu.scripps.fl.pubchem.PubChemDB;
import edu.scripps.fl.pubchem.db.PCAssay;
import edu.scripps.fl.pubchem.db.PCAssayXRef;

public class XrefInserter extends SqlInserter {

	public static void main(String[] args) throws Exception {
		URL url = XrefInserter.class.getClassLoader().getResource("log4j.config.xml");
		DOMConfigurator.configure(url);

		url = XrefInserter.class.getClassLoader().getResource("hibernate.broad.cfg.xml");
		PubChemDB.setUp(url);

		XrefInserter ins = new XrefInserter();
		if (args.length > 0) {
			List list = new ArrayList(Arrays.asList(args));
			ObjectConverter.convertList(list, Long.class);
			ins.process(list);
		} else
			ins.process(ins.getAids());
	}

	private ResultSet assayContextItemRs, assayContextRs;
	private Map<String, Long> attributeMap;
	private Long commentId, roleId, targetId;
	private Date date = new Date(new java.util.Date().getTime());
	private PreparedStatement deleteContextItems, labelForXref, deleteDocuments;
	private MultiKeyMap labelMap;

	// map database names in xref.xref_database field to what they are called in
	// bard's element table
	private Map<String, String> typeMap = new HashMap<String, String>() {
		{
			put("biosystems", "BioSystems");
			put("gi", "Genbank ID");
			put("gene", "gene");
			put("mmdb", "macromolecule name");
			put("omim", "Mendelian Inheritance in Man");
			put("mesh", "Mesh term");
			put("nucleotide", "nucleotide");
			put("protein", "protein");
			put("pcassay", "PubChem AID");
			put("pccompound", "PubChem CID");
			put("pcsubstance", "PubChem SID");
			put("pubmed", "PubMed");
			// put("regid", "registered ID");
			put("rn", "CAS registry");
			put("taxonomy", "species name");
		}
	};

	@Override
	Collection<Long> getAids() throws Exception {
		return Util.getAidsInBard();
	}

	protected String getLabel(PCAssayXRef axref) throws Exception {
//		String label = new QueryRunner().query(Util.getConnection(), "select label from xref_label where xref_identifier = ? and xref_database = ?",
//				new ScalarHandler<String>(), axref.getXRef().getXRefId(), axref.getXRef().getDatabase());
//		return label;
		if( labelMap == null )
			labelMap = new XRefLabelFetch().process();
		
		Object obj = labelMap.get(axref.getXRef().getXRefId(), axref.getXRef().getDatabase());
		return obj == null ? null : obj.toString();
	}

	@Override
	public void process(Collection<Long> aids) throws Exception {
		// for each bard name, get it's Id.
		Map attrMap = (Map) new QueryRunner()
				.query(Util.getConnection(),
						"select label, element_id from element where label in ( 'PubChem AID' , 'BioSystems' , 'PubChem CID' , 'gene' , 'Genbank ID' , 'Mesh term' , 'Mendelian Inheritance in Man' , 'macromolecule name' , 'PubMed' , 'protein' , 'registered ID' , 'CAS registry' , 'nucleotide' , 'PubChem SID' , 'species name' )",
						new SimpleMapHandler());
		// then map the id's to the ncbi database names
		attributeMap = new HashMap();
		for (String key : typeMap.keySet()) {
			String name = typeMap.get(key);
			if (!attrMap.containsKey(name))
				throw new Exception("Unknown xref type: " + name);
			Number val = (Number) attrMap.get(name);
			attributeMap.put(key, val.longValue());
		}

		typeMap.put("asurl", "Assay Link");
		typeMap.put("dburl", "External Database Link");
		typeMap.put("sburl", "Substance Link");

		commentId = Util.getElementIdByLabel("comment");
		roleId = Util.getElementIdByLabel("assay component role");
		targetId = Util.getElementIdByLabel("target");

		deleteContextItems = Util.getConnection().prepareStatement("delete from assay_context_item where assay_context_id = ?");
		deleteDocuments = Util.getConnection().prepareStatement(
				"delete from assay_document where modified_by = 'southern' and document_type in ('Paper','External URL')");

		assayContextRs = Util.getTable("assay_context");
		assayContextItemRs = Util.getTable("assay_context_item");

		super.process(aids);
	}

	protected Long getContextId(Long assay_id, String contextName) throws SQLException {
		Number contextId = new QueryRunner().query(Util.getConnection(), "select assay_context_id from assay_context where assay_id = ? and context_name = ?",
				new ScalarHandler<Number>(), assay_id, contextName);
		if (contextId != null) {
			deleteContextItems.setLong(1, contextId.longValue());
			deleteContextItems.executeUpdate();
		} else {
			contextId = Util.nextVal("assay_context");
			assayContextRs.moveToInsertRow();
			assayContextRs.updateLong("assay_context_id", contextId.longValue());
			assayContextRs.updateLong("display_order", 1L);
			assayContextRs.updateLong("assay_id", assay_id);
			assayContextRs.updateString("context_name", contextName);
			assayContextRs.updateLong("version", 1L);
			assayContextRs.updateDate("date_created", date);
			assayContextRs.updateString("modified_by", "southern");
			assayContextRs.insertRow();
		}
		return contextId.longValue();
	}

	@Override
	void processAid(Long aid) throws Exception {
		System.out.println(String.format("%s, Inserting aid: %s", new java.util.Date(), aid));
		PCAssay pcassay = Util.getPCAssay(aid);
		Long assay_id = Util.getAssayIdFromAid(aid);
		// delete existing items for this assay (created by same user

		String contextName = "Context for PubChem AID " + pcassay.getAID();

		Long targetContextId = null;
		Long contextId = getContextId(assay_id, contextName);
		deleteDocuments.executeUpdate();

		// xrefs are not unique in pubchem's schema. here we check for that and
		// don't enter the duplicates
		long displayOrder = 0;
		long targetDisplayOrder = 0;
		Set<String> unique = new HashSet();

		MultiValueMap map = new MultiValueMap();
		for (PCAssayXRef axref : pcassay.getAssayXRefs())
			map.put(axref.getXRef().getDatabase(), axref);
		for (String database : (Collection<String>) map.keySet()) {
			String type = typeMap.get(database);
			Collection<PCAssayXRef> col = map.getCollection(database);
			String attributeType = col.size() > 0 ? "List" : "Fixed";
			for (PCAssayXRef axref : col) {
				if ("0".equals(axref.getXRef().getXRefId()))
					continue;

				if (database.matches("..url")) {
					String name = axref.getXRef().getXRefId();
					DocumentUtil.insertDocument(assay_id, date, type, "External URL", name);
					continue;
				} else if ("pubmed".equalsIgnoreCase(type)) {
					String name = getLabel(axref);
					DocumentUtil.insertDocument(assay_id, date, name, "Paper", "http://www.ncbi.nlm.nih.gov/pubmed/" + axref.getXRef().getXRefId());
					continue;
				} else if (!attributeMap.containsKey(type)) // if its not in the
															// map, its not one
															// we care about
					continue;
				// we also don't care about assays since these relationships
				// should
				// be in the project table
				else if ("pcassay".equals(type))
					continue;

				Long attribute_id = attributeMap.get(type);
				Long id = Util.nextVal("assay_context_item");
				if (unique.contains(axref.getXRef().getXRefId()))
					continue;
				else
					unique.add(axref.getXRef().getXRefId());

				String valueDisplay = axref.getXRef().getXRefId();
				if ("pccompound".equalsIgnoreCase(axref.getXRef().getDatabase()))
					valueDisplay = String.format("PubChem CID [%s]", valueDisplay);
				else if ("pcsubstance".equalsIgnoreCase(axref.getXRef().getDatabase()))
					valueDisplay = String.format("PubChem SID [%s]", valueDisplay);
				else if ("mmdb".equalsIgnoreCase(axref.getXRef().getDatabase()))
					valueDisplay = String.format("3D Macromolecular Structure [%s]", valueDisplay);
				else {
					valueDisplay = getLabel(axref);
					// finally set the display equal to the external id. We can
					// check for these later.
					if (valueDisplay == null)
						valueDisplay = axref.getXRef().getXRefId();
				}

				assayContextItemRs.moveToInsertRow();
				assayContextItemRs.updateLong("assay_context_item_id", id);
				assayContextItemRs.updateLong("assay_context_id", contextId.longValue());
				assayContextItemRs.updateLong("display_order", displayOrder++);
				assayContextItemRs.updateLong("attribute_id", attribute_id);
				assayContextItemRs.updateString("attribute_type", attributeType);
				assayContextItemRs.updateNull("value_id");
				assayContextItemRs.updateString("ext_value_id", axref.getXRef().getXRefId());
				assayContextItemRs.updateString("value_display", valueDisplay);
				assayContextItemRs.updateLong("version", 1L);
				assayContextItemRs.updateDate("date_created", date);
				assayContextItemRs.updateString("modified_by", "southern");
				assayContextItemRs.insertRow();

				if (axref.isTarget()) {

					if (targetContextId == null)
						targetContextId = getContextId(assay_id, "Target context for PubChem AID " + pcassay.getAID());

					Long nid = Util.nextVal("assay_context_item");
					assayContextItemRs.moveToInsertRow();
					assayContextItemRs.updateLong("assay_context_item_id", nid);
					assayContextItemRs.updateLong("assay_context_id", targetContextId);
					assayContextItemRs.updateLong("display_order", targetDisplayOrder++);
					assayContextItemRs.updateLong("attribute_id", roleId);
					assayContextItemRs.updateString("attribute_type", "Fixed");
					assayContextItemRs.updateLong("value_id", targetId);
					assayContextItemRs.updateString("value_display", "target");
					assayContextItemRs.updateLong("version", 1L);
					assayContextItemRs.updateDate("date_created", date);
					assayContextItemRs.updateString("modified_by", "southern");
					assayContextItemRs.insertRow();
				}

				/*
				 * we don't care about inserting comments any more. They don't
				 * add much. if (StringUtils.isNotBlank(axref.getComment()) &&
				 * !axref
				 * .getComment().toUpperCase().equals(valueDisplay.toUpperCase
				 * ())) { Long nid = Util.nextVal("assay_context_item");
				 * assayContextItemRs.moveToInsertRow();
				 * assayContextItemRs.updateLong("assay_context_item_id", nid);
				 * assayContextItemRs.updateLong("assay_context_id",
				 * contextId.longValue());
				 * assayContextItemRs.updateLong("display_order",
				 * displayOrder++);
				 * assayContextItemRs.updateLong("attribute_id", commentId);
				 * assayContextItemRs.updateString("attribute_type", "Fixed");
				 * assayContextItemRs.updateNull("value_id");
				 * assayContextItemRs.updateString("value_display",
				 * axref.getComment()); assayContextItemRs.updateLong("version",
				 * 1L); assayContextItemRs.updateDate("date_created", date);
				 * assayContextItemRs.updateString("modified_by", "southern");
				 * assayContextItemRs.insertRow(); }
				 */
			}
		}
	}
}