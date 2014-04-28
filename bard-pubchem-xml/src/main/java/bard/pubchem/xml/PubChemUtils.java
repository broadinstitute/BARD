/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
