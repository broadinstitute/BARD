package dataexport.registration

import bard.db.dictionary.Element
import bard.db.dictionary.Unit
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import bard.db.registration.*

/**
 * Helper Service for handling generation for XML documents for Assay definition extraction
 * This is wired in resources.groovy
 */
class AssayExportHelperService {
    LinkGenerator grailsLinkGenerator
    final String elementMediaType
    final String assayMediaType
    final String assayDocMediaType
    final String assaysMediaType
    final String resultTypeMediaType

    AssayExportHelperService(final AssayDefinitionMediaTypesDTO dto) {
        this.elementMediaType = dto.elementMediaType
        this.assayMediaType = dto.assayMediaType
        this.assayDocMediaType = dto.assayDocMediaType
        this.assaysMediaType = dto.assaysMediaType
        this.resultTypeMediaType = dto.resultTypeMediaType
    }

    protected void generateMeasureContext(final MarkupBuilder markupBuilder, final MeasureContext measureContext) {

        markupBuilder.measureContext() {
            contextName(measureContext.contextName)
        }
    }
    /**
     *
     * @param measureDTO
     * @return Map
     */
    protected Map<String, String> createAttributesForMeasure(final Measure measure) {
        final Map<String, String> attributes = [:]

        if (measure.measureContext) {
            attributes.put('measureContextRef', measure.measureContext.contextName)
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
                    link(rel: 'related', href: "${href}", type: "${this.resultTypeMediaType}")
                }
            }
            final Unit unit = measure.entryUnit
            if (unit) {
                entryUnit(unit: unit.unit) {
                    if (unit.element?.id) {
                        final String href = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: unit.element.id]).toString()
                        link(rel: 'related', href: "${href}", type: "${this.elementMediaType}")
                    }
                }
            }
        }
    }

    protected Map<String, String> createAttributesForMeasureContextItem(final MeasureContextItem measureContextItem) {
        final Map<String, String> attributes = [:]

        if (measureContextItem.id) {
            attributes.put('measureContextItemId', measureContextItem.id.toString())
        }
        if (measureContextItem.parentGroup?.id) {
            attributes.put('measureContextItemRef', measureContextItem.parentGroup.id.toString())
        }
        if (measureContextItem.measureContext) {
            attributes.put('measureContextRef', measureContextItem.measureContext?.contextName)
        }
        if (measureContextItem.qualifier) {
            attributes.put('qualifier', measureContextItem.qualifier)
        }

        if (measureContextItem.valueDisplay) {
            attributes.put('valueDisplay', measureContextItem.valueDisplay)
        }
        if (measureContextItem.valueNum || measureContextItem.valueNum.toString().isInteger()) {
            attributes.put('valueNum', measureContextItem.valueNum.toString())
        }
        if (measureContextItem.valueMin || measureContextItem.valueMin.toString().isInteger()) {
            attributes.put('valueMin', measureContextItem.valueMin.toString())
        }
        if (measureContextItem.valueMax || measureContextItem.valueMax.toString().isInteger()) {
            attributes.put('valueMax', measureContextItem.valueMax.toString())
        }
        return attributes;
    }

    protected void generateMeasureContextItem(final MarkupBuilder markupBuilder, final MeasureContextItem measureContextItem) {
        final Map<String, String> attributes = createAttributesForMeasureContextItem(measureContextItem)

        markupBuilder.measureContextItem(attributes) {
            final Element valueElement = measureContextItem.valueElement
            final Element attributeElement = measureContextItem.attributeElement
            //add value id element
            if (valueElement) {
                valueId(label: valueElement.label) {
                    final String valueHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: valueElement.id]).toString()
                    link(rel: 'related', href: "${valueHref}", type: "${this.elementMediaType}")
                }
            }
            //add attributeId element
            if (attributeElement) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: attributeElement.id]).toString()
                attributeId(attributeType: measureContextItem.attributeType.value, label: attributeElement.label) {
                    link(rel: 'related', href: "${attributeHref}", type: "${this.elementMediaType}")
                }
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
            link(rel: 'item', href: "${assayDocumentHref}", type: "${this.assayDocMediaType}")
        }
    }
    /**
     * Generate a measure contexts
     * @param markupBuilder
     * @param measureContexts
     */
    protected void generateMeasureContexts(final MarkupBuilder markupBuilder, final Set<MeasureContext> measureContexts) {
        markupBuilder.measureContexts() {
            for (MeasureContext measureContext : measureContexts) {
                generateMeasureContext(markupBuilder, measureContext)
            }
        }
    }
    /**
     *
     * @param markupBuilder
     * @param measureContextItems
     */
    protected void generateMeasureContextItems(final MarkupBuilder markupBuilder, final Set<MeasureContextItem> measureContextItems) {
        markupBuilder.measureContextItems() {
            for (MeasureContextItem measureContextItem : measureContextItems) {
                generateMeasureContextItem(markupBuilder, measureContextItem)
            }
        }
    }
    /**
     * Generates a xml document for list of assays
     * @param markupBuilder
     */
    public void generateAssays(final MarkupBuilder markupBuilder) {
        final List<Assay> assays = Assay.findAllByReadyForExtraction('Ready')

        final int numberOfAssays = assays.size()


        markupBuilder.assays(count: numberOfAssays) {
            for (Assay assay : assays) {
                final String assayHref = grailsLinkGenerator.link(mapping: 'assay', absolute: true, params: [id: assay.id]).toString()
                markupBuilder.link(rel: 'related', title: "${assay.id}", type: "${this.assayMediaType}",
                        href: assayHref) {
                }
            }
        }
    }

    protected void generateLinksForAssay(final MarkupBuilder markupBuilder, BigDecimal assayId) {
        final String assayHref = grailsLinkGenerator.link(mapping: 'assay', absolute: true, params: [id: assayId]).toString()
        final String assaysHref = grailsLinkGenerator.link(mapping: 'assays', absolute: true).toString()

        markupBuilder.link(rel: 'edit', href: "${assayHref}", type: "${this.assayMediaType}")
        markupBuilder.link(rel: 'self', href: "${assayHref}", type: "${this.assayMediaType}")
        markupBuilder.link(rel: 'up', href: "${assaysHref}", type: "${this.assaysMediaType}")

    }

    protected void generateMeasures(final MarkupBuilder markupBuilder, final Set<Measure> measures) {
        markupBuilder.measures() {
            for (Measure measure : measures) {
                generateMeasure(markupBuilder, measure)
            }
        }
    }

    public void generateAssayDocuments(
            final MarkupBuilder markupBuilder,
            final Set<AssayDocument> assayDocuments) {

        markupBuilder.assayDocuments() {
            for (AssayDocument assayDocument : assayDocuments) {
                generateAssayDocument(markupBuilder, assayDocument, false)
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
        attributes.put('readyForExtraction', assay.readyForExtraction)
        if (assay.assayVersion) {
            attributes.put('assayVersion', assay.assayVersion)
        }
        if (assay.assayStatus) {
            attributes.put('status', assay.assayStatus)
        }

        markupBuilder.assay(attributes) {
            if (assay.assayName) {
                assayName(assay.assayName)
            }

            if (assay.designedBy) {
                designedBy(assay.designedBy)
            }
            generateMeasureContexts(markupBuilder, assay.measureContexts)
            generateMeasures(markupBuilder, assay.measures)
            generateMeasureContextItems(markupBuilder, assay.measureContextItems)
            generateAssayDocuments(markupBuilder, assay.assayDocuments)
            generateLinksForAssay(markupBuilder, assay.id)
        }
    }
}
