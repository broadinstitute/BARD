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

package bard.db.util

import bard.db.command.BardCommand
import bard.util.DownTimeSchedulerService
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import groovy.transform.InheritConstructors
import org.springframework.validation.Errors
import util.BardUser

import java.text.DateFormat
import java.text.SimpleDateFormat


class DownTimeSchedulerController {
    SpringSecurityService springSecurityService
    DownTimeSchedulerService downTimeSchedulerService

    def index() {
        redirect(action: "list")
    }

    @Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
    def list() {
        [downTimeSchedulerList: DownTimeScheduler.listOrderByDateCreated(order: "desc")]
    }

    @Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
    def show(Long id) {
        DownTimeScheduler downTimeScheduler = null
        if (id) {
            downTimeScheduler = DownTimeScheduler.findById(id)
        }
        [downTimeScheduler: downTimeScheduler]
    }

    @Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
    def create(DownTimeSchedulerCommand downTimeSchedulerCommand) {
        if (downTimeSchedulerCommand.createdBy) {
            return [downTimeSchedulerCommand: downTimeSchedulerCommand]
        }
        [downTimeSchedulerCommand: new DownTimeSchedulerCommand(createdBy: springSecurityService.principal?.username)]

    }
    /**
     * Ajax calls to find the current down time
     * This call is made every time a user navigates to a new page
     */
    def currentDownTimeInfo() {

        final List<DownTimeScheduler> downTimeSchedulerList = DownTimeScheduler.listOrderByDateCreated(order: "desc")
        String displayValue = ""
        if (downTimeSchedulerList) {
            final DownTimeScheduler downTimeScheduler = downTimeSchedulerList.get(0)
            displayValue = downTimeScheduler.getDisplayValueIfDownTimeGreaterThanCurrentTime()
            log.info("Display downtime to user ${displayValue}")
        }
        render(text: displayValue, contentType: 'text/plain', template: null)
    }

    @Secured(["hasRole('ROLE_BARD_ADMINISTRATOR')"])
    def save(DownTimeSchedulerCommand downTimeSchedulerCommand) {
        if (downTimeSchedulerCommand.hasErrors()) {
            redirect(action: "create", model: [downTimeSchedulerCommand: downTimeSchedulerCommand])
            return
        }
        final DownTimeScheduler downTimeScheduler = downTimeSchedulerCommand.createNewDownTimeScheduler()
        if (downTimeScheduler) { //broadcast the message to all listeners
            downTimeSchedulerService.downTimeScheduler(downTimeScheduler.displayValue)
        }
        render(view: "show", model: [downTimeScheduler: downTimeScheduler])
    }

}
@InheritConstructors
@Validateable
class DownTimeSchedulerCommand extends BardCommand {
    String createdBy
    String downTimeAsString //We use a string here, because otherwise we have to register a Date  property Editor at the application level
    String displayValue
    SpringSecurityService springSecurityService

    static constraints = {
        importFrom(DownTimeScheduler, exclude: ['downTime', 'createdBy', 'dateCreated', 'lastUpdated'])
        downTimeAsString(nullable: false, blank: false, validator: { String field, DownTimeSchedulerCommand instance, Errors errors ->

            Date date = DownTimeScheduler.dateFormat.parse(field)
            final long currentTimeMillis = System.currentTimeMillis()
            final long downTimeInMilliseconds = date.getTime()

            //validate that the shutdown time is later than the current time, should we require that the shutdown time is at least 5 minutes from the current time?
            if (currentTimeMillis > downTimeInMilliseconds) {
                errors.rejectValue(
                        'downTimeAsString',
                        'downtimescheduler.bard.downtime.valid')
            }
        })
        createdBy(nullable: false, blank: false, maxSize: DownTimeScheduler.CREATED_BY_MAX_SIZE, validator: { String field, DownTimeSchedulerCommand instance, Errors errors ->
            final BardUser user = instance.getBardUser()
            //validate that there is an existing user
            if (!user) {
                errors.reject('downtimeschedulerCommand.username.not.found')
                return
            }
            //validate that the logged in user matches the modified by user
            if (field != user.username) {
                errors.reject('downtimeschedulerCommand.username.mismatch')
                return
            }
            //validate that the logged in user is an admin
            if (!instance.isBardAdmin()) {
                errors.reject('downtimeschedulerCommand.bard.administrator.required')
                return
            }
        })
    }

    BardUser getBardUser() {
        return (BardUser) springSecurityService.principal
    }

    boolean isBardAdmin() {
        BardUser bardUser = getBardUser()
        boolean foundAdmin = false
        if (bardUser) {
            bardUser.authorities.collect {
                if (it.authority == "ROLE_BARD_ADMINISTRATOR") {
                    foundAdmin = true
                    return
                }
            }
        }
        return foundAdmin
    }

    Date getDownTimeAsDate() {
        return DownTimeScheduler.dateFormat.parse(this.downTimeAsString);
    }

    DownTimeScheduler createNewDownTimeScheduler() {
        DownTimeScheduler downTimeScheduler = null
        if (validate()) {
            DownTimeScheduler tempDownTimeSchedulerToReturn = new DownTimeScheduler()
            copyFromCmdToDomain(tempDownTimeSchedulerToReturn)
            if (attemptSave(tempDownTimeSchedulerToReturn)) {
                downTimeScheduler = tempDownTimeSchedulerToReturn
            }
        }
        return downTimeScheduler
    }

    void copyFromCmdToDomain(DownTimeScheduler downTimeScheduler) {
        downTimeScheduler.createdBy = this.createdBy
        final Date downTime = getDownTimeAsDate()
        downTimeScheduler.displayValue = displayValue
        downTimeScheduler.downTime = downTime
    }
}
