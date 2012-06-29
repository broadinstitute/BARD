package bard.util;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.balusc.util.ObjectConverter;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import edu.scripps.fl.pubchem.db.PCAssay;
import edu.scripps.fl.pubchem.db.PCAssayXRef;

public class XrefInserter extends SqlInserter {

	private ResultSet measureContextItemRs;
	private Date date = new Date(new java.util.Date().getTime());
	private Map<String, Long> attributeMap;
	// map database names in xref.xref_database field to what they are called in bard's element table
	private Map<String, String> typeMap = new HashMap<String, String>() {{
			put("biosystems", "Biosystems");
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
			put("regid", "registered ID");
			put("rn", "substance CAS");
			put("taxonomy", "taxonomy");
	}};
	private PreparedStatement deleteItems, labelForXref;
	private Long commentId, roleId, targetId;

	@Override Collection<Long> getAids() throws Exception {
		return Util.getAidsInBard();
	}

	@Override public void process(Collection<Long> aids) throws Exception {
		// for each bard name, get it's Id.
		Map<String, Long> attrMap = new QueryRunner()
				.query(Util.getConnection(),
						"select label, element_id from element where label in ( 'PubChem AID' , 'Biosystems' , 'PubChem CID' , 'gene' , 'Genbank ID' , 'Mesh term' , 'Mendelian Inheritance in Man' , 'macromolecule name' , 'PubMed' , 'protein' , 'registered ID' , 'substance CAS' , 'nucleotide' , 'PubChem SID' , 'taxonomy', 'External Link' )",
						new SimpleMapHandler<String, Long>());
		// then map the id's to the ncbi database names
		attributeMap = new HashMap();
		for (String key : typeMap.keySet()) {
			String name = typeMap.get(key);
			if (!attrMap.containsKey(name))
				throw new Exception("Unknown xref type: " + name);
			Number val = attrMap.get(name);
			attributeMap.put(key, val.longValue());
		}
		commentId = Util.getElementIdByLabel("comment");
		roleId = Util.getElementIdByLabel("assay component role");
		targetId = Util.getElementIdByLabel("target");
		super.process(aids);
	}

	@Override void processAid(Long aid) throws Exception {
		System.out.println(String.format("%s, Inserting aid: %s", new java.util.Date(), aid));
		PCAssay pcassay = Util.getPCAssay(aid);
		Long assay_id = Util.getAssayIdFromAid(aid);
		// delete existing items for this assay (created by same user)
		if (deleteItems == null)
			deleteItems = Util.getConnection().prepareStatement("delete from measure_context_item where assay_id = ? and modified_by = ?");
		deleteItems.setLong(1, assay_id);
		deleteItems.setString(2, "southern");
		deleteItems.executeUpdate();

		// xrefs are not unique in pubchem's schema. here we check for that and
		// don't enter the duplicates
		Set<String> unique = new HashSet();
		for (PCAssayXRef axref : pcassay.getAssayXRefs()) {
			String type = axref.getXRef().getDatabase();
			// if its not in the map, its not one we care about
			if (!attributeMap.containsKey(type))
				continue;
			// we also don't care about assays since these relationships should
			// be in the project table
			if ("pcassay".equals(type))
				continue;

			Long attribute_id = attributeMap.get(type);
			Long id = Util.nextVal("measure_context_item");
			if (unique.contains(axref.getXRef().getXRefId()))
				continue;
			else
				unique.add(axref.getXRef().getXRefId());

			String valueDisplay = axref.getXRef().getXRefId();
			if ("pccompound".equals(axref.getXRef().getDatabase()))
				valueDisplay = String.format("PubChem CID [%s]", valueDisplay);
			else if ("pcsubstance".equals(axref.getXRef().getDatabase()))
				valueDisplay = String.format("PubChem SID [%s]", valueDisplay);
			else if ("mmdb".equals(axref.getXRef().getDatabase()))
				valueDisplay = String.format("3D Macromolecular Structure [%s]", valueDisplay);
			else {
				valueDisplay = new QueryRunner().query(Util.getConnection(),
						"select label from southern.xref_label where xref_identifier = ? and xref_database = ?", new ScalarHandler<String>(), axref.getXRef()
								.getXRefId(), axref.getXRef().getDatabase());
				// finally set the display equal to the external id. We can
				// check for these later.
				if (valueDisplay == null)
					valueDisplay = axref.getXRef().getXRefId();
			}

			if (measureContextItemRs == null)
				measureContextItemRs = Util.getTable("measure_context_item");
			measureContextItemRs.moveToInsertRow();
			measureContextItemRs.updateLong("measure_context_item_id", id);
			measureContextItemRs.updateLong("group_measure_context_item_id", id);
			measureContextItemRs.updateLong("assay_id", assay_id);
			measureContextItemRs.updateLong("attribute_id", attribute_id);
			measureContextItemRs.updateString("attribute_type", "Fixed");
			measureContextItemRs.updateNull("value_id");
			measureContextItemRs.updateString("ext_value_id", axref.getXRef().getXRefId());
			measureContextItemRs.updateString("value_display", valueDisplay);
			measureContextItemRs.updateLong("version", 1L);
			measureContextItemRs.updateDate("date_created", date);
			measureContextItemRs.updateString("modified_by", "southern");
			measureContextItemRs.insertRow();

			if (axref.isTarget()) {
				Long nid = Util.nextVal("measure_context_item");
				measureContextItemRs.moveToInsertRow();
				measureContextItemRs.updateLong("measure_context_item_id", nid);
				measureContextItemRs.updateLong("group_measure_context_item_id", id);
				measureContextItemRs.updateLong("assay_id", assay_id);
				measureContextItemRs.updateLong("attribute_id", roleId);
				measureContextItemRs.updateString("attribute_type", "Fixed");
				measureContextItemRs.updateLong("value_id", targetId);
				measureContextItemRs.updateString("value_display", String.format("%s %s", WordUtils.capitalize(axref.getXRef().getDatabase()), "Target"));
				measureContextItemRs.updateLong("version", 1L);
				measureContextItemRs.updateDate("date_created", date);
				measureContextItemRs.updateString("modified_by", "southern");
				measureContextItemRs.insertRow();
			}

			if (StringUtils.isNotBlank(axref.getComment()) & !axref.getComment().toUpperCase().equals(valueDisplay.toUpperCase())) {
				Long nid = Util.nextVal("measure_context_item");
				measureContextItemRs.moveToInsertRow();
				measureContextItemRs.updateLong("measure_context_item_id", nid);
				measureContextItemRs.updateLong("group_measure_context_item_id", id);
				measureContextItemRs.updateLong("assay_id", assay_id);
				measureContextItemRs.updateLong("attribute_id", commentId);
				measureContextItemRs.updateString("attribute_type", "Fixed");
				measureContextItemRs.updateNull("value_id");
				measureContextItemRs.updateString("value_display", axref.getComment());
				measureContextItemRs.updateLong("version", 1L);
				measureContextItemRs.updateDate("date_created", date);
				measureContextItemRs.updateString("modified_by", "southern");
				measureContextItemRs.insertRow();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		XrefInserter ins = new XrefInserter();
		if (args.length > 0) {
			List list = new ArrayList(Arrays.asList(args));
			ObjectConverter.convertList(list, Long.class);
			ins.process(list);
		} else
			ins.process(ins.getAids());
	}
}