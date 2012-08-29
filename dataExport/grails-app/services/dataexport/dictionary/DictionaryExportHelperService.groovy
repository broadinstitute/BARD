package dataexport.dictionary

import dataexport.registration.MediaTypesDTO
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.sql.DataSource

import bard.db.dictionary.*

/**
 *  This is wired in resources.groovy
 */
class DictionaryExportHelperService {
    LinkGenerator grailsLinkGenerator
    String elementMediaType
    List<LaboratoryElement> laboratories
    DataSource dataSource

    DictionaryExportHelperService(final MediaTypesDTO mediaTypesDTO) {
        this.elementMediaType = mediaTypesDTO.elementMediaType
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
            generateDescriptors(xml)
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
    public void generateDescriptors(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)
        xml.descriptors() {
            sql.eachRow('SELECT * FROM ASSAY_DESCRIPTOR_TREE') { assayDescriptorRow ->
                String parentDescriptorLabel = null
                Long parentNodeId = assayDescriptorRow.PARENT_NODE_ID
                if (parentNodeId) {
                    def parentRow = sql.firstRow("SELECT LABEL FROM ASSAY_DESCRIPTOR_TREE WHERE NODE_ID=?", [parentNodeId])
                    parentDescriptorLabel = parentRow.LABEL
                }
                AssayElement assayElement = AssayElement.get(assayDescriptorRow.ELEMENT_ID)
                final DescriptorDTO descriptorDTO = new DescriptorDTO(assayDescriptorRow, 'assay', parentDescriptorLabel, assayElement?.label)
                generateDescriptor(xml, descriptorDTO)
            }

            sql.eachRow('SELECT * FROM BIOLOGY_DESCRIPTOR_TREE') { biologyDescriptorRow ->
                String parentDescriptorLabel = null
                Long parentNodeId = biologyDescriptorRow.PARENT_NODE_ID
                if (parentNodeId) {
                    def parentRow = sql.firstRow("SELECT LABEL FROM BIOLOGY_DESCRIPTOR_TREE WHERE NODE_ID=?", [parentNodeId])
                    parentDescriptorLabel = parentRow.LABEL

                }
                BiologyElement biologyElement = BiologyElement.get(biologyDescriptorRow.ELEMENT_ID)

                final DescriptorDTO descriptorDTO = new DescriptorDTO(biologyDescriptorRow, 'biology', parentDescriptorLabel, biologyElement?.label)
                generateDescriptor(xml, descriptorDTO)
            }

            sql.eachRow('SELECT * FROM INSTANCE_DESCRIPTOR_TREE') { instanceDescriptorRow ->
                String parentDescriptorLabel = null
                Long parentNodeId = instanceDescriptorRow.PARENT_NODE_ID
                if (parentNodeId) {
                    def parentRow = sql.firstRow("SELECT LABEL FROM INSTANCE_DESCRIPTOR_TREE WHERE NODE_ID=?", [parentNodeId])
                    parentDescriptorLabel = parentRow.LABEL
                }

                InstanceElement instanceElement = InstanceElement.get(instanceDescriptorRow.ELEMENT_ID)
                final DescriptorDTO descriptorDTO = new DescriptorDTO(instanceDescriptorRow, 'instance', parentDescriptorLabel, instanceElement?.label)
                generateDescriptor(xml, descriptorDTO)
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
            sql.eachRow('SELECT * FROM STAGE_TREE') { stageRow ->
                String parentName = null

                final String label
                Long parentNodeId = stageRow.PARENT_NODE_ID
                if (parentNodeId) {
                    def parentRow = sql.firstRow("SELECT STAGE FROM STAGE_TREE WHERE NODE_ID=?", [parentNodeId])
                    parentName = parentRow.STAGE
                }
                StageElement stageElement = StageElement.get(stageRow.STAGE_ID)
                generateStage(xml, new Stage(stageRow, parentName, stageElement?.label))
            }
        }
    }
/**
 * @param xml
 */
    public void generateLabs(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)
        xml.laboratories() {
            sql.eachRow('SELECT * FROM LABORATORY_TREE') { row ->
                String parentName = null

                final String label
                final Long parentNodeId = row.PARENT_NODE_ID
                if (parentNodeId) {
                    def parentRow = sql.firstRow("SELECT LABORATORY FROM LABORATORY_TREE WHERE NODE_ID=?", [parentNodeId])
                    parentName = parentRow.LABORATORY
                }
                LaboratoryElement laboratoryElement = LaboratoryElement.get(row.LABORATORY_ID)
                generateLab(xml, new Laboratory(row, parentName, laboratoryElement?.label))
            }

        }

    }
/**
 * Generate stages
 *
 * @param xml
 * @param stageId
 */
    public void generateStage(final MarkupBuilder xml, final Stage stage) {
        final Map<String, String> attributes = generateAttributesForStage(stage)
        xml.stage(attributes) {
            if (stage.stageName) {
                stageName(stage.stageName)
            }
            if (stage.description) {
                description(stage.description)
            }
            final String href = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: stage.stageId]).toString()
            link(rel: 'related', href: "${href}", type: "${this.elementMediaType}")
        }
    }
/**
 * Root Node for generating all elements

 * @param xml
 */
    public void generateElements(final MarkupBuilder xml) {
        List<Element> elements = Element.findAll()
        xml.elements() {
            for (Element element : elements) {
                generateElement(xml, element)
            }
        }
    }
/**
 * Root node for generating element hierarchies

 * @param xml
 */
    public void generateElementHierarchies(final MarkupBuilder xml) {
        final List<ElementHierarchy> elementHierarchies = ElementHierarchy.findAll()
        xml.elementHierarchies() {
            for (ElementHierarchy elementHierarchy : elementHierarchies) {
                generateElementHierarchy(xml, elementHierarchy)
            }
        }
    }

/**
 *

 * @param xml
 * @param resultTypeId
 */
    public void generateResultType(final MarkupBuilder xml, final ResultType resultType) {

        final Map<String, String> attributes = generateAttributesForResultType(resultType)

        xml.resultType(attributes) {

            if (resultType.resultTypeName) {
                resultTypeName(resultType.resultTypeName)
            }
            if (resultType.description) {
                description(resultType.description)
            }
            if (resultType.synonyms) {
                synonyms(resultType.synonyms)
            }

            final String href = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: resultType.resultTypeId]).toString()
            link(rel: 'related', href: "${href}", type: "${this.elementMediaType}")
        }
    }
/**
 * Generate Result Types

 * @param xml
 */
    public void generateResultTypes(final MarkupBuilder xml) {
        final Sql sql = new Sql(dataSource)


        xml.resultTypes() {
            sql.eachRow('SELECT * FROM RESULT_TYPE_TREE') { row ->
                Long parentNodeId = row.PARENT_NODE_ID
                String parentResultTypeName = null
                if (parentNodeId) {
                    def parentRow = sql.firstRow("SELECT RESULT_TYPE_NAME FROM RESULT_TYPE_TREE WHERE NODE_ID=?", [parentNodeId])
                    parentResultTypeName = parentRow.RESULT_TYPE_NAME
                }
                ResultTypeElement resultTypeElement = ResultTypeElement.get(row.RESULT_TYPE_ID)
                generateResultType(xml, new ResultType(row, parentResultTypeName, resultTypeElement?.label))
            }
        }
    }


    public void generateUnitConversions(final MarkupBuilder xml) {
        final List<UnitConversion> unitConversions = UnitConversion.findAll()
        xml.unitConversions() {
            for (UnitConversion unitConversion : unitConversions) {
                generateUnitConversion(xml, unitConversion)
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
            sql.eachRow('SELECT * FROM UNIT_TREE') { row ->
                Long parentNodeId = row.PARENT_NODE_ID
                String parentUnit = null
                if (parentNodeId) {
                    def parentRow = sql.firstRow("SELECT UNIT FROM UNIT_TREE WHERE NODE_ID=?", [parentNodeId])
                    parentUnit = parentRow.UNIT
                }
                UnitElement unitElement = UnitElement.get(row.UNIT_ID)
                generateUnit(xml, new Units(row, parentUnit, unitElement?.label))
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
            final UnitConversion unitConversion
    ) {
        final Map<String, String> attributes =
            generateAttributesForUnitConversion(unitConversion)

        xml.unitConversion(attributes) {
            if (unitConversion.formula) {
                formula(unitConversion.formula)
            }
        }
    }
/**
 * Generate a descriptorRow element for the given descriptorType
 * @param xml
 * @param descriptorDTO
 * @param descriptorType
 */
    public void generateDescriptor(final MarkupBuilder xml, final DescriptorDTO descriptorDTO) {
        final Map<String, String> attributes = generateAttributesForDescriptor(descriptorDTO);
        xml.descriptor(attributes) {
            generateSingleDescriptor(xml, descriptorDTO)
        }
    }
/**
 *  Generate element from a dto
 * @param xml
 * @param elementDTO
 *
 * <element></element>
 */
    public void generateElement(final MarkupBuilder xml, final Element element) {
        final Map<String, String> attributes = [:]


        attributes.put('elementId', element.id?.toString())
        attributes.put('readyForExtraction', element.readyForExtraction.toString())
        if (element.elementStatus) {
            attributes.put('elementStatus', element.elementStatus.toString())
        }
        if (element.abbreviation) {
            attributes.put('abbreviation', element.abbreviation)
        }

        if (element?.unit) {
            attributes.put('unit', element.unit)
        }
        xml.element(attributes) {
            if (element.label) {
                label(element.label)
            }
            if (element.description) {
                description(element.description)
            }
            if (element.synonyms) {
                synonyms(element.synonyms)
            }
            if (element.externalURL) {
                externalUrl(element.externalURL)
            }
            //now generate links for editing the element
            //clients can use this link to indicate that they have consumed the element
            final String ELEMENT_MEDIA_TYPE = this.elementMediaType
            final String elementHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: element.id]).toString()
            link(rel: 'edit', href: "${elementHref}", type: "${ELEMENT_MEDIA_TYPE}")

        }
    }
/**
 *
 * @param xml
 * @param elementHierarchy
 */
    public void generateElementHierarchy(final MarkupBuilder xml,
                                         final ElementHierarchy elementHierarchy) {

        xml.elementHierarchy() {
            childElement(childElement: elementHierarchy.childElement.label) {
                final String elementHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: elementHierarchy.childElement.id]).toString()
                link(rel: 'edit', href: "${elementHref}", type: "${this.elementMediaType}")
            }
            if (elementHierarchy?.parentElement) {
                parentElement(parentElement: elementHierarchy.parentElement.label) {
                    final String elementHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: elementHierarchy.parentElement.id]).toString()
                    link(rel: 'edit', href: "${elementHref}", type: "${this.elementMediaType}")
                }
            }
            if (elementHierarchy.relationshipType) {
                relationshipType(elementHierarchy.relationshipType)
            }
        }
    }
/**
 *
 * @param xml
 * @param laboratoryDTO
 */
    public void generateLab(final MarkupBuilder xml, final Laboratory laboratory) {
        Map<String, String> attributes = [:]
        if (laboratory) {
            attributes.put('laboratoryElement', laboratory.elementLabel)
        }

        if (laboratory.parentLaboratory) {
            attributes.put('parentLaboratory', laboratory.parentLaboratory)
        }
        xml.laboratory(attributes) {
            laboratoryName(laboratory.laboratoryName)
            description(laboratory.description)
        }
    }
/**
 *
 * @param xml
 * @param unit
 */
    public void generateUnit(
            final MarkupBuilder xml,
            final Units unit
    ) {


        final Map<String, String> attributes = generateAttributesForUnit(unit)

        xml.unit(attributes) {
            if (unit.description) {
                description(unit.description)
            }
        }
    }
/**
 * Generate a single descriptorRow element from a DTO
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
        if (descriptorDTO.elementId) {
            final String elementHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: descriptorDTO.elementId]).toString()
            xml.link(rel: 'edit', href: "${elementHref}", type: "${this.elementMediaType}")

        }
    }
/**
 *  Attributes for a Stage
 * @param stageId
 * @param parentStageId
 * @param stageStatus
 * @return Map for attribute
 */
    public Map<String, String> generateAttributesForStage(final Stage stage) {
        final Map<String, String> attributes = [:]

        //use stageId to get the element label
        if (stage.label) {
            attributes.put('stageElement', stage.label)
        }

        if (stage.parentName) {
            attributes.put('parentStageName', stage.parentName)
        }
        return attributes
    }


    public Map<String, String> generateAttributesForUnitConversion(final UnitConversion unitConversion) {
        final Map<String, String> attributes = [:]

        if (unitConversion.fromUnit) {
            attributes.put('fromUnit', unitConversion.fromUnit)
        }
        if (unitConversion.toUnit) {
            attributes.put('toUnit', unitConversion.toUnit)
        }
        if (unitConversion.multiplier) {
            attributes.put('multiplier', unitConversion.multiplier.toString())
        }
        if (unitConversion.offset) {
            attributes.put('offset', unitConversion?.offset?.toString())
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
    public Map<String, String> generateAttributesForUnit(final Units unit) {
        final Map<String, String> attributes = [:]
        if (unit.elementLabel) {
            attributes.put('unitElement', unit.elementLabel)
        }

        if (unit.parentUnit) {
            attributes.put('parentUnit', unit.parentUnit)
        }
        if (unit.unit) {
            attributes.put('unit', unit.unit)
        }
        return attributes
    }
/**
 *
 * @param resultTypeDTO
 * @return Map for attributes
 */
    public Map<String, String> generateAttributesForResultType(final ResultType resultType) {
        final Map<String, String> attributes = [:]

        //get the label of the element

        if (resultType.resultTypeLabel) {
            attributes.put("resultTypeElement", resultType.resultTypeLabel)
        }

        //get the result type of the parent
        if (resultType.parentResultTypeName) {
            attributes.put("parentResultType", resultType.parentResultTypeName)
        }
        if (resultType.abbreviation) {
            attributes.put("abbreviation", resultType.abbreviation)
        }

        if (resultType.baseUnit) {
            attributes.put("baseUnit", resultType.baseUnit)
        }
        if (resultType.resultTypeStatus) {
            attributes.put("resultTypeStatus", resultType.resultTypeStatus)
        }
        return attributes
    }
/**
 * Generate Attributes as a Map for a descriptorRow element
 * @param descriptorDTO
 * @return key value pairs to be used as attributes for element
 */
    public Map<String, String> generateAttributesForDescriptor(final DescriptorDTO descriptorDTO) {
        final Map<String, String> attributes = [:]


        if (descriptorDTO.parentDescriptorLabel) {
            attributes.put('parentDescriptorLabel', descriptorDTO.parentDescriptorLabel)
        }
        if (descriptorDTO.descriptorElement) {
            attributes.put('descriptorElement', descriptorDTO.descriptorElement)
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
        if (descriptorDTO.descriptor) {
            attributes.put('descriptor', descriptorDTO.descriptor)
        }
        return attributes;
    }

}

class DescriptorDTO {
    String parentDescriptorLabel
    String descriptorElement
    String label
    String description
    String abbreviation
    String synonyms
    String externalUrl
    String unit
    String elementStatus
    String descriptor
    BigDecimal elementId

    public DescriptorDTO() {
    }

    public DescriptorDTO(def descriptorRow, String descriptorLabel, String parentDescriptorLabel, String descriptorElementLabel) {

        this.parentDescriptorLabel = parentDescriptorLabel
        this.descriptorElement = descriptorElementLabel


        this.descriptor = descriptorLabel
        this.label = descriptorRow.LABEL
        this.description = descriptorRow.DESCRIPTION
        this.abbreviation = descriptorRow.ABBREVIATION
        this.synonyms = descriptorRow.SYNONYMS
        this.externalUrl = descriptorRow.EXTERNAL_URL
        this.unit = descriptorRow.UNIT
        this.elementStatus = descriptorRow.ELEMENT_STATUS
        this.elementId = descriptorRow.ELEMENT_ID
    }
}
public class Stage {
    String label
    String parentName
    String stageName
    String description
    Long stageId
    String stageStatus

    Stage() {

    }

    Stage(def stageRow, final String parentName, final String label) {
        this.stageName = stageRow.STAGE
        this.description = stageRow.DESCRIPTION
        this.stageId = stageRow.STAGE_ID
        this.stageStatus = stageRow.STAGE_STATUS
        this.parentName = parentName
        this.label = label
    }
}
public class Laboratory {
    String elementLabel
    String parentLaboratory
    String laboratoryName
    String description
    String laboratoryStatus

    public Laboratory() {

    }

    public Laboratory(def laboratoryRow, final String parentLaboratory, final String elementLabel) {
        this.parentLaboratory = parentLaboratory
        this.elementLabel = elementLabel
        this.laboratoryStatus = laboratoryRow.LABORATORY_STATUS
        this.laboratoryName = laboratoryRow.LABORATORY
        this.description = laboratoryRow.DESCRIPTION
    }

}
public class Units {
    String description
    String unit
    String elementLabel
    String parentUnit

    public Units() {

    }

    public Units(def row, final String parentUnit, final String elementLabel) {
        this.parentUnit = parentUnit
        this.elementLabel = elementLabel
        this.unit = row.UNIT
        this.description = row.DESCRIPTION
    }
}
