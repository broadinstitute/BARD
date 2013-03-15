import dataexport.registration.MediaTypesDTO
import dataexport.dictionary.DictionaryExportHelperService

// Place your Spring DSL code here
beans = {
//    def mediaTypesDTO = new MediaTypesDTO()
//    mediaTypesDTO.elementMediaType = grailsApplication.config.bard.data.export.dictionary.element.xml
//    mediaTypesDTO.stageMediaType = grailsApplication.config.bard.data.export.dictionary.stage.xml
//    mediaTypesDTO.resultTypeMediaType = grailsApplication.config.bard.data.export.dictionary.resultType.xml
//    mediaTypesDTO.assayMediaType = grailsApplication.config.bard.data.export.assay.xml
//    mediaTypesDTO.assayDocMediaType = grailsApplication.config.bard.data.export.assay.doc.xml
//    mediaTypesDTO.projectDocMediaType = grailsApplication.config.bard.data.export.project.doc.xml
//    mediaTypesDTO.dictionaryMediaType = grailsApplication.config.bard.data.export.dictionary.xml
//    mediaTypesDTO.assaysMediaType = grailsApplication.config.bard.data.export.assays.xml
//    mediaTypesDTO.projectMediaType = grailsApplication.config.bard.data.export.project.xml
//    mediaTypesDTO.projectsMediaType = grailsApplication.config.bard.data.export.projects.xml
//    mediaTypesDTO.experimentsMediaType = grailsApplication.config.bard.data.export.experiments.xml
//    mediaTypesDTO.experimentMediaType = grailsApplication.config.bard.data.export.experiment.xml
//    mediaTypesDTO.resultsMediaType = grailsApplication.config.bard.data.export.results.xml
//    mediaTypesDTO.resultMediaType = grailsApplication.config.bard.data.export.result.xml
//    mediaTypesDTO.externalReferenceMediaType = grailsApplication.config.bard.data.export.externalreference.xml
//    mediaTypesDTO.externalReferencesMediaType = grailsApplication.config.bard.data.export.externalreferences.xml
//    mediaTypesDTO.externalSystemMediaType = grailsApplication.config.bard.data.export.externalsystemediaTypesDTO.xml
//    mediaTypesDTO.externalSystemsMediaType = grailsApplication.config.bard.data.export.externalsystems.xml

    mediaTypesDTO(MediaTypesDTO){
        elementMediaType = '${bard.data.export.dictionary.element.xml}'
        stageMediaType = '${bard.data.export.dictionary.stage.xml}'
        resultTypeMediaType = '${bard.data.export.dictionary.resultType.xml}'
        assayMediaType = '${bard.data.export.assay.xml}'
        assayDocMediaType = '${bard.data.export.assay.doc.xml}'
        projectDocMediaType = '${bard.data.export.project.doc.xml}'
        dictionaryMediaType = '${bard.data.export.dictionary.xml}'
        assaysMediaType = '${bard.data.export.assays.xml}'
        projectMediaType = '${bard.data.export.project.xml}'
        projectsMediaType = '${bard.data.export.projects.xml}'
        experimentsMediaType = '${bard.data.export.experiments.xml}'
        experimentMediaType = '${bard.data.export.experiment.xml}'
        resultsMediaType = '${bard.data.export.results.xml}'
        resultMediaType = '${bard.data.export.result.xml}'
        externalReferenceMediaType = '${bard.data.export.externalreference.xml}'
        externalReferencesMediaType = '${bard.data.export.externalreferences.xml}'
        externalSystemMediaType = '${bard.data.export.externalsystem.xml}'
        externalSystemsMediaType = '${bard.data.export.externalsystems.xml}'
    }
    experimentExportService(dataexport.experiment.ExperimentExportService) {
        numberRecordsPerPage = '${bard.experiments.max.per.page}'
        mediaTypeDTO = ref('mediaTypesDTO')
        grailsLinkGenerator = ref('grailsLinkGenerator')
        utilityService = ref('utilityService')
    }
}