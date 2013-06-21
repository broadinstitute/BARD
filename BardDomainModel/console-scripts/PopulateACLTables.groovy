import acl.CapPermissionService
import bard.db.experiment.Experiment
import bard.db.project.Project
import bard.db.people.Role
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.Transaction
import org.springframework.security.acls.domain.BasePermission
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


SessionFactory sessionFactory = ctx.sessionFactory

Session session = sessionFactory.openSession()
Transaction tx = session.beginTransaction()


try {
    Map<String, String> aidToTeamMap = [:]
    Map<String, String> externalReferenceMap = [:]
    SpringSecurityUtils.reauthenticate('test', null)
    def capPermissionService = ctx.capPermissionService

    int index = 0
    new File("console-scripts/files/externalreference.txt").eachLine { line ->
        if (index > 0) {
            String[] args = line.split("\t")
            String aid = args[2]
            externalReferenceMap.put(aid.trim(), line)
        }
        ++index

    }
    index = 0

    new File("console-scripts/files/aid_to_team.txt").eachLine { line ->
        if (index > 0) {
            String[] args = line.split("\t")
            String aid = args[0]
            aidToTeamMap.put(aid.trim(), line)
        }
        ++index

    }
    index = 0
    externalReferenceMap.each { aid, externalValuesLine ->
        String aidToTeamLine = aidToTeamMap.get(aid)
        if (aidToTeamLine) {
            String[] aidToTeamArray = aidToTeamLine.split("\t")
            assert aidToTeamArray.length == 2
            // println aidToTeamArray[0] + ":" + aidToTeamArray[1];
            String team = aidToTeamArray[1]
            //append ROLE_ to it and then find out if it exists in the ROLE table
            //if not then add to the ROLE Table
            final String authority = "ROLE_" + team
            Role role = Role.findByAuthority(authority)
            if (!role) {
                role = new Role(authority: authority).save(flush: true)
            }
            
            String[] externalValuesArray = externalValuesLine.split("\t")
            assert externalValuesArray.length == 3
            String experimentId = externalValuesArray[1]
            String projectId = externalValuesArray[0]
            
            if (experimentId) {
                Experiment experiment = Experiment.findById(experimentId)
                if (experiment) {
                   capPermissionService.addPermission(experiment, role, BasePermission.ADMINISTRATION)
                }
                //log this
                //assert experiment
            }
            if(projectId){
                Project project = Project.findById(projectId)
                if(project){
                 capPermissionService.addPermission(project, role, BasePermission.ADMINISTRATION)
                }
            }

            index++;
            if(index % 100){
                session.flush();
                session.clear();
            }
        } else {
            println("Could not find aid " + aid + " in aidToTeamMap")
        }
        // println aidToTeam
        // println "${key}:${value}"

    }

    //for each line grab the experiment and the external reference
    //then find the team from the aid file using the externalreference id
    //if the team does not exist create it in the Role table by appending ROLE_ to it

    //do the same for project

    //finally for each experiment find the assay that it belongs to
    //if the assay has not been seen then add it to the acl tables

    //then finally for each experiment
    //Now find the experiment and add it to the permissions table
    //for each assay get the designed by name and then use it to
    //find the newObjectRole of the Team
//    final List<Assay> assays = Assay.list()
//    for (Assay assay : assays) {
//        final String designedBy = assay.designedBy
//        if (designedBy) {
//            final List<Role> roles = Role.findAllByDisplayName(designedBy)
//            if (roles) {
//                boolean existsInPersonTable = false
//                for(Role role: roles){
//                    //if role exist in the Person.userObjectRole then apply it and break out of loop
//                    final List<Person> role1 = Person.findAllByNewObjectRole(role)
//                    if(role1){
//                        //make sure it is in the newUserObject table
//                        Permission permission = BasePermission.ADMINISTRATION
//                        capPermissionService.addPermission(assay, role1, permission)
//                        existsInPersonTable = true
//                        break;
//                    }
//                }
//                if(!existsInPersonTable) {
//                    writeToFile(assay.id,assay.name,assay.designedBy)
//                    //write to a file and then handle separately
//                }
//
//
//            } else {
//                writeToFile(assay.id,assay.name,assay.designedBy)
//                //write to a file and then handle separately
//            }
//        } else {
//            writeToFile(assay.id,assay.name,assay.designedBy)
//            //write to a file and then handle separately
//        }
//
//    }
}
catch (Exception e) {
    println(e)
    tx.rollback()
}
finally {
tx.commit();
   // tx.rollback()
}