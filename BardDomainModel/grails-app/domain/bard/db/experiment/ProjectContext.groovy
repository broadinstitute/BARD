package bard.db.experiment

import bard.db.model.AbstractContext

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/1/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectContext extends AbstractContext{

    Project project
    List<ProjectContextItem> projectContextItems = []

    static belongsTo = [project: Project]

    static hasMany = [projectContextItems: ProjectContextItem]

    static mapping = {
        id(column: "PROJECT_CONTEXT_ID", generator: "sequence", params: [sequence: 'PROJECT_CONTEXT_ID_SEQ'])
        projectContextItems(indexColumn: [name: 'DISPLAY_ORDER'])
    }

}
