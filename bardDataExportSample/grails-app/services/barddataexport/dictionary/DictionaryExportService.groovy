package barddataexport.dictionary

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.sql.DataSource

class DictionaryExportService {
    DataSource dataSource
    GrailsApplication grailsApplication
    LinkGenerator grailsLinkGenerator

    protected void generateSingleDescriptor(final MarkupBuilder xml, final DescriptorDTO descriptorDTO) {
        if (descriptorDTO.elementStatus) {
            xml.elementStatus(descriptorDTO.elementStatus)
        }
        if (descriptorDTO.label) {
            xml.label(descriptorDTO.label)
        }
        if (descriptorDTO.description) {
            xml.description(descriptorDTO.description)
        }
        if (descriptorDTO.synonyms) {
            xml.synonyms(descriptorDTO.synonyms)
        }
    }

    protected void generateDescriptor(final MarkupBuilder xml, final DescriptorDTO descriptorDTO, final DescriptorType descriptorType) {
        def attributes = [:]
        attributes.put('descriptorId', descriptorDTO.descriptorId)

        if (descriptorDTO.parentDescriptorId || descriptorDTO.parentDescriptorId.toString().isInteger()) {
            attributes.put('parentDescriptorId', descriptorDTO.parentDescriptorId)
        }
        if (descriptorDTO.elementId || descriptorDTO.elementId.toString().isInteger()) {
            attributes.put('elementId', descriptorDTO.elementId)
        }
        if (descriptorDTO.abbreviation) {
            attributes.put('abbreviation', descriptorDTO.abbreviation)
        }
        if (descriptorDTO.externalUrl) {
            attributes.put('externalUrl', descriptorDTO.externalUrl)
        }
        if (descriptorDTO.unit) {
            attributes.put('unit', descriptorDTO.unit)
        }
        switch (descriptorType) {
            case DescriptorType.BIOLOGY_DESCRIPTOR:
                xml.biologyDescriptor(attributes) {
                    generateSingleDescriptor(xml, descriptorDTO)
                }
                break
            case DescriptorType.ASSAY_DESCRIPTOR:
                xml.assayDescriptor(attributes) {
                    generateSingleDescriptor(xml, descriptorDTO)
                }
                break
            case DescriptorType.INSTANCE_DESCRIPTOR:
                xml.instanceDescriptor(attributes) {
                    generateSingleDescriptor(xml, descriptorDTO)
                }
                break
            default:
                throw new RuntimeException("Unknown Descriptor Type ${descriptorType}")
        }
    }

    protected void generateDescriptors(final Sql sql, final MarkupBuilder xml, final DescriptorType descriptorType) {

        final String selectQuery = "SELECT NODE_ID,PARENT_NODE_ID,ELEMENT_ID,LABEL,DESCRIPTION,ABBREVIATION,SYNONYMS,EXTERNAL_URL,UNIT,ELEMENT_STATUS FROM ${descriptorType}"

        sql.eachRow(selectQuery) { descriptionRow ->
            final DescriptorDTO descriptorDTO =
                new DescriptorDTO(
                        descriptionRow.PARENT_NODE_ID,
                        descriptionRow.NODE_ID,
                        descriptionRow.ELEMENT_ID,
                        descriptionRow.LABEL,
                        descriptionRow.DESCRIPTION,
                        descriptionRow.ABBREVIATION,
                        descriptionRow.SYNONYMS,
                        descriptionRow.EXTERNAL_URL,
                        descriptionRow.UNIT,
                        descriptionRow.ELEMENT_STATUS
                )
            generateDescriptor(xml, descriptorDTO, descriptorType)
        }
    }

    public void generateDescriptors(final MarkupBuilder xml, final DescriptorType descriptorType) {
        final Sql sql = new Sql(dataSource)

        switch (descriptorType) {
            case DescriptorType.BIOLOGY_DESCRIPTOR:
                xml.biologyDescriptors() {
                    generateDescriptors(sql, xml, descriptorType)
                }
                break
            case DescriptorType.ASSAY_DESCRIPTOR:
                xml.assayDescriptors() {
                    generateDescriptors(sql, xml, descriptorType)
                }
                break
            case DescriptorType.INSTANCE_DESCRIPTOR:
                xml.instanceDescriptors() {
                    generateDescriptors(sql, xml, descriptorType)
                }
                break
            default:
                throw new RuntimeException("Unsupported Descriptor Type ${descriptorType}")
        }
    }

/**
 *
 * @param xml
 * @param resultTypeDTO
 */
    protected void generateResultType(final MarkupBuilder xml, final ResultTypeDTO resultTypeDTO) {
        def attributes = [:]

        if (resultTypeDTO.resultTypeId || resultTypeDTO.resultTypeId.toString().isInteger()) {
            attributes.put("resultTypeId", resultTypeDTO.resultTypeId)
        }


        if (resultTypeDTO.parentResultTypeId || resultTypeDTO.parentResultTypeId.toString().isInteger()) {
            attributes.put("parentResultTypeId", resultTypeDTO.parentResultTypeId)
        }
        if (resultTypeDTO.abbreviation) {
            attributes.put("abbreviation", resultTypeDTO.abbreviation)
        }

        if (resultTypeDTO.baseUnit) {
            attributes.put("baseUnit", resultTypeDTO.baseUnit)
        }
        if (resultTypeDTO.status) {
            attributes.put("resultTypeStatus", resultTypeDTO.status)
        }
        xml.resultType(attributes) {

            if (resultTypeDTO.resultTypeName) {
                resultTypeName(resultTypeDTO.resultTypeName)
            }
            if (resultTypeDTO.description) {
                description(resultTypeDTO.description)
            }
            if (resultTypeDTO.synonyms) {
                synonyms(resultTypeDTO.synonyms)
            }
        }
    }
/**
 * Generate stages
 * @param sql
 * @param xml
 */
    protected void generateStages(final Sql sql, final MarkupBuilder xml) {
        xml.stages() {
            sql.eachRow('SELECT STAGE_ID FROM STAGE') { row ->
                generateStage(xml, row.STAGE_ID)
            }
        }
    }
/**
 * Generate stages
 * @param xml
 */
    public void generateStage(final MarkupBuilder xml, final BigDecimal stageId) {
        final Sql sql = new Sql(dataSource)
        sql.eachRow('SELECT STAGE_ID, NODE_ID, PARENT_NODE_ID,STAGE,DESCRIPTION, STAGE_STATUS FROM STAGE WHERE STAGE_ID=' + stageId) { row ->

            final BigDecimal parentNodeId = row.PARENT_NODE_ID
            BigDecimal stageParentId = null
            sql.eachRow('SELECT STAGE_ID FROM STAGE WHERE NODE_ID=' + parentNodeId) { parentStageRow ->
                stageParentId = parentStageRow.STAGE_ID
            }
            generateStage(xml, row.STAGE_ID, stageParentId, row.STAGE, row.DESCRIPTION, row.STAGE_STATUS)
        }
    }
/**
 * Generate a single stage
 * @param xml
 * @param stageId
 * @param nodeId
 * @param parentNodeId
 * @param stage
 * @param description
 */
    protected void generateStage(final MarkupBuilder xml, final BigDecimal stageId, final BigDecimal parentStageId, final String stage,
                                 final String descriptionText, final String stageStatus) {
        def attributes = [:]

        if (stageId || stageId.toString().isInteger()) {
            attributes.put('stageId', stageId)
        }
        if (stageStatus) {
            attributes.put('stageStatus', stageStatus)
        }
        if (parentStageId || parentStageId.toString().isInteger()) {
            attributes.put('parentStageId', parentStageId)
        }
        xml.stage(attributes) {
            if (stage) {
                stageName(stage)
            }
            if (descriptionText) {
                description(descriptionText)
            }
        }
    }

    public void generateResultType(final Sql sql, final MarkupBuilder xml, final BigDecimal resultTypeId) {
        // final Sql sql = new Sql(dataSource)
        sql.eachRow('SELECT NODE_ID,PARENT_NODE_ID,RESULT_TYPE_ID,RESULT_TYPE_NAME,DESCRIPTION,ABBREVIATION,SYNONYMS,BASE_UNIT,RESULT_TYPE_STATUS FROM RESULT_TYPE WHERE RESULT_TYPE_ID=' + resultTypeId) { row ->

            final BigDecimal parentNodeId = row.PARENT_NODE_ID
            BigDecimal resultTypeParentId = null
            sql.eachRow('select RESULT_TYPE_ID FROM RESULT_TYPE WHERE NODE_ID=' + parentNodeId) { parentResultTypeRow ->
                resultTypeParentId = parentResultTypeRow.RESULT_TYPE_ID
            }
            final ResultTypeDTO resultTypeDTO =
                new ResultTypeDTO(
                        resultTypeParentId,
                        row.RESULT_TYPE_ID,
                        row.RESULT_TYPE_NAME,
                        row.DESCRIPTION,
                        row.ABBREVIATION,
                        row.SYNONYMS,
                        row.BASE_UNIT,
                        row.RESULT_TYPE_STATUS
                )
            generateResultType(xml, resultTypeDTO)
        }

    }

    public void generateResultType(final MarkupBuilder xml, final BigDecimal resultTypeId) {
        final Sql sql = new Sql(dataSource)
        generateResultType(sql, xml, resultTypeId)
    }
/**
 * Generate Result Types
 * @return all the result types in the schema
 */
    protected void generateResultTypes(final Sql sql, final MarkupBuilder xml) {

        xml.resultTypes() {
            sql.eachRow('SELECT RESULT_TYPE_ID FROM RESULT_TYPE') { row ->
                final BigDecimal resultTypeId = row.RESULT_TYPE_ID
                generateResultType(sql, xml, resultTypeId)
            }
        }
    }

    protected void generateElements(final Sql sql, final MarkupBuilder xml) {

        xml.elements() {
            sql.eachRow('SELECT ELEMENT_ID FROM ELEMENT') { elementRow ->
                final BigDecimal elementId = elementRow.ELEMENT_ID
                generateElementWithElementId(xml, elementId)
            }
        }
    }

    public void generateElementWithElementId(final MarkupBuilder xml, final BigDecimal elementId) {
        final Sql sql = new Sql(dataSource)
        sql.eachRow('SELECT ELEMENT_ID, LABEL,DESCRIPTION,ABBREVIATION,SYNONYMS, EXTERNAL_URL,UNIT, ELEMENT_STATUS, READY_FOR_EXTRACTION FROM ELEMENT WHERE ELEMENT_ID=' + elementId) { elementRow ->


            final ElementDTO elementDTO =
                new ElementDTO(
                        elementRow.ELEMENT_ID,
                        elementRow.LABEL,
                        elementRow.DESCRIPTION,
                        elementRow.ABBREVIATION,
                        elementRow.SYNONYMS,
                        elementRow.EXTERNAL_URL,
                        elementRow.UNIT,
                        elementRow.ELEMENT_STATUS,
                        elementRow.READY_FOR_EXTRACTION)
            generateElement(xml, elementDTO)
        }
    }

    public void generateElement(final MarkupBuilder xml, final ElementDTO elementDTO) {
        def attributes = [:]


        attributes.put('elementId', elementDTO.elementId)
        attributes.put('readyForExtraction', elementDTO.readyForExtraction)
        if (elementDTO.elementStatus) {
            attributes.put('elementStatus', elementDTO.elementStatus)
        }
        if (elementDTO.abbreviation) {
            attributes.put('abbreviation', elementDTO.abbreviation)
        }

        if (elementDTO.unit) {
            attributes.put('unit', elementDTO.unit)
        }
        xml.element(attributes) {
            if (elementDTO.label) {
                label(elementDTO.label)
            }
            if (elementDTO.description) {
                description(elementDTO.description)
            }
            if (elementDTO.synonyms) {
                synonyms(elementDTO.synonyms)
            }
            if (elementDTO.externalUrl) {
                externalUrl(elementDTO.externalUrl)
            }

            final String ELEMENT_MEDIA_TYPE = grailsApplication.config.bard.data.export.dictionary.element.xml
            final String elementHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: elementDTO.elementId]).toString()
            link(rel: 'edit', href: "${elementHref}", type: "${ELEMENT_MEDIA_TYPE}")

        }
    }

    protected void generateElementHierarchies(final Sql sql, final MarkupBuilder xml) {
        xml.elementHierarchies() {
            sql.eachRow('SELECT ELEMENT_HIERARCHY_ID,PARENT_ELEMENT_ID,CHILD_ELEMENT_ID, RELATIONSHIP_TYPE FROM ELEMENT_HIERARCHY') { elementHierarchyRow ->
                generateElementHierarchy(xml, elementHierarchyRow.ELEMENT_HIERARCHY_ID,
                        elementHierarchyRow.PARENT_ELEMENT_ID, elementHierarchyRow.CHILD_ELEMENT_ID, elementHierarchyRow.RELATIONSHIP_TYPE)
            }
        }
    }

    protected void generateElementHierarchy(final MarkupBuilder xml,
                                            final def elementHierarchyId,
                                            final def parentElementId,
                                            final def childElementId,
                                            final def relationshipTypeValue) {
        def attributes = [:]
        if (elementHierarchyId || elementHierarchyId.toString().isInteger()) {
            attributes.put('elementHierarchyId', elementHierarchyId)
        }
        if (parentElementId || parentElementId.toString().isInteger()) {
            attributes.put('parentElementId', parentElementId)
        }
        if (childElementId || childElementId.toString().isInteger()) {
            attributes.put('childElementId', childElementId)
        }
        xml.elementHierarchy(attributes) {
            if (relationshipTypeValue) {
                relationshipType(relationshipTypeValue)
            }
        }
    }

    protected void generateLabs(final Sql sql, MarkupBuilder xml) {
        xml.laboratories() {
            sql.eachRow('select LABORATORY_ID,LABORATORY, DESCRIPTION, PARENT_NODE_ID,LABORATORY_STATUS  FROM LABORATORY') { labRow ->
                BigDecimal parentLabId = null
                sql.eachRow('SELECT LABORATORY_ID FROM LABORATORY WHERE NODE_ID=' + labRow.PARENT_NODE_ID) { parentLaboratoryRow ->
                    parentLabId = parentLaboratoryRow.LABORATORY_ID
                }
                final LaboratoryDTO laboratoryDTO = new LaboratoryDTO(labRow.LABORATORY_ID, parentLabId, labRow.DESCRIPTION, labRow.LABORATORY, labRow.LABORATORY_STATUS)
                generateLab(xml, laboratoryDTO)
            }
        }
    }

    protected void generateLab(final MarkupBuilder xml, final LaboratoryDTO laboratoryDTO) {
        def attributes = [:]
        attributes.put('laboratoryId', laboratoryDTO.labId)

        if (laboratoryDTO.parentLabId || laboratoryDTO.parentLabId.toString().isInteger()) {
            attributes.put('parentLaboratoryId', laboratoryDTO.parentLabId)
        }
        if (laboratoryDTO.laboratoryStatus) {
            attributes.put('laboratoryStatus', laboratoryDTO.laboratoryStatus)
        }
        xml.laboratory(attributes) {
            laboratory(laboratoryDTO.labName)
            description(laboratoryDTO.description)
        }
    }
    /**
     *
     * @param sql
     * @param xml
     */
    protected void generateUnits(final Sql sql ,final MarkupBuilder xml) {
        xml.units() {
             sql.eachRow('SELECT UNIT_ID, NODE_ID,PARENT_NODE_ID,UNIT,DESCRIPTION FROM UNIT') { row ->

                final BigDecimal parentNodeId = row.PARENT_NODE_ID
                BigDecimal parentUnitId = null
                if (parentNodeId) {
                    sql.eachRow('SELECT UNIT_ID FROM UNIT WHERE NODE_ID=' + parentNodeId) { parentUnitRow ->
                        parentUnitId = parentUnitRow.UNIT_ID
                    }
                }
                generateUnit(xml, row.UNIT_ID, parentUnitId, row.UNIT, row.DESCRIPTION)
            }
        }
    }

    /**
     *
     * @param xml
     * @param unitId
     * @param parentUnitId
     * @param unit
     * @param descriptionText
     */
    protected void generateUnit(
            final MarkupBuilder xml,
            final BigDecimal unitId,
            final BigDecimal parentUnitId,
            final String unit,
            final String descriptionText
    ) {
        def attributes = [:]

        if (unitId || unitId.toString().isInteger()) {
            attributes.put('unitId', unitId)
        }

        if (parentUnitId || parentUnitId.toString().isInteger()) {
            attributes.put('parentUnitId', parentUnitId)
        }
        if (unit) {
            attributes.put('unit', unit)
        }
        xml.unit(attributes) {

            if (descriptionText) {
                description(descriptionText)
            }
        }
    }

/**
 * Root node for generating the entire cap
 * @return cap as XML
 */
    public void generateDictionary(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)
        generateDictionary(sql, xml)
    }
/**
 * Root node for generating the entire cap
 * @return cap as XML
 */
    protected void generateDictionary(final Sql sql, final MarkupBuilder xml) {
        xml.dictionary() {
            generateElements(sql, xml)
            generateElementHierarchies(sql, xml)
            generateResultTypes(sql, xml)
            generateStages(sql, xml)
            generateDescriptors(xml, DescriptorType.BIOLOGY_DESCRIPTOR)
            generateDescriptors(xml, DescriptorType.ASSAY_DESCRIPTOR)
            generateDescriptors(xml, DescriptorType.INSTANCE_DESCRIPTOR)
            generateLabs(sql, xml)
            generateUnits(sql,xml)
        }
    }

}
class LaboratoryDTO {
    final BigDecimal labId
    final BigDecimal parentLabId
    final String description
    final String labName
    final String laboratoryStatus

    LaboratoryDTO(final BigDecimal labId,
                  final BigDecimal parentLabId,
                  final String description,
                  final String labName, final String laboratoryStatus) {
        this.labId = labId
        this.parentLabId = parentLabId
        this.description = description
        this.labName = labName
        this.laboratoryStatus = laboratoryStatus
    }

}
class ElementDTO {
    final BigDecimal elementId
    final String label
    final String description
    final String abbreviation
    final String synonyms
    final String externalUrl
    final String unit
    final String elementStatus
    final String readyForExtraction

    ElementDTO(final BigDecimal elementId,
               final String label,
               final String description,
               final String abbreviation,
               final String synonyms,
               final String externalUrl,
               final String unit,
               final String elementStatus,
               final String readyForExtraction) {
        this.readyForExtraction = readyForExtraction
        this.elementId = elementId
        this.label = label
        this.description = description
        this.abbreviation = abbreviation
        this.synonyms = synonyms
        this.externalUrl = externalUrl
        this.unit = unit
        this.elementStatus = elementStatus
    }


}
class ResultTypeDTO {
    final BigDecimal parentResultTypeId
    final BigDecimal resultTypeId
    final String resultTypeName
    final String description
    final String abbreviation
    final String synonyms
    final String baseUnit
    final String status

    ResultTypeDTO(
            final BigDecimal parentResultTypeId,
            final BigDecimal resultTypeId,
            final String resultTypeName,
            final String description,
            final String abbreviation,
            final String synonyms,
            final String baseUnit,
            final String status) {

        this.parentResultTypeId = parentResultTypeId
        this.resultTypeId = resultTypeId
        this.resultTypeName = resultTypeName
        this.description = description
        this.abbreviation = abbreviation
        this.synonyms = synonyms
        this.baseUnit = baseUnit
        this.status = status
    }

}
class DescriptorDTO {
    final BigDecimal parentDescriptorId
    final BigDecimal descriptorId
    final BigDecimal elementId
    final String label
    final String description
    final String abbreviation
    final String synonyms
    final String externalUrl
    final String unit
    final String elementStatus

    DescriptorDTO(
            final BigDecimal parentDescriptorId,
            final BigDecimal descriptorId,
            final BigDecimal elementId,
            final String label,
            final String description,
            final String abbreviation,
            final String synonyms,
            final String externalUrl,
            final String unit,
            final String elementStatus
    ) {

        this.parentDescriptorId = parentDescriptorId
        this.descriptorId = descriptorId
        this.elementId = elementId
        this.label = label

        this.description = description
        this.abbreviation = abbreviation

        this.synonyms = synonyms
        this.externalUrl = externalUrl
        this.unit = unit
        this.elementStatus = elementStatus
    }
}
public enum DescriptorType {
    //name must match name of database table
    ASSAY_DESCRIPTOR, BIOLOGY_DESCRIPTOR, INSTANCE_DESCRIPTOR
}