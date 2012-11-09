package bard.db.project

import bard.db.model.AbstractContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectContextItem extends AbstractContextItem {
    ProjectContext projectContext

    static belongsTo = [projectContext: ProjectContext]

    static mapping = {
        id(column: 'PROJECT_CONTEXT_ITEM_ID', generator: 'sequence', params: [sequence: 'PROJECT_CONTEXT_ITEM_ID_SEQ'])
        projectContext(column: 'PROJECT_CONTEXT_ID')
        attributeElement(column: 'ATTRIBUTE_ID')
        valueElement(column: 'VALUE_ID')
        qualifier(column: "QUALIFIER", sqlType: "char", length: 2)
    }
}
