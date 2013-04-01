package maas

import bard.db.registration.AttributeType

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/1/13
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
class ContextGroupsBuilder {
    /**
     * define all columns that holds experiment information
     * @return
     */
   static List<ContextGroup> buildExperimentContextGroup() {
        List<ContextItemDto> numberOfReplicates = [
                new ContextItemDto(key:'2/AH', value:'$/AH', attributeType:AttributeType.Free, typeIn:true, assignedName:"concentration-point number"),
                new ContextItemDto(key:'2/AI', value:'$/AI', attributeType:AttributeType.Free, typeIn:true, assignedName:"number of replicates")
        ]

        List<ContextItemDto> activityThreshold = [
                new ContextItemDto(key:'2/Y', value:'$/Y',  attributeType:AttributeType.Fixed, assignedName: "result type"),
//                new ContextItemDto(key:'2/Z', value:'$/Z', attributeType:AttributeType.Fixed, assignedName: "activity qualifier"),
//                new ContextItemDto(key:'2/AA', value:'$/AA', attributeType:AttributeType.Free, typeIn: true, assignedName: "activity threshold"),
                new ContextItemDto(key:'2/AA', value:'$/AA', attributeType:AttributeType.Free, typeIn: false, qualifier: '$/Z', assignedName:"activity threshold"),
                new ContextItemDto(key:'2/AB', value:'$/AB', attributeType:AttributeType.Fixed, assignedName: "unit")
        ]

        List<ContextItemDto> projectLead = [             //  treat it as free, it should be an external url to Person domain, fix all of these by running another script
                new ContextItemDto(key:'2/AD', value:'$/AD', attributeType:AttributeType.Fixed, typeIn: true, assignedName: "project lead name")
               // new ContextItemDto(key:'2/AD', value:'$/AD', attributeType:AttributeType.Fixed, assignedName: "project lead name")
        ]

        List<ContextGroup> contextGroups = [
                new ContextGroup(name: 'number of replicates', contextItemDtoList: numberOfReplicates),
                new ContextGroup(name: 'activity threshold', contextItemDtoList: activityThreshold),
                new ContextGroup(name: 'project lead', contextItemDtoList: projectLead)
                ]

        return contextGroups
    }

    static List<ContextGroup> buildProjectContextGroup() {
        List<ContextItemDto> biology = [
                new ContextItemDto(key: '2/AE', value: '$/AE', attributeType: AttributeType.Free, typeIn: true, assignedName: "biology")
        ]

        List<ContextItemDto> intendedModeOfAction = [
                new ContextItemDto(key: '2/AF', value: '$/AF', attributeType: AttributeType.Fixed, assignedName: "intended mode-of-action")
        ]

        List<ContextItemDto> projectManagement = [  //  treat it as free, it should be an external url to Person domain, fix all of these by running another script
                new ContextItemDto(key: '2/AW', value: '$/AW', attributeType: AttributeType.Fixed, typeIn: true, assignedName: "assay provider name"),
                new ContextItemDto(key: '2/AX', value: '$/AX', attributeType: AttributeType.Fixed, typeIn: true, assignedName: "science officer")
//                new ContextItemDto(key: '2/AW', value: '$/AW', attributeType: AttributeType.Fixed, assignedName: "assay provider name"),
//               new ContextItemDto(key: '2/AX', value: '$/AX', attributeType: AttributeType.Fixed, assignedName: "science officer")

        ]

        List<ContextGroup> contextGroups = [
                new ContextGroup(name: 'biology', contextItemDtoList: biology),
                new ContextGroup(name: 'intended mode-of-action', contextItemDtoList: intendedModeOfAction),
                new ContextGroup(name: 'project management', contextItemDtoList: projectManagement)
        ]
        return contextGroups
    }

    // although stage is not a context item, just use this to parse spreadsheet
    static List<ContextGroup> buildProjectExperimentStage() {
        List<ContextItemDto> stage = [
                new ContextItemDto(key: '2/AG', value: '$/AG', attributeType: AttributeType.Free, typeIn: true, assignedName: "biology")
        ]

        List<ContextGroup> contextGroups = [
                new ContextGroup(name: 'dummy', contextItemDtoList: stage),
        ]
        return contextGroups
    }
}
