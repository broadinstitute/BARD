package bard.db.experiment

import bard.db.dictionary.Descriptor
import bard.db.dictionary.Element
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.apache.commons.lang.StringUtils
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/1/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentContext extends AbstractContext {

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

    @Override
    AbstractContextOwner getOwner() {
        return experiment
    }

    @Override
    String getSimpleClassName() {
        return "ExperimentContext"
    }

    @Override
    void addContextItem(AbstractContextItem item) {
        this.addToExperimentContextItems(item)
    }

    @Override
    Class<? extends AbstractContextItem> getItemSubClass() {
        return ExperimentContextItem
    }
}
