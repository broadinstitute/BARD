package bard.pubchem.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import bard.pubchem.model.PCAssay;
import bard.pubchem.model.PCAssayColumn;
import bard.pubchem.model.PCAssayPanel;
import bard.pubchem.model.PCAssayXRef;
import bard.pubchem.model.XRef;


public class PubChemXMLWriterFactory {

	private static final Logger log = LoggerFactory.getLogger(PubChemXMLWriterFactory.class);

	protected static boolean DEBUGGING = false;

	public static String assayDesc = "/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_assay/PC-AssaySubmit_assay_descr/PC-AssayDescription",
			dbTracking = "PC-AssayDescription_aid-source/PC-Source/PC-Source_db/PC-DBTracking",
			assayResults = "PC-AssayDescription_results", xref = "PC-AssayDescription_xref", target = "PC-AssayDescription_target";

	protected void add_E(String nodeName, String text, Element parent) {
		Node node = parent.selectSingleNode(nodeName);
		if (node != null)
			node.detach();
		node = parent.addElement(nodeName);
		if (text == null)
			text = "";
		for (String line : text.split("\\r?\\n")) {
			Element child = ((Element) node).addElement(nodeName + "_E");
			child.addText(line);
		}
	}

	protected void addTidPlots(Document document, Integer numberPlots) {
		Element root = (Element) document.selectSingleNode(assayDesc);
		Node dr = root.selectSingleNode("PC-AssayDescription_dr");
		if (dr != null)
			dr.detach();
		Element tidPlotDR = root.addElement("PC-AssayDescription_dr");
		for (int ii = 1; ii <= numberPlots; ii++) {
			Element assayDRAttr = tidPlotDR.addElement("PC-AssayDRAttr");
			assayDRAttr.addElement("PC-AssayDRAttr_id").addText("" + ii);
			assayDRAttr.addElement("PC-AssayDRAttr_descr").addText("CR Plot Labels " + ii);
			assayDRAttr.addElement("PC-AssayDRAttr_dn").addText("Concentration");
			assayDRAttr.addElement("PC-AssayDRAttr_rn").addText("Response");
		}
	}

	protected void addXref(Element parentElement, String elementName, String elementText, String comment) {
		Element annotatedXref = parentElement.addElement("PC-AnnotatedXRef");
		Element annotatedXrefXref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
		Element xrefData = annotatedXrefXref.addElement("PC-XRefData");
		xrefData.addElement("PC-XRefData_" + elementName).addText(elementText);
		if (null != comment)
			annotatedXref.addElement("PC-AnnotatedXRef_comment").addText(comment);
	}

	protected void attributeAndTextAdd(String nodeName, Object text, Object attribute, Element parent) {
		Node node = parent.selectSingleNode(nodeName);
		if (node != null)
			node.detach();
		if (attribute == null)
			attribute = "";
		if (text == null)
			text = "";
		Element element = parent.addElement(nodeName).addText(text.toString());
		element.addAttribute("value", attribute.toString());
	}

	private String getXML(Node node) {
		StringWriter sw = new StringWriter();
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter w = new XMLWriter(sw, format);
			w.write(node);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return sw.toString();
	}

	/**
	 * This method builds a PubChem XML Document using a PCAssay database backed
	 * object.
	 * 
	 * @param assay
	 * @return
	 * @throws Exception
	 */
	public Document build(PCAssay assay) throws IOException, SAXException, ParserConfigurationException {
		String template = "/bard/pubchem/xml/aid_template.xml";
		InputStream is = null;
		try {
			is = getClass().getResource(template).openStream();
		} catch (IOException ex) {
			throw new RuntimeException("Could not find PubChem assay template: " + template);
		}
		Document doc = loadPubChemXML(is);
		PubChemUtils.closeQuietly(is);
		buildAssayDescription(doc, assay);
		buildColumns(doc, assay);
		buildPanels(doc, assay);
		buildTargets(doc, assay);
		buildXRefs(doc, assay);
		buildComments(doc, assay.getCategorizedComments());
		organizeXMLDoc(doc);
		return doc;
	}

	protected void buildAssayDescription(Document document, PCAssay assay) {
		Node node;
		Element element;
		Element root = (Element) document.selectSingleNode(assayDesc);

		if (assay.getAID() != null) {
			element = (Element) root.selectSingleNode("PC-AssayDescription_aid/PC-ID");
			removeChildren(element);
			element.addElement("PC-ID_id").addText("" + assay.getAID());
			element = element.addElement("PC-ID_version");
			if (null != assay.getVersion())
				element.addText("" + assay.getVersion());
		}

		node = root.selectSingleNode("PC-AssayDescription_name");
		removeChildren((Element) node);
		node.setText(assay.getName());

		if (DEBUGGING)
			log.info("Name fragment: " + getXML(node));

		add_E("PC-AssayDescription_description", assay.getDescription(), root);
		add_E("PC-AssayDescription_protocol", assay.getProtocol(), root);
		add_E("PC-AssayDescription_comment", assay.getComment(), root);
		root.addElement("PC-AssayDescription_revision").addText("" + assay.getRevision());
		add_E("PC-AssayDescription_grant-number", assay.getGrantNumber(), root);

		attributeAndTextAdd("PC-AssayDescription_activity-outcome-method", assay.getActivityOutcomeMethodId(),
				assay.getActivityOutcomeMethod(), root);
		attributeAndTextAdd("PC-AssayDescription_project-category", assay.getProjectCategoryId(), assay.getProjectCategory(), root);

		element = (Element) root.selectSingleNode(dbTracking);
		removeChildren(element);
		element.addElement("PC-DBTracking_name").addText(assay.getSourceName());
		element = DocumentHelper.makeElement(element, "PC-DBTracking_source-id/Object-id/Object-id_str");
		element.addText(assay.getExtRegId());

		Date date = assay.getHoldUntilDate();
		if (DEBUGGING)
			log.info(date.toString());
		if (date != null) {
			element = (Element) root.selectSingleNode(dbTracking);
			String day = new SimpleDateFormat("dd").format(date);
			String month = new SimpleDateFormat("MM").format(date);
			String year = new SimpleDateFormat("yyyy").format(date);
			Element dateElement = DocumentHelper.makeElement(element, "PC-DBTracking_date/Date/Date_std/Date-std");
			dateElement.addElement("Date-std_year").setText(year);
			dateElement.addElement("Date-std_month").setText(month);
			dateElement.addElement("Date-std_day").setText(day);
		}
	}

	protected void buildColumns(Document document, PCAssay assay) throws IOException {
		List<PCAssayColumn> columns = assay.getColumns();
		if (columns.size() > 0) {
			Integer numberPlots = getCurvePlotCount(columns);
			if (numberPlots > 1)
				addTidPlots(document, numberPlots);

			Element root = (Element) document.selectSingleNode(assayDesc + "/" + assayResults);

			removeChildren(root);

			for (int tt = 0; tt < columns.size(); tt++) {
				PCAssayColumn column = columns.get(tt);
				if (column.getTID() > 0) {
					Element resultType = root.addElement("PC-ResultType");
					resultType.addElement("PC-ResultType_tid").addText("" + (tt + 1));
					resultType.addElement("PC-ResultType_name").addText(column.getName());
					add_E("PC-ResultType_description", column.getDescription(), resultType);
					attributeAndTextAdd("PC-ResultType_type", column.getTypeId(), column.getType(), resultType);
					if (column.getUnit() != null)
						attributeAndTextAdd("PC-ResultType_unit", column.getUnitId(), column.getUnit(), resultType);
					if (column.isActiveConcentration())
						attributeAndTextAdd("PC-ResultType_ac", "", "true", resultType);

					Double concentration = column.getTestedConcentration();
					if (null != concentration) {
						Element tc = resultType.addElement("PC-ResultType_tc");
						Element concentrationattr = tc.addElement("PC-ConcentrationAttr");
						concentrationattr.addElement("PC-ConcentrationAttr_concentration").addText("" + concentration);
						concentrationattr.addElement("PC-ConcentrationAttr_unit").addAttribute("value", "um").addText("5");
						Integer curvePlotLabel = column.getCurvePlotLabel();
						if (null != curvePlotLabel)
							concentrationattr.addElement("PC-ConcentrationAttr_dr-id").addText("" + curvePlotLabel);
					}

					PCAssayPanel panel = column.getPanel();
					if (null != panel) {
						Element element = DocumentHelper.makeElement(resultType, "PC-ResultType_panel-info/PC-AssayPanelTestResult");
						element.addElement("PC-AssayPanelTestResult_mid").addText("" + panel.getPanelNumber());
						attributeAndTextAdd("PC-AssayPanelTestResult_readout-annot", column.getPanelReadoutTypeId(),
								column.getPanelReadoutType(), element);
					}
					if (DEBUGGING)
						log.info("Column fragment: " + getXML(resultType));
				}
			}
		}
	}

	protected void buildComments(Document document, Map<String, String> comments) {
		Set<String> keys = comments.keySet();
		if (keys.size() > 0) {
			Element root = (Element) document.selectSingleNode("//PC-AssayDescription");
			Element assayDescCComment = (Element) root.selectSingleNode("PC-AssayDescription_categorized-comment");
			if (assayDescCComment == null)
				assayDescCComment = root.addElement("PC-AssayDescription_categorized-comment");
			for (String key : keys) {
				Element cComment = assayDescCComment.addElement("PC-CategorizedComment");
				cComment.addElement("PC-CategorizedComment_title").addText(key);
				add_E("PC-CategorizedComment_comment", comments.get(key), cComment);
			}
		}
	}

	protected void buildPanels(Document document, PCAssay assay) {
		List<PCAssayPanel> panels = assay.getPanels();
		if (panels.size() < 1)
			return;

		String panelNodePath = "PC-AssayDescription_is-panel";
		String panelInfoPath = "PC-AssayDescription_panel-info";

		Element root = (Element) document.selectSingleNode(assayDesc);
		root.addElement(panelNodePath).addAttribute("value", "true");
		Node node = root.selectSingleNode(panelInfoPath);
		if (node != null) {
			node.detach();
			root.selectSingleNode(panelNodePath).detach();
		}

		Element pI = root.addElement(panelInfoPath);
		Element assayPanel = pI.addElement("PC-AssayPanel");
		assayPanel.addElement("PC-AssayPanel_name").addText(assay.getPanelName());
		assayPanel.addElement("PC-AssayPanel_descr").addText(assay.getPanelDescription());
		Element assayPanelMember = assayPanel.addElement("PC-AssayPanel_member");

		for (int rr = 0; rr < panels.size(); rr++) {
			PCAssayPanel panel = panels.get(rr);
			Element member = assayPanelMember.addElement("PC-AssayPanelMember");
			member.addElement("PC-AssayPanelMember_mid").addText(rr + 1 + "");
			member.addElement("PC-AssayPanelMember_name").addText(panel.getName());
			member.addElement("PC-AssayPanelMember_description").addText(panel.getDescription());
			add_E("PC-AssayPanelMember_protocol", panel.getProtocol(), member);
			add_E("PC-AssayPanelMember_comment", panel.getComment(), member);
			XRef target = panel.getTarget();
			if (null != target) {
				Element targetElem = member.addElement("PC-AssayPanelMember_target");
				Element targetInfo = targetElem.addElement("PC-AssayTargetInfo");
				String name = target.getName();
				String type = target.getType();
				String typeId = "";
				if ("protein".equalsIgnoreCase(type))
					typeId = "1";
				else if ("gene".equalsIgnoreCase(type))
					typeId = "2";
				else if ("rna".equalsIgnoreCase(type))
					typeId = "3";
				else
					typeId = "4";
				targetInfo.addElement("PC-AssayTargetInfo_name").addText(name);
				targetInfo.addElement("PC-AssayTargetInfo_mol-id").addText(target.getXRefId());
				targetInfo.addElement("PC-AssayTargetInfo_molecule-type").addAttribute("value", type).addText(typeId);
			}
			if (null != panel.getTaxonomy() | null != panel.getGene()) {
				Element xref = member.addElement("PC-AssayPanelMember_xref");
				if (null != panel.getTaxonomy()) {
					Element annotatedXref = xref.addElement("PC-AnnotatedXRef");
					Element annotatedXref_xref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
					Element XrefData = annotatedXref_xref.addElement("PC-XRefData");
					XrefData.addElement("PC-XRefData_taxonomy").addText("" + panel.getTaxonomy().getXRefId());
				}
				if (null != panel.getGene()) {
					Element annotatedXref = xref.addElement("PC-AnnotatedXRef");
					Element annotatedXref_xref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
					Element XrefData = annotatedXref_xref.addElement("PC-XRefData");
					XrefData.addElement("PC-XRefData_gene").addText("" + panel.getGene().getXRefId());
				}
			}
		}
	}

	protected void buildTargets(Document document, PCAssay assay) {
		List<PCAssayXRef> xrefs = assay.getAssayXRefs();
		List<PCAssayXRef> targets = new ArrayList<PCAssayXRef>();
		for (PCAssayXRef xref : xrefs) {
			if (xref.isTarget())
				targets.add(xref);
		}
		if (targets.size() > 0) {
			Element root = (Element) document.selectSingleNode(assayDesc);
			Node targetNode = root.selectSingleNode("//" + target);
			if (targetNode != null) {
				targetNode.detach();
			}
			Element targetElement = root.addElement(target);
			for (PCAssayXRef xx : targets) {
				XRef xref = xx.getXRef();
				String type = xref.getType();
				String name = xref.getName();

				String typeId = "";
				Long taxon = xx.getTaxon();
				if (taxon != null && type != null && name != null) {
					if (type.equalsIgnoreCase("nucleotide")) {
						if (name.contains("dna") | name.contains("DNA") | name.contains("gene"))
							typeId = "2";
						else if (name.contains("RNA") | name.contains("rna"))
							typeId = "3";
					} else if (type.equalsIgnoreCase("protein")) {
						typeId = "1";
					}

					Element info = targetElement.addElement("PC-AssayTargetInfo");
					info.addElement("PC-AssayTargetInfo_name").addText(name);
					info.addElement("PC-AssayTargetInfo_mol-id").addText(String.valueOf(xref.getXRefId()));
					attributeAndTextAdd("PC-AssayTargetInfo_molecule-type", typeId, type, info);
					Element organism = info.addElement("PC-AssayTargetInfo_organism");
					Element element = organism.addElement("BioSource");
					element = element.addElement("BioSource_org");
					element = element.addElement("Org-ref");
					element.addElement("Org-ref_taxname").addText(xx.getTaxonName());
					element.addElement("Org-ref_common").addText(xx.getTaxonCommon());
					element = element.addElement("Org-ref_db");
					element = element.addElement("Dbtag");
					element.addElement("Dbtag_db").addText("taxon");
					element = element.addElement("Dbtag_tag");
					element = element.addElement("Object-id");
					element.addElement("Object-id_id").addText(taxon.toString());
					if (DEBUGGING)
						log.info("Target fragment: " + getXML(info));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void buildXRefs(Document document, PCAssay assay) {
		List<PCAssayXRef> xrefs = assay.getAssayXRefs();

		if (xrefs.size() > 0) {
			Element rootElement = (Element) document.selectSingleNode(assayDesc);
			Element adXref = (Element) rootElement.selectSingleNode((String) xref);
			if (adXref != null) {
				List<Node> nodes = adXref.selectNodes("PC-AnnotatedXRef");
				for (Node nn : nodes)
					nn.detach();
			} else
				adXref = rootElement.addElement((String) xref);

			for (PCAssayXRef PCxref : xrefs) {
				XRef xref = PCxref.getXRef();
				String comment = PCxref.getComment();
				String type = xref.getType().toLowerCase();
				String id = xref.getXRefId();
				if (PCxref.getPanel() == null && !PCxref.isTarget()) {
					if (type.equals("protein") || type.equals("nucleotide"))
						type = type + "-gi";
					addXref(adXref, type, id, comment);
				}
			}
		}
	}

	protected Integer getCurvePlotCount(List<PCAssayColumn> columns) {
		Set<String> labels = new HashSet<String>();
		for (PCAssayColumn col : columns) {
			col.getCurvePlotLabel();
		}
		return labels.size();
	}

	protected Document loadPubChemXML(InputStream inputStream) throws IOException, SAXException, ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document doc2 = builder.parse(inputStream);
		org.w3c.dom.Node node = doc2.getElementsByTagName("PC-AssaySubmit_data").item(0);
		if (node != null) {
			org.w3c.dom.Node parent = node.getParentNode();
			parent.removeChild(node);
		}
		DOMReader reader = new DOMReader();
		Document doc = reader.read(doc2);
		return doc;
	}

	// This method puts all of the PC-AssayDescription child elements in the
	// correct order.
	protected void organizeXMLDoc(Document doc) {
		Element parent = (Element) doc.selectSingleNode(assayDesc);
		String[] nodesOrder = { "PC-AssayDescription_description", "PC-AssayDescription_protocol", "PC-AssayDescription_comment", xref,
				assayResults, "PC-AssayDescription_revision", target, "PC-AssayDescription_activity-outcome-method",
				"PC-AssayDescription_dr", "PC-AssayDescription_grant-number", "PC-AssayDescription_project-category",
				"PC-AssayDescription_is-panel", "PC-AssayDescription_panel-info", "PC-AssayDescription_categorized-comment" };
		for (String nodeString : nodesOrder) {
			Node node = parent.selectSingleNode(nodeString);
			if (node != null) {
				Node clone = (Node) node.clone();
				node.detach();
				parent.add(clone);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void removeChildren(Element element) {
		for (Element child : (List<Element>) element.selectNodes("*")) {
			element.remove(child);
		}
	}

	public void write(PCAssay assay, File toFile) throws IOException, SAXException, ParserConfigurationException {
		FileOutputStream fos = new FileOutputStream(toFile);
		try {
			write(build(assay), fos);
		} finally {
			PubChemUtils.closeQuietly(fos);
		}
	}

	public void write(Document doc, OutputStream os) throws IOException {
		XMLWriter writer = null;
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			writer = new XMLWriter(os, format);
			writer.write(doc);
		} finally {
			writer.close();
		}
	}

	public void replaceComments(Document doc, Map<String, String> comments, boolean replace) {
		List<Node> nodes = doc.selectNodes("//PC-AssayDescription/PC-AssayDescription_categorized-comment/PC-CategorizedComment");
		// store existing comments and remove old nodes.
		String separator = System.getProperty("line.separator");
		Map<String, String> oldComments = new LinkedHashMap<String, String>();
		for (Node node : nodes) {
			String key = node.selectSingleNode("PC-CategorizedComment_title").getText();
			String value = PubChemXMLParserFactory.getInstance().join(
					node.selectNodes("PC-CategorizedComment_comment/PC-CategorizedComment_comment_E"), separator);
			oldComments.put(key, value);
			node.detach();
		}

		Map<String, String> newComments = new LinkedHashMap<String, String>();
		newComments.putAll(comments);
		if (!replace) {
			for (String key : oldComments.keySet()) {
				if (!newComments.containsKey(key))
					newComments.put(key, oldComments.get(key));
			}
		}
		// add the comments
		buildComments(doc, newComments);
	}
}
