/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
                factory.createLiteral(project.name))

        model.add(projectRes,
                RDFS.COMMENT,
                factory.createLiteral(project.description))

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
            Resource resource = factory.createBNode("externalref${it.id}");
            model.add(subject, identifiedBy, resource)
            model.add(resource, hasExternalSystem, factory.createURI(it.externalSystem.systemUrl))
            model.add(resource, hasExternalRef, factory.createLiteral(it.extAssayRef))
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
                factory.createLiteral(experiment.experimentName))

        model.add(
                expRes,
                RDFS.COMMENT,
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

        //if(context.contextType != ContextType.UNCLASSIFIED) {
            model.add(contextRes,
                    hasContextType,
                    factory.createURI(adfOntology, context.contextType.id)
            )
        //}

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

        //TODO: we might want to use context time validation logic to export these values, but at this point, we are still in the process to fix migrated data, so just export all values
        // associated with the context time
        if (item.valueElement) {
            model.add(itemRes, hasValue, getElementURI(item.valueElement))
        }
        if (item.extValueId) {
            model.add(itemRes, hasExternalOnto, getExternalOnto(item.attributeElement))
            model.add(itemRes, hasExternalOntoId, factory.createLiteral(item.extValueId))
        }
        if (item.valueNum != null) {
            if(item.qualifier) {
                model.add(itemRes, hasQualifier, factory.createLiteral(item.qualifier))
            }
            model.add(itemRes, hasNumericValue, factory.createLiteral(item.valueNum))
        }
        if (item.valueMax != null && item.valueMin != null) {
            model.add(itemRes, hasMaxValue, factory.createLiteral(item.valueMax))
            model.add(itemRes, hasMinValue, factory.createLiteral(item.valueMin))
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

        model.add(
                docRes,
                hasDocumentType,
                factory.createLiteral(document.documentType.id)
        )

        return docRes;
    }


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
