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
        List<ContextItemDto> processOrTarget = [
                new ContextItemDto('2/C', '$/C', AttributeType.Fixed, true, null, null),
                new ContextItemDto('$/C', '$/D', AttributeType.Fixed, true, null, null)
        ]

        List<ContextItemDto> assayFormat = [
                new ContextItemDto('1/E', '$/E', AttributeType.Fixed), new ContextItemDto('2/F', '$/F', AttributeType.Fixed)
        ]

        List<ContextItemDto> assayComponent = [
                new ContextItemDto('2/G', '$/G', AttributeType.Fixed),
                new ContextItemDto('2/H', '$/H', AttributeType.Fixed),
                new ContextItemDto('2/I', '$/I', AttributeType.Fixed, true, null, null),
                new ContextItemDto('2/J', '$/J', AttributeType.Fixed, true, null, '$/K'), //concentration + units
//                new Attribute('2/K', '$/K', AttributeType.Fixed),
                new ContextItemDto('2/L', '$/L', AttributeType.Fixed, true, null, null),
                new ContextItemDto('2/M', '$/M', AttributeType.Fixed)
        ]

        List<ContextItemDto> assayDetector = [
                new ContextItemDto('2/O', '$/O', AttributeType.Fixed),
                new ContextItemDto('$/O', '$/N', AttributeType.Fixed, true, null, null)
        ]

        List<ContextItemDto> assayDetection = [
                new ContextItemDto('2/P', '$/P', AttributeType.Fixed),
                new ContextItemDto('2/Q', '$/Q', AttributeType.Fixed)
        ]

        List<ContextItemDto> assayReadout = [
                new ContextItemDto('2/R', '$/R', AttributeType.Fixed),
                new ContextItemDto('2/S', '$/S', AttributeType.Fixed),
                new ContextItemDto('2/T', '$/T', AttributeType.Fixed)
        ]

        List<ContextItemDto> assayFootprint = [
                new ContextItemDto('2/U', '$/U', AttributeType.Fixed)
        ]

        List<ContextItemDto> assayExcitation = [
                new ContextItemDto('2/V', '$/V', AttributeType.Fixed, true, null,'2/BI'),//The wavelength units are fixed in cell 2/BI
                new ContextItemDto('2/W', '$/W', AttributeType.Fixed, true, null,'2/BI')
        ]//The wavelength units are fixed in cell 2/BI

        List<ContextItemDto> assayAbsorbance = [
                new ContextItemDto('2/X', '$/X', AttributeType.Fixed)
        ]

        List<ContextItemDto> resultActivityThreshold = [
                new ContextItemDto('2/Y', '$/Y', AttributeType.Fixed),            //result type
                new ContextItemDto('2/AA', '$/AA', AttributeType.Free, false, '$/Z', null), //activity threshold
                new ContextItemDto('2/AB', '$/AB', AttributeType.Fixed)                     //unit
        ]//the qualifier belongs to the Activity-threshold attribute

        List<ContextItemDto> resultDetails = [
                new ContextItemDto('2/AH', '$/AH', AttributeType.Free),       //concentration-point number
                new ContextItemDto('2/AI', '$/AI', AttributeType.Free)        //number of replicates
        ]

        List<ContextItemDto> projectLeadName = [                             //project lead name
                new ContextItemDto('2/AD', '$/AD', AttributeType.Free)
        ]

        List<ContextGroup> spreadsheetAssayContextGroups = [
                new ContextGroup(name: 'processOrTarget', contextItemDtoList: processOrTarget),
                new ContextGroup(name: 'assayFormat', contextItemDtoList: assayFormat),
                new ContextGroup(name: 'assayComponent', contextItemDtoList: assayComponent),
                new ContextGroup(name: 'assayDetection', contextItemDtoList: assayDetection),
                new ContextGroup(name: 'assayReadout', contextItemDtoList: assayReadout),
                new ContextGroup(name: 'assayDetector', contextItemDtoList: assayDetector),
                new ContextGroup(name: 'assayFootprint', contextItemDtoList: assayFootprint),
                new ContextGroup(name: 'assayExcitation', contextItemDtoList: assayExcitation),
                new ContextGroup(name: 'assayAbsorbance', contextItemDtoList: assayAbsorbance),
                new ContextGroup(name: 'activity threshold', contextItemDtoList: resultActivityThreshold),
                new ContextGroup(name: 'number of replicates', contextItemDtoList: resultDetails),
                new ContextGroup(name: 'project lead name', contextItemDtoList: projectLeadName)
        ]


        return spreadsheetAssayContextGroups
    }
}
