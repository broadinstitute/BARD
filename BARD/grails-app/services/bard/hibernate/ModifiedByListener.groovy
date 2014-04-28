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

package bard.hibernate

import bard.db.people.Person
import grails.plugins.springsecurity.SpringSecurityService
import groovy.transform.InheritConstructors
import org.hibernate.event.*

/**
 * Listener to set modifiedBy for all entities modified in CAP.
 * User: ddurkin
 * Date: 9/13/12
 * Time: 1:09 PM
 */
class ModifiedByListener implements PreInsertEventListener, PreUpdateEventListener {
    SpringSecurityService springSecurityService

    boolean onPreInsert(PreInsertEvent preInsertEvent) {
        updateModifiedBy(preInsertEvent)
        return false
    }

    boolean onPreUpdate(PreUpdateEvent preUpdateEvent) {
        updateModifiedBy(preUpdateEvent)
        return false
    }

    private void updateModifiedBy(AbstractPreDatabaseOperationEvent abstractEvent) {
        def entity = abstractEvent.entity
        if(entity instanceof Person && !springSecurityService.getPrincipal()?.username){ //When a user is registering themselves they would not be authenticated
            return
        }
        if (entity.hasProperty("modifiedBy")) {
            String username = springSecurityService.getPrincipal()?.username
            if (username) {
                entity.modifiedBy = username
            } else {

                throw new AuthenticatedUserRequired('An authenticated user was expected this point');
            }
        }
    }
}
@InheritConstructors
class AuthenticatedUserRequired extends RuntimeException {}
