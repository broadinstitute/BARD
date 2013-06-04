package bard.db.experiment

import bard.db.dictionary.Descriptor
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import org.apache.commons.lang.StringUtils

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

    // TODO: this is a temp changes, copy & pasted from assay context.
    // Due to the incomplete information of context_group and context_name given a projectcontext, we can not use them directly to display context detail.
    //  Need to be removed if data is completed.
    private static final List<String> KEY_LABELS = ['assay component role', 'assay component type', 'detection', 'assay readout', 'wavelength', 'number']

    private static final Map<String, String> KEY_LABEL_NAME_MAP = ['assay component role': 'label',
            'assay component type': 'label', 'detection': 'detection method',
            'assay readout': 'assay readout', 'wavelength': 'fluorescence/luminescence',
            'number': 'result detail']
    /**
     *
     * @return
     */
    Descriptor getPreferredDescriptor() {
        Descriptor preferredDescriptor
        List<Descriptor> preferredDescriptors = contextItems.collect { it.attributeElement.ontologyBreadcrumb.preferedDescriptor }
        preferredDescriptors = preferredDescriptors.findAll() // hack to eliminate any nulls (Elements (971 and 1329)
        for (String keyLabel in KEY_LABELS) {
            if (preferredDescriptors.any { it?.label?.contains(keyLabel) }) {
                preferredDescriptor = preferredDescriptors.find { it.label.contains(keyLabel) }
                break
            }
        }

        if (preferredDescriptor == null && preferredDescriptors) {
            preferredDescriptor = preferredDescriptors.first()
        }
        println("preferredDescriptor: ${preferredDescriptor.generateOntologyBreadCrumb()}")
        return preferredDescriptor
    }

    String getPreferredName() {
        String preferredName = 'undefined'
        if (StringUtils.isNotBlank(this.contextName)) {
            preferredName = this.contextName
        } else {
            preferredName = getPreferredDescriptor()?.label
            for (Map.Entry entry in KEY_LABEL_NAME_MAP) {
                if (preferredName && preferredName.contains(entry.key)) {
                    if ('label' != entry.value) {
                        preferredName = entry.value
                    }
                    break
                }
            }
        }
        return preferredName
    }

}
