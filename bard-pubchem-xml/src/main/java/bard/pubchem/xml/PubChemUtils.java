package bard.pubchem.xml;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXException;

public class PubChemUtils {

	public static Document getDocument(InputStream inputStream) throws IOException, SAXException, ParserConfigurationException {
		inputStream = decompress(inputStream);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		DOMReader reader = new DOMReader();
		Document doc = reader.read(builder.parse(inputStream));
		closeQuietly(inputStream);
//		doc.write(new OutputStreamWriter(System.out));
		return doc;
	}
	
	public static InputStream decompress(InputStream inputStream) throws IOException {
		inputStream = new BufferedInputStream(inputStream);
		inputStream.mark(1024);
	    try {
	    	inputStream = new GZIPInputStream(inputStream);
	    }
	    catch(IOException e) {
	    	inputStream.reset();
	    }
	    return inputStream;
	}
	
	public static void closeQuietly(Closeable closeable) {
		try{
			if( closeable != null )
				closeable.close();
		}
		catch(Exception ex) {
			
		}
	}
}
