<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="entry">
        <entry field="{field}"/>
    </xsl:template>

    <xsl:template match="field">
        <xsl:apply-templates/>
    </xsl:template>

</xsl:stylesheet>