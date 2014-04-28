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

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import bard.db.registration.Assay
import org.springframework.transaction.TransactionStatus
import org.hibernate.Session
import bard.db.audit.BardContextUtils
import org.apache.commons.lang3.StringUtils

/**
 * use assay 8111, experiment 5631, project 3 to test
 */
String entityType =  System.getProperty("entity")
String filename = System.getProperty("inputfile")
String add2assay = System.getProperty("add2assay")
String add2project = System.getProperty('projectid')
println "Usage: grails -Dentity=assay|experiment|project -Dinputfile=filename [-Dadd2assay=assayid] run-script scripts/import-metadata.groovy "
if (!entityType) {
    println("Please provide entity type: -Dentity=assay|experiment|project")
    return
}
if (entityType.equals('experiment') && (!add2assay || !StringUtils.isNumeric(add2assay))) {
    println("Please provide a valid assay id to associate: -Dadd2assay=assayid")
    return
}


if (!filename) {
    println("Please provide a input file to import -Dinputfile=filename")
    return
}

model = ctx.rdfToBardMetadataService.createModel(filename)
SpringSecurityUtils.reauthenticate("integrationTestUser", "integrationTestUser")
if (entityType.equals('assay')) {
    def assays = []
    Assay.withTransaction { TransactionStatus status ->
        Assay.withSession { Session session ->
            BardContextUtils.setBardContextUsername(session, 'integrationTestUser')
            assays = ctx.rdfToBardMetadataService.handleAssay(model)
        }
    }
    assays.each{println "assay ${it.id} is imported"}
}

else if (entityType.equals('experiment')) {
    def experiments = []
    Assay.withTransaction { TransactionStatus status ->
        Assay.withSession { Session session ->
            BardContextUtils.setBardContextUsername(session, 'integrationTestUser')
            Assay assay = Assay.findById(Integer.parseInt(add2assay))
            assert assay
            experiments = ctx.rdfToBardMetadataService.handleExperiment(model, assay)
        }
    }
    experiments.each{println "experiment ${it.id} is imported"}
}

else if (entityType.equals('project')) {
    def projects = []
    Assay.withTransaction { TransactionStatus status ->
        Assay.withSession { Session session ->
            BardContextUtils.setBardContextUsername(session, 'integrationTestUser')
            projects = ctx.rdfToBardMetadataService.handleProject(model)
        }
    }
    projects.each{println "project ${it.id} is imported"}
}
