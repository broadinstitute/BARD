package dataexport.registration

import bard.db.dictionary.Element
import bard.db.enums.ContextType
import bard.db.enums.ReadyForExtraction
import bard.db.experiment.Experiment
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AssayDocument
import dataexport.util.ExportAbstractService
import groovy.xml.MarkupBuilder
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

/**
 * Helper Service for handling generation for XML documents for Assay definition extraction
 * This is wired in resources.groovy
 */
class AssayExportHelperService extends ExportAbstractService {
    LinkGenerator grailsLinkGenerator
    MediaTypesDTO mediaTypesDTO

    protected void generateAssayContext(final MarkupBuilder markupBuilder, final AssayContext assayContext) {
        def attributes = ['assayContextId': assayContext.id,
                'displayOrder': assayContext.assay.assayContexts.indexOf(assayContext)]
        markupBuilder.assayContext(attributes) {
            contextName(assayContext.getPreferredName())
            if (assayContext.contextType != ContextType.UNCLASSIFIED) {
                contextGroup(assayContext.contextType.id)
            }
            generateAssayContextItems(markupBuilder, assayContext.assayContextItems)
        }
    }


    public static void createElementRef(MarkupBuilder markupBuilder, Element element, String refName, String mediaType, LinkGenerator grailsLinkGenerator) {
        markupBuilder."${refName}"(label: element.label) {
            final String href = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: element.id]).toString()
            link(rel: 'related', href: href, type: mediaType)
        }
    }


    protected void generateAssayContextItem(final MarkupBuilder markupBuilder, final AssayContextItem assayContextItem) {
        generateContextItem(markupBuilder,
                assayContextItem,
                assayContextItem.attributeType.toString(),
                'assayContextItem',
                this.mediaTypesDTO.elementMediaType,
                this.grailsLinkGenerator,
                assayContextItem.id,
                assayContextItem.assayContext.assayContextItems.indexOf(assayContextItem))
    }

    /**
     * Generate a measure contexts
     * @param markupBuilder
     * @param assayContexts
     */
    protected void generateAssayContexts(final MarkupBuilder markupBuilder, final List<AssayContext> assayContexts) {
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
                    if (assayContextItem != null) {
                        generateAssayContextItem(markupBuilder, assayContextItem)
                    }
                }
            }
        }
    }
    /**
     * Generates a xml document for list of assays
     * @param markupBuilder
     */
    public void generateAssays(final MarkupBuilder markupBuilder) {
        final List<Assay> assays = Assay.findAllByReadyForExtraction(ReadyForExtraction.READY)

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

        for (AssayDocument assayDocument in assay.assayDocuments) {
            final String assayDocumentHref = grailsLinkGenerator.link(mapping: 'assayDocument', absolute: true, params: [id: assayDocument.id]).toString()
            markupBuilder.link(rel: 'item', href: "${assayDocumentHref}", type: "${this.mediaTypesDTO.assayDocMediaType}")
        }

        for (Experiment experiment : assay.experiments) {
            final String experimentHref = grailsLinkGenerator.link(mapping: 'experiment', absolute: true, params: [id: experiment.id]).toString()
            markupBuilder.link(rel: 'related', type: "${this.mediaTypesDTO.experimentMediaType}", href: "${experimentHref}")
        }
    }



    public void generateDocument(final MarkupBuilder markupBuilder, AssayDocument assayDocument) {
        generateDocument(this.grailsLinkGenerator, markupBuilder, assayDocument,
                'assayDocument', 'assay',
                assayDocument.id,
                assayDocument.assay.id,
                this.mediaTypesDTO.assayDocMediaType, this.mediaTypesDTO.assayMediaType)

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
        attributes.put("assayId", assay.id.toString())
        attributes.put('readyForExtraction', assay.readyForExtraction.id)
        if (assay.assayVersion) {
            attributes.put('assayVersion', assay.assayVersion)
        }
        if (assay.assayType) {
            attributes.put('assayType', assay.assayType.id)
        }
        if (assay.assayStatus) {
            attributes.put('status', assay.assayStatus.id)
        }
        if (assay.lastUpdated) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(assay.lastUpdated);
            final XMLGregorianCalendar lastUpdatedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            attributes.put('lastUpdated', lastUpdatedDate.toString())
        }
        if (StringUtils.isNotBlank(assay.modifiedBy)) {
            attributes.put('modifiedBy', assay.modifiedBy)
        }
        markupBuilder.assay(attributes) {
            assayShortName(assay.assayShortName)
            assayName(assay.assayName)
            if (assay.designedBy) {
                designedBy(assay.designedBy)
            }
            if (assay.assayContexts) {
                generateAssayContexts(markupBuilder, assay.assayContexts)
            }

            generateLinksForAssay(markupBuilder, assay)
        }
    }
}
