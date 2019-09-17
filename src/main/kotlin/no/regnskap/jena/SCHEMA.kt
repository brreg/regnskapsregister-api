package no.regnskap.jena

import org.apache.jena.rdf.model.ModelFactory

object SCHEMA {

    val uri = "http://schema.org/"

    private val model = ModelFactory.createDefaultModel()

    val currency = model.createProperty(uri, "currency")
    val startDate = model.createProperty(uri, "startDate")
    val endDate = model.createProperty(uri, "endDate")

}