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

import bard.db.experiment.Experiment
import bard.db.people.Role
import bard.db.registration.Assay
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclClass
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclEntry
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclObjectIdentity
import org.codehaus.groovy.grails.plugins.springsecurity.acl.AclSid
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.springframework.security.acls.domain.BasePermission

SessionFactory sessionFactory = ctx.sessionFactory

Session session = sessionFactory.openSession()
Transaction tx = session.beginTransaction()


try {
    Set<Long> seenAssays = new HashSet<Long>()
    SpringSecurityUtils.reauthenticate('test', null)
    def capPermissionService = ctx.capPermissionService

    AclClass aclClass = AclClass.findByClassName("bard.db.experiment.Experiment")
    List<AclObjectIdentity> aclObjectIdentities = AclObjectIdentity.findAllByAclClass(aclClass)
    for (AclObjectIdentity aclObjectIdentity : aclObjectIdentities) {
        final AclEntry aclEntry = AclEntry.findByAclObjectIdentity(aclObjectIdentity)
        final AclSid aclSid = aclEntry.sid
        String authority = aclSid.sid

        Role role = Role.findByAuthority(authority)
        if (role != null) {
            Long experimentId = aclObjectIdentity.getObjectId()
            Experiment experiment = Experiment.findById(experimentId)
            final Assay assay = experiment.assay
            if (!seenAssays.contains(assay.id)) {
                capPermissionService.addPermission(assay, role, BasePermission.ADMINISTRATION)
                seenAssays.add(assay.id)
            }
        } else {
            println "Role ${authority} not found"
        }
    }
    //Now Use sql to find the entities that have no acl
    tx.commit()
}
catch (Exception e) {
    e.printStackTrace()
    tx.rollback()
}
