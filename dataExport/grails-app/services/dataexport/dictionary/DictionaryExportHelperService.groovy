package dataexport.dictionary

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.sql.DataSource

class DictionaryExportHelperService {
    LinkGenerator grailsLinkGenerator
    String elementMediaType
    DataSource dataSource

    DictionaryExportHelperService(final String elementMediaType) {
        this.elementMediaType = elementMediaType
    }
    /**
     * Root node for generating the entire dictionary
     *
     * @param xml
     */
    public void generateDictionary(final MarkupBuilder xml) {
        xml.dictionary() {
            generateElements(xml)
            generateElementHierarchies(xml)
            generateResultTypes(xml)
            generateStages(xml)
            generateDescriptors(xml, [DescriptorType.BIOLOGY_DESCRIPTOR, DescriptorType.ASSAY_DESCRIPTOR, DescriptorType.INSTANCE_DESCRIPTOR])
            generateLabs(xml)
            generateUnits(xml)
            generateUnitConversions(xml)
        }
    }
    /**
     * Root node for generating all the descriptors
     *
     * @param xml
     * @param descriptorType
     */
    public void generateDescriptors(final MarkupBuilder xml, final List<DescriptorType> descriptorTypes) {
        for (DescriptorType descriptorType in descriptorTypes) {
            switch (descriptorType) {
                case DescriptorType.BIOLOGY_DESCRIPTOR:
                    xml.biologyDescriptors() {
                        generateDescriptors(xml, descriptorType)
                    }
                    break
                case DescriptorType.ASSAY_DESCRIPTOR:
                    xml.assayDescriptors() {
                        generateDescriptors(xml, descriptorType)
                    }
                    break
                case DescriptorType.INSTANCE_DESCRIPTOR:
                    xml.instanceDescriptors() {
                        generateDescriptors(xml, descriptorType)
                    }
                    break
                default:
                    throw new RuntimeException("Unsupported Descriptor Type ${descriptorType}")
            }
        }
    }


    /**
     * Generate stages
     *
     * @param xml
     */
    public void generateStages(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)

        xml.stages() {
            sql.eachRow('SELECT STAGE_ID FROM STAGE') { row ->
                final BigDecimal stageId = row.STAGE_ID
                generateStage(xml, stageId)
            }
        }
    }
    /**
     * @param xml
     */
    public void generateLabs(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)

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
    /**
     * Generate stages
     *
     * @param xml
     * @param stageId
     */
    public void generateStage(final MarkupBuilder xml, final BigDecimal stageId) {
        final Sql sql = new Sql(dataSource)

        sql.eachRow('SELECT STAGE_ID, NODE_ID, PARENT_NODE_ID,STAGE,DESCRIPTION, STAGE_STATUS FROM STAGE WHERE STAGE_ID=' + stageId) { row ->

            final BigDecimal parentNodeId = row.PARENT_NODE_ID
            BigDecimal stageParentId = null
            sql.eachRow('SELECT STAGE_ID FROM STAGE WHERE NODE_ID=' + parentNodeId) { parentStageRow ->
                stageParentId = parentStageRow.STAGE_ID
            }
            final String stage = row.STAGE
            final String descriptionText = row.DESCRIPTION
            final String stageStatus = row.STAGE_STATUS
            generateStage(xml, new StageDTO(stageId, stageParentId, stage, descriptionText, stageStatus))
        }
    }
    /**
     * Root node for generating XML for a specific descriptor type (instanceDescriptor)
     *
     *
     * @param xml
     * @param descriptorType
     */
    public void generateDescriptors(final MarkupBuilder xml, final DescriptorType descriptorType) {
        final Sql sql = new Sql(this.dataSource)

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
    /**
     * Generate an element from an elementId
     * <element elementId=""></element>

     * @param xml
     * @param elementId
     */
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
    /**
     * Root Node for generating all elements

     * @param xml
     */
    public void generateElements(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)

        xml.elements() {
            sql.eachRow('SELECT ELEMENT_ID FROM ELEMENT') { elementRow ->
                final BigDecimal elementId = elementRow.ELEMENT_ID
                generateElementWithElementId(xml, elementId)
            }
        }
    }
    /**
     * Root node for generating element hierarchies

     * @param xml
     */
    public void generateElementHierarchies(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)

        xml.elementHierarchies() {
            sql.eachRow('SELECT ELEMENT_HIERARCHY_ID,PARENT_ELEMENT_ID,CHILD_ELEMENT_ID, RELATIONSHIP_TYPE FROM ELEMENT_HIERARCHY') { elementHierarchyRow ->
                generateElementHierarchy(xml, new ElementHierarchyDTO(elementHierarchyRow.PARENT_ELEMENT_ID, elementHierarchyRow.CHILD_ELEMENT_ID, elementHierarchyRow.RELATIONSHIP_TYPE))
            }
        }
    }

    /**
     *

     * @param xml
     * @param resultTypeId
     */
    public void generateResultType(final MarkupBuilder xml, final BigDecimal resultTypeId) {
        final Sql sql = new Sql(dataSource)

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
    /**
     * Generate Result Types

     * @param xml
     */
    public void generateResultTypes(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)

        xml.resultTypes() {
            sql.eachRow('SELECT RESULT_TYPE_ID FROM RESULT_TYPE') { row ->
                final BigDecimal resultTypeId = row.RESULT_TYPE_ID
                generateResultType(xml, resultTypeId)
            }
        }
    }


    public void generateUnitConversions(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)

        xml.unitConversions() {
            sql.eachRow('SELECT FROM_UNIT,TO_UNIT,MULTIPLIER,OFFSET,FORMULA FROM UNIT_CONVERSION') { row ->
                final UnitConversionDTO unitConversionDTO = new UnitConversionDTO(row.FROM_UNIT, row.TO_UNIT, row.FORMULA, row.OFFSET, row.MULTIPLIER)
                generateUnitConversion(xml, unitConversionDTO)
            }
        }
    }
    /**
     *

     * @param xml
     */
    public void generateUnits(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)

        xml.units() {
            sql.eachRow('SELECT UNIT_ID, NODE_ID,PARENT_NODE_ID,UNIT,DESCRIPTION FROM UNIT') { row ->

                final BigDecimal parentNodeId = row.PARENT_NODE_ID
                BigDecimal parentUnitId = null
                if (parentNodeId) {
                    sql.eachRow('SELECT UNIT_ID FROM UNIT WHERE NODE_ID=' + parentNodeId) { parentUnitRow ->
                        parentUnitId = parentUnitRow.UNIT_ID
                    }
                }
                final BigDecimal unitId = new BigDecimal(row.UNIT_ID.toString())
                final String unit = row.UNIT
                final String descriptionText = row.DESCRIPTION
                final UnitDTO unitDTO = new UnitDTO(unitId, parentUnitId, unit, descriptionText)
                generateUnit(xml, unitDTO)
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
    public void generateUnitConversion(
            final MarkupBuilder xml,
            final UnitConversionDTO unitConversionDTO
    ) {
        final Map<String, String> attributes =
            generateAttributesForUnitConversion(unitConversionDTO)

        xml.unitConversion(attributes) {
            if (unitConversionDTO.formula) {
                formula(unitConversionDTO.formula)
            }
        }
    }
    /**
     * Generate a descriptor element for the given descriptorType
     * @param xml
     * @param descriptorDTO
     * @param descriptorType
     */
    public void generateDescriptor(final MarkupBuilder xml, final DescriptorDTO descriptorDTO, final DescriptorType descriptorType) {
        final Map<String, String> attributes = generateAttributesForDescriptor(descriptorDTO);
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
    /**
     * Generate result type element for a given dto
     *
     * <resultType> </resultType>
     * @param xml
     * @param resultTypeDTO
     */
    public void generateResultType(final MarkupBuilder xml, final ResultTypeDTO resultTypeDTO) {
        final Map<String, String> attributes = generateAttributesForResultType(resultTypeDTO)

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
     *  Generate element from a dto
     * @param xml
     * @param elementDTO
     *
     * <element></element>
     */
    public void generateElement(final MarkupBuilder xml, final ElementDTO elementDTO) {
        final Map<String, String> attributes = [:]


        attributes.put('elementId', elementDTO.elementId.toString())
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
            //now generate links for editing the element
            //clients can use this link to indicate that they have consumed the element
            final String ELEMENT_MEDIA_TYPE = this.elementMediaType
            final String elementHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: elementDTO.elementId]).toString()
            link(rel: 'edit', href: "${elementHref}", type: "${ELEMENT_MEDIA_TYPE}")

        }
    }
    /**
     *
     * @param xml
     * @param elementHierarchyDTO
     */
    public void generateElementHierarchy(final MarkupBuilder xml,
                                         final ElementHierarchyDTO elementHierarchyDTO) {

        final Map<String, String> attributes = generateAttributesForElementHierarchy(elementHierarchyDTO.parentElementId, elementHierarchyDTO.childElementId)
        xml.elementHierarchy(attributes) {
            if (elementHierarchyDTO.relationshipTypeValue) {
                relationshipType(elementHierarchyDTO.relationshipTypeValue)
            }
        }
    }
    /**
     * Generate a single stage from the given parameters
     * @param xml
     * @param stageId
     * @param parentStageId
     * @param stage
     * @param descriptionText
     * @param stageStatus
     */
    public void generateStage(final MarkupBuilder xml,
                              final StageDTO stageDTO) {

        final Map<String, String> attributes = generateAttributesForStage(stageDTO.stageId, stageDTO.parentStageId, stageDTO.stageStatus)
        xml.stage(attributes) {
            if (stageDTO.stage) {
                stageName(stageDTO.stage)
            }
            if (stageDTO.descriptionText) {
                description(stageDTO.descriptionText)
            }
        }
    }
    /**
     *
     * @param xml
     * @param laboratoryDTO
     */
    public void generateLab(final MarkupBuilder xml, final LaboratoryDTO laboratoryDTO) {
        Map<String, String> attributes = [:]
        attributes.put('laboratoryId', laboratoryDTO.labId.toString())

        if (laboratoryDTO.parentLabId) {
            attributes.put('parentLaboratoryId', laboratoryDTO.parentLabId.toString())
        }
        if (laboratoryDTO.laboratoryStatus) {
            attributes.put('laboratoryStatus', laboratoryDTO.laboratoryStatus)
        }
        xml.laboratory(attributes) {
            laboratoryName(laboratoryDTO.labName)
            description(laboratoryDTO.description)
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
    public void generateUnit(
            final MarkupBuilder xml,
            final UnitDTO unitDTO
    ) {
        final Map<String, String> attributes = generateAttributesForUnit(unitDTO.unitId, unitDTO.parentUnitId, unitDTO.unit)

        xml.unit(attributes) {

            if (unitDTO.descriptionText) {
                description(unitDTO.descriptionText)
            }
        }
    }
    /**
     * Generate a single descriptor element from a DTO
     *
     * <biologyDescriptor></biologyDescriptor>
     * @param xml
     * @param descriptorDTO
     */
    public void generateSingleDescriptor(final MarkupBuilder xml, final DescriptorDTO descriptorDTO) {

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
    /**
     *  Attributes for a Stage
     * @param stageId
     * @param parentStageId
     * @param stageStatus
     * @return Map for attribute
     */
    public Map<String, String> generateAttributesForStage(final BigDecimal stageId, final BigDecimal parentStageId, final String stageStatus) {
        final Map<String, String> attributes = [:]

        if (stageId) {
            attributes.put('stageId', stageId.toString())
        }

        if (parentStageId) {
            attributes.put('parentStageId', parentStageId.toString())
        }
        if (stageStatus) {
            attributes.put('stageStatus', stageStatus)
        }
        return attributes
    }
    /**
     *
     * @param elementHierarchyId
     * @param parentElementId
     * @param childElementId
     * @return Map
     */
    public Map<String, String> generateAttributesForElementHierarchy(final BigDecimal parentElementId, final BigDecimal childElementId) {
        final Map<String, String> attributes = [:]

        if (parentElementId) {
            attributes.put('parentElementId', parentElementId.toString())
        }
        if (childElementId) {
            attributes.put('childElementId', childElementId.toString())
        }
        return attributes
    }

    public Map<String, String> generateAttributesForUnitConversion(final UnitConversionDTO unitConversionDTO) {
        final Map<String, String> attributes = [:]

        if (unitConversionDTO.fromUnit) {
            attributes.put('fromUnit', unitConversionDTO.fromUnit)
        }
        if (unitConversionDTO.toUnit) {
            attributes.put('toUnit', unitConversionDTO.toUnit)
        }
        if (unitConversionDTO.multiplier) {
            attributes.put('multiplier', unitConversionDTO.multiplier.toString())
        }
        if (unitConversionDTO.offSet) {
            attributes.put('offset', unitConversionDTO.offSet.toString())
        }
        return attributes
    }
    /**
     *
     * @param unitId
     * @param parentUnitId
     * @param unit
     * @return Map
     */
    public Map<String, String> generateAttributesForUnit(final BigDecimal unitId,
                                                         final BigDecimal parentUnitId,
                                                         final String unit) {
        final Map<String, String> attributes = [:]
        if (unitId) {
            attributes.put('unitId', unitId.toString())
        }

        if (parentUnitId) {
            attributes.put('parentUnitId', parentUnitId.toString())
        }
        if (unit) {
            attributes.put('unit', unit)
        }
        return attributes
    }
    /**
     *
     * @param resultTypeDTO
     * @return Map for attributes
     */
    public Map<String, String> generateAttributesForResultType(final ResultTypeDTO resultTypeDTO) {
        final Map<String, String> attributes = [:]

        if (resultTypeDTO.resultTypeId) {
            attributes.put("resultTypeId", resultTypeDTO.resultTypeId.toString())
        }


        if (resultTypeDTO.parentResultTypeId) {
            attributes.put("parentResultTypeId", resultTypeDTO.parentResultTypeId.toString())
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
        return attributes
    }
    /**
     * Generate Attributes as a Map for a descriptor element
     * @param descriptorDTO
     * @return key value pairs to be used as attributes for element
     */
    public Map<String, String> generateAttributesForDescriptor(final DescriptorDTO descriptorDTO) {
        final Map<String, String> attributes = [:]

        //use toString because map expects a string key and value
        attributes.put('descriptorId', descriptorDTO.descriptorId.toString())

        if (descriptorDTO.parentDescriptorId) {
            attributes.put('parentDescriptorId', descriptorDTO.parentDescriptorId.toString())
        }
        if (descriptorDTO.elementId) {
            attributes.put('elementId', descriptorDTO.elementId.toString())
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
        return attributes;
    }
}

class UnitDTO {
    final BigDecimal unitId
    final BigDecimal parentUnitId
    final String unit
    final String descriptionText

    UnitDTO(final BigDecimal unitId,
            final BigDecimal parentUnitId,
            final String unit,
            final String descriptionText) {
        this.unitId = unitId
        this.parentUnitId = parentUnitId
        this.unit = unit
        this.descriptionText = descriptionText
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
                  final String labName,
                  final String laboratoryStatus) {
        this.labId = labId
        this.parentLabId = parentLabId
        this.description = description
        this.labName = labName
        this.laboratoryStatus = laboratoryStatus
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
class StageDTO {
    final BigDecimal stageId
    final BigDecimal parentStageId
    final String stage
    final String descriptionText
    final String stageStatus

    StageDTO(final BigDecimal stageId,
             final BigDecimal parentStageId,
             final String stage,
             final String descriptionText,
             final String stageStatus) {

        this.stageId = stageId
        this.parentStageId = parentStageId
        this.stage = stage
        this.descriptionText = descriptionText
        this.stageStatus = stageStatus
    }


}
class ElementHierarchyDTO {
    final BigDecimal parentElementId
    final BigDecimal childElementId
    final String relationshipTypeValue

    ElementHierarchyDTO(
            final BigDecimal parentElementId,
            final BigDecimal childElementId,
            final String relationshipTypeValue) {
        this.parentElementId = parentElementId
        this.childElementId = childElementId
        this.relationshipTypeValue = relationshipTypeValue
    }
}
class UnitConversionDTO {


    final String fromUnit
    final String toUnit
    final String formula
    final BigDecimal offSet
    final BigDecimal multiplier

    UnitConversionDTO(final String fromUnit,
                      final String toUnit,
                      final String formula,
                      final BigDecimal offSet,
                      final BigDecimal multiplier) {
        this.fromUnit = fromUnit
        this.toUnit = toUnit
        this.formula = formula
        this.offSet = offSet
        this.multiplier = multiplier
    }

}