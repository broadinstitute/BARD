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
                new ContextItemDto(key:'2/AH', value:'$/AH', attributeType:AttributeType.Free, typeIn:true, assignedName:"concentration point number"),
                new ContextItemDto(key:'2/AI', value:'$/AI', attributeType:AttributeType.Free, typeIn:true, assignedName:"number of replicates")
        ]

        List<ContextItemDto> activityThreshold = [
                new ContextItemDto(key:'2/Y', value:'$/Y',  attributeType:AttributeType.Fixed, assignedName: "result type"),
                new ContextItemDto(key:'2/Z', value:'$/Z', attributeType:AttributeType.Fixed, assignedName: "activity qualifier"),
                new ContextItemDto(key:'2/AA', value:'$/AA', attributeType:AttributeType.Free, typeIn: true, assignedName: "activity threshold"),
                new ContextItemDto(key:'2/AB', value:'$/AB', attributeType:AttributeType.Fixed, assignedName: "unit")
        ]

        List<ContextItemDto> projectLead = [
                new ContextItemDto(key:'2/AD', value:'$/AD', attributeType:AttributeType.Fixed, assignedName: "project lead")
        ]

        List<ContextGroup> spreadsheetExperimentContextGroups = [
                new ContextGroup(name: 'number of replicates', contextItemDtoList: numberOfReplicates),
                new ContextGroup(name: 'activity threshold', contextItemDtoList: activityThreshold),
                new ContextGroup(name: 'project lead', contextItemDtoList: projectLead)
                ]

        return spreadsheetExperimentContextGroups
    }
}
