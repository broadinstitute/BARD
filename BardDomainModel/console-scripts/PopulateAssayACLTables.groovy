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
