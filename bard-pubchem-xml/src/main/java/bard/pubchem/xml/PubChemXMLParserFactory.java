/*
 * Copyright 2010 The Scripps Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bard.pubchem.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.list.GrowthList;
import org.dom4j.Document;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import bard.pubchem.PubChemException;
import bard.pubchem.model.PCAssay;
import bard.pubchem.model.PCAssayColumn;
import bard.pubchem.model.PCAssayPanel;
import bard.pubchem.model.PCAssayResult;
import bard.pubchem.model.PCAssayXRef;
import bard.pubchem.model.PCPlotLabel;
import bard.pubchem.model.XRef;

/**
 * @author Mark Southern
 * @author Stephanie Canny
 * 
 *         // Code Example: InputStream is =
 *         PubChemFactory.getInstance().getXmlDescr(2551L); List<PCAssay> assays
 *         = PubChemXMLParserFactory.getInstance().populateAssayFromXML(is,
 *         true);
 * 
 */
public class PubChemXMLParserFactory {

	private static final Logger log = LoggerFactory.getLogger(PubChemXMLParserFactory.class);

	protected static boolean DEBUGGING = false;
	private static PubChemXMLParserFactory instance;
	private static Map<String, String> targetType;
	private String separator = System.getProperty("line.separator");

	private static void createTargetDBMap() {
		targetType = new HashMap<String, String>();
		targetType.put("aid", "pcassay");
		targetType.put("biosystem", "biosystems");
		targetType.put("cid", "pccompound");
		targetType.put("gene", "gene");
		targetType.put("gi", "gi"); // Genbank general Id
		targetType.put("mesh", "mesh");
		targetType.put("mim", "omim");
		targetType.put("mmdb", "mmdb"); // 3d domains db not in einfo list of
										// databases!
		targetType.put("nucleotide", "nucleotide");
		targetType.put("nucleotide-gi", "nucleotide");
		targetType.put("pmid", "pubmed");
		targetType.put("protein", "protein");
		targetType.put("protein-gi", "protein");
		targetType.put("regid", "regid"); // gene symbols, urls, aids.
		targetType.put("rn", "rn"); // Substance Registry, CAS numbers etc.
		targetType.put("rna", "nucleotide");
		targetType.put("sid", "pcsubstance");
		targetType.put("taxonomy", "taxonomy");
	}

	public static PubChemXMLParserFactory getInstance() {
		if (instance == null) {
			synchronized (PubChemXMLParserFactory.class) { // 1
				if (instance == null) {
					synchronized (PubChemXMLParserFactory.class) { // 3
						// inst = new Singleton(); //4
						instance = new PubChemXMLParserFactory();
						createTargetDBMap();
					}
					// instance = inst; //5
				}
			}
		}
		return instance;
	}

	public PCAssay parsePublicAssayDescription(int aid) throws IOException, SAXException, ParserConfigurationException, PubChemException {
		String url = String.format("http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=%s&q=expdesc_xmldisplay", aid);
		InputStream is = new URL(url).openStream();
		List<PCAssay> assays = populateAssaysFromXML(is, false);
		return assays.get(0);
	}

	public List<PCAssay> populateAssaysFromXML(InputStream inputStream, boolean includeResults) throws IOException, SAXException,
			ParserConfigurationException, PubChemException {
		Document doc = PubChemUtils.getDocument(inputStream);
		PubChemUtils.closeQuietly(inputStream);
		// SAXReader reader = new SAXReader();
		// reader.addHandler("/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_data/PC-AssayResults",
		// new ElementHandler() {
		// public void onStart(ElementPath path) {
		//
		// }
		//
		// public void onEnd(ElementPath path) {
		// path.getCurrent().detach();
		// }
		// });
		// Document doc = reader.read(inputStream);

		return populateAssaysFromXMLDocument(doc, includeResults);
	}

	public List<PCAssay> populateAssaysFromXMLDocument(Document doc, boolean includeResults) throws PubChemException {
		List<PCAssay> assays = new ArrayList<PCAssay>();
		// List<Node> assayNodes =
		// doc.selectNodes("/PC-AssayContainer/PC-AssaySubmit");
		List<Node> assayNodes = doc.selectNodes("//PC-AssayDescription");
		for (Node node : assayNodes) {
			PCAssay assay = populateAssayFromXMLNode(node);
			populateAssayResultsFromXML(assay, node);
			assays.add(assay);
		}
		return assays;
	}

	protected void populateAssayResultsFromXML(PCAssay assay, Node assayDescriptionNode) {
		List<Node> assayResultNodes = assayDescriptionNode.selectNodes("../../../PC-AssaySubmit_data/PC-AssayResults");
		if (assayResultNodes.size() == 0)
			return;

		for (Node resultNode : assayResultNodes) {
			PCAssayResult result = new PCAssayResult();

			String val = resultNode.selectSingleNode("PC-AssayResults_sid").valueOf("text()");
			result.setSID(Long.parseLong(val));

			val = resultNode.selectSingleNode("PC-AssayResults_outcome").valueOf("@value");
			val = val.substring(0, 1).toUpperCase() + val.substring(1);
			result.setOutcome(val);

			val = resultNode.selectSingleNode("PC-AssayResults_rank").valueOf("text()");
			result.setRankScore(Integer.parseInt(val));

			List<Node> assayDataNodes = resultNode.selectNodes("PC-AssayResults_data/PC-AssayData");

			List<String> all = GrowthList.decorate(new ArrayList<String>(assay.getColumns().size() - 2));
			result.setAllValues(all);
			for (Node node : assayDataNodes) {
				val = node.valueOf("PC-AssayData_tid/text()");
				int index = Integer.parseInt(val) - 1;
				val = node.selectSingleNode(".//*[starts-with(name(),'PC-AssayData_value_')]").getText();
				all.set(index, val);
			}
			assay.getResults().add(result);
		}

	}

	protected PCAssay populateAssayFromXMLNode(Node topNode) throws PubChemException {
		// String assayDescPath =
		// "PC-AssaySubmit_assay/PC-AssaySubmit_assay_descr/PC-AssayDescription";
		Node assayDescNode = null;
		if (topNode.getName().equals("PC-AssayDescription"))
			assayDescNode = topNode;
		else {
			assayDescNode = topNode.selectSingleNode(".//PC-AssayDescription");
		}
		if (assayDescNode == null)
			throw new PubChemException(String.format("Cannot find PC-AssayDescription node in provided node %s", topNode.getPath()));

		Node node = assayDescNode.selectSingleNode("PC-AssayDescription_aid/PC-ID/PC-ID_id");
		Integer aid = new Integer(node.getText());

		try {
			PCAssay assay = new PCAssay();
			if (aid > 0)
				assay.setAID(aid);

			node = assayDescNode.selectSingleNode("PC-AssayDescription_aid/PC-ID/PC-ID_version");
			Integer version = new Integer(node.getText());
			assay.setVersion(version);

			node = assayDescNode.selectSingleNode("PC-AssayDescription_revision");
			Integer revision = new Integer(node.getText());
			assay.setRevision(revision);

			Node trackingNode = assayDescNode.selectSingleNode("PC-AssayDescription_aid-source/PC-Source/PC-Source_db/PC-DBTracking");

			node = trackingNode.selectSingleNode("PC-DBTracking_name");
			assay.setSourceName(node.getText());

			node = trackingNode.selectSingleNode("PC-DBTracking_source-id/Object-id/Object-id_str");
			assay.setExtRegId(node.getText());

			// hold until date
			node = trackingNode.selectSingleNode("PC-DBTracking_date");
			if (node != null) {
				String year = node.selectSingleNode("Date/Date_std/Date-std/Date-std_year").getText();
				String month = node.selectSingleNode("Date/Date_std/Date-std/Date-std_month").getText();
				String day = node.selectSingleNode("Date/Date_std/Date-std/Date-std_day").getText();
				if (DEBUGGING)
					log.info("year: " + year + " month: " + month + " day: " + day);
				Calendar calendar = Calendar.getInstance();
				calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
				assay.setHoldUntilDate(calendar.getTime());
				if (DEBUGGING)
					log.info(calendar.getTime().toString());
			}

			node = assayDescNode.selectSingleNode("PC-AssayDescription_name");
			assay.setName(node.getText());

			List<Node> nodes = assayDescNode.selectNodes("PC-AssayDescription_description/PC-AssayDescription_description_E");
			assay.setDescription(join(nodes, separator));

			nodes = assayDescNode.selectNodes("PC-AssayDescription_protocol/PC-AssayDescription_protocol_E");
			assay.setProtocol(join(nodes, separator));

			nodes = assayDescNode.selectNodes("PC-AssayDescription_comment/PC-AssayDescription_comment_E");
			assay.setComment(join(nodes, separator));

			node = assayDescNode.selectSingleNode("PC-AssayDescription_activity-outcome-method");
			if (node != null)
				assay.setActivityOutcomeMethod(node.valueOf("@value"));

			handlePlots(assay, assayDescNode);

			node = assayDescNode.selectSingleNode("PC-AssayDescription_grant-number/PC-AssayDescription_grant-number_E");
			if (node != null)
				assay.setGrantNumber(node.getText());

			node = assayDescNode.selectSingleNode("PC-AssayDescription_project-category");
			if (node != null)
				assay.setProjectCategory(node.valueOf("@value"));

			assay.getAssayXRefs().removeAll(assay.getAssayXRefs());

			nodes = assayDescNode.selectNodes("PC-AssayDescription_xref/PC-AnnotatedXRef");
			handleXRefs(assay, null, nodes);

			nodes = assayDescNode.selectNodes("PC-AssayDescription_target/PC-AssayTargetInfo");
			handleTargetXRefs(assay, null, nodes);

			handlePanels(assay, assayDescNode);

			handleColumns(assay, assayDescNode);

			handleComments(assay, assayDescNode);

			return assay;
		} catch (Exception ex) {
			throw new RuntimeException("Problem with AID " + aid, ex);
		}
	}

	protected void handlePlots(PCAssay assay, Node assayDescNode) {
		assay.getPlotLabels().clear();
		List<Node> nodes = assayDescNode.selectNodes("PC-AssayDescription_dr/PC-AssayDRAttr");
		for (Node node : nodes) {
			PCPlotLabel label = new PCPlotLabel();
			label.setAssay(assay);
			label.setPlotLabel(Integer.parseInt(node.selectSingleNode("PC-AssayDRAttr_id").getText()));
			label.setTitle(node.selectSingleNode("PC-AssayDRAttr_descr").getText());
			label.setConcentrationTitle(node.selectSingleNode("PC-AssayDRAttr_dn").getText());
			label.setResponseTitle(node.selectSingleNode("PC-AssayDRAttr_rn").getText());
			String test = nullSafeGet(node, "PC-AssayDRAttr_type", "text()");
			label.setDerivedByEquation(!"".equals(test) & !"0".equals(test) ? true : false);
			assay.getPlotLabels().add(label);
		}
	}

	protected void handleComments(PCAssay assay, Node assayDescNode) {
		assay.getCategorizedComments().clear();
		List<Node> nodes = assayDescNode.selectNodes("PC-AssayDescription_categorized-comment/PC-CategorizedComment");
		for (Node commentNode : nodes) {
			String key = commentNode.selectSingleNode("PC-CategorizedComment_title").getText();
			String value = join(commentNode.selectNodes("PC-CategorizedComment_comment/PC-CategorizedComment_comment_E"), separator);
			assay.getCategorizedComments().put(key, value);
		}
	}

	protected void handlePanels(PCAssay assay, Node assayDescNode) {
		Node node = assayDescNode.selectSingleNode("PC-AssayDescription_is-panel");
		if (node == null)
			assay.setPanel(false);
		else
			assay.setPanel("true".equals(node.valueOf("@value")));

		if (assay.isPanel()) {
			node = assayDescNode.selectSingleNode("PC-AssayDescription_panel-info/PC-AssayPanel/PC-AssayPanel_name");
			assay.setPanelName(node.valueOf("text()"));
			node = assayDescNode.selectSingleNode("PC-AssayDescription_panel-info/PC-AssayPanel/PC-AssayPanel_descr");
			assay.setPanelDescription(node == null ? "" : node.valueOf("text()"));
		}

		List<Node> nodes = assayDescNode
				.selectNodes("PC-AssayDescription_panel-info/PC-AssayPanel/PC-AssayPanel_member/PC-AssayPanelMember");
		for (Node n : nodes) {
			String mid = n.selectSingleNode("PC-AssayPanelMember_mid").getText();
			Node node2 = n.selectSingleNode("PC-AssayPanelMember_name");
			String name = node2 == null ? "" : node2.getText();

			PCAssayPanel panel = new PCAssayPanel();
			panel.setAssay(assay);
			assay.getPanels().add(panel);
			panel.setPanelNumber(Integer.parseInt(mid));
			panel.setName(name);

			node2 = n.selectSingleNode("PC-AssayPanelMember_description");
			panel.setDescription(node2 == null ? "" : node2.getText());

			List<Node> nodes2 = n.selectNodes("PC-AssayPanelMember_protocol/PC-AssayPanelMember_protocol_E");
			panel.setProtocol(join(nodes2, separator));

			nodes2 = n.selectNodes("PC-AssayPanelMember_comment/PC-AssayPanelMember_comment_E");
			panel.setComment(join(nodes2, separator));

			List<Node> xrefNodes = n.selectNodes("PC-AssayPanelMember_xref/PC-AnnotatedXRef");
			handleXRefs(assay, panel, xrefNodes);

			List<Node> targetNodes = n.selectNodes("PC-AssayPanelMember_target/PC-AssayTargetInfo");
			handleTargetXRefs(assay, panel, targetNodes);
		}
	}

	private PCAssayColumn ensureColumn(PCAssay assay, int tid, String name, String type) {
		PCAssayColumn col;
		col = new PCAssayColumn();
		col.setAssay(assay);
		col.setTID(tid);
		assay.getColumns().add(col);
		col.setName(name);
		col.setType(type);
		return col;
	}

	protected void handleColumns(PCAssay assay, Node assayDescNode) {
		Map<Integer, PCAssayColumn> map = new HashMap<Integer, PCAssayColumn>();
		for (PCAssayColumn col : assay.getColumns())
			map.put(col.getTID(), col);

		ensureColumn(assay, -1, "Outcome", "string");
		ensureColumn(assay, 0, "Score", "float");

		Map<Integer, PCAssayPanel> mapPanels = new HashMap<Integer, PCAssayPanel>();
		for (PCAssayPanel panel : assay.getPanels())
			mapPanels.put(panel.getPanelNumber(), panel);

		List<Node> nodes = assayDescNode.selectNodes("PC-AssayDescription_results/PC-ResultType");
		for (Node n : nodes) {
			String tid = n.selectSingleNode("PC-ResultType_tid").getText();
			String name = n.selectSingleNode("PC-ResultType_name").getText();
			String type = n.selectSingleNode("PC-ResultType_type").valueOf("@value");
			PCAssayColumn column = ensureColumn(assay, Integer.parseInt(tid), name, type);

			Node node = n.selectSingleNode("PC-ResultType_unit");
			if (node != null)
				column.setUnit(node.valueOf("@value"));

			List<Node> descNodes = n.selectNodes("PC-ResultType_description/PC-ResultType_description_E");
			column.setDescription(join(descNodes, separator));
			if (DEBUGGING)
				log.info("Column description: " + join(descNodes, separator));

			node = n.selectSingleNode("PC-ResultType_ac");
			if (node != null)
				column.setActiveConcentration("true".equals(node.valueOf("@value")));

			node = n.selectSingleNode("PC-ResultType_tc");
			if (node != null) {
				Node node2 = node.selectSingleNode("PC-ConcentrationAttr/PC-ConcentrationAttr_dr-id");
				if (node2 != null) {
					column.setCurvePlotLabel(Integer.parseInt(node2.getText()));
				}
				String testedConc = node.selectSingleNode("PC-ConcentrationAttr/PC-ConcentrationAttr_concentration").getText();
				column.setTestedConcentration(Double.parseDouble(testedConc));
				String testedUnit = node.selectSingleNode("PC-ConcentrationAttr/PC-ConcentrationAttr_unit").valueOf("@value");
				column.setTestedConcentrationUnit(testedUnit);
			}

			node = n.selectSingleNode("PC-ResultType_panel-info/PC-AssayPanelTestResult");
			if (node != null) {
				String panelId = node.selectSingleNode("PC-AssayPanelTestResult_mid").getText();
				PCAssayPanel panel = mapPanels.get(Integer.parseInt(panelId));
				column.setPanel(panel);
				String panelColumnType = node.selectSingleNode("PC-AssayPanelTestResult_readout-annot").valueOf("@value");
				column.setPanelReadoutType(panelColumnType);
			}
		}
	}

	protected void handleXRefs(PCAssay assay, PCAssayPanel panel, List<Node> nodes) {
		for (Node n : nodes) {
			Node node = n.selectSingleNode("PC-AnnotatedXRef_comment");
			String comment = node == null ? "" : node.getText();

			node = n.selectSingleNode("PC-AnnotatedXRef_xref/PC-XRefData/*");

			String type = node.getName();
			type = type.substring(type.lastIndexOf("_") + 1, type.length());
			String database = targetType.containsKey(type) ? targetType.get(type) : type;
			String id = node.getText();

			XRef xref = new XRef();
			xref.setXRefId(id);
			xref.setDatabase(database);
			xref.setType(type);

			PCAssayXRef aXref = new PCAssayXRef();
			aXref.setPanel(panel);
			aXref.setAssay(assay);
			aXref.setComment(comment);
			aXref.setXRef(xref);
			aXref.setTarget(false);
			assay.getAssayXRefs().add(aXref);
		}
	}

	private String nullSafeGet(Node node, String xpath, String expression) {
		Node node2 = node.selectSingleNode(xpath);
		if (null != node2)
			return node2.valueOf(expression);
		return "";
	}

	protected void handleTargetXRefs(PCAssay assay, PCAssayPanel panel, List<Node> nodes) {
		for (Node n : nodes) {
			String name = n.selectSingleNode("PC-AssayTargetInfo_name").getText();
			String id = n.selectSingleNode("PC-AssayTargetInfo_mol-id").getText();
			String type = n.selectSingleNode("PC-AssayTargetInfo_molecule-type").valueOf("@value");
			String database = targetType.containsKey(type) ? targetType.get(type) : type;
			Node taxonNode = n.selectSingleNode("PC-AssayTargetInfo_organism/BioSource/BioSource_org/Org-ref");
			String taxonName = "", taxonCommon = "", taxon = "";
			if (taxonNode != null) {
				taxonName = nullSafeGet(taxonNode, "Org-ref_taxname", "text()");
				taxonCommon = nullSafeGet(taxonNode, "Org-ref_common", "text()");
				String db = nullSafeGet(taxonNode, "Org-ref_db/Dbtag/Dbtag_db", "text()");
				if (!"taxon".equals(db))
					throw new RuntimeException("Non taxon BioSource Org-ref_db (was " + db + ")");
				taxonNode = taxonNode.selectSingleNode("Org-ref_db/Dbtag/Dbtag_tag/Object-id/Object-id_id");
				taxon = taxonNode.getText();
			}

			XRef xref = new XRef();
			xref.setXRefId(id);
			xref.setDatabase(database);
			xref.setType(type);
			xref.setName(name);

			PCAssayXRef aXref = new PCAssayXRef();
			aXref.setTarget(true);
			aXref.setPanel(panel);
			aXref.setAssay(assay);
			aXref.setXRef(xref);
			if (!taxon.equals("")) {
				aXref.setTaxon(Long.parseLong(taxon));
				aXref.setTaxonName(taxonName);
				aXref.setTaxonCommon(taxonCommon);
			}
			assay.getAssayXRefs().add(aXref);
		}
	}

	protected String join(Collection<Node> nodes, String separator) {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		for (Node node : nodes) {
			sb.append(node.getText());
			if (count++ < nodes.size() - 1)
				sb.append(separator);
		}
		return sb.toString();
	}
}