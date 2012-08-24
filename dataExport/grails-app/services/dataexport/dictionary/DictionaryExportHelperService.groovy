package dataexport.dictionary

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import bard.db.dictionary.*
import dataexport.registration.MediaTypesDTO

/**
 *  This is wired in resources.groovy
 */
class DictionaryExportHelperService {
    LinkGenerator grailsLinkGenerator
    String elementMediaType
    List<LaboratoryElement> laboratories

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
        final List<AssayDescriptor> assayDescriptors = AssayDescriptor.findAll()
        xml.descriptors() {
            for (AssayDescriptor descriptor : assayDescriptors) {
                final DescriptorDTO descriptorDTO =
                    new DescriptorDTO(
                            descriptor
                    )
                generateDescriptor(xml, descriptorDTO)
            }
            final List<BiologyDescriptor> biologyDescriptors = BiologyDescriptor.findAll()
            for (BiologyDescriptor descriptor : biologyDescriptors) {
                final DescriptorDTO descriptorDTO =
                    new DescriptorDTO(
                            descriptor
                    )
                generateDescriptor(xml, descriptorDTO)
            }

            final List<InstanceDescriptor> instanceDescriptors = InstanceDescriptor.findAll()
            for (InstanceDescriptor descriptor : instanceDescriptors) {
                final DescriptorDTO descriptorDTO =
                    new DescriptorDTO(
                            descriptor
                    )
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
        final List<Stage> stages = Stage.findAll()

        xml.stages() {
            for (Stage stage : stages) {
                generateStage(xml, stage)
            }
        }
    }
/**
 * @param xml
 */
    public void generateLabs(final MarkupBuilder xml) {
        laboratories = Laboratory.findAll()
        if (laboratories) {
            xml.laboratories() {
                for (Laboratory laboratory : laboratories) {
                    generateLab(xml, laboratory)
                }
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
        final Map<String, String> attributes = generateAttributesForStage(stage, stage.parent)
        xml.stage(attributes) {
            if (stage.stage) {
                stageName(stage.stage)
            }
            if (stage.description) {
                description(stage.description)
            }

            final Element element = stage.element
            if (element) {
                final String href = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: element.id]).toString()
                link(rel: 'related', href: "${href}", type: "${this.elementMediaType}")
            }
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
            final Element element = resultType.element
            if (element) {
                final String href = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: element.id]).toString()
                link(rel: 'related', href: "${href}", type: "${this.elementMediaType}")
            }
        }
    }
/**
 * Generate Result Types

 * @param xml
 */
    public void generateResultTypes(final MarkupBuilder xml) {
        final List<ResultType> resultTypes = ResultType.findAll()

        xml.resultTypes() {
            for (ResultType resultType : resultTypes) {
                generateResultType(xml, resultType)
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
        final List<Unit> units = Unit.findAll()
        xml.units() {
            for (Unit unit : units) {
                generateUnit(xml, unit)
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
 * Generate a descriptor element for the given descriptorType
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
        attributes.put('readyForExtraction', element.readyForExtraction)
        if (element.elementStatus) {
            attributes.put('elementStatus', element.elementStatus)
        }
        if (element.abbreviation) {
            attributes.put('abbreviation', element.abbreviation)
        }

        if (element?.unit?.unit) {
            attributes.put('unit', element.unit.unit)
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
    public void generateLab(final MarkupBuilder xml, final LaboratoryElement laboratoryElement) {
        Map<String, String> attributes = [:]
        if (laboratoryElement) {
            attributes.put('laboratoryElement', laboratoryElement.label)
        }

        if (laboratory.parent) {
            attributes.put('parentLaboratory', laboratory.parent.laboratory)
        }
        xml.laboratory(attributes) {
            laboratoryName(laboratory.laboratory)
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
            final Unit unit
    ) {
        Unit parentUnit = null
        if (unit.parentNodeId) {
            parentUnit = Unit.findByNodeId(unit.parentNodeId)
        }

        final Map<String, String> attributes = generateAttributesForUnit(unit, parentUnit)

        xml.unit(attributes) {

            if (unit.description) {
                description(unit.description)
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
    public Map<String, String> generateAttributesForStage(final Stage stage, final Stage parentStage) {
        final Map<String, String> attributes = [:]

        if (stage.element) {
            attributes.put('stageElement', stage.element.label)
        }

        if (parentStage) {
            attributes.put('parentStageName', parentStage.stage)
        }
        return attributes
    }


    public Map<String, String> generateAttributesForUnitConversion(final UnitConversion unitConversion) {
        final Map<String, String> attributes = [:]

        if (unitConversion.fromUnit) {
            attributes.put('fromUnit', unitConversion.fromUnit.unit)
        }
        if (unitConversion.toUnit) {
            attributes.put('toUnit', unitConversion.toUnit.unit)
        }
        if (unitConversion.multiplier) {
            attributes.put('multiplier', unitConversion.multiplier.toString())
        }
        if (unitConversion.offset) {
            attributes.put('offset', unitConversion.offset.toString())
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
    public Map<String, String> generateAttributesForUnit(final Unit unit, final Unit parentUnit) {
        final Map<String, String> attributes = [:]
        if (unit.element) {
            attributes.put('unitElement', unit.element.label)
        }

        if (parentUnit) {
            attributes.put('parentUnit', parentUnit.unit)
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

        if (resultType.element?.label) {
            attributes.put("resultTypeElement", resultType.element.label)
        }


        if (resultType.parent?.resultTypeName) {
            attributes.put("parentResultType", resultType.parent.resultTypeName)
        }
        if (resultType.abbreviation) {
            attributes.put("abbreviation", resultType.abbreviation)
        }

        if (resultType.baseUnit) {
            attributes.put("baseUnit", resultType.baseUnit.unit)
        }
        if (resultType.resultTypeStatus) {
            attributes.put("resultTypeStatus", resultType.resultTypeStatus)
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
    final String parentDescriptorLabel
    final String descriptorElement
    final String label
    final String description
    final String abbreviation
    final String synonyms
    final String externalUrl
    final String unit
    final String elementStatus
    final String descriptor
    final BigDecimal elementId

    //We do not type the parameters here because they could be any one of the three types
    //We will be refactoring to make this redudant as soon as possible
    DescriptorDTO(descriptor, descriptorLabel) {
        this.parentDescriptorLabel = descriptor.parent?.label
        this.descriptorElement = descriptor.element?.label
        this.label = descriptor.label
        this.description = descriptor.description
        this.abbreviation = descriptor.abbreviation
        this.synonyms = descriptor.synonyms
        this.externalUrl = descriptor.externalURL
        this.unit = descriptor.unit?.unit
        this.elementStatus = descriptor.elementStatus
        this.descriptor = descriptorLabel
        this.elementId = descriptor.element?.id
    }

    DescriptorDTO(BiologyDescriptor descriptor) {
        this(descriptor, 'biology')
    }

    DescriptorDTO(AssayDescriptor descriptor) {
        this(descriptor, 'assay')
    }

    DescriptorDTO(InstanceDescriptor descriptor) {
        this(descriptor, 'instance')
    }

}