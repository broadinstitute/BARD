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
    MediaTypesDTO mediaTypesDTO
    List<LaboratoryElement> laboratories
    DataSource dataSource


    String getElementMediaType() {
        this.mediaTypesDTO.elementMediaType
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
            for (AssayDescriptor descriptor in AssayDescriptor.list()) {
                generateDescriptor(xml, descriptor)
            }
            for (BiologyDescriptor descriptor in BiologyDescriptor.list()) {
                generateDescriptor(xml, descriptor)
            }
            for (InstanceDescriptor descriptor in InstanceDescriptor.list()) {
                generateDescriptor(xml, descriptor)
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
            List<StageTree> stageTrees = StageTree.list()
            for (StageTree stageTree in stageTrees) {
                generateStage(xml, stageTree)
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
    public void generateStage(final MarkupBuilder xml, final StageTree stageTree) {
        final Map<String, String> attributes = generateAttributesForStage(stageTree)
        xml.stage(attributes) {
            // TODO eleminate stageName
            if (stageTree.element.label) {
                stageName(stageTree.element.label)
            }
            if (stageTree.element.description) {
                description(stageTree.element.description)
            }
            final String href = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: stageTree.element.id]).toString()
            link(rel: 'related', href: "${href}", type: "${this.getElementMediaType()}")
        }
    }
/**
 * Root Node for generating all elements

 * @param xml
 */
    public void generateElements(final MarkupBuilder xml) {
        List<Element> elements = Element.findAll(sort: 'id')
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
    public void generateResultType(final MarkupBuilder xml, final ResultTypeTree resultTypeTree) {

        final Map<String, String> attributes = generateAttributesForResultType(resultTypeTree)

        xml.resultType(attributes) {
            resultTypeName(resultTypeTree.element.label)
            if (resultTypeTree.element.description) {
                description(resultTypeTree.element.description)
            }
            if (resultTypeTree.element.synonyms) {
                synonyms(resultTypeTree.element.synonyms)
            }

            final String href = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: resultTypeTree.element.id]).toString()
            link(rel: 'related', href: "${href}", type: "${this.getElementMediaType()}")
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
                ResultTypeTree resultTypeTree = ResultTypeTree.read(row.NODE_ID)
                generateResultType(xml, resultTypeTree)
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
                UnitTree unitTree = UnitTree.read(row.NODE_ID)
                generateUnit(xml, unitTree)
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
    public void generateDescriptor(final MarkupBuilder xml, Descriptor descriptor) {
        final Map<String, String> attributes = generateAttributesForDescriptor(descriptor);
        xml.descriptor(attributes) {
            generateSingleDescriptor(xml, descriptor)
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
        attributes.put('readyForExtraction', element.readyForExtraction.getId())
        if (element.elementStatus) {
            attributes.put('elementStatus', element.elementStatus.toString())
        }
        if (element.abbreviation) {
            attributes.put('abbreviation', element.abbreviation)
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
            if (element.ontologyItems) {
                generateOntologyItems(xml, element.ontologyItems)
            }
            //now generate links for editing the element
            //clients can use this link to indicate that they have consumed the element
            final String ELEMENT_MEDIA_TYPE = this.getElementMediaType()
            final String elementHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: element.id])
            link(rel: 'edit', href: "${elementHref}", type: "${ELEMENT_MEDIA_TYPE}")
            if (element?.unit) {
                final String unitHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: element.unit.id])
                link(rel: 'related', href: "${unitHref}", type: "${ELEMENT_MEDIA_TYPE}")
            }

        }
    }

    public void generateOntologyItems(final MarkupBuilder xml, Set<OntologyItem> ontologyItems) {
        xml.ontologies() {
            for (OntologyItem ontologyItem : ontologyItems) {
                generateOntologyItem(xml, ontologyItem)
            }
        }
    }

    public void generateOntologyItem(final MarkupBuilder xml, OntologyItem ontologyItem) {
        final Ontology ontology = ontologyItem.getOntology()
        final Map<String, String> attributes = [:]


        attributes.put('name', ontology.ontologyName)
        if (ontology.abbreviation) {
            attributes.put('abbreviation', ontology.abbreviation)
        }
        if (ontology.systemUrl) {
            attributes.put('sourceUrl', ontology.systemUrl)
        }
        xml.ontology(attributes) {}


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
                link(rel: 'edit', href: "${elementHref}", type: "${this.getElementMediaType()}")
            }
            if (elementHierarchy.parentElement) {
                parentElement(parentElement: elementHierarchy.parentElement.label) {
                    final String elementHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: elementHierarchy.parentElement.id]).toString()
                    link(rel: 'edit', href: "${elementHref}", type: "${this.getElementMediaType()}")
                }
            }
            relationshipType(elementHierarchy.relationshipType)
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
 * @param unitTree
 */
    public void generateUnit(
            final MarkupBuilder xml,
            final UnitTree unitTree
    ) {
        final Map<String, String> attributes = generateAttributesForUnit(unitTree)
        xml.unit(attributes) {
            if (unitTree.element.description) {
                description(unitTree.element.description)
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
    public void generateSingleDescriptor(final MarkupBuilder xml, final Descriptor descriptor) {
        xml.elementStatus(descriptor.element.elementStatus)
        xml.label(descriptor.element.label)
        if (descriptor.element.description) {
            xml.description(descriptor.element.description)
        }
        if (descriptor.element.synonyms) {
            xml.synonyms(descriptor.element.synonyms)
        }
        final String elementHref = grailsLinkGenerator.link(mapping: 'element', absolute: true, params: [id: descriptor.element.id]).toString()
        xml.link(rel: 'edit', href: "${elementHref}", type: "${this.getElementMediaType()}")
    }
    /**
     *  Attributes for a Stage
     * @param stageId
     * @param parentStageId
     * @param stageStatus
     * @return Map for attribute
     */
    public Map<String, String> generateAttributesForStage(final StageTree stageTree) {
        final Map<String, String> attributes = [:]
        //use stageId to get the element label
        if (stageTree.element.label) {
            attributes.put('stageElement', stageTree.element.label)
        }
        if (stageTree.parent) {
            attributes.put('parentStageName', stageTree.parent.element.label)
        }
        return attributes
    }


    public Map<String, String> generateAttributesForUnitConversion(final UnitConversion unitConversion) {
        final Map<String, String> attributes = [:]

        if (unitConversion.fromUnit) {
            attributes.put('fromUnit', unitConversion.fromUnit.label)
        }
        if (unitConversion.toUnit) {
            attributes.put('toUnit', unitConversion.toUnit.label)
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
    public Map<String, String> generateAttributesForUnit(final UnitTree unitTree) {
        final Map<String, String> attributes = [:]
        attributes.put('unitElement', unitTree.element.label)
        if (unitTree.parent) {
            attributes.put('parentUnit', unitTree.parent.element.label)
        }
        return attributes
    }
    /**
     *
     * @param resultTypeDTO
     * @return Map for attributes
     */
    public Map<String, String> generateAttributesForResultType(final ResultTypeTree resultTypeTree) {
        final Map<String, String> attributes = [:]

        //get the label of the element
        attributes.put("resultTypeElement", resultTypeTree.element.label)
        //get the result type of the parent
        if (resultTypeTree.parent) {
            attributes.put("parentResultType", resultTypeTree.parent.element.label)
        }
        if (resultTypeTree.element.abbreviation) {
            attributes.put("abbreviation", resultTypeTree.element.abbreviation)
        }

        if (resultTypeTree.baseUnit) {
            attributes.put("baseUnit", resultTypeTree.baseUnit.label)
        }
        if (resultTypeTree.element.elementStatus) {
            attributes.put("resultTypeStatus", resultTypeTree.element.elementStatus)
        }
        return attributes
    }
/**
 * Generate Attributes as a Map for a descriptorRow element
 * @param descriptor
 * @return key value pairs to be used as attributes for element
 */
    public Map<String, String> generateAttributesForDescriptor(final Descriptor descriptor) {
        final Map<String, String> attributes = [:]


        if (descriptor.parent) {
            attributes.put('parentDescriptorLabel', descriptor.parent.label)
        }
        attributes.put('descriptorElement', descriptor.element.label)
        if (descriptor.element.abbreviation) {
            attributes.put('abbreviation', descriptor.element.abbreviation)
        }
        if (descriptor.element.externalURL) {
            attributes.put('externalUrl', descriptor.element.externalURL)
        }
        if (descriptor.element.unit) {
            attributes.put('unit', descriptor.element.unit.label)
        }
        // TODO eliminate descriptorElement or descriptor, they'll always have the same value
        attributes.put('descriptor', descriptor.element.label)
        return attributes;
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