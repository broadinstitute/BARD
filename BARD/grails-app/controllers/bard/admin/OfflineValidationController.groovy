package bard.admin

import bard.util.OfflineValidationService
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.commons.GrailsDomainClass

/**
 * Created by ddurkin on 3/7/14.
 */
@Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
class OfflineValidationController {

    OfflineValidationService offlineValidationService

    def index() {
        final Map<String,String> simpleNameFullNameMap = getMapOfDomainNames()
        render(view: 'index', model:[simpleNameFullNameMap: simpleNameFullNameMap, message: ''])
    }

    def validate() {
        final List<String> fullClassNames = params.list('fullClassNames')
        Map resultMap = offlineValidationService.validate(fullClassNames)
        final Map<String,String> simpleNameFullNameMap = getMapOfDomainNames()
        render(view: 'index', model: [simpleNameFullNameMap: simpleNameFullNameMap, message: "${resultMap.numIdsSubmitted} instances were scheduled for validation reporting."] )
    }

    private Map<String,String> getMapOfDomainNames() {
        final Map<String,String> simpleNameFullNameMap = [:]
        for(GrailsDomainClass domainClass in grailsApplication.domainClasses){
            simpleNameFullNameMap.put(domainClass.fullName, domainClass.shortName,)
        }
        simpleNameFullNameMap.sort(){a,b-> a.value <=> b.value}
    }

}