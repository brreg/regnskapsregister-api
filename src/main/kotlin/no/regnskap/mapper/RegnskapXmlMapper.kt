package no.regnskap.mapper

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import no.regnskap.model.RegnskapXmlWrap
import java.io.IOException

private val xmlMapper = XmlMapper()

@Throws(IOException::class)
fun deserializeXmlString(xmlString: String): RegnskapXmlWrap =
    xmlMapper.readValue(xmlString, RegnskapXmlWrap::class.java)