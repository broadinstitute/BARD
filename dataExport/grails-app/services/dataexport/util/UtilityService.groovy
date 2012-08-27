package dataexport.util

import dataexport.registration.BardHttpResponse
import exceptions.NotFoundException
import javax.servlet.http.HttpServletResponse
import bard.db.enums.ReadyForExtraction

class UtilityService {

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
        if (domainObject.version > clientVersion) { //There is a conflict, supplied version is less than the current version
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_CONFLICT, ETag: domainObject.version)
        }
        if (domainObject.version != clientVersion) {//supplied version is not equal to the version in database
            return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_PRECONDITION_FAILED, ETag: domainObject.version)
        }

        //no-op if the status is already completed
        final ReadyForExtraction currentStatus = domainObject.readyForExtraction
        if (currentStatus != latestStatus) {
            domainObject.readyForExtraction = latestStatus
            domainObject.save(flush: true, failOnError:true)
        }
        return new BardHttpResponse(httpResponseCode: HttpServletResponse.SC_OK, ETag: domainObject.version)
    }
}