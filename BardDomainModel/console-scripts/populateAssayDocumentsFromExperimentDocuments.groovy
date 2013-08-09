package bard.db.registration

import bard.db.enums.DocumentType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentDocument
import bard.db.registration.Assay
import bard.db.registration.AssayDocument
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.commons.lang3.time.StopWatch
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.transaction.TransactionStatus
import bard.db.audit.BardContextUtils
import org.hibernate.Session

SpringSecurityUtils.reauthenticate("user", "user")
Assay.withTransaction { TransactionStatus status ->
    Assay.withSession { Session session ->
        BardContextUtils.setBardContextUsername(session, 'user')
        StopWatch sw = new StopWatch()
        sw.start()
        println("started copying documents from experiment back to assays")

        Integer assaysUpdated = 0
        Integer assayProtocols = 0
        Integer assayDescriptions = 0
        Integer assayComments = 0
        Integer progress = 1

        Integer totalAssays = Assay.count()
        Assay.list().each { Assay assay ->
            //find an experiment that has all three document types: description, protocol and comments
            Experiment experiment = assay.experiments.find { Experiment exp ->
                List<DocumentType> experimentDocumentTypes = exp.documents*.documentType
                return experimentDocumentTypes.containsAll([DocumentType.DOCUMENT_TYPE_DESCRIPTION, DocumentType.DOCUMENT_TYPE_PROTOCOL, DocumentType.DOCUMENT_TYPE_COMMENTS])
            }

            ExperimentDocument descriptionDoc
            ExperimentDocument protocolDoc
            ExperimentDocument commentsDoc
            //if such experiment exist, use its documents to populate the assay documents
            if (experiment) {
                descriptionDoc = experiment.documents.find { ExperimentDocument experimentDocument ->
                    experimentDocument.documentType == DocumentType.DOCUMENT_TYPE_DESCRIPTION }
                protocolDoc = experiment.documents.find { ExperimentDocument experimentDocument ->
                    experimentDocument.documentType == DocumentType.DOCUMENT_TYPE_PROTOCOL }
                commentsDoc = experiment.documents.find { ExperimentDocument experimentDocument ->
                    experimentDocument.documentType == DocumentType.DOCUMENT_TYPE_COMMENTS }
            }
            //else, try to take as many documents as possible from the assay's experiments
            else {
                descriptionDoc = assay.experiments*.documents.flatten().find { ExperimentDocument experimentDocument ->
                    return experimentDocument.documentType == DocumentType.DOCUMENT_TYPE_DESCRIPTION
                }
                protocolDoc = assay.experiments*.documents.flatten().find { ExperimentDocument experimentDocument ->
                    return experimentDocument.documentType == DocumentType.DOCUMENT_TYPE_PROTOCOL
                }
                commentsDoc = assay.experiments*.documents.flatten().find { ExperimentDocument experimentDocument ->
                    return experimentDocument.documentType == DocumentType.DOCUMENT_TYPE_COMMENTS
                }
            }

            Boolean assayChanged = false
            if (descriptionDoc) {
                AssayDocument assayDescriptionDoc = new AssayDocument(descriptionDoc.properties)
                assayDescriptionDoc.lastUpdated = descriptionDoc.lastUpdated ?: new Date()
                assayChanged = true
                assayDescriptions++
//                println("for ADID=${assay.id} we found a new Descriptoin document: ${assayDescriptionDoc.dump()}\n")
            }
            if (protocolDoc) {
                AssayDocument assayProtocolDoc = new AssayDocument(protocolDoc.properties)
                assayProtocolDoc.lastUpdated = protocolDoc.lastUpdated ?: new Date()
                assay.addToAssayDocuments(assayProtocolDoc)
                assayChanged = true
                assayProtocols++
//                println("for ADID=${assay.id} we found a new Protocol document: ${assayProtocolDoc.dump()}\n")
            }
            if (commentsDoc) {
                AssayDocument assayCommentsDoc = new AssayDocument(commentsDoc.properties)
                assayCommentsDoc.lastUpdated = commentsDoc.lastUpdated ?: new Date()
                assay.addToAssayDocuments(assayCommentsDoc)
                assayChanged = true
                assayComments++
//                println("for ADID=${assay.id} we found a new Comments document: ${assayCommentsDoc.dump()}\n")
            }
            if (assayChanged) {
                assay.save(failOnError: true)
                assaysUpdated++
            }
            println("Progress: ${progress++}/${totalAssays}")
        }

        ctx.getBean('sessionFactory').currentSession.flush()
        sw.stop()
        println("finished processing flush() duration: ${sw}\n")
        println("Total assays updated: ${assaysUpdated}\nTotal protocols added: ${assayProtocols}\nTotal Descriptions added: ${assayDescriptions}\nTotal comments added: ${assayComments}")
    }
//      comment below when ready to commit
    status.setRollbackOnly()
}

AssayDocument createAssayDocumentFromExperimentDocument(ExperimentDocument experimentDocument) {
    return new AssayDocument(experimentDocument.properties)
}