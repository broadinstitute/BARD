package bard.dm.minimumassayannotation

import bard.db.dictionary.Element
import bard.db.registration.AttributeType

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/26/12
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextGroupsBuilder {
    private static final String biologyElementLabel = "biology"

    private final Element biologyElement

    AssayContextGroupsBuilder() {
        biologyElement = Element.findByLabel(biologyElementLabel)
    }

    List<ContextGroup> build() {
        List<Attribute> processOrTarget = [new Attribute('2/C', '$/C', AttributeType.Fixed, true, null, null),
                new Attribute('$/C', '$/D', AttributeType.Fixed, true, null, null)]

        List<Attribute> assayFormat = [new Attribute('1/E', '$/E', AttributeType.Fixed), new Attribute('2/F', '$/F', AttributeType.Fixed)]

        List<Attribute> assayComponent = [
                new Attribute('2/G', '$/G', AttributeType.Fixed),
                new Attribute('2/H', '$/H', AttributeType.Fixed),
                new Attribute('2/I', '$/I', AttributeType.Fixed, true, null, null),
                new Attribute('2/J', '$/J', AttributeType.Fixed, true, null, '$/K'), //concentration + units
//                new Attribute('2/K', '$/K', AttributeType.Fixed),
                new Attribute('2/L', '$/L', AttributeType.Fixed, true, null, null),
                new Attribute('2/M', '$/M', AttributeType.Fixed)]

        List<Attribute> assayDetector = [
                new Attribute('2/O', '$/O', AttributeType.Fixed),
                new Attribute('$/O', '$/N', AttributeType.Fixed, true, null, null)]

        List<Attribute> assayDetection = [
                new Attribute('2/P', '$/P', AttributeType.Fixed),
                new Attribute('2/Q', '$/Q', AttributeType.Fixed)]

        List<Attribute> assayReadout = [
                new Attribute('2/R', '$/R', AttributeType.Fixed),
                new Attribute('2/S', '$/S', AttributeType.Fixed),
                new Attribute('2/T', '$/T', AttributeType.Fixed)]

        List<Attribute> assayFootprint = [new Attribute('2/U', '$/U', AttributeType.Fixed)]

        List<Attribute> assayExcitation = [
                new Attribute('2/V', '$/V', AttributeType.Fixed, false, null,'2/BI'),//The wavelength units are fixed in cell 2/BI
                new Attribute('2/W', '$/W', AttributeType.Fixed, false, null,'2/BI')]//The wavelength units are fixed in cell 2/BI

        List<Attribute> assayAbsorbance = [new Attribute('2/X', '$/X', AttributeType.Fixed)]

        List<Attribute> resultActivityThreshold = [new Attribute('2/AA', '$/AA', AttributeType.Fixed, false, '$/Z', null)]//the qualifier belongs to the Activity-threshold attribute

        List<Attribute> resultDetails = [new Attribute('2/AH', '$/AH', AttributeType.Free),
                new Attribute('2/AI', '$/AI', AttributeType.Free)]

        List<ContextGroup> spreadsheetAssayContextGroups = [new ContextGroup(name: 'processOrTarget', attributes: processOrTarget),
                new ContextGroup(name: 'assayFormat', attributes: assayFormat),
                new ContextGroup(name: 'assayComponent', attributes: assayComponent),
                new ContextGroup(name: 'assayDetection', attributes: assayDetection),
                new ContextGroup(name: 'assayReadout', attributes: assayReadout),
                new ContextGroup(name: 'assayDetector', attributes: assayDetector),
                new ContextGroup(name: 'assayFootprint', attributes: assayFootprint),
                new ContextGroup(name: 'assayExcitation', attributes: assayExcitation),
                new ContextGroup(name: 'assayAbsorbance', attributes: assayAbsorbance),
                new ContextGroup(name: 'resultActivityThreshold', attributes: resultActivityThreshold),
                new ContextGroup(name: 'resultDetails', attributes: resultDetails)]

        return spreadsheetAssayContextGroups
    }
}
