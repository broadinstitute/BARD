/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
