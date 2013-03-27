package bard.db.project

import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/30/12
 * Time: 12:21 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectExperimentContextItem extends AbstractContextItem {
    ProjectExperimentContext context

    static belongsTo = [context: ProjectExperimentContext]

    static mapping = {
        table('PRJCT_EXPRMT_CNTXT_ITEM')
        id(column: 'PRJCT_EXPRMT_CNTXT_ITEM_ID', generator: 'sequence', params: [sequence: 'PRJCT_EXPRMT_CNTXT_ITEM_ID_SEQ'])
        valueElement(column: "value_id", fetch: 'join')
        attributeElement(column: "attribute_id", fetch: 'join')
        qualifier(column: "qualifier", sqlType: "char", length: 2)
        context(column: 'PRJCT_EXPRMT_CONTEXT_ID')
    }

    @Override
    void setContext(AbstractContext context) {
        this.context = context
    }
}
