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

package dataexport.util

import bard.db.audit.BardContextUtils
import bard.db.enums.ReadyForExtraction
import dataexport.registration.BardHttpResponse
import exceptions.NotFoundException
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.hibernate.SessionFactory

import javax.servlet.http.HttpServletResponse

class UtilityService {
    LinkGenerator grailsLinkGenerator
    SessionFactory sessionFactory

    /**
     * Set the ReadyForExtraction value on the domainObject to 'Complete'
     *
     * Return a 409, conflict, if the version supplied by client is less than the version in the database
     *
     * Return a 412, precondition failed, if the version supplied by client is not equal to the version in the database
     *
     * Return a 404 , if the element cannot be found
     *
     * @param id
     * @param version
     * Returns the HTTPStatus Code
     */
    public BardHttpResponse update(def domainObject, final Long id, final Long clientVersion, final ReadyForExtraction latestStatus, final String domainObjectType) {
        if (!domainObject) { //we could not find the element
            throw new NotFoundException("Could not find ${domainObjectType} with ID: ${id}")
        }
        if(domainObject.readyForExtraction==ReadyForExtraction.NOT_READY){
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_FORBIDDEN, ETag: domainObject.version)
        }
        if (domainObject.version > clientVersion) { //There is a conflict, supplied version is less than the current version
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_CONFLICT, ETag: domainObject.version)
        }
        if (domainObject.version != clientVersion) {//supplied version is not equal to the version in database
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_PRECONDITION_FAILED, ETag: domainObject.version)
        }

        //no-op if the status is already completed
        final ReadyForExtraction currentStatus = domainObject.readyForExtraction
        if (currentStatus != latestStatus) {
            BardContextUtils.doWithContextUsername(sessionFactory.currentSession, 'dataExport') {->
                domainObject.readyForExtraction = latestStatus
                domainObject.save(flush: true, failOnError: true)
            }
        }
        return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_OK, ETag: domainObject.version)
    }
}
