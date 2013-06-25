import acl.CapPermissionService
import bard.db.experiment.Experiment
import bard.db.project.Project
import bard.db.people.Role
import groovy.sql.Sql
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
    SpringSecurityUtils.reauthenticate('integrationTestUser', null)
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
    sql.eachRow("SELECT PROJECT_ID,EXPERIMENT_ID,EXT_ASSAY_REF FROM EXTERNAL_REFERENCE WHERE EXT_ASSAY_REF LIKE 'aid=%'") { row ->
        String projectId = row.PROJECT_ID
        String experimentId = row.EXPERIMENT_ID
        String aid = row.EXT_ASSAY_REF
        String line = projectId ?: "" + "\t" + experimentId ?: "" + "\t" + aid ?: ""
        externalReferenceMap.put(aid.trim(), line)
    }
    //Generate this file from running this query
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
            if (projectId) {
                Project project = Project.findById(projectId)
                if (project) {
                    capPermissionService.addPermission(project, role, BasePermission.ADMINISTRATION)
                }
            }

            index++;
            if (index % 100) {
                session.flush();
                session.clear();
            }
        } else {
            println("Could not find aid " + aid + " in aidToTeamMap")
        }
    }
}
catch (Exception e) {
    println(e)
    tx.rollback()
}
finally {
    tx.commit();
    // tx.rollback()
}