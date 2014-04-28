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
