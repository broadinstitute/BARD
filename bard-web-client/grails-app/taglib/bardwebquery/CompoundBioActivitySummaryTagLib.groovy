package bardwebquery

import java.math.MathContext
import java.text.DecimalFormat

class CompoundBioActivitySummaryTagLib {
    def assayDescription = { attrs, body ->

        out << generateShortNameHTML(attrs.name, attrs.bardAssayId, attrs.adid, 'showAssay')
    }

    def projectDescription = { attrs, body ->

        out << generateShortNameHTML(attrs.name, attrs.bardProjectId, attrs.pid, 'showProject')
    }

    def experimentDescription = { attrs, body ->

        out << generateShortNameHTML(attrs.name, attrs.bardExptId, attrs.eid, 'showExperiment')
    }

    def curvePlot = { attrs, body ->

        out << """<img alt="curve here" title="title here"
                    src="${
            createLink(
                    controller: 'doseResponseCurve',
                    action: 'doseResponseCurve',
                    params: [
                            sinf: attrs?.curveFitParameters?.sInf,
                            s0: attrs?.curveFitParameters?.s0,
                            slope: attrs?.slope,
                            hillSlope: attrs?.curveFitParameters?.hillCoef,
                            concentrations: attrs.concentrationSeries,
                            activities: attrs.activitySeries,
                            xAxisLabel: attrs.xAxisLabel,
                            yAxisLabel: attrs.yAxisLabel,
                            yNormMin: attrs?.yNormMin,
                            yNormMax: attrs?.yNormMax
                    ]
            )
        }"/>
        """

        out << "<p style='padding-top: 10px;'><b>${attrs?.title?.value?.left?.value ?: ''}"
        if (attrs?.title?.dictionaryElement) {
            out << "<a href=\"${attrs?.title?.dictionaryElement.value}\" target=\"datadictionary\">"
            out << "<i class=\"icon-question-sign\"></i></a>"
        }
        out << ": ${attrs?.title?.value?.right?.value ?: ''}</b></p>"
        out << "<p>sinf: ${attrs?.curveFitParameters?.sInf ?: ''}</p>"
        out << "<p>s0: ${attrs?.curveFitParameters?.s0 ?: ''}</p>"
        out << "<p>hillSlope: ${attrs?.curveFitParameters?.hillCoef ?: ''}</p>"
    }

    def curveValues = { attrs, body ->

        String responseUnit = attrs.responseUnit
        responseUnit = responseUnit?.trim()?.equalsIgnoreCase('percent') ? '%' : responseUnit //replace 'percent' with '%'

        out << "<h5>${responseUnit}</h5>"


        out << "<table><tbody>"
        List sortedConcentrations = new ArrayList(attrs.concentrationSeries).sort()
        sortedConcentrations.each { Double conc ->
            int i = attrs.concentrationSeries.indexOf(conc)
            //Get the intValue and reminder of the value
            BigDecimal activity = new BigDecimal(attrs.activitySeries[i])
            String[] activitySplit = (new DecimalFormat("#.##")).format(activity).split(/\./)
            String activityIntValue = activitySplit[0]
            String activityReminder = activitySplit.size() > 1 ? activitySplit[1] : '00'
            BigDecimal concentration = new BigDecimal(attrs.concentrationSeries[i])
            String[] concentrationSplit = (new DecimalFormat("#.##")).format(concentration).split(/\./)
            String concentrationIntValue = concentrationSplit[0]
            String concentrationReminder = concentrationSplit.size() > 1 ? concentrationSplit[1] : '00'

            out << "<tr>"
            out << "<td><p style='text-align:right;'><small>${activityIntValue}</small></p></td>"
            out << "<td><p>.</p></td>"
            out << "<td><p style='text-align:left;'><small>${activityReminder}</small></p></td>"
            out << "<td><p>@</p></td>"
            out << "<td><p style='text-align:right;'><small>${concentrationIntValue}</small></p></td>"
            out << "<td><p>.</p></td>"
            out << "<td><p style='text-align:left;'><small>${concentrationReminder}</small></p></td>"
            out << "<td><p><small>${attrs.testConcentrationUnit}</small></p></td>"
            out << "</tr>"

            i++
        }
        out << "</tbody></table>"
    }

    String generateShortNameHTML(String name, Long bardId, Long capId, String action) {
        String[] nameWords = name.split()
        StringBuilder sb = new StringBuilder()

        if (nameWords.size() > 7) {
            sb.append("<p title='${name}' data-placement='bottom' data-toggle='tooltip'>${nameWords[0..6].join(' ')} ...")
        }
        else if (name) {
            sb.append("<p>${name}")
        }
        else {
            sb.append("<p>")
        }

        if (bardId && capId) {
            sb.append("""<a href="${createLink(controller: 'bardWebInterface', action: action, id: bardId)}"> (${capId})</a>""")
        }

        sb.append("</p>")
        return sb.toString()
    }
}
