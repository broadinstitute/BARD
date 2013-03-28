package bardwebquery

class CompoundBioActivitySummaryTagLib {
    def assayDescription = { attrs, body ->

        out << "<p>" << attrs.name << " (${attrs.adid})" << "</p>"
    }

    def projectDescription = { attrs, body ->

        out << "<p>" << attrs.name << " (${attrs.pid})" << "</p>"
    }

    def experimentDescription = { attrs, body ->

        out << "<p>" << attrs.name << " (${attrs.bardExperimentId})" << "</p>"
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

        out << "<p><b>${attrs?.title?.value?.left?.value ?: ''}"
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

        out << "<h5>${attrs.title.value.left.value}"
        if (attrs?.title?.dictionaryElement) {
            out << "<a href=\"${attrs?.title?.dictionaryElement.value}\" target=\"datadictionary\">"
            out << "<i class=\"icon-question-sign\"></i></a>"
        }
        out << "</h5>"

        int i = 0
        while (i < attrs.concentrationSeries.size()) {
            String responseUnit = attrs.responseUnit
            responseUnit = responseUnit?.trim()?.equalsIgnoreCase('percent') ? '%' : responseUnit //replace 'percent' with '%'
            out << "<p><small>${attrs.activitySeries[i]} ${attrs.responseUnit ? '[' + responseUnit + ']' : ''} @ ${attrs.concentrationSeries[i]} ${attrs.testConcentrationUnit}</small></p>"
            i++
        }
    }
}
