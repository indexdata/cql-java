<?xml version='1.0'?>

<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:srw="http://www.loc.gov/zing/srw/"
     xmlns:dc="info:srw/schema/1/dc-v1.1"
     xmlns:card="http://srw.o-r-g.org/schemas/ccg/1.0/">

<xsl:import href="stdiface.xsl"/>
<xsl:import href="dublinCoreRecord.xsl"/>
<xsl:import href="adlibRecord.xsl"/>

<xsl:variable name="title">Result of search: <xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/></xsl:variable>

<xsl:template match="/">
<xsl:call-template name="stdiface">
<xsl:with-param name="title" select="$title"/>
</xsl:call-template>
</xsl:template>

<xsl:template match="srw:searchRetrieveResponse">
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="srw:numberOfRecords">
  <p><b>Matches:</b><xsl:text> </xsl:text><xsl:value-of select="."/></p>
</xsl:template>

<xsl:template match="srw:resultSetId">
  Result Set Identifier:<xsl:text> </xsl:text><xsl:value-of select="."/>
</xsl:template>

<xsl:template match="srw:resultSetIdleTime">
  <xsl:text> </xsl:text>(Will last for<xsl:text> </xsl:text><xsl:value-of select="."/><xsl:text> </xsl:text>seconds)
</xsl:template>

<xsl:template match="srw:records">
  <h3 style="font-family: sans-serif; color: #F65500; text-indent: 20px; border-left: solid 1px #3333FF; border-top: solid 1px #3333FF; padding-top: 5px">Records</h3>
  <xsl:call-template name="prev-nextRecord"/>
  <xsl:apply-templates/>
  <xsl:call-template name="prev-nextRecord"/>
</xsl:template>

<xsl:template match="srw:record">
  <p>
    <xsl:apply-templates select="child::srw:recordPosition"/>
    <xsl:apply-templates select="child::srw:recordSchema"/>
    <xsl:apply-templates select="child::srw:recordData"/>
  </p>
</xsl:template>

<xsl:template match="srw:record/srw:recordSchema">
  <b>Schema: </b>
  <xsl:variable name="schema" select="."/> 
  <xsl:choose>
      <xsl:when test="$schema = 'info:srw/schema/1/dc-v1.1'">
	      Dublin Core
      </xsl:when>
      <xsl:when test="$schema = 'info:srw/schema/1/marcxml-v1.1'">
	      MARC XML
      </xsl:when>
      <xsl:when test="$schema = 'info:srw/schema/1/mods-v3.0'">
	      MODS
      </xsl:when>
      <xsl:when test="$schema = 'http://srw.o-r-g.org/schemas/ccg/1.0/'">
	      Collectable Card Schema
      </xsl:when>
      <xsl:when test="$schema = 'http://www.adlibsoft.com/adlibXML'">
	      adlibXML
      </xsl:when>
      <xsl:otherwise>
	      <xsl:value-of select="$schema"/>
      </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="srw:recordPosition">
  <b>Position: </b> <xsl:value-of select="."/> <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="srw:nextRecordPosition">
  <!-- Not used -->
</xsl:template>

<xsl:template match="srw:recordData">
  <table width="100%" style="vertical-align: top; border: 1px solid; padding: 3px; border-collapse: collapse; background-color: #eefdff">

<xsl:choose>
<xsl:when test="../srw:recordPacking = 'string'">
<tr><td style="border: 1px solid">
<pre><xsl:value-of select="."/></pre>
</td></tr>
</xsl:when>
<xsl:otherwise>
<xsl:apply-templates/>
</xsl:otherwise>
</xsl:choose>

</table>
</xsl:template>


<xsl:template match="srw:echoedSearchRetrieveRequest"/>
<xsl:template match="srw:extraResponseData"/>


<xsl:template name="prev-nextRecord">
  <xsl:variable name="startRecord"
    select="number(/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:startRecord)"/>
  <xsl:variable name="maximumRecords">
    <xsl:value-of select="number(/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:maximumRecords)"/>
    </xsl:variable>
  <xsl:variable name="prev" select="$startRecord - $maximumRecords"/>
  <xsl:variable name="recordSchema"><xsl:if test="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:recordSchema">&amp;recordSchema=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:recordSchema"/></xsl:if></xsl:variable>
  <xsl:variable name="sortKeys" select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:sortKeys"/>

  <xsl:if test="$prev>0">
  <a>
    <xsl:attribute name="href">?operation=searchRetrieve&amp;version=1.1&amp;sortKeys=<xsl:value-of select="$sortKeys"/>&amp;stylesheet=searchRetrieveResponse.xsl&amp;startRecord=<xsl:value-of select="$prev"/>&amp;maximumRecords=<xsl:value-of select="$maximumRecords"/>&amp;resultSetTTL=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:resultSetTTL"/><xsl:value-of select="$recordSchema"/>&amp;query=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/></xsl:attribute>Previous Record(s)</a>
  </xsl:if>
<xsl:text> </xsl:text>
  <xsl:if test="/srw:searchRetrieveResponse/srw:nextRecordPosition">
  <a>
    <xsl:attribute name="href">?operation=searchRetrieve&amp;version=1.1&amp;sortKeys=<xsl:value-of select="$sortKeys"/>&amp;stylesheet=searchRetrieveResponse.xsl&amp;startRecord=<xsl:value-of select="/srw:searchRetrieveResponse/srw:nextRecordPosition"/>&amp;maximumRecords=<xsl:value-of select="$maximumRecords"/>&amp;resultSetTTL=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:resultSetTTL"/><xsl:value-of select="$recordSchema"/>&amp;query=<xsl:value-of select="/srw:searchRetrieveResponse/srw:echoedSearchRetrieveRequest/srw:query"/></xsl:attribute>Next Record(s)</a>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
