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
        List<ContextItemDto> biology = [
                new ContextItemDto('2/C', '$/C', AttributeType.Fixed, true, null, null),
                new ContextItemDto('$/C', '$/D', AttributeType.Fixed, true, null, null)  //  biology value
        ]

        List<ContextItemDto> assayFormat = [
                new ContextItemDto('1/E', '$/E', AttributeType.Fixed), new ContextItemDto('2/F', '$/F', AttributeType.Fixed)
        ]

        List<ContextItemDto> assayComponent = [
                new ContextItemDto('2/G', '$/G', AttributeType.Fixed),
                new ContextItemDto('2/H', '$/H', AttributeType.Fixed),        // CID can be here , assay component type
                new ContextItemDto('2/I', '$/I', AttributeType.Fixed, true, null, null),
                new ContextItemDto('2/J', '$/J', AttributeType.Fixed, true, null, '$/K'), //concentration + units
//                new Attribute('2/K', '$/K', AttributeType.Fixed),
                new ContextItemDto('2/L', '$/L', AttributeType.Fixed, true, null, null),
                new ContextItemDto('2/M', '$/M', AttributeType.Fixed, true, null, null)      // Species
        ]

        List<ContextItemDto> detectionMethod = [
                new ContextItemDto('2/O', '$/O', AttributeType.Fixed),
                new ContextItemDto('$/O', '$/N', AttributeType.Fixed, true, null, null)
        ]

        List<ContextItemDto> detectionMethodType = [
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

        List<ContextItemDto> measurementWavelength = [
                new ContextItemDto('2/V', '$/V', AttributeType.Fixed, true, null,'2/BI'),//The wavelength units are fixed in cell 2/BI
                new ContextItemDto('2/W', '$/W', AttributeType.Fixed, true, null,'2/BI')
        ]//The wavelength units are fixed in cell 2/BI

        List<ContextItemDto> absorbanceWavelength = [
                new ContextItemDto('2/X', '$/X', AttributeType.Fixed)
        ]

        List<ContextItemDto> activityThreshold = [
                new ContextItemDto('2/Y', '$/Y', AttributeType.Free),            //result type
                new ContextItemDto('2/AA', '$/AA', AttributeType.Free),         //activity threshold, column "Z" should not be in assay context item
                new ContextItemDto('2/AB', '$/AB', AttributeType.Free)                     //unit
        ]//the qualifier belongs to the Activity-threshold attribute

        List<ContextItemDto> numberOfReplicates = [
                new ContextItemDto('2/AH', '$/AH', AttributeType.Free),       //concentration-point number
                new ContextItemDto('2/AI', '$/AI', AttributeType.Free)        //number of replicates
        ]

        List<ContextItemDto> projectLeadName = [                             //project lead name
                new ContextItemDto('2/AD', '$/AD', AttributeType.Free)
        ]

        List<ContextGroup> spreadsheetAssayContextGroups = [
                new ContextGroup(name: 'biology', contextItemDtoList: biology),
                new ContextGroup(name: 'assay format', contextItemDtoList: assayFormat),
                new ContextGroup(name: 'assay component', contextItemDtoList: assayComponent),
                new ContextGroup(name: 'detection method type', contextItemDtoList: detectionMethodType),
                new ContextGroup(name: 'assay readout', contextItemDtoList: assayReadout),
                new ContextGroup(name: 'detection method', contextItemDtoList: detectionMethod),
                new ContextGroup(name: 'assay footprint', contextItemDtoList: assayFootprint),
                new ContextGroup(name: 'measurement wavelength', contextItemDtoList: measurementWavelength),
                new ContextGroup(name: 'absorbance wavelength', contextItemDtoList: absorbanceWavelength),
                new ContextGroup(name: 'activity threshold', contextItemDtoList: activityThreshold),
                new ContextGroup(name: 'number of replicates', contextItemDtoList: numberOfReplicates),
                new ContextGroup(name: 'project lead name', contextItemDtoList: projectLeadName)
        ]


        return spreadsheetAssayContextGroups
    }
}
