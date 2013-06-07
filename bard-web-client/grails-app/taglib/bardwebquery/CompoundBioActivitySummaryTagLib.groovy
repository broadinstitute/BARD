package bardwebquery

import bard.core.rest.spring.BiologyRestService
import bard.core.rest.spring.biology.BiologyEntity

import java.text.DecimalFormat

class CompoundBioActivitySummaryTagLib {

    BiologyRestService biologyRestService

    def assayDescription = { attrs, body ->

        out << generateShortNameHTML(attrs.assayAdapter?.title, attrs.assayAdapter?.bardAssayId, attrs.assayAdapter?.capAssayId, 'showAssay')
        out << "<p><b>Designed by:</b>${attrs.assayAdapter?.designedBy}</p>"
        List<BiologyEntity> biologyEntities = biologyRestService.convertBiologyId(attrs.assayAdapter?.targetIds as List<Long>)
        String targets = biologyEntities.collect {BiologyEntity biologyEntity ->
            switch (biologyEntity.biology) {
                case 'PROCESS':
                    return "${biologyEntity.name} [${biologyEntity.biology.toLowerCase()}: <a href='http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=${biologyEntity.extId}'>${biologyEntity.extId}</a>]"
                    break;
                case 'PROTEIN':
                    return "${biologyEntity.name} [${biologyEntity.biology.toLowerCase()}: <a href='http://www.uniprot.org/uniprot/?query=${biologyEntity.extId}'>${biologyEntity.extId}</a>]"
                    break;
                default:
                    return "${biologyEntity.name} [${biologyEntity.biology.toLowerCase()}: ${biologyEntity.extId}]"
            }
        }.join(', ')
        out << "<p><b>Targets:</b> ${targets}</p>"
    }

    def projectDescription = { attrs, body ->

        out << generateShortNameHTML(attrs.projectAdapter?.name, attrs.projectAdapter?.id, attrs.projectAdapter?.capProjectId, 'showProject')
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

        out << "<p class='lineSpacing' style='padding-top: 10px;'><b>${attrs?.title?.value?.left?.value ?: ''}"
        if (attrs?.title?.dictionaryElement) {
            out << "<a href=\"${attrs?.title?.dictionaryElement.value}\" target=\"datadictionary\">"
            out << "<i class=\"icon-question-sign\"></i></a>"
        }
        out << ": ${attrs?.title?.value?.right?.value ?: ''}</b></p>"
        out << "<p class='lineSpacing'>sinf: ${attrs?.curveFitParameters?.sInf ?: ''}</p>"
        out << "<p class='lineSpacing'>s0: ${attrs?.curveFitParameters?.s0 ?: ''}</p>"
        out << "<p class='lineSpacing'>hillSlope: ${attrs?.curveFitParameters?.hillCoef ?: ''}</p>"
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
            out << "<td><p class='lineSpacing' style='text-align:right;'><small>${activityIntValue}</small></p></td>"
            out << "<td><p class='lineSpacing'>.</p></td>"
            out << "<td><p class='lineSpacing' style='text-align:left;'><small>${activityReminder}</small></p></td>"
            out << "<td><p class='lineSpacing'>@</p></td>"
            out << "<td><p class='lineSpacing' style='text-align:right;'><small>${concentrationIntValue}</small></p></td>"
            out << "<td><p class='lineSpacing'>.</p></td>"
            out << "<td><p class='lineSpacing' style='text-align:left;'><small>${concentrationReminder}</small></p></td>"
            out << "<td><p class='lineSpacing'><small>${attrs.testConcentrationUnit}</small></p></td>"
            out << "</tr>"

            i++
        }
        out << "</tbody></table>"
    }

    String generateShortNameHTML(String name, Long bardId, Long capId, String action) {
        String[] nameWords = name.split()
        StringBuilder sb = new StringBuilder()

        if (nameWords.size() > 7) {
            sb.append("<p title='${name}' data-placement='bottom' data-toggle='tooltip'><em>${nameWords[0..6].join(' ')} ...</em>")
        }
        else if (name) {
            sb.append("<p><em>${name}</em>")
        }
        else {
            sb.append("<p>")
        }

        if (bardId && capId) {
            sb.append("""<a href="${createLink(controller: 'bardWebInterface', action: action, id: bardId)}"><em> (${capId})</em></a>""")
        }

        sb.append("</p>")
        return sb.toString()
    }

    def generateLinksList = { attrs, body ->

        String controller = attrs.controller
        String action = attrs.action
        List ids = attrs.ids

        List<String> idsLinkStringList = []
        for (def id in ids) {
            String idLinkString = "<a href='${createLink(controller: controller, action: action, id: id)}'>${id}</a>"
            idsLinkStringList << idLinkString
        }
        out << "<a class='linksListPopup' data-content=\"${idsLinkStringList.join(', ')}\" data-placement='top' data-toggle='popover' data-trigger='hover' href='#' data-html='true'>${idsLinkStringList.size()}</a>"
    }
}
