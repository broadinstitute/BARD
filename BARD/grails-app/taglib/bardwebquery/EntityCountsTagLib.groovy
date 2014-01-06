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
