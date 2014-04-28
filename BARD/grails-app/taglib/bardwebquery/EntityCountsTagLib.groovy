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

import bardqueryapi.QueryService

class EntityCountsTagLib {


    QueryService queryService

    def projectCount = { attrs, body ->
        long projects = queryService.numberOfProjects()
        if (projects > 0) {
            String link = generateLink(projects, "Projects", "#tab-projects")

            out << "${link}"
        } else {
            out << "Warehouse server is unavailable"
        }
    }

    def assayCount = { attrs, body ->
        long assays = queryService.numberOfAssays()
        if (assays > 0) {
            String link = generateLink(assays, "Assay Definitions", "#tab-definitions")
            out << "${link}"
        } else {
            out << "Warehouse server is unavailable"
        }
    }

    def experimentCount = { attrs, body ->
        long experiments = queryService.numberOfExperiments()
        if (experiments > 0) {
            String link = generateLink(experiments, "Experiments", "#tab-experiments")
            out << "${link}"
        } else {
            out << "Warehouse server is unavailable"
        }
    }

    def exptDataCount = { attrs, body ->
        long exptData = queryService.numberOfExperimentData()
        if (exptData > 0) {
            String link = generateLink(exptData, "Number of results", "#tab-results")
            out << "${link}"
        } else {
            out << "Warehouse server is unavailable"
        }
    }


    def probeCount = { attrs, body ->
        Long probes = queryService.totalNumberOfProbes()
        String link = generateLink(probes, "Number of Probes", "#tab-probes")
        out << "${link}"
    }
    def probeCIDCount = { attrs, body ->
        long probes = queryService.numberOfProbeCompounds()
        String link = generateLink(probes, "Number of Probe Compounds", "#tab-probes")
        out << "${link}"
    }
    String generateLink(Long number, String displayName, String tabName) {
        StringBuilder sb = new StringBuilder()
        def num = formatNumber(number: number, type: "number", groupingUsed: true)
        sb.append("<a href='${tabName}' data-toggle='tab'><span> <strong class='number'>${num}</strong> ${displayName}</span><i class='arrow'></i></a>")
        return sb.toString()
    }


}
