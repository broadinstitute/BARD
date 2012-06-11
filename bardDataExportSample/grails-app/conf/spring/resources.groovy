// Place your Spring DSL code here
beans = {
    String elementXmlMimeType = grailsApplication.config.bard.data.export.dictionary.element.xml
    dictionaryExportHelperService(barddataexport.dictionary.DictionaryExportHelperService,elementXmlMimeType) {
    }
}
