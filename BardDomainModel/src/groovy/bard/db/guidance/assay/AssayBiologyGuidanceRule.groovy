package bard.db.guidance.assay

import bard.db.dictionary.Element
import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.registration.Assay

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/9/13
 * Time: 2:42 PM
 *
 * Assay should have at least 1 context that defines biology, they can have more than 1 but need at least 1
 */
class AssayBiologyGuidanceRule implements AssayGuidanceRule {

    private static final String ONE_BIOLOGY_ATTRIBUTE_REQUIRED = 'All assays should have at least 1 Context that defines the biology.'
    Assay assay

    AssayBiologyGuidanceRule(Assay assay){
        this.assay = assay
    }

    @Override
    List<Guidance> getGuidance() {
        final List<Guidance> guidance = []
        final Element biology = Element.findByLabel('biology')
        List<Element> contextTypes = assay.assayContexts.collect{it.getContextType()}
        if( contextTypes.find{it == biology } == null ){
            guidance.add( new DefaultGuidanceImpl(ONE_BIOLOGY_ATTRIBUTE_REQUIRED))
        }
        guidance
    }
}
