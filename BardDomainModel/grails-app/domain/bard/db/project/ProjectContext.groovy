package bard.db.project

import bard.db.guidance.Guidance
import bard.db.guidance.context.BiologyShouldHaveOneSupportingReferencePerContextRule
import bard.db.guidance.context.OneBiologyAttributePerContextRule
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/1/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectContext extends AbstractContext {
    Project project
    List<ProjectContextItem> contextItems = []

    static belongsTo = [project: Project]

    static hasMany = [contextItems: ProjectContextItem]

    static mapping = {
        id(column: "PROJECT_CONTEXT_ID", generator: "sequence", params: [sequence: 'PROJECT_CONTEXT_ID_SEQ'])
        contextItems(indexColumn: [name: 'DISPLAY_ORDER'])
    }

    @Override
    AbstractContextOwner getOwner() {
        return project
    }

    @Override
    String getSimpleClassName() {
        return "ProjectContext"
    }

    @Override
    void addContextItem(AbstractContextItem item) {
        addToContextItems(item)
    }

    @Override
    Class<? extends AbstractContextItem> getItemSubClass() {
        return ProjectContextItem
    }

    @Override
    List<Guidance> getGuidance() {
        List<Guidance> guidanceList = super.getGuidance()
        guidanceList.add(new OneBiologyAttributePerContextRule(this).getGuidance())
        guidanceList.add(new BiologyShouldHaveOneSupportingReferencePerContextRule(this).getGuidance())
        guidanceList.flatten()
    }
}
