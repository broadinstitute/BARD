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
 * Assay should have at least 1 context that defines biology
 */
class AssayBiologyGuidanceRule implements AssayGuidanceRule {

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
            guidance.add( new DefaultGuidanceImpl('All assays should have at least 1 Context that defines the biology.'))
        }
        guidance
    }
}
