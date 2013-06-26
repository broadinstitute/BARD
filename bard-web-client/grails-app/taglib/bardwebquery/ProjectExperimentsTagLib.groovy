package bardwebquery

import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.experiment.ExperimentSearch

class ProjectExperimentsTagLib {

    def displayExperimentsGroupedByAssay = { attrs, body ->
        Map<Assay,List<ExperimentSearch>> assayExperimentListMap = [:]
        attrs.assays.each { Assay assay ->
            List<ExperimentSearch> exptsForAssay = attrs.experiments.findAll { ExperimentSearch experiment -> experiment.bardAssayId == assay.bardAssayId }
            exptsForAssay.sort(true) { -(it.compounds) }
            assayExperimentListMap[assay] = exptsForAssay
        }
        List<Assay> sortedAssays = attrs.assays.sort { Assay assay ->
            List<ExperimentSearch> exptList = assayExperimentListMap[assay]
            if (exptList && exptList.first()) {
                -(exptList.first().compounds)
            } else {
                0
            }
        }
        out << render(template: "/tagLibTemplates/projectExperiments",
                model: [assayExperimentListMap: assayExperimentListMap, sortedAssays: sortedAssays, experimentTypes: attrs.experimentTypes])
    }

}