package bard.db.project

import bard.db.model.AbstractContext
import bard.db.dictionary.Descriptor
import bard.db.model.AbstractContextOwner
import org.apache.commons.lang.StringUtils

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/1/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectContext extends AbstractContext{
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
}
