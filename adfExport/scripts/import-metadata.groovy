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
