import acl.CapPermissionService
import bard.db.enums.Status
import bard.db.experiment.Experiment
import bard.db.people.Role
import org.apache.commons.lang.StringUtils
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
    final List<Experiment> experiments = Experiment.findAllByOwnerRoleIsNullAndExperimentStatusNotInList([Status.RETIRED])
    for (Experiment experiment : experiments) {
        String roleDisplayName = capPermissionService.getOwner(experiment)
        Role role = Role.findByDisplayName(roleDisplayName)
        if (!role) {
            notFound.add("Could not find Role with display name ${roleDisplayName} and experiment id ${experiment.id}")
        } else {
            experiment.ownerRole = role
           
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