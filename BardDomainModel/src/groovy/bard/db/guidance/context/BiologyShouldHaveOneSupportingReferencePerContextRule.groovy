package bard.db.guidance.context

import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceReporter
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/9/13
 * Time: 2:57 PM
 * For any given Context,
 *   if the context contains an item with a 'biology' based on the value associated with biology
 *   then there should be a another contextItem in the context with a supporting reference
 *   So biological process should try and reference specific biological process term
 *   or a molecular target should try and reference a specifc molecular target term
 */
class BiologyShouldHaveOneSupportingReferencePerContextRule implements GuidanceReporter {

    private static final String BIOLOGY_LABEL = 'biology'
    private static final List<String> BIOLOGICAL_PROCESS_LABELS = ['biological process']
    private static final List<String> BIOLOGICAL_PROCESS_TERMS = [
            'GO biological process term',
            'NCBI BioSystems term'
    ]

    private static final List<String> MOLECULAR_TARGET_LABELS = [
            'molecular interaction',
            'molecular target',
            'macromolecule',
            'antibody',
            'gene',
            'nucleotide',
            'peptide',
            'protein-DNA complex',
            'protein-reporter complex',
            'protein'
    ]
    private static final List<String> MOLECULAR_TARGET_TERMS = [
            'GO gene-product ID',
            'GenBank ID',
            'NCBI accession number',
            'UniProt accession number',
            'gene Entrez GI',
            'protein Entrez GI'
    ]


    private static final String BIOLOGICAL_PROCESS_SHOULD_HAVE_ONE_BIOLOGICAL_PROCESS_TERM_MSG = "When a context has biology defined as a 'biological process', one other item in the text should reference one of these terms ( ${BIOLOGICAL_PROCESS_TERMS.join(',')} )."
    private static final String MOLECULAR_TARGETS_SHOULD_HAVE_ONE_BIOLOGICAL_PROCESS_TERM_MSG = "When a context has biology defined as a something that can be considered a molecular target,  one other item in the text should reference one of these terms ( ${MOLECULAR_TARGET_TERMS.join(',')} )."

    AbstractContext context

    BiologyShouldHaveOneSupportingReferencePerContextRule(AbstractContext context) {
        this.context = context
    }

    @Override
    List<Guidance> getGuidance() {
        List<Guidance> guidanceList = []
        if (BIOLOGY_LABEL == context.contextType?.label) {
            // find first item with 'biology' = 'biological process'
            final List<AbstractContextItem> biologicalProcessItems = context.contextItems.findAll { AbstractContextItem item ->
                item.attributeElement.label == BIOLOGY_LABEL && item.valueElement?.label in BIOLOGICAL_PROCESS_LABELS
            }
            // find first item with 'biology' = 'biological process'
            final List<AbstractContextItem> molecularTargetItems = context.contextItems.findAll { AbstractContextItem item ->
                item.attributeElement.label == BIOLOGY_LABEL && item.valueElement?.label in MOLECULAR_TARGET_LABELS
            }
            // if  the biology is a biological process 'biology' = 'biological process'
            if (biologicalProcessItems) {
                final List<AbstractContextItem> biologicalProcessTerms = context.contextItems.findAll { AbstractContextItem item ->
                    item.attributeElement.label in BIOLOGICAL_PROCESS_TERMS
                }
                if(biologicalProcessTerms.size()!=1){
                    guidanceList.add(new DefaultGuidanceImpl(BIOLOGICAL_PROCESS_SHOULD_HAVE_ONE_BIOLOGICAL_PROCESS_TERM_MSG))
                }
            } else if (molecularTargetItems) { // else if  the biology is a molecular target e.g. 'biology' = 'molecular interaction'
                final List<AbstractContextItem> molecularTargetTerms = context.contextItems.findAll { AbstractContextItem item ->
                    item.attributeElement.label in MOLECULAR_TARGET_TERMS
                }
                if(molecularTargetTerms.size()!=1) {
                    guidanceList.add(new DefaultGuidanceImpl(MOLECULAR_TARGETS_SHOULD_HAVE_ONE_BIOLOGICAL_PROCESS_TERM_MSG))
                }
            }
        }
        return guidanceList
    }
}
