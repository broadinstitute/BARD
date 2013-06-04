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
                new ContextItemDto('1/E', '$/E', AttributeType.Fixed),
                new ContextItemDto('2/F', '$/F', AttributeType.Fixed)
        ]

        List<ContextItemDto> assayComponent = [
                new ContextItemDto('2/G', '$/G', AttributeType.Fixed),
                new ContextItemDto('2/H', '$/H', AttributeType.Fixed),        // CID can be here , assay component type
                new ContextItemDto('2/I', '$/I', AttributeType.Fixed, true, null, null),
                new ContextItemDto('2/J', '$/J', AttributeType.Fixed, true, null, '$/K'), //concentration + units
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
                new ContextItemDto('2/V', '$/V', AttributeType.Fixed, true, null),
                new ContextItemDto('2/W', '$/W', AttributeType.Fixed, true, null)
        ]

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
                new ContextGroup("biology", "biology>", biology),
                new ContextGroup("assay format", "assay protocol> assay format>", assayFormat),
                new ContextGroup("assay component", "assay protocol> assay component>", assayComponent),
                new ContextGroup("detection method type", "assay protocol> assay readout>",detectionMethodType),
                new ContextGroup("assay readout", "assay protocol> assay readout>", assayReadout),
                new ContextGroup("detection method", "assay protocol> assay readout>", detectionMethod),
                new ContextGroup("assay footprint", "assay protocol> assay design>", assayFootprint),
                new ContextGroup("measurement wavelength", "assay protocol> assay readout>", measurementWavelength),
                new ContextGroup("absorbance wavelength", "assay protocol> assay readout> ", absorbanceWavelength),
                new ContextGroup("activity threshold", "project management> experiment>", activityThreshold),
                new ContextGroup("number of replicates", "project management> experiment>", numberOfReplicates),
                new ContextGroup("project lead name", "project management> project information>", projectLeadName)
        ]


        return spreadsheetAssayContextGroups
    }
}
