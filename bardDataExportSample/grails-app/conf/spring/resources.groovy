import barddataexport.registration.AssayDefinitionMediaTypesDTO

// Place your Spring DSL code here
beans = {
    final String elementMediaType=grailsApplication.config.bard.data.export.dictionary.element.xml
    //inject element mime type here
    dictionaryExportHelperService(barddataexport.dictionary.DictionaryExportHelperService, elementMediaType) {
        dataSource = ref('dataSource')
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }
    String dictionaryMediaType = grailsApplication.config.bard.data.export.dictionary.xml
    rootService(barddataexport.util.RootService, dictionaryMediaType) {
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }
    final String resultTypeMediaType=grailsApplication.config.bard.data.export.dictionary.resultType.xml
    final String assayMediaType=grailsApplication.config.bard.data.export.assay.xml
    final String assayDocMediaType=grailsApplication.config.bard.data.export.assay.doc.xml
    final String assaysMediaType=grailsApplication.config.bard.data.export.assays.xml

    AssayDefinitionMediaTypesDTO dto =  new AssayDefinitionMediaTypesDTO( elementMediaType,resultTypeMediaType,
           assayMediaType,assayDocMediaType,
          assaysMediaType)
    assayExportHelperService(barddataexport.registration.AssayExportHelperService,dto){
        dataSource = ref('dataSource')
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }
}
