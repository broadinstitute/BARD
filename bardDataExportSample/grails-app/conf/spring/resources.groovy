// Place your Spring DSL code here
beans = {
    String elementXmlMimeType = grailsApplication.config.bard.data.export.dictionary.element.xml
    //inject element mime type here
    dictionaryExportHelperService(barddataexport.dictionary.DictionaryExportHelperService, elementXmlMimeType) {
        // elementMimeType = elementXmlMimeType
        dataSource = ref('dataSource')
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }
    String dictionaryXmlMimeType = grailsApplication.config.bard.data.export.dictionary.xml
    rootService(barddataexport.util.RootService, dictionaryXmlMimeType) {
        grailsLinkGenerator = ref('grailsLinkGenerator')
    }
}
