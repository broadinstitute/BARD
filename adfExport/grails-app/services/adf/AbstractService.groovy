package adf

import org.openrdf.model.ValueFactory
import org.openrdf.model.URI
import org.openrdf.model.impl.ValueFactoryImpl
import org.openrdf.model.Model
import org.openrdf.model.impl.TreeModel

class AbstractService {

    static final String adfOntology = "http://bard.broadinstitute.org/adf#";
    static final String internalBardOntology = "http://bard.broadinstitute.org/bard#";
    static final String publicBardOntology = "http://www.bard.nih.gov/ontology/bard#"
    ValueFactory factory;

    URI definedBy;
    URI hasDocument;
    URI hasContent;
    URI hasContext;
    URI hasItem;
    URI hasAttribute;
    URI hasValue;
    URI hasQualifier;
    URI hasNumericValue;
    URI hasMaxValue;
    URI hasMinValue;
    URI hasExperiment;
    URI identifiedBy;
    URI hasContextType;

    URI documentClass;
    URI experimentClass;
    URI projectClass;
    URI itemClass;
    URI contextClass;
    URI assayClass;

    URI hasExternalOnto
    URI hasExternalOntoId

    URI hasValueType
    URI hasAttributeType
    URI hasValueDisplay

    AbstractService() {
        factory = ValueFactoryImpl.getInstance()

        definedBy = factory.createURI(adfOntology, "definedBy");
        hasDocument = factory.createURI(adfOntology, "hasDocument");
        hasContent = factory.createURI(adfOntology, "hasContent");
        hasItem = factory.createURI(adfOntology, "hasItem");
        hasExperiment = factory.createURI(adfOntology, "hasExperiment");
        hasAttribute = factory.createURI(adfOntology, "hasAttribute");
        hasValue = factory.createURI(adfOntology, "hasValue");
        hasQualifier = factory.createURI(adfOntology, "hasQualifier");
        hasNumericValue = factory.createURI(adfOntology, "hasNumericValue");
        hasMaxValue = factory.createURI(adfOntology, "hasMaxValue");
        hasMinValue = factory.createURI(adfOntology, "hasMinValue");
        identifiedBy = factory.createURI(adfOntology, "identifiedBy");
        hasContextType = factory.createURI(adfOntology, "hasContextType");
        hasContext = factory.createURI(adfOntology, "hasContext");

        documentClass = factory.createURI(adfOntology, "Document");
        contextClass = factory.createURI(adfOntology, "Context");
        itemClass = factory.createURI(adfOntology, "Item");
        experimentClass = factory.createURI(adfOntology, "Experiment");
        assayClass = factory.createURI(adfOntology, "Assay");
        projectClass = factory.createURI(adfOntology, "Project");

        hasExternalOnto = factory.createURI(adfOntology, "hasExternalOnto");
        hasExternalOntoId = factory.createURI(adfOntology, "hasExternalOntoId")

        hasValueType = factory.createURI(adfOntology, "hasValueType")
        hasAttributeType = factory.createURI(adfOntology, "hasAttributeType")
        hasValueDisplay =  factory.createURI(adfOntology, "hasValueDisplay")
    }
}
