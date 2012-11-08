package bard.db.experiment

import bard.db.registration.AbstractContext

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
}
