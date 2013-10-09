import dataexport.registration.MediaTypesDTO

// Place your Spring DSL code here
beans = {
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