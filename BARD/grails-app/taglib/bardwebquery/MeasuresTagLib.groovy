package bardwebquery

import bard.core.rest.spring.assays.Context
import bard.core.rest.spring.assays.Measure

class MeasuresTagLib {

    def displayMeasures = { attrs, body ->
        List<Measure> topLevelMeasures = attrs.measures.findAll { it.parent == null }
        this.renderMeasures(topLevelMeasures)
    }

    private void renderMeasures(List<Measure> measures) {
        out << "<ul>"
        measures.each { Measure measure ->
            out << "<li>" << measure.comps.first().display
            if (!measure.relatedContexts.isEmpty()) {
                out << " (linked to contexts: "
                measure.relatedContexts.eachWithIndex { Context context, int i ->
                    out << "<a href=\"#card-" << context.id << "\">" << context.name << "</a>"
                    if (i < measure.relatedContexts.size()-1) {
                        out << ", "
                    }
                }
                out << ")"
            }
            out << "</li>"
            if (!measure.children.isEmpty()) {
                this.renderMeasures(measure.children)
            }
        }
        out << "</ul>"
    }
}
