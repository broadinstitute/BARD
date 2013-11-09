package bardwebquery

import bardqueryapi.QueryService

class EntityCountsTagLib {


    QueryService queryService

    def projectCount = { attrs, body ->
        long projects = queryService.numberOfProjects()
        String link = generateLink(projects, "Projects", "#tab-projects")

        out << "${link}"
    }

    def assayCount = { attrs, body ->
        long assays = queryService.numberOfAssays()
        String link = generateLink(assays, "Assay Definitions", "#tab-definitions")
        out << "${link}"
    }

    def experimentCount = { attrs, body ->
        long experiments = queryService.numberOfExperiments()
        String link = generateLink(experiments, "Experiments", "#tab-experiments")
        out << "${link}"
    }

    def exptDataCount = { attrs, body ->
        long exptData = queryService.numberOfExperimentData()
        String link = generateLink(exptData, "Number of results", "#tab-results")
        out << "${link}"
    }


    def probeCount = { attrs, body ->
        long probes = queryService.numberOfProbes()
        String link = generateLink(probes, "Number of Probes", "#tab-probes")
        out << "${link}"
    }

    String generateLink(Long number, String displayName, String tabName) {
        StringBuilder sb = new StringBuilder()
        def num = formatNumber(number: number, type: "number", groupingUsed: true)
        sb.append("<a href='${tabName}' data-toggle='tab'><span> <strong class='number'>${num}</strong> ${displayName}</span><i class='arrow'></i></a>")
        return sb.toString()
    }


}
