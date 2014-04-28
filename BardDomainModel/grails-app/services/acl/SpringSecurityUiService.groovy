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

/* Copyright 2009-2012 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package acl

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclEntry

import java.text.SimpleDateFormat

/**
 * Helper methods for UI management.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class SpringSecurityUiService {

	static final String DATE_FORMAT = 'd MMM yyyy HH:mm:ss'

	def messageSource
	def springSecurityService
    def grailsApplication

	boolean updatePersistentLogin(persistentLogin, newProperties) {
		if (newProperties.lastUsed && newProperties.lastUsed instanceof String) {
			Calendar c = Calendar.instance
			c.time = new SimpleDateFormat(DATE_FORMAT).parse(newProperties.lastUsed)
			persistentLogin.lastUsed = c.time
		}

		if (newProperties.token) {
			persistentLogin.token = newProperties.token
		}

		persistentLogin.save()
		return !persistentLogin.hasErrors()
	}

	void deletePersistentLogin(persistentLogin) {
		persistentLogin.delete()
	}

	boolean updateRegistrationCode(registrationCode, String username, String token) {
		registrationCode.token = token
		registrationCode.username = username
		registrationCode.save()
		return !registrationCode.hasErrors()
	}

	void deleteRegistrationCode(registrationCode) {
		registrationCode.delete()
	}

	boolean updateAclClass(aclClass, String newName) {
		aclClass.className = newName
		aclClass.save()
		return !aclClass.hasErrors()
	}

	void deleteAclClass(aclClass) {
		// will fail if there are FK references
		aclClass.delete()
	}

	boolean updateAclSid(aclSid, String newName, boolean newPrincipal) {
		aclSid.sid = newName
		aclSid.principal = newPrincipal
		aclSid.save()
		return !aclSid.hasErrors()
	}

	void deleteAclSid(aclSid) {
		// will fail if there are FK references
		aclSid.delete()
	}

	boolean updateAclObjectIdentity(aclObjectIdentity, long objectId, long aclClassId,
			Long parentId, Long ownerId, boolean entriesInheriting) {

		aclObjectIdentity.objectId = objectId
		aclObjectIdentity.entriesInheriting = entriesInheriting

		if (aclObjectIdentity.aclClass.id != aclClassId) {
			aclObjectIdentity.aclClass = retrieveAclClass('AclClass', aclClassId)
		}

		if (parentId) {
			if (aclObjectIdentity.parent?.id != parentId) {
				aclObjectIdentity.parent = retrieveAclClass('AclObjectIdentity', parentId)
			}
		}
		else {
			aclObjectIdentity.parent = null
		}

		if (ownerId) {
			if (aclObjectIdentity.owner.id != ownerId) {
				aclObjectIdentity.owner = retrieveAclClass('AclSid', ownerId)
			}
		}
		else {
			aclObjectIdentity.parent = null
		}

		aclObjectIdentity.save()
		return !aclObjectIdentity.hasErrors()
	}

	void deleteAclObjectIdentity(aclObjectIdentity) {
		// will fail if there are FK references
		aclObjectIdentity.delete()
	}

	boolean updateAclEntry(AclEntry aclEntry, long aclObjectIdentityId, long sidId, int aceOrder,
			int mask, boolean granting, boolean auditSuccess, boolean auditFailure) {

		aclEntry.aceOrder = aceOrder
		aclEntry.mask = mask
		aclEntry.granting = granting
		aclEntry.auditSuccess = auditSuccess
		aclEntry.auditFailure = auditFailure

		if (aclEntry.aclObjectIdentity.id != aclObjectIdentityId) {
			aclEntry.aclObjectIdentity = retrieveAclClass('AclObjectIdentity', aclObjectIdentityId)
		}

		if (aclEntry.sid.id != sidId) {
			aclEntry.sid = retrieveAclClass('AclSid', sidId)
		}

		aclEntry.save(flush:true)
		return !aclEntry.hasErrors()
	}

	void deleteAclEntry(aclEntry) {
		aclEntry.delete()
	}

	protected retrieveAclClass(String name, id) {
        if(!name.startsWith(".")){
            name = "."+name;
        }
		def clazz = grailsApplication.getClassForName('org.codehaus.groovy.grails.plugins.springsecurity.acl' + name)
		clazz.get id
	}

	void warnErrors(bean, messageSource, Locale locale = Locale.getDefault()) {
		if (!log.isWarnEnabled()) {
			return
		}

		def message = new StringBuilder(
				"problem ${bean.id ? 'updating' : 'creating'} ${bean.getClass().simpleName}: $bean")
		for (fieldErrors in bean.errors) {
			for (error in fieldErrors.allErrors) {
				message.append("\n\t").append(messageSource.getMessage(error, locale))
			}
		}
		log.warn message
	}

	String encodePassword(String password, salt) {
		def encode = SpringSecurityUtils.securityConfig.ui.encodePassword
		if (!(encode instanceof Boolean) || (encode instanceof Boolean && encode)) {
			password = springSecurityService.encodePassword(password, salt)
		}
		password
	}

	def register(user, String cleartextPassword, salt) {
       return null
//		String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName
//		String passwordFieldName = SpringSecurityUtils.securityConfig.userLookup.passwordPropertyName
//
//		String password = encodePassword(cleartextPassword, salt)
//		user."$passwordFieldName" = password
//		if (!user.save()) {
//			warnErrors user, messageSource
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
//			return null
//		}
//
//		def registrationCode = new RegistrationCode(username: user."$usernameFieldName")
//		if (!registrationCode.save()) {
//			warnErrors registrationCode, messageSource
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
//		}
//
//		registrationCode
	}
}
