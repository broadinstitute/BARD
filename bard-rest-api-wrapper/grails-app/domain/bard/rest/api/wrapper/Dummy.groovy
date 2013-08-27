package bard.rest.api.wrapper

import bard.core.rest.spring.DictionaryRestService
/**
 * We introduce this Domain class so that we can get our hands
 * on the dataExportRest Service, allowing classes in src/groovy to use it
 *
 * The alternative was to inject the application context into the src/groovy classes
 */
class Dummy {
    DictionaryRestService dictionaryRestService
    static transients = ['dictionaryRestService']


    static constraints = {
    }
}
