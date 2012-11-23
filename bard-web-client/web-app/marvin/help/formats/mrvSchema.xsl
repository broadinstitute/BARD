<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" version="1.0">
	<!-- Main template for selecting the elements under the root -->
 <xsl:strip-space elements="*"/>         

 <xsl:template match="/">
 <html>
  <head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
   <meta NAME="description" CONTENT="mrv file format"/>
   <meta NAME="keywords" CONTENT="mrv, schema, marvin, file format"/>
   <meta NAME="author" CONTENT="Peter Szakacs"/>
   <link REL="stylesheet" TYPE="text/css" HREF="../marvinmanuals.css" 
               TITLE="Style"/>
   <title>The mrv file format</title>
  </head>

  <body>
   <h1> The mrv file format </h1>
   <h2> Contents</h2>
     <ul>
       <li><a href='#format'>Marvin Document Format </a> </li>
       <li><a href='#escape'>Escape Characters </a> </li>
       <li><a href='#schema'>Schema </a> </li>
       <li><a href='#old'>Old mrv format documentation</a> </li>
       <li><a href='#export'>Export Options </a> </li>
     </ul>
   <h2> <a name='format'/> Marvin Document Format </h2>
    An mrv file may contain four elements,
    <a href="#cml">cml</a>, 
    <a href="#MDocument">MDocument</a>, 
    <a href="#molecule">molecule</a>, 
    <a href="#reaction">reaction</a>,
    all of which has complex type.
    The elements of the complex types are shown as list items,
    while its attributes are written with italic under the name of
    the corresponding complex type name.
    <ul>
     <xsl:for-each select="xs:schema/xs:element">
      <xsl:variable name="ttype" select="@type" />
      <xsl:variable name="nname" select="@name" />
       <h3>
       <a name="{@name}"></a> 
        Element: <xsl:value-of select="@name"></xsl:value-of>
       </h3>
       <xsl:call-template name="PrintComplexSubtypes">
        <xsl:with-param name="type" select="@type" />
        <xsl:with-param name="indent" select="1" />
       </xsl:call-template>
      </xsl:for-each>
    </ul>
   <h2> <a name="escape"></a>Escape characters</h2>
    Special value of an element or attribute is escaped as follows:<ul>
    <table>
     <tr>
      <td> null </td>
      <td> "0"</td>
     </tr>
     <tr>
      <td> 0 </td>
      <td> "zero" <i>(character string)</i></td>
     </tr>
     <tr>
      <td> no value   </td>
      <td> "."</td>
     </tr>
     <tr>
      <td> "."</td>
      <td> &#38;#<i>n</i>;, <i>n</i> is the character code  </td>
     </tr>
    </table>
   </ul>
    
    <h2><a name="schema"></a>Schema</h2>
	Mrv files can be validated by using the <a href="mrvSchema-pure.xsd">XSD schema</a> 
	containing the description of the mrv format.
    <h2><a name="old"></a>Old mrv format documentation</h2>
	The old description of mrv format is available <a href="mrv-doc-old.html">here</a>. 
    <h2><a name="export"></a>  Export Options</h2>
    The mrv has all the export options that 
    <a href="cml-doc.html#export">cml</a> has, except option ':A' (in mrv the 
    usage of array format for the description of atoms is decided automatically 
    upon the content of the molecule).
    In addition, mrv has a further 
    export option: 
    
    <table cellpadding="0" width="850" cellspacing="0">
      <col width="103"/>
      <col width="748"/>
      <tr valign="TOP">
        <td width="103" style="border: none; padding: 0cm">
          <p style="border: none; padding: 0cm">mrv:S</p>
        </td>
        <td width="746" style="; border: none; padding: 0cm">
          <p style="border: none; padding: 0cm">
              Save selection. If set then the mrv output will contain 
              which atoms and graphical objects are
              in selected state.</p>
        </td>
      </tr>
    </table>
	<P>Example of MRV file exported without options: 
	</P>
	<PRE STYLE="margin-left: 1cm; margin-right: 1cm">
	&lt;?xml version=&quot;1.0&quot;?&gt;&lt;cml&gt;
	&lt;MDocument&gt;&lt;MChemicalStruct&gt;&lt;molecule title=&quot;Ethane&quot; molID=&quot;m1&quot;&gt;&lt;atomArray atomID=&quot;a1 a2&quot; elementType=&quot;C C&quot; x2=&quot;0.0 0.0&quot; y2=&quot;0.0 1.54&quot;&gt;&lt;/atomArray&gt;&lt;bondArray&gt;&lt;bond atomRefs2=&quot;a1 a2&quot; order=&quot;1&quot;&gt;&lt;/bond&gt;&lt;/bondArray&gt;&lt;/molecule&gt;&lt;/MChemicalStruct&gt;&lt;MRectangle id=&quot;o2&quot;&gt;&lt;MPoint x=&quot;-6.599999904632568&quot; y=&quot;1.7050000429153442&quot;&gt;&lt;/MPoint&gt;&lt;MPoint x=&quot;-2.640000104904175&quot; y=&quot;1.7050000429153442&quot;&gt;&lt;/MPoint&gt;&lt;MPoint x=&quot;-2.640000104904175&quot; y=&quot;-0.16500000655651093&quot;&gt;&lt;/MPoint&gt;&lt;MPoint x=&quot;-6.599999904632568&quot; y=&quot;-0.16500000655651093&quot;&gt;&lt;/MPoint&gt;&lt;/MRectangle&gt;&lt;/MDocument&gt;
	&lt;/cml&gt;
	</PRE>
	
	<P>
	Example of MRV file exported with option &quot;S&quot;: 
	</P>
	<PRE STYLE="margin-left: 1cm; margin-right: 1cm">
	&lt;?xml version=&quot;1.0&quot;?&gt;&lt;cml&gt;
	&lt;MDocument&gt;&lt;MChemicalStruct&gt;&lt;molecule title=&quot;Ethane&quot; molID=&quot;m1&quot;&gt;&lt;atomArray atomID=&quot;a1 a2&quot; elementType=&quot;C C&quot; isSelected=&quot;true true&quot; x2=&quot;0.0 0.0&quot; y2=&quot;0.0 1.54&quot;&gt;&lt;/atomArray&gt;&lt;bondArray&gt;&lt;bond atomRefs2=&quot;a1 a2&quot; order=&quot;1&quot;&gt;&lt;/bond&gt;&lt;/bondArray&gt;&lt;/molecule&gt;&lt;/MChemicalStruct&gt;&lt;MRectangle id=&quot;o2&quot; isSelected=&quot;true&quot;&gt;&lt;MPoint x=&quot;-6.599999904632568&quot; y=&quot;1.7050000429153442&quot;&gt;&lt;/MPoint&gt;&lt;MPoint x=&quot;-2.640000104904175&quot; y=&quot;1.7050000429153442&quot;&gt;&lt;/MPoint&gt;&lt;MPoint x=&quot;-2.640000104904175&quot; y=&quot;-0.16500000655651093&quot;&gt;&lt;/MPoint&gt;&lt;MPoint x=&quot;-6.599999904632568&quot; y=&quot;-0.16500000655651093&quot;&gt;&lt;/MPoint&gt;&lt;/MRectangle&gt;&lt;/MDocument&gt;
	&lt;/cml&gt;
	</PRE>
	
	<P>
	Example of MRV file exported with option &quot;P&quot;: 
	</P>
	<PRE STYLE="margin-left: 1cm; margin-right: 1cm">
	&lt;?xml version=&quot;1.0&quot;?&gt;
	&lt;cml&gt;
	&lt;MDocument&gt;
	  &lt;MChemicalStruct&gt;
	    &lt;molecule title=&quot;Ethane&quot; molID=&quot;m1&quot;&gt;
	      &lt;atomArray atomID=&quot;a1 a2&quot; elementType=&quot;C C&quot; x2=&quot;0.0 0.0&quot; y2=&quot;0.0 1.54&quot;/&gt;
	      &lt;bondArray&gt;
	        &lt;bond atomRefs2=&quot;a1 a2&quot; order=&quot;1&quot;/&gt;
	      &lt;/bondArray&gt;
	    &lt;/molecule&gt;
	  &lt;/MChemicalStruct&gt;
	  &lt;MRectangle id=&quot;o2&quot;&gt;
	    &lt;MPoint x=&quot;-6.599999904632568&quot; y=&quot;1.7050000429153442&quot;/&gt;
	    &lt;MPoint x=&quot;-2.640000104904175&quot; y=&quot;1.7050000429153442&quot;/&gt;
	    &lt;MPoint x=&quot;-2.640000104904175&quot; y=&quot;-0.16500000655651093&quot;/&gt;
	    &lt;MPoint x=&quot;-6.599999904632568&quot; y=&quot;-0.16500000655651093&quot;/&gt;
	  &lt;/MRectangle&gt;
	&lt;/MDocument&gt;
	&lt;/cml&gt;
	</PRE>
	
	<P>
	Example of MRV file exported with options &quot;SP&quot;: 
	</P>
	<PRE STYLE="margin-left: 1cm; margin-right: 1cm">
	&lt;?xml version=&quot;1.0&quot;?&gt;
	&lt;cml&gt;
	&lt;MDocument&gt;
	  &lt;MChemicalStruct&gt;
	    &lt;molecule title=&quot;Ethane&quot; molID=&quot;m1&quot;&gt;
	      &lt;atomArray atomID=&quot;a1 a2&quot; elementType=&quot;C C&quot; isSelected=&quot;true true&quot; x2=&quot;0.0 0.0&quot; y2=&quot;0.0 1.54&quot;/&gt;
	      &lt;bondArray&gt;
	        &lt;bond atomRefs2=&quot;a1 a2&quot; order=&quot;1&quot;/&gt;
	      &lt;/bondArray&gt;
	    &lt;/molecule&gt;
	  &lt;/MChemicalStruct&gt;
	  &lt;MRectangle id=&quot;o2&quot; isSelected=&quot;true&quot;&gt;
	    &lt;MPoint x=&quot;-6.599999904632568&quot; y=&quot;1.7050000429153442&quot;/&gt;
	    &lt;MPoint x=&quot;-2.640000104904175&quot; y=&quot;1.7050000429153442&quot;/&gt;
	    &lt;MPoint x=&quot;-2.640000104904175&quot; y=&quot;-0.16500000655651093&quot;/&gt;
	    &lt;MPoint x=&quot;-6.599999904632568&quot; y=&quot;-0.16500000655651093&quot;/&gt;
	  &lt;/MRectangle&gt;
	&lt;/MDocument&gt;
	&lt;/cml&gt;
	</PRE>
    
   </body>
  </html>
 </xsl:template>
 
 
 <xsl:template match="xs:annotation/xs:documentation">
  <xsl:value-of select="."></xsl:value-of>
 </xsl:template>
 
 <xsl:template match="xs:annotation/xs:documentation">
  <xsl:value-of select="."></xsl:value-of>
 </xsl:template>
 
 <xsl:template name="indent">
    <xsl:param name="count" />
    <xsl:param name="i" />
    <xsl:if test="$i &lt;= $count">
     <xsl:call-template name="indent">
       <xsl:with-param name="i" select="$i + 1 " />
       <xsl:with-param name="count" select="$count" />
     </xsl:call-template>
    </xsl:if>
 </xsl:template>
 
 <xsl:template name="PrintComplexSubtypes">
  <xsl:param name="type" />
  <xsl:param name="indent" />
  <xsl:param name="isCallingType">true</xsl:param>
  <xsl:param name="typeList" />
  <xsl:variable name="typeName" select="@name" />
 		<!-- Print attributes for complex type -->
  <table>
    <xsl:for-each select="//xs:complexType[@name=$type]//xs:attribute"> 
     <tr><td><i><xsl:value-of select="@name"></xsl:value-of></i></td>
     <td><xsl:apply-templates select="."/></td></tr>
    </xsl:for-each>
  </table>
  <xsl:for-each select="//xs:complexType[@name=$type]//xs:element">
     <xsl:choose>
      <xsl:when test="@type = $type">
      </xsl:when>
      <xsl:otherwise>
       <ul>
        <li>
         <xsl:variable name="subType" select="@type" />
         <xsl:choose>
          <xsl:when test="@name='MDocument'">  
            <a href="#{@name}">
            <xsl:value-of select="@name"></xsl:value-of></a>:
            <xsl:apply-templates select="." />
          </xsl:when>
          <xsl:when test="@name='molecule' and @type='moleculeType'">  
            <a href="#{@name}">
            <xsl:value-of select="@name"></xsl:value-of></a>:
            <xsl:apply-templates select="." />
          </xsl:when>
          <xsl:when test="@name='reaction'">  
            <a href="#{@name}">
            <xsl:value-of select="@name"></xsl:value-of></a>:
            <xsl:apply-templates select="." />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@name"></xsl:value-of>:
            <xsl:apply-templates select="." /> 
            <xsl:call-template name="PrintComplexSubtypes">
             <xsl:with-param name="type" select="$subType" />
             <xsl:with-param name="indent" select="$indent +1" />
             <xsl:with-param name="isCallingType">false</xsl:with-param>
             <xsl:with-param name="typeList"
              select="concat($typeList, '*', $typeName, '+')" />
           </xsl:call-template>
          </xsl:otherwise>
         </xsl:choose>
        </li>
       </ul>
      </xsl:otherwise>
     </xsl:choose>
  </xsl:for-each>
 </xsl:template>
 
</xsl:stylesheet>
 
