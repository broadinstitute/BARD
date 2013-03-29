package bard.db.project

import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/1/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectExperimentContext extends AbstractContext{

    ProjectExperiment projectExperiment
    List<ProjectExperimentContextItem> contextItems = []

    static belongsTo = [projectExperiment: ProjectExperiment]

    static hasMany = [contextItems: ProjectExperimentContextItem]

    static mapping = {
        table('PRJCT_EXPRMT_CONTEXT')
        id(column: "PRJCT_EXPRMT_CONTEXT_ID", generator: "sequence", params: [sequence: 'PRJCT_EXPRMT_CONTEXT_ID_SEQ'])
        contextItems(indexColumn: [name: 'DISPLAY_ORDER'])
    }

    @Override
    AbstractContextOwner getOwner() {
        return projectExperiment
    }
}
