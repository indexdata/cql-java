<?xml version='1.0'?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:srw="http://www.loc.gov/zing/srw/" xmlns:diag="http://www.loc.gov/zing/srw/diagnostic/"  xmlns:xcql="http://www.loc.gov/zing/srw/xcql/" version="1.0"> 

<xsl:output method="html"/>

<xsl:template name="stdiface">
<html><head><title><xsl:value-of select="$title"/></title>
</head><body bgcolor="white"><center><h2 style="font-family: sans-serif; color: #FF5500; background-color: #eeeeff; padding-top: 10px; padding-bottom: 10px; border: 1px solid #3333FF"><xsl:value-of select="$title"/></h2></center>
<p><xsl:apply-templates/></p>
<p>
<a href="?operation=explain&amp;version=1.1&amp;stylesheet=explainResponse.xsl">Home</a>
</p>
</body>
</html>

</xsl:template>

<xsl:template match="srw:version"/>

<xsl:template match="srw:diagnostics">
  <h3 style="font-family: sans-serif; color: #F65500; text-indent: 20px; border-left: solid 1px #3333FF; border-top: solid 1px #3333FF; padding-top: 5px">Diagnostics</h3>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="diag:diagnostic">
  <!-- Just feed down -->
  <table>
    <xsl:apply-templates/>
  </table>
</xsl:template>

<xsl:template match="diag:uri">
<tr>
<td><b>Identifier:</b></td>
<td><xsl:value-of select="."/></td>
</tr>
</xsl:template>

<xsl:template match="diag:code">
<tr>
<td><b>Code:</b></td>
<td><xsl:value-of select="."/></td>
</tr>
<tr>
<td><b>Meaning:</b></td>
<xsl:variable name="diag" select="."/>
<td>
<xsl:choose>
  <xsl:when test="$diag='1'">
    <xsl:text>General System Error</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='10'">
    <xsl:text>Query Syntax Error</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='16'">
    <xsl:text>Unsupported Index</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='51'">
    <xsl:text>Result Set Does Not Exist</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='61'">
    <xsl:text>First Record Position Out Of Range</xsl:text>
    </xsl:when>
  <xsl:when test="$diag='66'">
    <xsl:text>Unknown Schema For Retrieval</xsl:text>
    </xsl:when>
  </xsl:choose>
</td>
</tr>
</xsl:template>

<xsl:template match="diag:details"><tr><td><b>Details:</b></td><td><xsl:value-of select="."/></td></tr></xsl:template>
<xsl:template match="diag:message"><tr><td><b>Message:</b></td><td><xsl:value-of select="."/></td></tr></xsl:template>

</xsl:stylesheet>
