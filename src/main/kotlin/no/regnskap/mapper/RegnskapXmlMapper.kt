package no.regnskap.mapper

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import no.regnskap.model.RegnskapXmlWrap

private val xmlMapper = XmlMapper()

fun String.deserializeXmlString(): RegnskapXmlWrap =
    xmlMapper.readValue(this, RegnskapXmlWrap::class.java)