<html>
<head><title>Posted molecule</title>
<%@page import="java.io.*,chemaxon.common.util.Base64InputStream,java.util.zip.GZIPInputStream"%>
<%!
/**
 * Replaces the occurences of a string with another 
 * one in a third string.
 * For example, useful for inserting molfiles into script
 * generated HTML pages.
 * @param input original string
 * @param query string to be replaced
 * @param replacement string that will replace all occurences 
 * of the query string
 * @return the modified string.
 */
static String replaceString(String input, String query, String replacement) {
	StringBuffer sb = new StringBuffer();
	int from=0;
	int pos;
	while((pos = input.indexOf(query, from)) >= 0) {
	    if(pos > from) {
			sb.append(input.substring(from,pos));
	    }
	    sb.append(replacement);
	    from = pos+query.length();
	}
	if(input.length() > from)
	    sb.append(input.substring(from,input.length()));
	return sb.toString();
}

/**
 * Converts a string to a format that can be used as a
 * value of JavaScript variable in an HTML page.
 * Converts line separators to UNIX style and replaces
 * new line characters with a backslash and an "n"
 * character.
 * @param input original string containing line terminators
 * @return the modified string.
 */
public static String convertForJavaScript(String input) {
    String value = input;
    //Converting Windows style string to UNIX style
    value = replaceString(value, "\r\n", "\n");
    //Converting Macintosh style string to UNIX style
    value = replaceString(value, "\r", "\n");
    //Converting special characters
    value = replaceString(value, "\\", "\\\\");
    value = replaceString(value, "\n", "\\n");
    value = replaceString(value, "\"", "\\\"");
    value = replaceString(value, "'", "\\'");
    return value;
}

/**
 * Converts gzip compressed data with base64 encoding to text.
 * @param str encoded data
 * @return decoded data. If the data is not encoded, returns with the input value.
 */
public static String convertBase64Gzip2Text(String str) {
	ByteArrayInputStream bin = new ByteArrayInputStream(str.getBytes());	
	Base64InputStream gin = new Base64InputStream(bin);
	InputStream in = null;
	try{
		in = new GZIPInputStream(gin);
	} catch(IOException e) {
		//not valid gziped text
		return str;
		}
	int b = -1;
	StringBuffer sb = new StringBuffer();
	try{
		while((b = in.read()) != -1) {
			sb.append((char)b);
		}
		in.close();
	} catch(IOException e) {
		//not a valid base64 encoded text
		return str;
	}
	return sb.toString();
}

%>
</head>
<body>
    <h1>Display Posted Molecule</h1>
    <%
	String molstr = request.getParameter("molecule");
    String jmolstr = "";
	if(molstr != null) {
		molstr = convertBase64Gzip2Text(molstr);
		jmolstr = convertForJavaScript(molstr);
	}
    %>
    <center>
    <p>
    <script language="JavaScript1.1" src="../../marvin.js"></script>
    <script language="JavaScript1.1">
	mview_begin("../..",300,300);
	mview_param("mol","<%= jmolstr %>");
	mview_end();
    </script>
    </p>
	<form>
	<textarea cols=70 rows=20><%=molstr%></textarea>
	</form>
    </center>
</body>
</html>
