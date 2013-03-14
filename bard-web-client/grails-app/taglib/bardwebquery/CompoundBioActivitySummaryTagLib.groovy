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
//                            xAxisLabel: hillCurveValueHolder.xAxisLabel,
//                            yAxisLabel: hillCurveValueHolder.yAxisLabel,
//                            yNormMin: yMinimum,
//                            yNormMax: yMaximum
                    ]
            )
        }"/>
        """
    }

    def curveValues = { attrs, body ->

        out << "<h3>${attrs.title}</h3>"

        int i = 0
        while (i < attrs.concentrationSeries.size()) {
            out << "<p>${attrs.activitySeries[i]} @ ${attrs.concentrationSeries[i]} uM</p>"
            i++
        }
    }
}
