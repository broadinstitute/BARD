package gov.ncgc;
// $Id: XmlTwig.java 2952 2009-07-19 07:34:05Z nguyenda $
// 06.19.07

import java.io.Reader;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Vector;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/*
 * this class is inspired by the Perl::Twig module
 */
public class XmlTwig {
    private Document m_doc;


    public XmlTwig (Reader reader) {
	this (new InputSource (reader));
    }

    public XmlTwig (InputSource is) {
	try {
	    DocumentBuilder db = DocumentBuilderFactory
		.newInstance().newDocumentBuilder();
	    m_doc = db.parse(is);
	}
	catch (Exception ex) {
	    ex.printStackTrace();
	    throw new IllegalArgumentException ("bogus xml stream", ex);
	}
    }

    public XmlTwig (InputStream is) {
	try {
	    DocumentBuilder db = DocumentBuilderFactory
		.newInstance().newDocumentBuilder();
	    m_doc = db.parse(is);
	}
	catch (Exception ex) {
	    ex.printStackTrace();
	    throw new IllegalArgumentException ("bogus xml stream", ex);
	}
    }

    public XmlTwig (Document doc) {
	m_doc = doc;
    }

    public Document getDocument () { return m_doc; }

    static protected void walkElement 
	(Element match[], Element node, String names[], int depth) {
	if (depth < names.length) {
	    /*
	    System.err.println
		(depth + ": " + node.getTagName() + " " + names[depth]);
	    */
	    if (names[depth].equals(node.getTagName())) {
		match[depth] = node;
		/*
		for (Element e : match) {
		    if (e != null) {
			System.err.print("->" + e.getTagName());
		    }
		}
		System.out.println();
		*/
		NodeList childs = node.getChildNodes();
		for (int i = 0; i < childs.getLength(); ++i) {
		    Node n = childs.item(i);
		    if (n.getNodeType() == Node.ELEMENT_NODE) {
			walkElement (match, (Element)n, names, depth + 1);
		    }
		}
	    }
	}
    }

    static protected void getElementValue (String values[], Element node) {
	NodeList childs = node.getChildNodes();
	for (int i = 0; i < childs.getLength(); ++i) {
	    Node n = childs.item(i);
	    short type = n.getNodeType();
	    switch (type) {
	    case Node.TEXT_NODE:
		values[0] = n.getNodeValue();
		break;

	    case Node.ELEMENT_NODE:
		getElementValue (values, (Element)n);
		break;
	    }
	}
    }

    public static String getElementValue (Element node, String path) {
	Element n = getElement (node, path);
	String value = null;
	if (n != null) {
	    String values[] = new String[1];
	    getElementValue (values, n);
	    value = values[0];
	}
	return value;
    }

    public String getElementValue (String path) {
	return getElementValue (m_doc.getDocumentElement(), path);
    }

    public String getElementAttrValue (String path, String attr) {
	Element elm = getElement (path);
	String value = null;
	if (elm != null) {
	    value = elm.getAttribute(attr);
	}
	return value;
    }

    public static Element getElement (Element node, String path) {
	String names[] = path.split("/");
	Element match[] = new Element[names.length];
	//System.err.println("## node: " + node.getTagName() + "... " + path);
	walkElement (match, node, names, 0);
	Element elm = match[match.length-1];
	if (elm != null && elm.getTagName().equals(names[names.length-1])) {
	    //System.err.println("## MATCHED!!!");
	    return elm;
	}
	//System.err.println("## NOT MATCHED!!!");
	return null;
    }

    public Element getElement (String path) {
	return getElement (m_doc.getDocumentElement(), path);
    }

    public boolean hasElement (String path) {
	return getElement (path) != null;
    }

    public boolean hasElement (Element node, String path) {
	return getElement (node, path) != null;
    }

    public static String getElementsAsText (Element root, 
					    String tag, 
					    String separator) {
	StringBuffer sb = new StringBuffer ();
	NodeList nodes = root.getElementsByTagName(tag);
	String[] value = new String[1];
	for (int i = 0; i < nodes.getLength(); ++i) {
	    getElementValue (value, (Element)nodes.item(i));
	    sb.append(value[0] == null ? "" : value[0]);
	    if (i+1 < nodes.getLength()) {
		sb.append(separator);
	    }
	    value[0] = null;
	}
	return sb.toString();
    }

    public static String[] getElementsAsArray (Element root, String tag) {
	NodeList nodes = root.getElementsByTagName(tag);
	Vector<String> values = new Vector<String>();
	String[] v = new String[1];
	for (int i = 0; i < nodes.getLength(); ++i) {
	    getElementValue (v, (Element)nodes.item(i));
	    if (v[0] != null) {
		values.add(v[0]);
	    }
	}
	return values.toArray(new String[0]);
    }

    public String getElementsAsText (String tag, String separator) {
	return getElementsAsText (m_doc.getDocumentElement(), tag, separator);
    }

    public static void main (String argv[]) throws Exception {
	String xml1 = "<?xml version=\"1.0\"?>"
+"<!DOCTYPE PCT-Data PUBLIC \"-//NCBI//NCBI PCTools/EN\" \"http://pubchem.ncbi.nlm.nih.gov/pug/pug.dtd\">"
+"<PCT-Data>"
+"  <PCT-Data_output>"
+"    <PCT-OutputData>"
+"      <PCT-OutputData_status>"
+"        <PCT-Status-Message>"
+"          <PCT-Status-Message_status>"
+"            <PCT-Status value=\"success\"/>"
+"          </PCT-Status-Message_status>"
+"        </PCT-Status-Message>"
+"      </PCT-OutputData_status>"
+"      <PCT-OutputData_output>"
+"        <PCT-OutputData_output_download-url>"
+"          <PCT-Download-URL>"
+"            <PCT-Download-URL_url>ftp://ftp-private.ncbi.nlm.nih.gov/pubchem/.fetch/623889957832777372.sdf</PCT-Download-URL_url>"
+"          </PCT-Download-URL>"
+"        </PCT-OutputData_output_download-url>"
+"      </PCT-OutputData_output>"
+"    </PCT-OutputData>"
+"  </PCT-Data_output>"
+"</PCT-Data>";
	XmlTwig twig = new XmlTwig 
	    (new ByteArrayInputStream (xml1.getBytes()));
	
	String path = "PCT-Data/PCT-Data_output/PCT-OutputData/PCT-OutputData_status/PCT-Status-Message/PCT-Status-Message_status/PCT-Status";

	System.out.println
	    (path + "=" + twig.getElementAttrValue(path, "value"));

	path = "PCT-Data/PCT-Data_output/PCT-OutputData/PCT-OutputData_output/PCT-OutputData_output_download-url/PCT-Download-URL/PCT-Download-URL_url";
	System.out.println(path + "=" + twig.getElementValue(path));

	path = "PCT-Data/PCT-Data_output/PCT-OutputData/PCT-OutputData_status/PCT-Status-Message";
	Element elm = twig.getElement(path);
	System.out.println(path + ": " + (elm != null ? "yes" : "no"));
	if (elm != null) {
	    Element elm2 = twig.getElement(elm, "PCT-Status-Message/PCT-Status-Message_status/PCT-Status");
	    System.out.println("** " + elm2 + " " + elm2.getAttribute("value"));
	}
    }
}
