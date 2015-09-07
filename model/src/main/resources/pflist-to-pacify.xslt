<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs xsi xsl">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:key name="relativePathDistinct" match="/property_file_list/property/file" use="@relative_path"/>
    <xsl:template match="/">
        <Pacify>
            <xsl:attribute name="xsi:noNamespaceSchemaLocation"><xsl:value-of select="pathToXsd/pacify.xsd'"/></xsl:attribute>
            <xsl:for-each select="/property_file_list/property/file[generate-id() = generate-id(key('relativePathDistinct', @relative_path)[1])]">
                <xsl:sort select="@relative_path"/>
                <File>
                    <xsl:variable name="var_file_relative_path" select="@relative_path"/>
                    <xsl:attribute name="RelativePath"><xsl:value-of select="$var_file_relative_path"/></xsl:attribute>
                    <xsl:for-each select="/property_file_list/property[file/@relative_path = $var_file_relative_path]">
                        <xsl:sort select="@id"/>
                        <Property>
                            <xsl:attribute name="Name"><xsl:value-of select="@id"/></xsl:attribute>
                        </Property>
                    </xsl:for-each>
                </File>
            </xsl:for-each>
        </Pacify>
    </xsl:template>
</xsl:stylesheet>
