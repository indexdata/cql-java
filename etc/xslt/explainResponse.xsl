<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:srw="http://www.loc.gov/zing/srw/"
    xmlns:zr="http://explain.z3950.org/dtd/2.0/"
    xmlns:diag="http://www.loc.gov/zing/srw/diagnostic/">

  <!--      Author: Rob Sanderson (azaroth@liv.ac.uk)
           Version:  0.6
      Last Updated:  27/11/2003
           Licence:  GPL 
    
    Updated by Hedzer Westra (Adlib Information Systems) on 07-12-2004
    
    -->

  <xsl:output method="html"/>

  <xsl:template match="/srw:explainResponse">
    <html>
      <head>
        <title>
          <xsl:value-of select="srw:record/srw:recordData/zr:explain/zr:databaseInfo/zr:title"/>
          </title>
        <style>
          H2 {font-family: sans-serif; color: #FF5500; background-color: #eeeeff; padding-top: 10px; padding-bottom: 10px; border: 1px solid #3333FF}
          H3 { font-family: sans-serif; color: #F65500; text-indent: 20px; border-left: solid 1px #3333FF; border-top: solid 1px #3333FF; padding-top: 5px }
          .paramTable { vertical-align: top; border: 1px solid; padding: 3px; border-collapse: collapse }
          .exampleTable { vertical-align: top; border: 1px solid; padding: 3px; border-collapse: collapse; background-color: #eeeeff}
          
          .paramTable TD {border: 1px solid}
          .exampleTable TD {border: 1px solid}
          TH {border: 1px solid; background-color: #eeeeff}

        </style>

        <script>
          <xsl:text>
            function mungeForm() {
            inform = document.CQLForm;
            outform = document.SRUForm;
            max = inform.maxIndex.value;
            cql = "";
            prevIdx = 0;
            // Step through elements in form to create CQL
            for (var idx = 1; idx &lt;= max; idx++) {
              term = inform["term"+idx].value;
              if (term) {
                if (prevIdx) {
                  cql += " " + inform["bool" + prevIdx].value + " "
                }
                if (term.indexOf(' ')) {
                  term = '"' + term + '"';
                }
                cql += inform["index" + idx].value + " " + inform["relat" + idx].value + " " + term
                prevIdx = idx
             }
            }
            if (!cql) {
            alert("At least one term is required to search.");
            return false;
            }
            outform.query.value = cql
            outform.submit();
            return false;
            }

            function mungeScanForm() {
            inform = document.ScanIndexes;
            outform = document.ScanSubmit;
            index = inform.scanIndex.value;
            term = inform.term.value;
            relat = inform.relat.value;
            outform.scanClause.value = index + " " + relat +" \"" + term + "\""
            outform.submit();
            return false;
            }

          </xsl:text>
        </script>
      </head>
      <body bgcolor="#FFFFFF">
        <center>
          <h2>
            <xsl:value-of select="srw:record/srw:recordData/zr:explain/zr:databaseInfo/zr:title"/>
          <br/>
            <xsl:value-of select="srw:record/srw:recordData/zr:explain/zr:databaseInfo/zr:implementation/zr:title"/>
          </h2>
        </center>

        <p>
          <xsl:value-of select="srw:record/srw:recordData/zr:explain/zr:databaseInfo/zr:description"/>
        </p>

	<xsl:apply-templates select="srw:diagnostics"/>

<table width="100%" cellspacing="5">
<tr><td>

        <h3>Search</h3>

        <p>
        <form name="CQLForm" onSubmit="return mungeForm();">
          <input type="submit" value="Search" onClick="return mungeForm();"/>
        <table class="paramTable">
          <tr><th>Index</th><th>Relation</th><th>Term</th><th>Boolean</th></tr>
          <input type="hidden" name="maxIndex">
            <xsl:attribute name="value">
              <xsl:value-of select="count(srw:record/srw:recordData/zr:explain/zr:indexInfo/zr:index)"/>
            </xsl:attribute>
          </input>
          <xsl:for-each select="srw:record/srw:recordData/zr:explain/zr:indexInfo/zr:index">
            <xsl:sort select="."/>
            <tr>
              <td align="right">
                <b><xsl:value-of select="zr:title"/></b>
                <input type="hidden">
                  <xsl:attribute name="name">index<xsl:value-of select="position()"/></xsl:attribute>
                  <xsl:attribute name="value"><xsl:value-of select="zr:map[1]/zr:name/@set"/>.<xsl:value-of select="zr:map[1]/zr:name"/></xsl:attribute>
                </input>
              </td>
              <td>
                <select>
                  <xsl:attribute name="name">relat<xsl:value-of select="position()"/></xsl:attribute>
                  <option value="=">=</option>
                  <option value="exact">exact</option>
                  <option value="any">any</option>
                  <option value="all">all</option>
                  <option value="&lt;">&lt;</option>
                  <option value="&gt;">&gt;</option>
                  <option value="&lt;=">&lt;=</option>
                  <option value="&gt;=">&gt;=</option>
                  <option value="&lt;&gt;">not</option>
                  <option value="broader">broader</option>
                  <option value="narrower">narrower</option>
                  <option value="generic">generic</option>
                  <option value="realted">related</option>
                  <option value="topterm">topterm</option>
                  <option value="parents">parents</option>
                </select>
              </td>
              <td>
                <input type="text" value="">
                  <xsl:attribute name="name">term<xsl:value-of select="position()"/></xsl:attribute>
                </input>
              </td>
              <td>
                <select>
                  <xsl:attribute name="name">bool<xsl:value-of select="position()"/></xsl:attribute>
                  <option value="and">and</option>
                  <option value="or">or</option>
                  <option value="not">not</option>
                </select>
              </td>
            </tr>
          </xsl:for-each>
        </table>
      </form>

        <form method="GET" name="SRUForm" onSubmit="mungeForm()">
        
        <input type="hidden" name="query" value=""/>
        <input type="hidden" name="version" value="1.1"/>
        <input type="hidden" name="operation" value="searchRetrieve"/>
        <input type="hidden" name="stylesheet" value="searchRetrieveResponse.xsl"/>
        
        <table>
          <tr>
            <td><b>Record Schema:</b>
          </td>
          <td>
            <select name="recordSchema">
              <xsl:for-each select="srw:record/srw:recordData/zr:explain/zr:schemaInfo/zr:schema">
                <option>
                  <xsl:attribute name="value">
                    <xsl:value-of select="@identifier"/>
                  </xsl:attribute>
                  <xsl:value-of select="zr:title"/>
                </option>
              </xsl:for-each>
            </select>
          </td>
        </tr>
        <tr>
          <td><b>Number of Records:</b></td>
          <td>
            <input type="text" name="maximumRecords">
              <xsl:attribute name="value">
              <xsl:choose>
                <xsl:when test='srw:record/srw:recordData/zr:explain/zr:configInfo/zr:default[@type="numberOfRecords"]'>
                  <xsl:value-of select='srw:record/srw:recordData/zr:explain/zr:configInfo/zr:default[@type="numberOfRecords"]'/>
                  </xsl:when>
                <xsl:otherwise>
                  <xsl:text>1</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
            </input>
          </td>
        </tr>

        <tr>
          <td><b>Record Position:</b></td>
          <td>
            <input type="text" name="startRecord" value="1"/>
          </td>
        </tr>
        <tr>
          <td><b>Result Set TTL:</b></td>
          <td>
            <input type="text" name="resultSetTTL" value="0"/>
          </td>
        </tr>

        <tr>
          <td><b>Record Packing:</b></td>
          <td>
            <select name="recordPacking">
              <option value="xml">XML</option>
              <option value="string">String</option>
            </select>
          </td>
        </tr>

	<tr>
	<td><b>Record XPath:</b></td>
	<td><input type="text" name="recordXPath" value = ""/></td>
	</tr>	
	<tr>

	<td><b>Sort Keys:</b></td>
	<td><input type="text" name="sortKeys" value = ""/></td>
	</tr>	

      </table>

          <input type="submit" value="Search" onClick="return mungeForm();"/>
        </form>
      </p>

</td><td valign="top">

  <h3>Browse</h3>
     <!-- Some browsers won't display when forms inside tables :( -->

     <form name="ScanIndexes" onSubmit="return mungeScanForm();">
   <table>
          <tr><th>Index</th><th>Relation</th><th>Term</th><th>Boolean</th></tr>
     <tr>
       <td><select name="scanIndex">
         <xsl:for-each select="srw:record/srw:recordData/zr:explain/zr:indexInfo/zr:index">
           <xsl:sort select="."/>
           <option>
             <xsl:attribute name="value"><xsl:value-of select="zr:map[1]/zr:name/@set"/>.<xsl:value-of select="zr:map[1]/zr:name"/></xsl:attribute>
                <xsl:value-of select="zr:title"/>
            </option>
         </xsl:for-each>
         </select>
      </td>
      <td><select name="relat">
          <option value="=">=</option>
          <option value="exact">exact</option>
          <option value="any">any</option>
          <option value="all">all</option>
          <option value="&lt;">&lt;</option>
          <option value="&gt;">&gt;</option>
          <option value="&lt;=">&lt;=</option>
          <option value="&gt;=">&gt;=</option>
          <option value="&lt;&gt;">not</option>
        </select>
      </td>
      <td><input name="term" type="text" value = ""/>
      </td>
    </tr>
    </table>
    </form>

    <form name="ScanSubmit" method="GET">
          <xsl:attribute name="action">http://<xsl:value-of select="srw:record/srw:recordData/zr:explain/zr:serverInfo/zr:host"/>:<xsl:value-of select="srw:record/srw:recordData/zr:explain/zr:serverInfo/zr:port"/>/<xsl:value-of select="srw:record/srw:recordData/zr:explain/zr:serverInfo/zr:database"/>
        </xsl:attribute>
    <table>
    <tr>
      <td>Response Position:</td>
      <td>
        <input type="text" name="responsePosition" value="1" size="5"/>
      </td>
    </tr>
    <tr>
      <td>Maximum Terms:</td>
      <td>
        <input type="text" name="maximumTerms" value="20" size="5"/>
      </td>
    </tr>
    <tr>
      <td colspan="2">
         <input type="submit" value="Browse" onClick="return mungeScanForm();"/>
      </td>
    </tr>
    </table>
    <input type="hidden" name="operation" value="scan"/>
    <input type="hidden" name="scanClause" value=""/>
    <input type="hidden" name="version" value="1.1"/>
    <input type="hidden" name="stylesheet" value="scanResponse.xsl"/>
    </form>

</td></tr>
</table>            


      </body>
    </html>
  </xsl:template>

  <xsl:template match="srw:diagnostics">
     <h3>Diagnostics</h3>
     <xsl:apply-templates/>
  </xsl:template>

<xsl:template match="diag:diagnostic">
<table>
<xsl:apply-templates/>
</table>
</xsl:template>
<xsl:template match="diag:code">
<tr><td><b>Code:</b></td><td> <xsl:value-of select="."/></td></tr>
</xsl:template>
<xsl:template match="diag:message">
<tr><td><b>Message:</b></td><td><xsl:value-of select="."/></td></tr>
</xsl:template>
<xsl:template match="diag:details">
<tr><td><b>Details:</b></td><td><xsl:value-of select="."/></td></tr>
</xsl:template>

</xsl:stylesheet>
