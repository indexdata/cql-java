<?xml version='1.0'?>

<xsl:stylesheet version="1.0"
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:srw_dc="info:srw/schema/1/dc-v1.1" xmlns:dc="http://purl.org/dc/elements/1.1/">

<!-- Dublin Core -->

<xsl:template match="srw_dc:dc">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="dc:*">
  <xsl:if test="not(name()=dc)">
    <tr><td align="right" width="25%" valign="top">
          <b><xsl:value-of select="name()"/></b>:<xsl:text> </xsl:text> </td>
        <td><xsl:text> </xsl:text> <xsl:value-of select="."/></td>
    </tr>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>
