package adf

import bard.db.dictionary.Element
import bard.db.enums.ContextType
import bard.db.enums.ValueType
import bard.db.experiment.Experiment
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractDocument
import bard.db.project.Project
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import org.openrdf.model.Resource
import org.openrdf.model.impl.TreeModel
import org.openrdf.model.Model
import org.openrdf.model.ValueFactory
import org.openrdf.model.impl.ValueFactoryImpl
import org.openrdf.model.vocabulary.RDFS
import org.openrdf.model.vocabulary.XMLSchema
import org.openrdf.rio.RDFFormat
import org.openrdf.rio.Rio
import org.openrdf.rio.RDFWriter
import org.openrdf.model.Statement
import org.openrdf.model.URI
import org.openrdf.rio.RDFHandlerException
import bard.db.registration.AssayContextItem


class BardMetadataToRdfService extends AbstractService{
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

    BardMetadataToRdfService() {
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
    }

    Model createModel() {
        return new TreeModel ()
    }

    Resource getExperimentResource(Long experimentId) {
        return factory.createBNode("exp${experimentId}");
    }
    Resource getAssayResource(Long id) {
        return factory.createBNode("assay${id}");
    }
    Resource getProjectResource(Long id) {
        return factory.createBNode("proj${id}");
    }

    Model addProject(Long projectId, Model model) {
        Project project = Project.get(projectId)

        Resource projectRes = getProjectResource(project.id)

        model.add(projectRes,
                RDFS.CLASS,
                projectClass)

        model.add(projectRes,
                RDFS.LABEL,
                project.name)

        addExternalReferences(projectRes, project.externalReferences, model)

        project.documents.each {
            Resource docRes = addDocument(it, model);

            model.add(
                    projectRes,
                    hasDocument,
                    docRes
            )
        }

        project.contexts.each {
            Resource contextRes = addContext(it, model);

            model.add(
                    projectRes,
                    hasContext,
                    contextRes
            )
        }

        project.associatedExperiments.each {
            model.add(projectRes, hasExperiment, getExperimentResource(it.id));
        }

        return model

    }

    Model addAssay(Long assayId, Model model) {
        Assay assay = Assay.get(assayId)

        Resource assayRes = getAssayResource(assay.id)

        model.add(assayRes,
            RDFS.CLASS,
            assayClass)

        model.add(assayRes,
            RDFS.LABEL,
            factory.createLiteral(assay.assayName))

        assay.assayDocuments.each {
            Resource docRes = addDocument(it, model);

            model.add(
                    assayRes,
                    hasDocument,
                    docRes
            )
        }

        assay.assayContexts.each {
            Resource contextRes = addContext(it, model);

            model.add(
                    assayRes,
                    hasContext,
                    contextRes
            )
        }

        return model
    }

    void addExternalReferences(Resource subject, Collection<ExternalReference> refs, Model model) {
        refs.each {
            String url = it.externalSystem.systemUrl+it.extAssayRef
            model.add(subject, identifiedBy, factory.createURI(url))
        }
    }

    Model addExperiment(Long experimentId, Model model) {

        Experiment experiment = Experiment.get(experimentId)

        Resource expRes = getExperimentResource(experimentId)

        model.add(
                expRes,
                RDFS.CLASS,
                experimentClass
        )

        addExternalReferences(expRes, experiment.externalReferences, model)

        model.add(
                expRes,
                RDFS.LABEL,
                factory.createLiteral(experiment.description))

        model.add(
                expRes,
                definedBy,
                getAssayResource(experiment.assay.id)
        )

        experiment.documents.each {
            Resource docRes = addDocument(it, model);

            model.add(
                    expRes,
                    hasDocument,
                    docRes
            )
        }

        experiment.contexts.each {
            Resource contextRes = addContext(it, model);

            model.add(
                    expRes,
                    hasContext,
                    contextRes
            )
        }

        return model
    }

    Resource addContext(AbstractContext context, Model model) {
        Resource contextRes = factory.createBNode("ctx${context.id}");

        model.add(
            contextRes,
            RDFS.CLASS,
            contextClass
        )

        model.add(contextRes,
            RDFS.LABEL,
            factory.createLiteral(context.contextName))

        if(context.contextType != ContextType.UNCLASSIFIED) {
            model.add(contextRes,
                    hasContextType,
                    factory.createURI(adfOntology, context.contextType.id)
            )
        }

        context.contextItems.each {
            Resource itemRes = addContextItem(it, model);

            model.add(
                    contextRes,
                    hasItem,
                    itemRes
            )
        }

        return contextRes
    }

    Resource addContextItem(AbstractContextItem item, Model model) {
        Resource itemRes = factory.createBNode("item${item.id}");

        model.add(
                itemRes,
                RDFS.CLASS,
                itemClass
        )

        model.add(
                itemRes,
                hasAttribute,
                getElementURI(item.attributeElement)
        )

        model.add(
                itemRes,
                hasValueType,
                factory.createLiteral(item.valueType.id)
        )

        if (item instanceof AssayContextItem) {
            model.add(
                itemRes,
                hasAttributeType,
                factory.createLiteral(((AssayContextItem) item).attributeType.name())
            )
        }

        if (item.valueDisplay) {
            model.add(
                    itemRes,
                    hasValueDisplay,
                    factory.createLiteral(item.valueDisplay)
            )
        }

        switch(item.valueType) {
            case ValueType.ELEMENT:
                model.add(itemRes, hasValue, getElementURI(item.valueElement))
                break;
            case ValueType.EXTERNAL_ONTOLOGY:
               // model.add(itemRes, hasValue, getExternalURI(item.attributeElement, item.extValueId))
                model.add(itemRes, hasExternalOnto, getExternalOnto(item.attributeElement))
                model.add(itemRes, hasExternalOntoId, factory.createLiteral(item.extValueId))
                break;
            case ValueType.FREE_TEXT:
                model.add(itemRes, hasValue, factory.createLiteral(item.valueDisplay))
                break;
            case ValueType.NONE:
                break;
            case ValueType.NUMERIC:
                if(item.qualifier) {
                    model.add(itemRes, hasQualifier, factory.createLiteral(item.qualifier))
                }
                model.add(itemRes, hasNumericValue, factory.createLiteral(item.valueNum))
                break;
            case ValueType.RANGE:
                model.add(itemRes, hasMaxValue, factory.createLiteral(item.valueMax))
                model.add(itemRes, hasMinValue, factory.createLiteral(item.valueMin))
                break;
            default:
                throw new RuntimeException("Unknown value type ${item.valueType}")
        }

        return itemRes;
    }

    Resource getExternalURI(Element element, String id) {
        return factory.createURI(element.externalURL+id);
    }

    Resource getExternalOnto(Element element) {
        return factory.createURI(element.externalURL)
    }

    Resource getElementURI(Element element) {
        if(element.bardURI) {
            return factory.createURI(element.bardURI);
        } else {
            return factory.createURI(internalBardOntology, element.id.toString());
        }
    }

    Resource addDocument(AbstractDocument document, Model model) {
        Resource docRes = factory.createBNode("doc${document.id}");

        model.add(
                docRes,
                RDFS.CLASS,
                factory.createURI(adfOntology, document.documentType.id)
        )

        model.add(
                docRes,
                RDFS.LABEL,
                factory.createLiteral(document.documentName)
        )

        model.add(
                docRes,
                hasContent,
                factory.createLiteral(document.documentContent)
        )

        return docRes;
    }

//    Model getMeasure(Sql sql, Long experimentId, Model model) {
//        def queryString = """select exprmt_measure_id, parent_child_relationship, parent_exprmt_measure_id, result_type_id
//from exprmt_measure where experiment_id=:experimentId"""
//
//        ValueFactory factory = ValueFactoryImpl.getInstance()
//        sql.eachRow(queryString, [experimentId:experimentId]) {
//            // experiment has measure
//            model.add(
//                    factory.createURI(BardNameSpace.EXPERIMENT_BASE_URI.label + experimentId),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasMeasure"),
//                    factory.createURI(BardNameSpace.EXPRMT_MEASURE_BASE_URI.label + it.exprmt_measure_id)
//            )
//            // measure relationship
//            model.add(
//                    factory.createURI(BardNameSpace.EXPRMT_MEASURE_BASE_URI.label + it.exprmt_measure_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + it.parent_child_relationship),
//                    factory.createURI(BardNameSpace.EXPRMT_MEASURE_BASE_URI.label + it.parent_exprmt_measure_id)
//            )
//            // measure resultType
//            model.add(
//                    factory.createURI(BardNameSpace.EXPRMT_MEASURE_BASE_URI.label  + it.exprmt_measure_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasResultType"),
//                    factory.createURI(BardNameSpace.ELEMENT_URI.label + it.result_type_id)
//            )
//        }
//        return model
//    }
//
//    Model getContext(Sql sql, Long experimentId, Model model) {
//        def queryString = """select ec.exprmt_context_id, ec.context_name, eci.exprmt_context_item_id, eci.attribute_id, eci.value_display
//from exprmt_context ec join exprmt_context_item eci
//on eci.exprmt_context_id = eci.exprmt_context_id where experiment_id=:experimentId"""
//
//        ValueFactory factory = ValueFactoryImpl.getInstance()
//        sql.eachRow(queryString, [experimentId:experimentId]) {
//            // experiment has context
//            model.add(
//                    factory.createURI(BardNameSpace.EXPERIMENT_BASE_URI.label + experimentId),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasExprmtContext"),
//                    factory.createURI(BardNameSpace.EXPRMT_CONTEXT_BASE_URI.label + it.exprmt_context_id)
//            )
//
//            // context has name
//            model.add(
//                    factory.createURI(BardNameSpace.EXPRMT_CONTEXT_BASE_URI.label + it.exprmt_context_id),
//                    RDFS.LABEL,
//                    factory.createLiteral((String)it.context_name)
//            )
//            // context has item
//            model.add(
//                    factory.createURI(BardNameSpace.EXPRMT_CONTEXT_BASE_URI.label + it.exprmt_context_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasExprmtContextItem"),
//                    factory.createURI(BardNameSpace.EXPRMT_CONTEXT_ITEM_BASE_URI.label + it.exprmt_context_item_id)
//            )
//            // context item has attribute and value
//            model.add(
//                    factory.createURI(BardNameSpace.EXPRMT_CONTEXT_ITEM_BASE_URI.label + it.exprmt_context_item_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasAttribute"),
//                    factory.createURI(BardNameSpace.ELEMENT_URI.label + it.attribute_id)
//            )
//            model.add(
//                    factory.createURI(BardNameSpace.EXPRMT_CONTEXT_ITEM_BASE_URI.label + it.exprmt_context_item_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasValue"),
//                    factory.createLiteral((String)it.value_display)
//            )
//        }
//        return model
//    }
//
//    Model getResultFile(Sql sql, Long experimentId, Model model) {
//        def queryString = """select experiment_file_id, original_file, export_file from experiment_file
// where experiment_id=:experimentId"""
//        ValueFactory factory = ValueFactoryImpl.getInstance()
//        addExperimentType(experimentId, model)
//        sql.eachRow(queryString, [experimentId:experimentId]) {
//            model.add(
//                    factory.createURI(BardNameSpace.EXPERIMENT_BASE_URI.label + experimentId),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasResultFile"),
//                    factory.createURI(BardNameSpace.EXPRMT_RESULT_FILE_BASE_URI.label + it.experiment_file_id)
//            )
//            model.add(
//                    factory.createURI(BardNameSpace.EXPRMT_RESULT_FILE_BASE_URI.label + it.experiment_file_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasOriginalFile"),
//                    factory.createLiteral((String)it.original_file)
//            )
//            model.add(
//                    factory.createURI(BardNameSpace.EXPRMT_RESULT_FILE_BASE_URI.label + it.experiment_file_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasExportFile"),
//                    factory.createLiteral((String)it.export_file)
//            )
//        }
//        return model
//    }
//
//    def getProject(Sql sql, Long experimentId, Model model) {
//        def queryString = """select pe.project_experiment_id,
//pe.project_id,
//pc.project_context_id,
//p.project_name,
//pc.context_name,
//pci.project_context_item_id,
//pci.attribute_id,
//pci.value_display
//from project_experiment pe
//join project p on p.project_id=pe.project_id
//join project_context pc on pc.project_id=p.project_id
//join project_context_item pci on pci.project_context_id=pc.project_context_id
//where experiment_id=:experimentId"""
//
//        ValueFactory factory = ValueFactoryImpl.getInstance()
//        def projectId
//        sql.eachRow(queryString, [experimentId:experimentId]) {
//            projectId = it.project_id
//            // use name as label
//            model.add(
//                    factory.createURI(BardNameSpace.PROJECT_BASE_URI.label + it.project_id),
//                    RDFS.LABEL,
//                    factory.createLiteral(it.project_name)
//            )
//            // experiment is part of project
//            model.add(
//                    factory.createURI(BardNameSpace.EXPERIMENT_BASE_URI.label + experimentId),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "partOfProject"),
//                    factory.createURI(BardNameSpace.PROJECT_BASE_URI.label + it.project_id)
//            )
//
//            // project has context
//            model.add(
//                    factory.createURI(BardNameSpace.PROJECT_BASE_URI.label + it.project_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasProjectContext"),
//                    factory.createURI(BardNameSpace.PROJECT_CONTEXT_BASE_URI.label + it.project_context_id)
//            )
//            // context has name
//            model.add(
//                    factory.createURI(BardNameSpace.PROJECT_CONTEXT_BASE_URI.label + it.project_context_id),
//                    RDFS.LABEL,
//                    factory.createLiteral((String)it.context_name)
//            )
//            // context has item
//            model.add(
//                    factory.createURI(BardNameSpace.PROJECT_CONTEXT_BASE_URI.label + it.project_context_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasProjectContextItem"),
//                    factory.createURI(BardNameSpace.PROJECT_CONTEXT_ITEM_BASE_URI.label + it.project_context_item_id)
//            )
//            // context item has attribute and value
//            model.add(
//                    factory.createURI(BardNameSpace.PROJECT_CONTEXT_ITEM_BASE_URI.label + it.project_context_item_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasAttribute"),
//                    factory.createURI(BardNameSpace.ELEMENT_URI.label + it.attribute_id)
//            )
//            model.add(
//                    factory.createURI(BardNameSpace.PROJECT_CONTEXT_ITEM_BASE_URI.label + it.project_context_item_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasValue"),
//                    factory.createLiteral((String)it.value_display)
//            )
//        }
//        addProjectType((Long)projectId, model)
//        return model
//    }
//
//    Model getAssay(Sql sql, Long experimentId, Model model) {
//        def queryString = """select e.experiment_id,
//a.assay_id,
//a.assay_short_name,
//ac.assay_context_id,
//ac.context_name,
//ac.context_group,
//aci.assay_context_item_id,
//aci.attribute_id,
//aci.value_display
//from experiment e
//join assay a on a.assay_id=e.assay_id
//join assay_context ac on ac.assay_id=a.assay_id
//join assay_context_item aci on aci.assay_context_id=ac.assay_context_id
//where experiment_id=:experimentId"""
//
//        ValueFactory factory = ValueFactoryImpl.getInstance()
//        def assayId
//        sql.eachRow(queryString, [experimentId:experimentId]) {
//            assayId = it.assay_id
//            // use name as label
//            model.add(
//                    factory.createURI(BardNameSpace.ASSAY_BASE_URI.label + it.assay_id),
//                    RDFS.LABEL,
//                    factory.createLiteral(it.assay_short_name)
//            )
//            // experiment is defined by assay
//            model.add(
//                    factory.createURI(BardNameSpace.EXPERIMENT_BASE_URI.label + experimentId),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "definedBy"),
//                    factory.createURI(BardNameSpace.ASSAY_BASE_URI.label + it.assay_id)
//            )
//
//            // assay has assay context group
////            model.add(
////                    factory.createURI(BardNameSpace.ASSAY_BASE_URI.label + it.assay_id),
////                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasContextGroup"),
////
////            )
//
//            // assay has context
//            model.add(
//                    factory.createURI(BardNameSpace.ASSAY_BASE_URI.label + it.assay_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasAssayContext"),
//                    factory.createURI(BardNameSpace.ASSAY_CONTEXT_BASE_URI.label + it.assay_context_id)
//            )
//            // context has name
//            model.add(
//                    factory.createURI(BardNameSpace.ASSAY_CONTEXT_BASE_URI.label + it.assay_context_id),
//                    RDFS.LABEL,
//                    factory.createLiteral((String)it.context_name)
//            )
//            // context belong to context group
//            if (it.context_group)
//            model.add(
//                    factory.createURI(BardNameSpace.ASSAY_CONTEXT_BASE_URI.label + it.assay_context_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasContextGroup"),
//                    factory.createLiteral((String)it.context_group)
//            )
//            // context has item
//            model.add(
//                    factory.createURI(BardNameSpace.ASSAY_CONTEXT_BASE_URI.label + it.assay_context_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasAssayContextItem"),
//                    factory.createURI(BardNameSpace.ASSAY_CONTEXT_ITEM_BASE_URI.label + it.assay_context_item_id)
//            )
//            // context item has attribute and value
//            model.add(
//                    factory.createURI(BardNameSpace.ASSAY_CONTEXT_ITEM_BASE_URI.label + it.assay_context_item_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasAttribute"),
//                    factory.createURI(BardNameSpace.ELEMENT_URI.label + it.attribute_id)
//            )
//            String value = ""
//            if (it.value_display)
//                value = (String)it.value_display
//            model.add(
//                    factory.createURI(BardNameSpace.ASSAY_CONTEXT_ITEM_BASE_URI.label + it.assay_context_item_id),
//                    factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "hasValue"),
//                    factory.createLiteral(value)
//            )
//        }
//        addAssayType((Long)assayId, model)
//        return model
//    }
//
//
//    Model addExperimentType(Long experimentId, Model model) {
//        ValueFactory factory = ValueFactoryImpl.getInstance()
//        model.add(
//                factory.createURI(BardNameSpace.EXPERIMENT_BASE_URI.label + experimentId),
//                RDF.TYPE,
//                factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "experiment")
//        )
//    }
//
//    Model addAssayType(Long assayId, Model model) {
//        ValueFactory factory = ValueFactoryImpl.getInstance()
//        model.add(
//                factory.createURI(BardNameSpace.ASSAY_BASE_URI.label + assayId),
//                RDF.TYPE,
//                factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "assay")
//        )
//    }
//
//    Model addProjectType(Long projectId, Model model) {
//        ValueFactory factory = ValueFactoryImpl.getInstance()
//        model.add(
//                factory.createURI(BardNameSpace.PROJECT_BASE_URI.label + projectId),
//                RDF.TYPE,
//                factory.createURI(BardNameSpace.BARD_ONTOLOGY_BASE_URI.label + "project")
//        )
//    }
//

    Model writeToFileInXmlFormat(Model model, String filePath) {
        writeToFileInFormat(model, filePath, RDFFormat.RDFXML)
    }

    Model writeToFileInN3Format(Model model, String filePath) {
        writeToFileInFormat(model, filePath, RDFFormat.N3)
    }

    Model writeToFileInNTripleFormat(Model model, String filePath) {
        writeToFileInFormat(model, filePath, RDFFormat.NTRIPLES)
    }

    Model writeToFileInFormat(Model model, String filePath, RDFFormat format) {
        FileOutputStream out = new FileOutputStream(filePath)
        RDFWriter writer = Rio.createWriter(format, out);
        writer.handleNamespace("adf", adfOntology);
        writer.handleNamespace("bard", publicBardOntology);
        writer.handleNamespace("local", internalBardOntology);
        writer.handleNamespace("rdfs", RDFS.NAMESPACE)
        writer.handleNamespace("xmls", XMLSchema.NAMESPACE);
        try {
            writer.startRDF();
            for (Statement st: model) {
                try{
                    writer.handleStatement(st);
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
            writer.endRDF();
        }
        catch (RDFHandlerException e) {
            throw new RuntimeException(e);
        }
    }
}
