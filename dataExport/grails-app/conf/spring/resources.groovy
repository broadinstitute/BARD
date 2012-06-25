import dataexport.registration.AssayDefinitionMediaTypesDTO

// Place your Spring DSL code here
beans = {
    final String elementMediaType = grailsApplication.config.bard.data.export.dictionary.element.xml
    final String resultTypeMediaType = grailsApplication.config.bard.data.export.dictionary.resultType.xml
    final String assayMediaType = grailsApplication.config.bard.data.export.assay.xml
    final String assayDocMediaType = grailsApplication.config.bard.data.export.assay.doc.xml
    String dictionaryMediaType = grailsApplication.config.bard.data.export.dictionary.xml
    final String assaysMediaType = grailsApplication.config.bard.data.export.assays.xml
    final String projectMediaType = grailsApplication.config.bard.data.export.project.xml
    final String projectsMediaType = grailsApplication.config.bard.data.export.projects.xml

    //inject element mime type here
    dictionaryExportHelperService(dataexport.dictionary.DictionaryExportHelperService, elementMediaType) {
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }

    rootService(dataexport.util.RootService, dictionaryMediaType, assaysMediaType, projectsMediaType) {
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }

    AssayDefinitionMediaTypesDTO assayDefinitionMediaTypesDTO = new AssayDefinitionMediaTypesDTO(elementMediaType, resultTypeMediaType,
            assayMediaType, assayDocMediaType,
            assaysMediaType)
    assayExportHelperService(dataexport.registration.AssayExportHelperService, assayDefinitionMediaTypesDTO) {
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }

    projectExportService(dataexport.experiment.ProjectExportService, projectsMediaType, projectMediaType) {
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }
}
