import acl.CapPermissionService
import bard.db.people.Role
import bard.db.project.Project
import clover.org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction

SessionFactory sessionFactory = ctx.sessionFactory

Session session = sessionFactory.openSession()
Transaction tx = session.beginTransaction()
CapPermissionService capPermissionService = ctx.capPermissionService
List<String> notFound = []
try {
    SpringSecurityUtils.reauthenticate('integrationTestUser', null)
    final List<Project> projects = Project.findAllByOwnerRoleIsNull()
    for (Project project : projects) {
        String roleDisplayName = capPermissionService.getOwner(project)
        Role role = Role.findByDisplayName(roleDisplayName)
        if (!role) {
            notFound.add("Could not find Role with display name ${roleDisplayName} and project id ${project.id}")
        } else {
            project.ownerRole = role
           
        }

    }
     tx.commit()
}
catch (Exception e) {
    println(e)
    // tx.rollback()
}
finally {
    // tx.commit();
    // tx.rollback()
}
if(notFound){
 println StringUtils.join(notFound,"\n");
}