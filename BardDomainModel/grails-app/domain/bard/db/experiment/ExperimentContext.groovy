package bard.db.experiment

import bard.db.model.AbstractContext

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/1/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentContext extends AbstractContext{

    Experiment experiment
    List<ExperimentContextItem> experimentContextItems = []

    static belongsTo = [experiment: Experiment]

    static hasMany = [experimentContextItems: ExperimentContextItem]

    static mapping = {
        table('EXPRMT_CONTEXT')
        id(column: "EXPRMT_CONTEXT_ID", generator: "sequence", params: [sequence: 'EXPRMT_CONTEXT_ID_SEQ'])
        experimentContextItems(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    @Override
    List<ExperimentContextItem> getContextItems() {
        return getExperimentContextItems()
    }
}
