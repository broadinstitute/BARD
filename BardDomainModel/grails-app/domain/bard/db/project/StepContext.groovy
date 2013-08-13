package bard.db.project

import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/2/12
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */
class StepContext extends AbstractContext {

    ProjectStep projectStep

    List<StepContextItem> stepContextItems = []

    static belongsTo = [projectStep: ProjectStep]

    static hasMany = [stepContextItems: StepContextItem]

    static mapping = {
        table('STEP_CONTEXT')
        id(column: "STEP_CONTEXT_ID", generator: "sequence", params: [sequence: 'STEP_CONTEXT_ID_SEQ'])
        stepContextItems(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    @Override
    List<StepContextItem> getContextItems() {
        return getStepContextItems()
    }

    @Override
    AbstractContextOwner getOwner() {
        return projectStep
    }

    @Override
    String getSimpleClassName() {
        return "StepContext"
    }

    @Override
    void addContextItem(AbstractContextItem item) {
        this.addToStepContextItems(item)
    }

    @Override
    Class<? extends AbstractContextItem> getItemSubClass() {
        return StepContextItem
    }
}
