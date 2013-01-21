package bard.rest.api.wrapper

import bard.core.rest.spring.DataExportRestService
/**
 * We introduce this Domain class so that we can get our hands
 * on the dataExportRest Service, allowing classes in src/groovy to use it
 *
 * The alternative was to inject the application context into the src/groovy classes
 */
class Dummy {
    DataExportRestService dataExportRestService
    static transients = ['dataExportRestService']


    static constraints = {
    }
}
