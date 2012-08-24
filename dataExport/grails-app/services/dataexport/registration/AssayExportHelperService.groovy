package dataexport.registration

import bard.db.dictionary.Element
import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import bard.db.registration.*

/**
 * Helper Service for handling generation for XML documents for Assay definition extraction
 * This is wired in resources.groovy
 */
class AssayExportHelperService {
    LinkGenerator grailsLinkGenerator
    final MediaTypesDTO mediaTypesDTO

    AssayExportHelperService(final MediaTypesDTO mediaTypesDTO) {
        this.mediaTypesDTO = mediaTypesDTO
    }

    protected void generateAssayContext(final MarkupBuilder markupBuilder, final AssayContext assayContext) {

        markupBuilder.assayContext() {
            contextName(assayContext.contextName)
            generateAssayContextItems(markupBuilder, assayContext.assayContextItems)
        }
    }
    /**
     *
     * @param measureDTO
     * @return Map
     */
    protected Map<String, String> createAttributesForMeasure(final Measure measure) {
        final Map<String, String> attributes = [:]

        if (measure.assayContext) {
            attributes.put('assayContextRef', measure.assayContext.contextName)
        }
        if (measure.parentMeasure) {
            attributes.put('parentMeasure', measure.parentMeasure.id.toString())
        }
        return attributes
    }

    protected void generateMeasure(final MarkupBuilder markupBuilder, final Measure measure) {

        final Map<String, String> attributes = createAttributesForMeasure(measure);
        markupBuilder.measure(attributes) {
            final Element resultType = measure.element
            if (resultType) { //this is the result type
                resultTypeRef(label: resultType.label) {
                    final String href = grailsLinkGenerator.link(mapping: 'resultType', absolute: true, params: [id: resultType.id]).toString()
                    link(rel: 'related', href: "${href}", type: "${this.mediaTypesDTO.resultTypeMediaType}")
                }
            }
            final String unit = measure.entryUnit
            if (unit) {
                entryUnit(unit: unit) {
                }
            }
        }
    }

    protected Map<String, String> createAttributesForAssayContextItem(final AssayContextItem assayContextItem) {
        final Map<String, String> attributes = [:]

        if (assayContextItem.id) {
            attributes.put('assayContextItemId', assayContextItem.id.toString())
        }
        if (assayContextItem.assayContext) {
            attributes.put('assayContextRef', assayContextItem.assayContext?.contextName)
        }
        if (assayContextItem.qualifier) {
            attributes.put('qualifier', assayContextItem.qualifier)
        }

        if (assayContextItem.valueDisplay) {
            attributes.put('valueDisplay', assayContextItem.valueDisplay)
        }
        if (assayContextItem.valueNum || assayContextItem.valueNum.toString().isInteger()) {
            attributes.put('valueNum', assayContextItem.valueNum.toString())
        }
        if (assayContextItem.valueMin || assayContextItem.valueMin.toString().isInteger()) {
            attributes.put('valueMin', assayContextItem.valueMin.toString())
        }
        if (assayContextItem.valueMax || assayContextItem.valueMax.toString().isInteger()) {
            attributes.put('valueMax', assayContextItem.valueMax.toString())
        }
        return attributes;
    }

    protected void generateAssayContextItem(final MarkupBuilder markupBuilder, final AssayContextItem assayContextItem) {
        final Map<String, String> attributes = createAttributesForAssayContextItem(assayContextItem)

        markupBuilder.assayContextItem(attributes) {
            final Element valueElement = assayContextItem.valueElement
            final Element attributeElement = assayContextItem.attributeElement

            //add value id element
            if (valueElement) {
                valueId(label: valueElement.label) {
                    final String valueHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: valueElement.id]).toString()
                    link(rel: 'related', href: "${valueHref}", type: "${this.mediaTypesDTO.elementMediaType}")
                }
            }
            //add attributeId element
            if (attributeElement) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: attributeElement.id]).toString()
                attributeId(attributeType: assayContextItem.attributeType.toString(), label: attributeElement.label) {
                    link(rel: 'related', href: "${attributeHref}", type: "${this.mediaTypesDTO.elementMediaType}")
                }
            }
            if (assayContextItem.extValueId) {
                extValueId(assayContextItem.extValueId)
            }
        }

    }
    /**
     *
     * @param markupBuilder
     * @param assayDocument
     * @param generateContent - True if we should add the contents of this document
     */
    public void generateAssayDocument(
            final MarkupBuilder markupBuilder, final AssayDocument assayDocument, final boolean generateContent) {

        final String assayDocumentHref = grailsLinkGenerator.link(mapping: 'assayDocument', absolute: true, params: [id: assayDocument.id]).toString()

        markupBuilder.assayDocument(documentType: assayDocument.documentType) {
            if (assayDocument.documentName) {
                documentName(assayDocument.documentName)
            }
            if (generateContent) {
                if (assayDocument.documentContent) {
                    documentContent(assayDocument.documentContent)
                }
            }
            link(rel: 'item', href: "${assayDocumentHref}", type: "${this.mediaTypesDTO.assayDocMediaType}")
        }
    }
    /**
     * Generate a measure contexts
     * @param markupBuilder
     * @param assayContexts
     */
    protected void generateAssayContexts(final MarkupBuilder markupBuilder, final Set<AssayContext> assayContexts) {
        if (assayContexts) {
            markupBuilder.assayContexts() {
                for (AssayContext assayContext : assayContexts) {
                    generateAssayContext(markupBuilder, assayContext)
                }
            }
        }
    }
    /**
     *
     * @param markupBuilder
     * @param assayContextItems
     */
    public void generateAssayContextItems(final MarkupBuilder markupBuilder, final List<AssayContextItem> allAssayContextItems) {
        if (allAssayContextItems) {
            markupBuilder.assayContextItems() {
                for (AssayContextItem assayContextItem : allAssayContextItems) {
                    generateAssayContextItem(markupBuilder, assayContextItem)
                }
            }
        }
    }
    /**
     * Generates a xml document for list of assays
     * @param markupBuilder
     */
    public void generateAssays(final MarkupBuilder markupBuilder) {
        final List<Assay> assays = Assay.findAllByReadyForExtraction(ReadyForExtraction.Ready)

        final int numberOfAssays = assays.size()


        markupBuilder.assays(count: numberOfAssays) {
            for (Assay assay : assays) {
                final String assayHref = grailsLinkGenerator.link(mapping: 'assay', absolute: true, params: [id: assay.id]).toString()
                markupBuilder.link(rel: 'related', title: "${assay.id}", type: "${this.mediaTypesDTO.assayMediaType}",
                        href: assayHref) {
                }
            }
        }
    }

    protected void generateLinksForAssay(final MarkupBuilder markupBuilder, final Assay assay) {
        final String assayHref = grailsLinkGenerator.link(mapping: 'assay', absolute: true, params: [id: assay.id]).toString()
        final String assaysHref = grailsLinkGenerator.link(mapping: 'assays', absolute: true).toString()

        markupBuilder.link(rel: 'edit', href: "${assayHref}", type: "${this.mediaTypesDTO.assayMediaType}")
        markupBuilder.link(rel: 'self', href: "${assayHref}", type: "${this.mediaTypesDTO.assayMediaType}")
        markupBuilder.link(rel: 'up', href: "${assaysHref}", type: "${this.mediaTypesDTO.assaysMediaType}")

        for (Experiment experiment : assay.experiments) {
            final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: experiment.id]).toString()
            markupBuilder.link(rel: 'related', type: "${this.mediaTypesDTO.experimentMediaType}", href: "${experimentHref}")
        }
    }

    protected void generateMeasures(final MarkupBuilder markupBuilder, final Set<Measure> measures) {
        if (measures) {
            markupBuilder.measures() {
                for (Measure measure : measures) {
                    generateMeasure(markupBuilder, measure)
                }
            }
        }
    }

    public void generateAssayDocuments(
            final MarkupBuilder markupBuilder,
            final Set<AssayDocument> assayDocuments) {
        if (assayDocuments) {
            markupBuilder.assayDocuments() {
                for (AssayDocument assayDocument : assayDocuments) {
                    generateAssayDocument(markupBuilder, assayDocument, false)
                }
            }
        }
    }

    /**
     *
     * @param markupBuilder
     * @param assay
     */
    public void generateAssay(
            final MarkupBuilder markupBuilder,
            final Assay assay) {

        final Map<String, String> attributes = [:]

        if (assay.id) {
            attributes.put("assayId", assay.id.toString())
        }
        attributes.put('readyForExtraction', assay.readyForExtraction.toString())
        if (assay.assayVersion) {
            attributes.put('assayVersion', assay.assayVersion)
        }
        if (assay.assayType) {
            attributes.put('assayType', assay.assayType)
        }
        if (assay.assayStatus) {
            attributes.put('status', assay.assayStatus.toString())
        }

        markupBuilder.assay(attributes) {
            if(assay.assayTitle){
                assayTitle(assay.assayTitle)
            }
            if (assay.assayName) {
                assayName(assay.assayName)
            }

            if (assay.designedBy) {
                designedBy(assay.designedBy)
            }
            generateAssayContexts(markupBuilder, assay.assayContexts)
            generateMeasures(markupBuilder, assay.measures)
            generateAssayContextItems(markupBuilder, assay.assayContextItems)
            generateAssayDocuments(markupBuilder, assay.assayDocuments)
            generateLinksForAssay(markupBuilder, assay)
        }
    }
}
