package bardwebquery

class CompoundBioActivitySummaryTagLib {
    def assayDescription = { attrs, body ->

        out << "<p>" << attrs.name << "</p>"
    }

    def projectDescription = { attrs, body ->

        out << "<p>" << attrs.name << "</p>"
    }

    def experimentDescription = { attrs, body ->

        out << "<p>" << attrs.name << "</p>"
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
                            xAxisLabel: attrs.testConcentrationUnit,
                            yAxisLabel: attrs.responseUnit,
//                            yNormMin: yMinimum,
//                            yNormMax: yMaximum
                    ]
            )
        }"/>
        """
    }

    def curveValues = { attrs, body ->

        out << "<h5>${attrs.title}</h5>"

        int i = 0
        while (i < attrs.concentrationSeries.size()) {
            out << "<p><small>${attrs.activitySeries[i]} ${attrs.responseUnit ? '[' + attrs.responseUnit + ']' : ''} @ ${attrs.concentrationSeries[i]} ${attrs.testConcentrationUnit}</small></p>"
            i++
        }
    }
}
