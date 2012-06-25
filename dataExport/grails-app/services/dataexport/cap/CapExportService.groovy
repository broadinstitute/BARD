package dataexport.cap

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.sql.DataSource

class CapExportService {
    DataSource dataSource
    LinkGenerator grailsLinkGenerator
    GrailsApplication grailsApplication

    /**
     * Generate a measure contexts
     * @param sql
     * @param xml
     */
    protected void generateMeasureContexts(final Sql sql, final MarkupBuilder xml, final BigDecimal assayId) {

        xml.measureContexts() {
            sql.eachRow('SELECT MEASURE_CONTEXT_ID,CONTEXT_NAME FROM MEASURE_CONTEXT WHERE ASSAY_ID=' + assayId) { row ->
                generateMeasureContext(xml, row.MEASURE_CONTEXT_ID, row.CONTEXT_NAME)
            }
        }
    }

    protected void generateMeasureContext(final MarkupBuilder xml, final BigDecimal measureContextId, final String contextNameText) {

        xml.measureContext(measureContextId: measureContextId) {
            contextName(contextNameText)
        }
    }

    protected void generateMeasures(final Sql sql, final MarkupBuilder xml, final BigDecimal assayId) {

        xml.measures() {
            sql.eachRow('SELECT MEASURE_ID,MEASURE_CONTEXT_ID,RESULT_TYPE_ID,ENTRY_UNIT,PARENT_MEASURE_ID FROM MEASURE WHERE ASSAY_ID=' + assayId) { row ->
                final MeasureDTO measureDTO = new MeasureDTO(row.MEASURE_ID, row.MEASURE_CONTEXT_ID, row.RESULT_TYPE_ID, row.PARENT_MEASURE_ID, row.ENTRY_UNIT)
                generateMeasure(xml, measureDTO)
            }
        }
    }

    protected void generateMeasure(final MarkupBuilder xml, final MeasureDTO measureDTO) {

        def attributes = [:]

        if (measureDTO.measureId || measureDTO.measureId.toString().isInteger()) {
            attributes.put('measureId', measureDTO.measureId)
        }


        if (measureDTO.measureContextId || measureDTO.measureContextId.toString().isInteger()) {
            attributes.put('measureContextRef', measureDTO.measureContextId)
        }
        if (measureDTO.measureId || measureDTO.measureId.toString().isInteger()) {
            attributes.put('measureRef', measureDTO.measureId)
        }
        if (measureDTO.entryUnit) {
            attributes.put('entryUnit', measureDTO.entryUnit)
        }
        xml.measure(attributes) {
            if (measureDTO.resultTypeId || measureDTO.resultTypeId.toString().isInteger()) {
                resultTypeRef() {
                    final String RESULT_TYPE_MEDIA_TYPE = grailsApplication.config.bard.data.export.dictionary.resultType.xml
                    final String resultTypeHref = grailsLinkGenerator.link(mapping: 'resultType', absolute: true, params: [id: measureDTO.resultTypeId]).toString()

                    link(rel: 'related', href: "${resultTypeHref}", type: "${RESULT_TYPE_MEDIA_TYPE}")
                }
            }
        }
    }

    protected void generateMeasureContextItems(final Sql sql, final MarkupBuilder xml, final BigDecimal assayId) {

        xml.measureContextItems() {
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
                generateMeasureContextItem(xml, dto)
            }
        }

    }

    protected void generateMeasureContextItem(final MarkupBuilder xml, final MeasureContextItemDTO dto) {
        def attributes = [:]

        if (dto.measureContextItemId || dto.measureContextItemId.toString().isInteger()) {
            attributes.put('measureContextItemId', dto.measureContextItemId)
        }
        if (dto.groupMeasureContextItemId || dto.groupMeasureContextItemId.toString().isInteger()) {
            attributes.put('measureContextItemRef', dto.groupMeasureContextItemId)
        }
        if (dto.measureContextId || dto.measureContextId.toString().isInteger()) {
            attributes.put('measureContextRef', dto.measureContextId)
        }
        if (dto.qualifier) {
            attributes.put('qualifier', dto.qualifier)
        }
        attributes.put('attributeType', dto.attributeType)

        if (dto.valueDisplay) {
            attributes.put('valueDisplay', dto.valueDisplay)
        }
        if (dto.valueNum || dto.valueNum.toString().isInteger()) {
            attributes.put('valueNum', dto.valueNum)
        }
        if (dto.valueMin || dto.valueMin.toString().isInteger()) {
            attributes.put('valueMin', dto.valueMin)
        }
        if (dto.valueMax || dto.valueMax.toString().isInteger()) {
            attributes.put('valueMax', dto.valueMax)
        }


        xml.measureContextItem(attributes) {
            final String ELEMENT_MEDIA_TYPE = grailsApplication.config.bard.data.export.dictionary.element.xml
            if (dto.valueId || dto.valueId.toString().isInteger()) {
                valueId() {
                    final String valueHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: dto.valueId]).toString()

                    link(rel: 'related', href: "${valueHref}", type: "${ELEMENT_MEDIA_TYPE}")
                }
            }
            if (dto.attributeId || dto.attributeId.toString().isInteger()) {
                final String attributeHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: dto.attributeId]).toString()

                attributeId() {
                    link(rel: 'related', href: "${attributeHref}", type: "${ELEMENT_MEDIA_TYPE}")
                }
            }
        }

    }

    protected void generateExternalAssays(final Sql sql,
                                          final MarkupBuilder xml, final BigDecimal assayId) {

        xml.externalAssays() {
            sql.eachRow('SELECT EXTERNAL_SYSTEM_ID,EXT_ASSAY_ID FROM EXTERNAL_ASSAY WHERE ASSAY_ID=' + assayId) { externalAssayRow ->
                generateExternalAssay(sql, xml, externalAssayRow.EXT_ASSAY_ID, externalAssayRow.EXTERNAL_SYSTEM_ID)
            }
        }
    }

    protected void generateExternalAssay(final Sql sql,
                                         final MarkupBuilder xml,
                                         final String externalAssayId,
                                         final BigDecimal externalSystemId) {


        xml.externalAssay(externalAssayId: externalAssayId) {
            if (externalSystemId) {
                sql.eachRow('SELECT SYSTEM_NAME,OWNER,SYSTEM_URL FROM EXTERNAL_SYSTEM WHERE EXTERNAL_SYSTEM_ID=' + externalSystemId) { externalSystemRow ->
                    def attributes = [:]

                    if (externalSystemRow.SYSTEM_URL) {
                        attributes.put('systemUrl', externalSystemRow.SYSTEM_URL + externalAssayId)
                    }
                    externalSystem(attributes) {
                        if (externalSystemRow.SYSTEM_NAME) {
                            systemName(externalSystemRow.SYSTEM_NAME)
                        }
                        if (externalSystemRow.OWNER) {
                            owner(externalSystemRow.OWNER)
                        }
                    }
                }
            }
        }
    }



    protected void generateAssayDocuments(final Sql sql,
                                          final MarkupBuilder xml,
                                          final BigDecimal assayId) {
        xml.assayDocuments() {
            sql.eachRow('SELECT ASSAY_DOCUMENT_ID,DOCUMENT_NAME FROM ASSAY_DOCUMENT WHERE ASSAY_ID=' + assayId) { documentRow ->
                generateAssayDocument(xml, documentRow.DOCUMENT_NAME, documentRow.ASSAY_DOCUMENT_ID)
            }
        }
    }
    /**
     * Should we expose this as a service?
     * @param xml
     * @param documentNameText
     * @param assayDocumentId
     */
    public void generateAssayDocument(
            final MarkupBuilder xml, final String documentNameText, final BigDecimal assayDocumentId) {
        final String assayDocumentUri = grailsLinkGenerator.link(mapping: 'assayDocument', absolute: true, params: [id: assayDocumentId]).toString()
        xml.assayDocument(uri: assayDocumentUri) {
            documentName(documentNameText)
        }
    }


    protected void generateProjectAssay(final Sql sql,
                                        final MarkupBuilder xml,
                                        final ProjectAssayDTO projectAssayDTO
    ) {

        def attributes = [:]
        if (projectAssayDTO.relatedAssayId || projectAssayDTO.relatedAssayId.toString().isInteger()) {
            attributes.put('relatedAssayRef', projectAssayDTO.relatedAssayId)
        }
        if (projectAssayDTO.sequenceNumber || projectAssayDTO.sequenceNumber.toString().isInteger()) {
            attributes.put('sequenceNumber', projectAssayDTO.sequenceNumber)
        }
        if (projectAssayDTO.promotionThreshold || projectAssayDTO.promotionThreshold.toString().isInteger()) {
            attributes.put('promotionThreshold', projectAssayDTO.promotionThreshold)
        }
        xml.projectAssay(
                attributes
        ) {
            if (projectAssayDTO.promotionCriteria) {
                promotionCriteria(projectAssayDTO.promotionCriteria)
            }
            if (projectAssayDTO.stageId) {
                stage() {
                    final String STAGE_MEDIA_TYPE = grailsApplication.config.bard.data.export.dictionary.stage.xml
                    final String stageHref = grailsLinkGenerator.link(mapping: 'stage', absolute: true, params: [id: projectAssayDTO.stageId]).toString()

                    link(rel: 'related', href: "${stageHref}", type: "${STAGE_MEDIA_TYPE}")
                }
            }

            sql.eachRow('SELECT ASSAY_NAME, ASSAY_STATUS, ASSAY_VERSION, DESCRIPTION, DESIGNED_BY,READY_FOR_EXTRACTION FROM ASSAY WHERE ASSAY_ID=' + projectAssayDTO.assayId) { assayRow ->
                generateAssay(sql, xml, new AssayDTO(projectAssayDTO.projectId, projectAssayDTO.assayId,
                        assayRow.ASSAY_VERSION, assayRow.ASSAY_STATUS, assayRow.ASSAY_NAME, assayRow.DESCRIPTION,
                        assayRow.DESIGNED_BY, assayRow.READY_FOR_EXTRACTION))
            }
        }
    }



    protected void generateProjectAssays(final Sql sql, final MarkupBuilder xml, final BigDecimal projectId) {
        xml.projectAssays() {
            sql.eachRow('SELECT PROJECT_ASSAY_ID,PROJECT_ID,ASSAY_ID,STAGE_ID,RELATED_ASSAY_ID,SEQUENCE_NO,PROMOTION_THRESHOLD,PROMOTION_CRITERIA FROM PROJECT_ASSAY WHERE PROJECT_ID=' + projectId) { projectAssayRow ->

                final ProjectAssayDTO projectAssayDTO =
                    new ProjectAssayDTO(
                            projectAssayRow.PROJECT_ID,
                            projectAssayRow.PROJECT_ASSAY_ID,
                            projectAssayRow.RELATED_ASSAY_ID,
                            projectAssayRow.ASSAY_ID,
                            projectAssayRow.STAGE_ID,
                            projectAssayRow.SEQUENCE_NO,
                            projectAssayRow.PROMOTION_THRESHOLD,
                            projectAssayRow.PROMOTION_CRITERIA)
                generateProjectAssay(sql,
                        xml, projectAssayDTO)
            }

        }

    }

    protected void generateProject(Sql sql, final MarkupBuilder xml, ProjectDTO projectDTO) {
        def attributes = [:]
        attributes.put('projectId', projectDTO.projectId)
        attributes.put('readyForExtraction', projectDTO.readyForExtraction)
        if (projectDTO.groupType) {
            attributes.put('groupType', projectDTO.groupType)
        }
        xml.project(
                attributes
        ) {
            if (projectDTO.projectName) {
                projectName(projectDTO.projectName)
            }
            if (projectDTO.description) {
                description(projectDTO.description)
            }
            generateProjectAssays(sql, xml, projectDTO.projectId)
            final String PROJECT_MEDIA_TYPE = grailsApplication.config.bard.data.export.cap.project.xml
            final String projectHref = grailsLinkGenerator.link(mapping: 'project', absolute: true, params: [id: projectDTO.projectId]).toString()
            link(rel: 'edit', href: "${projectHref}", type: "${PROJECT_MEDIA_TYPE}")

        }

    }



    protected void generateProjects(final Sql sql, final MarkupBuilder xml, final boolean onlyNewProjects) {

        xml.projects() {
            String selectStatement
            if (onlyNewProjects) {
                selectStatement = "SELECT PROJECT_ID, PROJECT_NAME, GROUP_TYPE, DESCRIPTION, READY_FOR_EXTRACTION FROM PROJECT WHERE READY_FOR_EXTRACTION='Ready'"
            }
            else {
                selectStatement = 'SELECT PROJECT_ID, PROJECT_NAME, GROUP_TYPE, DESCRIPTION, READY_FOR_EXTRACTION FROM PROJECT'
            }
            sql.eachRow(selectStatement) { row ->
                generateProject(sql, xml, new ProjectDTO(row.PROJECT_ID, row.GROUP_TYPE, row.PROJECT_NAME, row.DESCRIPTION,
                        row.READY_FOR_EXTRACTION))
            }
        }
    }
    /**
     * Root node for generating the entire cap
     * @return cap as XML
     */
    protected String generateCap(final Sql sql, final MarkupBuilder xml) {
        xml.cap() {
            generateProjects(sql, xml, false)
        }
    }

    // These are the methods called from the Controller

    public void generateProject(final MarkupBuilder xml, final BigDecimal projectId) {
        final Sql sql = new Sql(dataSource)
        final String selectStatement = 'SELECT PROJECT_ID, PROJECT_NAME, GROUP_TYPE, DESCRIPTION, READY_FOR_EXTRACTION FROM PROJECT WHERE PROJECT_ID=' + projectId

        sql.eachRow(selectStatement) { row ->
            generateProject(sql, xml,
                    new ProjectDTO(
                            row.PROJECT_ID,
                            row.GROUP_TYPE,
                            row.PROJECT_NAME,
                            row.DESCRIPTION,
                            row.READY_FOR_EXTRACTION
                    )
            )
        }
    }
    /**
     * Root node for generating the entire cap
     * @return cap as XML
     */
    public String generateCap(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)
        generateCap(sql, xml)
    }

    public void generateNewProjects(final MarkupBuilder xml) {
        Sql sql = new Sql(dataSource)
        generateProjects(sql, xml, true)
    }

    protected void generateAssay(final Sql sql,
                                 final MarkupBuilder xml,
                                 final AssayDTO assayDTO) {

        def attributes = [:]
        attributes.put('assayId', assayDTO.assayId)
        attributes.put('readyForExtraction', assayDTO.readyForExtraction)

        if (assayDTO.assayVersion) {
            attributes.put('assayVersion', assayDTO.assayVersion)
        }
        if (assayDTO.assayStatus) {
            attributes.put('status', assayDTO.assayStatus)
        }

        xml.assay(attributes) {
            if (assayDTO.assayName) {
                assayName(assayDTO.assayName)
            }

            if (assayDTO.description) {
                description(assayDTO.description)
            }
            if (assayDTO.designedBy) {
                designedBy(assayDTO.designedBy)
            }

            generateExternalAssays(sql, xml, assayDTO.assayId)
            generateMeasures(sql, xml, assayDTO.assayId)
            generateMeasureContexts(sql, xml, assayDTO.assayId)
            generateMeasureContextItems(sql, xml, assayDTO.assayId)
            generateAssayDocuments(sql, xml, assayDTO.assayId)

            final String PROJECT_MEDIA_TYPE = grailsApplication.config.bard.data.export.cap.project.xml
            final String projectHref = grailsLinkGenerator.link(mapping: 'project', absolute: true, params: [id: assayDTO.projectId]).toString()

            final String ASSAY_MEDIA_TYPE = grailsApplication.config.bard.data.export.cap.assay.xml
            final String assayHref = grailsLinkGenerator.link(mapping: 'assay', absolute: true, params: [id: assayDTO.assayId]).toString()
            link(rel: 'edit', href: "${assayHref}", type: "${ASSAY_MEDIA_TYPE}")
            link(rel: 'up', href: "${projectHref}", type: "${PROJECT_MEDIA_TYPE}")

        }
    }

    public void generateAssay(
            final MarkupBuilder xml,
            final BigDecimal assayId) {
        final Sql sql = new Sql(dataSource)

        sql.eachRow('SELECT ASSAY_NAME,ASSAY_STATUS,ASSAY_VERSION,DESCRIPTION,DESIGNED_BY FROM ASSAY WHERE ASSAY_ID=' + assayId) { assayRow ->
            BigDecimal projectId = null
            sql.eachRow('SELECT PROJECT_ID FROM PROJECT_ASSAY WHERE ASSAY_ID=' + assayId) { projectAssayRow ->
                projectId = projectAssayRow.PROJECT_ID
            }
            generateAssay(
                    sql, xml,
                    new AssayDTO(projectId,
                            assayId,
                            assayRow.ASSAY_VERSION,
                            assayRow.ASSAY_STATUS,
                            assayRow.ASSAY_NAME,
                            assayRow.DESCRIPTION,
                            assayRow.DESIGNED_BY,
                            assayRow.READY_FOR_EXTRACTION
                    )
            )
        }
    }
}
class ProjectDTO {
    final BigDecimal projectId
    final String groupType
    final String projectName
    final String description
    final String readyForExtraction

    ProjectDTO(final BigDecimal projectId, final String groupType, final String projectName, final String description, final String readyForExtraction) {
        this.projectId = projectId
        this.groupType = groupType
        this.projectName = projectName
        this.description = description
        this.readyForExtraction = readyForExtraction
    }
}
class ProjectAssayDTO {
    final BigDecimal projectAssayId
    final BigDecimal projectId
    final BigDecimal relatedAssayId
    final BigDecimal assayId
    final BigDecimal stageId
    final BigDecimal sequenceNumber
    final BigDecimal promotionThreshold
    final String promotionCriteria

    ProjectAssayDTO(
            final BigDecimal projectId,
            final BigDecimal projectAssayId,
            final BigDecimal relatedAssayId,
            final BigDecimal assayId,
            final BigDecimal stageId,
            final BigDecimal sequenceNumber,
            final BigDecimal promotionThreshold,
            final String promotionCriteria) {
        this.projectId = projectId
        this.projectAssayId = projectAssayId
        this.relatedAssayId = relatedAssayId
        this.assayId = assayId
        this.stageId = stageId
        this.sequenceNumber = sequenceNumber
        this.promotionThreshold = promotionThreshold
        this.promotionCriteria = promotionCriteria
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
    final BigDecimal projectId
    final String assayVersion
    final String assayStatus
    final String assayName
    final String description
    final String designedBy
    final String readyForExtraction

    AssayDTO(
            final BigDecimal projectId,
            final BigDecimal assayId,
            final String assayVersion,
            final String assayStatus,
            final String assayName,
            final String description,
            final String designedBy,
            final String readyForExtraction) {
        this.projectId = projectId
        this.assayId = assayId
        this.assayVersion = assayVersion
        this.assayStatus = assayStatus
        this.assayName = assayName
        this.description = description
        this.designedBy = designedBy
        this.readyForExtraction = readyForExtraction
    }
}