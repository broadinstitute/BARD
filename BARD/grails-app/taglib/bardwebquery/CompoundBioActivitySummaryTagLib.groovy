/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bardwebquery

import bard.core.rest.spring.BiologyRestService
import bard.core.rest.spring.biology.BiologyEntity

import java.text.DecimalFormat

class CompoundBioActivitySummaryTagLib {

    BiologyRestService biologyRestService

    def assayDescription = { attrs, body ->
        out << generateShortNameHTML(attrs.assayAdapter?.name, attrs.assayAdapter?.bardAssayId, attrs.assayAdapter?.capAssayId, 'show', 'assayDefinition')
        out << "<p><b>Designed by:</b>${attrs.assayAdapter?.designedBy}</p>"
        List<BiologyEntity> biologyEntities = biologyRestService.convertBiologyId(attrs.assayAdapter?.biologyIds as List<Long>)
        String biology = biologyEntities.collect {BiologyEntity biologyEntity ->
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
        out << "<p><b>Biology:</b> ${biology}</p>"
    }

    def projectDescription = { attrs, body ->

        out << generateShortNameHTML(attrs.projectAdapter?.name, attrs.projectAdapter?.id, attrs.projectAdapter?.capProjectId, 'show', 'project')
    }

    def experimentDescription = { attrs, body ->

        out << generateShortNameHTML(attrs.name, attrs.capExptId, attrs.eid, 'show')
    }

    def curvePlot = { attrs, body ->

        out << """<img alt="${attrs?.title}" title="${attrs?.title}"
                    src="${
            createLink(
                    controller: 'doseResponseCurve',
                    action: 'doseResponseCurve',
                    params: [
                            slope: attrs?.slope, // We could have also made that Math.pow(10, attrs?.curveFitParameters?.logEc50). Since attrs.slope is actually the AC50/EC50/IC50 (concentration) value, this should work as well.
                            sinf: attrs?.curveFitParameters?.sInf,
                            s0: attrs?.curveFitParameters?.s0,
                            hillSlope: attrs?.curveFitParameters?.hillCoef,
                            concentrations: attrs.concentrationSeries,
                            activities: attrs.activitySeries,
                            xAxisLabel: attrs.xAxisLabel,
                            yAxisLabel: attrs.yAxisLabel,
                            yNormMin: attrs?.yNormMin,
                            yNormMax: attrs?.yNormMax
                    ]
            )
        }"
        style="min-width: 200px; min-height: 133px"/>
        """

        out << "<p class='lineSpacing' style='padding-top: 10px;'><b>${attrs?.title?.value?.left?.value ?: ''}"
        if (attrs?.title?.dictionaryElement) {
            out << "<a href=\"${attrs?.title?.dictionaryElement.value}\" target=\"datadictionary\">"
            out << "<i class=\"icon-question-sign\"></i></a>"
        }
        out << ": "
        out << "${attrs?.title?.value?.right?.value ?: ''}"
        out << "${attrs?.testConcentrationUnit ?: ''}"
        out << "</b></p>"
        out << "<p class='lineSpacing'>sinf: ${attrs?.curveFitParameters?.sInf ?: ''}</p>"
        out << "<p class='lineSpacing'>s0: ${attrs?.curveFitParameters?.s0 ?: ''}</p>"
        out << "<p class='lineSpacing'>hillSlope: ${attrs?.curveFitParameters?.hillCoef?.round(4) ?: ''}</p>"
        out << "<p class='lineSpacing'>logEc50: ${attrs?.curveFitParameters?.logEc50?.round(4) ?: ''}</p>"
    }

    def curveValues = { attrs, body ->

        String responseUnit = attrs.responseUnit
        responseUnit = responseUnit?.trim()?.equalsIgnoreCase('percent') ? '%' : responseUnit //replace 'percent' with '%'

        out << "<h5>${responseUnit}</h5>"


        out << "<table><tbody>"
        List sortedConcentrations = new ArrayList(attrs.concentrationSeries).sort()
        DecimalFormat df = new DecimalFormat("0.000E0")
        sortedConcentrations.each { Double conc ->
            int i = attrs.concentrationSeries.indexOf(conc)
            //Get the intValue and reminder of the value
            String activity = df.format(new BigDecimal(attrs.activitySeries[i]))
            String concentration = df.format(new BigDecimal(attrs.concentrationSeries[i]))

            out << "<tr>"
            out << "<td style='white-space: nowrap;'><p class='lineSpacing' style='text-align:right;'><small>${activity}</small></p></td>"
            out << "<td><p class='lineSpacing'>@</p></td>"
            out << "<td style='white-space: nowrap;'><p class='lineSpacing' style='text-align:right;'><small>${concentration}</small></p></td>"
            out << "<td><p class='lineSpacing'><small>${attrs.testConcentrationUnit}</small></p></td>"
            out << "</tr>"

            i++
        }
        out << "</tbody></table>"
    }

    String generateShortNameHTML(String name, Long bardId, Long capId, String action, String controller = "experiment") {
        if(name == null) {
            name = "";
        }
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
            if(controller == "bardWebInterface") {
                sb.append("""<a href="${createLink(controller: controller, action: action, id: bardId)}"><em> (${capId})</em></a>""")
            } else {
                sb.append("""<a href="${createLink(controller: controller, action: action, id: capId)}"><em> (${capId})</em></a>""")
            }
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
