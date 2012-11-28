import dataexport.registration.MediaTypesDTO
import dataexport.registration.ExternalReferenceExportService

// Place your Spring DSL code here
beans = {
    final MediaTypesDTO mediaTypesDTO = new MediaTypesDTO()
    mediaTypesDTO.elementMediaType = grailsApplication.config.bard.data.export.dictionary.element.xml
    mediaTypesDTO.stageMediaType = grailsApplication.config.bard.data.export.dictionary.stage.xml
    mediaTypesDTO.resultTypeMediaType = grailsApplication.config.bard.data.export.dictionary.resultType.xml
    mediaTypesDTO.assayMediaType = grailsApplication.config.bard.data.export.assay.xml
    mediaTypesDTO.assayDocMediaType = grailsApplication.config.bard.data.export.assay.doc.xml
    mediaTypesDTO.projectDocMediaType = grailsApplication.config.bard.data.export.project.doc.xml
    mediaTypesDTO.dictionaryMediaType = grailsApplication.config.bard.data.export.dictionary.xml
    mediaTypesDTO.assaysMediaType = grailsApplication.config.bard.data.export.assays.xml
    mediaTypesDTO.projectMediaType = grailsApplication.config.bard.data.export.project.xml
    mediaTypesDTO.projectsMediaType = grailsApplication.config.bard.data.export.projects.xml
    mediaTypesDTO.experimentsMediaType = grailsApplication.config.bard.data.export.experiments.xml
    mediaTypesDTO.experimentMediaType = grailsApplication.config.bard.data.export.experiment.xml
    mediaTypesDTO.resultsMediaType = grailsApplication.config.bard.data.export.results.xml
    mediaTypesDTO.resultMediaType = grailsApplication.config.bard.data.export.result.xml
    mediaTypesDTO.externalReferenceMediaType = grailsApplication.config.bard.data.export.externalreference.xml
    mediaTypesDTO.externalReferencesMediaType = grailsApplication.config.bard.data.export.externalreferences.xml
    mediaTypesDTO.externalSystemMediaType = grailsApplication.config.bard.data.export.externalsystem.xml
    mediaTypesDTO.externalSystemsMediaType = grailsApplication.config.bard.data.export.externalsystems.xml



    externalReferenceExportService(ExternalReferenceExportService) {
        mediaTypesDTO = mediaTypesDTO
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }
    final int maxExperimentsRecordsPerPage = grailsApplication.config.bard.experiments.max.per.page
    final int resultsRecordsPerPage = grailsApplication.config.bard.results.max.per.page
    resultExportService(dataexport.experiment.ResultExportService) {
        maxResultsRecordsPerPage = resultsRecordsPerPage
        mediaTypes = mediaTypesDTO
        grailsLinkGenerator = ref('grailsLinkGenerator')
        sessionFactory = ref('sessionFactory')
        dataSource = ref('dataSource')
        utilityService = ref('utilityService')
    }

    experimentExportService(dataexport.experiment.ExperimentExportService) {
        numberRecordsPerPage = maxExperimentsRecordsPerPage
        mediaTypeDTO = mediaTypesDTO
        grailsLinkGenerator = ref('grailsLinkGenerator')
        resultExportService = ref('resultExportService')
        utilityService = ref('utilityService')
    }
    //inject element mime type here
    dictionaryExportHelperService(dataexport.dictionary.DictionaryExportHelperService, mediaTypesDTO) {
        grailsLinkGenerator = ref('grailsLinkGenerator')
        dataSource = ref('dataSource')
    }

    rootService(dataexport.util.RootService, mediaTypesDTO) {
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }


    assayExportHelperService(dataexport.registration.AssayExportHelperService, mediaTypesDTO) {
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }

    projectExportService(dataexport.experiment.ProjectExportService) {
        grailsLinkGenerator = ref('grailsLinkGenerator')
        mediaTypesDTO = mediaTypesDTO
        utilityService = ref('utilityService')
    }
}
