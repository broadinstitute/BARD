package dataexport.registration

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.sql.DataSource
/**
 * Helper Service for handling generation for XML documents for Assay definition extraction
 * TODO: We will be using the domain plugin instead, once it is ready
 */
class AssayExportHelperService {
    DataSource dataSource
    LinkGenerator grailsLinkGenerator
    final String elementMediaType
    final String resultTypeMediaType
    final String assayMediaType
    final String assayDocMediaType
    final String assaysMediaType

    AssayExportHelperService(final AssayDefinitionMediaTypesDTO dto) {
        this.elementMediaType = dto.elementMediaType
        this.resultTypeMediaType = dto.resultTypeMediaType
        this.assayMediaType = dto.assayMediaType
        this.assayDocMediaType = dto.assayDocMediaType
        this.assaysMediaType = dto.assaysMediaType
    }

    protected void generateMeasureContext(final MarkupBuilder markupBuilder, final BigDecimal measureContextId, final String contextNameText) {

        markupBuilder.measureContext(measureContextId: measureContextId) {
            contextName(contextNameText)
        }
    }
    /**
     *
     * @param measureDTO
     * @return Map
     */
    protected Map<String, String> createAttributesForMeasure(final MeasureDTO measureDTO) {
        final Map<String, String> attributes = [:]

        if (measureDTO.measureId || measureDTO.measureId.toString().isInteger()) {
            attributes.put('measureId', measureDTO.measureId.toString())
        }


        if (measureDTO.measureContextId || measureDTO.measureContextId.toString().isInteger()) {
            attributes.put('measureContextRef', measureDTO.measureContextId.toString())
        }
        if (measureDTO.parentMeasureId || measureDTO.parentMeasureId.toString().isInteger()) {
            attributes.put('measureRef', measureDTO.parentMeasureId.toString())
        }
        return attributes

    }

    protected void generateMeasure(final MarkupBuilder markupBuilder, final MeasureDTO measureDTO) {

        final Map<String, String> attributes = createAttributesForMeasure(measureDTO);
        markupBuilder.measure(attributes) {
            if (measureDTO.resultTypeId || measureDTO.resultTypeId.toString().isInteger()) {
                resultTypeRef() {
                    final String RESULT_TYPE_MEDIA_TYPE = this.resultTypeMediaType
                    final String resultTypeHref = grailsLinkGenerator.link(mapping: 'resultType', absolute: true, params: [id: measureDTO.resultTypeId]).toString()
                    link(rel: 'related', href: "${resultTypeHref}", type: "${RESULT_TYPE_MEDIA_TYPE}")
                }
            }
            if (measureDTO.entryUnit) {
                entryUnit(entryUnit: measureDTO.entryUnit) {
                }
            }
        }
    }

    protected Map<String, String> createAttributesForMeasureContextItem(final MeasureContextItemDTO dto) {
        final Map<String, String> attributes = [:]

        if (dto.measureContextItemId || dto.measureContextItemId.toString().isInteger()) {
            attributes.put('measureContextItemId', dto.measureContextItemId.toString())
        }
        if (dto.groupMeasureContextItemId || dto.groupMeasureContextItemId.toString().isInteger()) {
            attributes.put('measureContextItemRef', dto.groupMeasureContextItemId.toString())
        }
        if (dto.measureContextId || dto.measureContextId.toString().isInteger()) {
            attributes.put('measureContextRef', dto.measureContextId.toString())
        }
        if (dto.qualifier) {
            attributes.put('qualifier', dto.qualifier)
        }

        if (dto.valueDisplay) {
            attributes.put('valueDisplay', dto.valueDisplay)
        }
        if (dto.valueNum || dto.valueNum.toString().isInteger()) {
            attributes.put('valueNum', dto.valueNum.toString())
        }
        if (dto.valueMin || dto.valueMin.toString().isInteger()) {
            attributes.put('valueMin', dto.valueMin.toString())
        }
        if (dto.valueMax || dto.valueMax.toString().isInteger()) {
            attributes.put('valueMax', dto.valueMax.toString())
        }
        return attributes;
    }

    protected void generateMeasureContextItem(final MarkupBuilder markupBuilder, final MeasureContextItemDTO dto) {
        final Map<String, String> attributes = createAttributesForMeasureContextItem(dto)

        markupBuilder.measureContextItem(attributes) {
            final String ELEMENT_MEDIA_TYPE = this.elementMediaType

            //add value id element
            if (dto.valueId || dto.valueId.toString().isInteger()) {
                valueId() {
                    final String valueHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: dto.valueId]).toString()
                    link(rel: 'related', href: "${valueHref}", type: "${ELEMENT_MEDIA_TYPE}")
                }
            }
            //add attributeId element
            if (dto.attributeId || dto.attributeId.toString().isInteger()) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: dto.attributeId]).toString()
                attributeId('attributeType': dto.attributeType) {
                    link(rel: 'related', href: "${attributeHref}", type: "${ELEMENT_MEDIA_TYPE}")
                }
            }
        }

    }

    protected void generateExternalSystem(final MarkupBuilder markupBuilder, final ExternalSystemDTO externalSystemDTO) {
        markupBuilder.externalSystem() {

            if (externalSystemDTO.systemName) {
                systemName(externalSystemDTO.systemName)
            }

            if (externalSystemDTO.systemOwner) {
                owner(externalSystemDTO.systemOwner)
            }
            if (externalSystemDTO.systemUrl) {
                systemUrl("${externalSystemDTO.systemUrl}${externalSystemDTO.externalAssayId}")
            }
        }
    }
    /**
     * Should we expose this as a service?
     * @param markupBuilder
     * @param documentNameText
     * @param assayDocumentId
     */
    protected void generateAssayDocument(
            final MarkupBuilder markupBuilder, final AssayDocumentDTO assayDocumentDTO) {
        final String assayDocumentHref = grailsLinkGenerator.link(mapping: 'assayDocument', absolute: true, params: [id: assayDocumentDTO.assayDocumentId]).toString()
        final String ASSAY_DOC_MEDIA_TYPE = this.assayDocMediaType

        markupBuilder.assayDocument(documentType: assayDocumentDTO.documentType) {
            if (assayDocumentDTO.documentName) {
                documentName(assayDocumentDTO.documentName)
            }
            if(assayDocumentDTO.documentContent){
                documentContent(assayDocumentDTO.documentContent)
            }
            link(rel: 'item', href: "${assayDocumentHref}", type: "${ASSAY_DOC_MEDIA_TYPE}")
        }
    }
    /**
     * Should we expose this as a service?
     * @param markupBuilder
     * @param documentNameText
     * @param assayDocumentId
     */
    protected void generateAssayDocument(
            final MarkupBuilder markupBuilder, final BigDecimal assayDocumentId) {
        final Sql sql = new Sql(dataSource)
        sql.eachRow('SELECT ASSAY_DOCUMENT_ID,DOCUMENT_NAME,DOCUMENT_TYPE, DOCUMENT_CONTENT FROM ASSAY_DOCUMENT WHERE ASSAY_DOCUMENT_ID=' + assayDocumentId) { documentRow ->
           String documentContent = null
            if(documentRow.DOCUMENT_CONTENT){//TODO: This should go away once we use the domain plugin
                java.sql.Clob clob = (java.sql.Clob) documentRow.DOCUMENT_CONTENT
                documentContent = clob?.asciiStream.text
            }

            generateAssayDocument(markupBuilder, new AssayDocumentDTO(documentRow.DOCUMENT_NAME, documentRow.DOCUMENT_TYPE, documentRow.ASSAY_DOCUMENT_ID, documentContent))
        }
    }
    /**
     * Generate a measure contexts
     * @param sql
     * @param markupBuilder
     */
    protected void generateMeasureContexts(final MarkupBuilder markupBuilder, final BigDecimal assayId) {
        final Sql sql = new Sql(dataSource)
        markupBuilder.measureContexts() {
            sql.eachRow('SELECT MEASURE_CONTEXT_ID,CONTEXT_NAME FROM MEASURE_CONTEXT WHERE ASSAY_ID=' + assayId) { row ->
                generateMeasureContext(markupBuilder, row.MEASURE_CONTEXT_ID, row.CONTEXT_NAME)
            }
        }
    }

    protected void generateMeasureContextItems(final MarkupBuilder markupBuilder, final BigDecimal assayId) {
        final Sql sql = new Sql(dataSource)
        markupBuilder.measureContextItems() {
            sql.eachRow('SELECT MEASURE_CONTEXT_ITEM_ID,GROUP_MEASURE_CONTEXT_ITEM_ID,MEASURE_CONTEXT_ID,ATTRIBUTE_TYPE,ATTRIBUTE_ID,QUALIFIER,VALUE_ID,VALUE_DISPLAY,VALUE_NUM,VALUE_MIN,VALUE_MAX FROM MEASURE_CONTEXT_ITEM WHERE ASSAY_ID=' + assayId) { row ->


                MeasureContextItemDTO dto = new MeasureContextItemDTO(
                        row.MEASURE_CONTEXT_ITEM_ID,
                        row.GROUP_MEASURE_CONTEXT_ITEM_ID,
                        row.MEASURE_CONTEXT_ID,
                        row.ATTRIBUTE_ID,
                        row.VALUE_ID,
                        row.VALUE_NUM,
                        row.VALUE_MIN,
                        row.VALUE_MAX,
                        row.VALUE_DISPLAY,
                        row.QUALIFIER,
                        row.ATTRIBUTE_TYPE)

                generateMeasureContextItem(markupBuilder, dto)
            }
        }

    }
    protected void generateExternalAssays(
            final MarkupBuilder markupBuilder, final BigDecimal assayId) {
        final Sql sql = new Sql(dataSource)

        markupBuilder.externalAssays() {
            sql.eachRow('SELECT EXTERNAL_SYSTEM_ID,EXT_ASSAY_ID FROM EXTERNAL_ASSAY WHERE ASSAY_ID=' + assayId) { externalAssayRow ->
                generateExternalAssay(markupBuilder, externalAssayRow.EXT_ASSAY_ID, externalAssayRow.EXTERNAL_SYSTEM_ID)
            }
        }
    }
    /**
     *
     * @param markupBuilder
     * @param externalAssayId
     * @param externalSystemId
     */
    protected void generateExternalAssay(
            final MarkupBuilder markupBuilder,
            final String externalAssayId,
            final BigDecimal externalSystemId) {

        final Sql sql = new Sql(dataSource)

        //The externalAssayId is something like aid='224'
        markupBuilder.externalAssay(externalAssayId: externalAssayId) {
            //now we add the external system information.
            if (externalSystemId) {
                sql.eachRow('SELECT SYSTEM_NAME,OWNER,SYSTEM_URL FROM EXTERNAL_SYSTEM WHERE EXTERNAL_SYSTEM_ID=' + externalSystemId) { externalSystemRow ->
                    generateExternalSystem(markupBuilder, new ExternalSystemDTO(externalSystemRow.SYSTEM_NAME, externalSystemRow.OWNER, externalSystemRow.SYSTEM_URL, externalAssayId))
                }
            }
        }
    }
    /**
     * Generates a xml document for list of assays
     * @param markupBuilder
     */
    public void generateAssays(final MarkupBuilder markupBuilder) {
        final Sql sql = new Sql(dataSource)
        int numberOfAssays = 0
        sql.eachRow("SELECT COUNT(*) AS ASSAYCOUNT FROM ASSAY WHERE READY_FOR_EXTRACTION='Ready'") { assayCountRow ->
            numberOfAssays = assayCountRow.ASSAYCOUNT
        }

        markupBuilder.assays(count: numberOfAssays) {
            sql.eachRow("SELECT DISTINCT ASSAY_ID FROM ASSAY WHERE READY_FOR_EXTRACTION='Ready'") { assayRow ->
                final String assayHref = grailsLinkGenerator.link(mapping: 'assay', absolute: true, params: [id: assayRow.ASSAY_ID]).toString()
                final String ASSAY_MEDIA_TYPE = this.assayMediaType
                markupBuilder.link(rel: 'related', title: "${assayRow.ASSAY_ID}", type: "${ASSAY_MEDIA_TYPE}",
                        href: assayHref) {
                }
            }
        }
    }
    protected void generateLinksForAssay(final MarkupBuilder markupBuilder,BigDecimal assayId){
        final String ASSAY_MEDIA_TYPE = this.assayMediaType
        final String ASSAYS_MEDIA_TYPE = this.assaysMediaType
        final String assayHref = grailsLinkGenerator.link(mapping: 'assay', absolute: true, params: [id: assayId]).toString()
        final String assaysHref = grailsLinkGenerator.link(mapping: 'assays', absolute: true).toString()

        markupBuilder.link(rel: 'edit', href: "${assayHref}", type: "${ASSAY_MEDIA_TYPE}")
        markupBuilder.link(rel: 'self', href: "${assayHref}", type: "${ASSAY_MEDIA_TYPE}")
        markupBuilder.link(rel: 'up', href: "${assaysHref}", type: "${ASSAYS_MEDIA_TYPE}")

    }
    //TODO: Waiting on updated plugin to test the following
    protected void generateMeasures(final MarkupBuilder markupBuilder, final BigDecimal assayId) {
        final Sql sql = new Sql(dataSource)

        markupBuilder.measures() {
            sql.eachRow('SELECT MEASURE_ID,MEASURE_CONTEXT_ID,RESULT_TYPE_ID,ENTRY_UNIT,PARENT_MEASURE_ID FROM MEASURE WHERE ASSAY_ID=' + assayId) { row ->
                final MeasureDTO measureDTO = new MeasureDTO(row.MEASURE_ID, row.MEASURE_CONTEXT_ID, row.RESULT_TYPE_ID, row.PARENT_MEASURE_ID, row.ENTRY_UNIT)
                generateMeasure(markupBuilder, measureDTO)
            }
        }
    }

    public void generateAssayDocuments(
            final MarkupBuilder markupBuilder,
            final BigDecimal assayId) {
        final Sql sql = new Sql(dataSource)

        markupBuilder.assayDocuments() {
            sql.eachRow('SELECT ASSAY_DOCUMENT_ID,DOCUMENT_NAME,DOCUMENT_TYPE FROM ASSAY_DOCUMENT WHERE ASSAY_ID=' + assayId) { documentRow ->
                generateAssayDocument(markupBuilder, new AssayDocumentDTO(documentRow.DOCUMENT_NAME, documentRow.DOCUMENT_TYPE, documentRow.ASSAY_DOCUMENT_ID, null))
            }
        }
    }


    protected void generateAssay(
            final MarkupBuilder markupBuilder,
            final AssayDTO assayDTO) {

        Map<String, String> attributes = [:]
        attributes.put('assayId', assayDTO.assayId.toString())
        attributes.put('readyForExtraction', assayDTO.readyForExtraction)

        if (assayDTO.assayVersion) {
            attributes.put('assayVersion', assayDTO.assayVersion)
        }
        if (assayDTO.assayStatus) {
            attributes.put('status', assayDTO.assayStatus)
        }

        markupBuilder.assay(attributes) {
            if (assayDTO.assayName) {
                assayName(assayDTO.assayName)
            }

            if (assayDTO.designedBy) {
                designedBy(assayDTO.designedBy)
            }

            generateExternalAssays(markupBuilder, assayDTO.assayId)
            generateMeasures(markupBuilder, assayDTO.assayId)
            generateMeasureContexts(markupBuilder, assayDTO.assayId)
            generateMeasureContextItems(markupBuilder, assayDTO.assayId)
            generateAssayDocuments(markupBuilder, assayDTO.assayId)
            generateLinksForAssay(markupBuilder,assayDTO.assayId)
        }
    }
    /**
     *
     * @param markupBuilder
     * @param assayId
     */
    public void generateAssay(
            final MarkupBuilder markupBuilder,
            final BigDecimal assayId) {
        final Sql sql = new Sql(dataSource)

        sql.eachRow('SELECT ASSAY_NAME,ASSAY_STATUS,ASSAY_VERSION,DESIGNED_BY,READY_FOR_EXTRACTION FROM ASSAY WHERE ASSAY_ID=' + assayId) { assayRow ->
            generateAssay(
                    markupBuilder,
                    new AssayDTO(
                            assayId,
                            assayRow.ASSAY_VERSION,
                            assayRow.ASSAY_STATUS,
                            assayRow.ASSAY_NAME,
                            assayRow.DESIGNED_BY,
                            assayRow.READY_FOR_EXTRACTION
                    )
            )
        }
    }
}
class AssayDocumentDTO {
    final String documentName
    String documentType
    final BigDecimal assayDocumentId
    final String documentContent
    /**
     *
     * @param documentName
     * @param documentType
     * @param assayDocumentId
     * @param documentContent
     */
    AssayDocumentDTO(String documentName, String documentType, BigDecimal assayDocumentId, String documentContent) {
        this.documentName = documentName
        this.documentType = documentType
        this.assayDocumentId = assayDocumentId
        this.documentContent = documentContent
    }
}
class ExternalSystemDTO {
    final String systemName
    final String systemOwner
    final String systemUrl
    final String externalAssayId

    ExternalSystemDTO(final String systemName,
                      final String systemOwner,
                      final String systemUrl,
                      final String externalAssayId) {
        this.systemName = systemName
        this.systemOwner = systemOwner
        this.systemUrl = systemUrl
        this.externalAssayId = externalAssayId
    }

}
class MeasureContextItemDTO {
    final BigDecimal measureContextItemId
    final BigDecimal groupMeasureContextItemId
    final BigDecimal measureContextId
    final BigDecimal attributeId
    final BigDecimal valueId
    final BigDecimal valueNum
    final BigDecimal valueMin
    final BigDecimal valueMax
    final String valueDisplay
    final String qualifier
    final String attributeType

    MeasureContextItemDTO(
            final BigDecimal measureContextItemId,
            final BigDecimal groupMeasureContextItemId,
            final BigDecimal measureContextId,
            final BigDecimal attributeId,
            final BigDecimal valueId,
            final BigDecimal valueNum,
            final BigDecimal valueMin,
            final BigDecimal valueMax,
            final String valueDisplay,
            final String qualifier,
            final String attributeType) {

        this.measureContextItemId = measureContextItemId
        this.groupMeasureContextItemId = groupMeasureContextItemId
        this.measureContextId = measureContextId
        this.attributeId = attributeId
        this.valueId = valueId
        this.valueNum = valueNum
        this.valueMin = valueMin
        this.valueMax = valueMax
        this.valueDisplay = valueDisplay
        this.qualifier = qualifier
        this.attributeType = attributeType
    }

}
class MeasureDTO {
    final BigDecimal measureId
    final BigDecimal measureContextId
    final BigDecimal resultTypeId
    final BigDecimal parentMeasureId
    final String entryUnit

    MeasureDTO(final BigDecimal measureId,
               final BigDecimal measureContextId,
               final BigDecimal resultTypeId,
               final BigDecimal parentMeasureId,
               final String entryUnit) {
        this.measureId = measureId
        this.measureContextId = measureContextId
        this.resultTypeId = resultTypeId
        this.parentMeasureId = parentMeasureId
        this.entryUnit = entryUnit
    }
}
class AssayDTO {
    final BigDecimal assayId
    final String assayVersion
    final String assayStatus
    final String assayName
    final String designedBy
    final String readyForExtraction

    AssayDTO(
            final BigDecimal assayId,
            final String assayVersion,
            final String assayStatus,
            final String assayName,
            final String designedBy,
            final String readyForExtraction) {
        this.assayId = assayId
        this.assayVersion = assayVersion
        this.assayStatus = assayStatus
        this.assayName = assayName
        this.designedBy = designedBy
        this.readyForExtraction = readyForExtraction
    }
}