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
                println("couldn't find a single experiment with description, protocol and comments to copy to the assay. Searching within separate experiments (ADID=${assay.id})")
                descriptionDoc = assay.experiments*.documents.flatten().find { ExperimentDocument experimentDocument ->
                    if (experimentDocument.documentType == DocumentType.DOCUMENT_TYPE_DESCRIPTION) {
                        AssayDocument assayDescriptionDoc = new AssayDocument(experimentDocument.properties)
                        assayDescriptionDoc.assay = assay
                        assayDescriptionDoc.lastUpdated = experimentDocument.lastUpdated ?: new Date()
                        return assayDescriptionDoc.validate()
                    }
                    return false
                }
                protocolDoc = assay.experiments*.documents.flatten().find { ExperimentDocument experimentDocument ->
                    if (experimentDocument.documentType == DocumentType.DOCUMENT_TYPE_PROTOCOL) {
                        AssayDocument assayProtocolDoc = new AssayDocument(experimentDocument.properties)
                        assayProtocolDoc.assay = assay
                        assayProtocolDoc.lastUpdated = experimentDocument.lastUpdated ?: new Date()
                        return assayProtocolDoc.validate()
                    }
                    return false
                }
                commentsDoc = assay.experiments*.documents.flatten().find { ExperimentDocument experimentDocument ->
                    if (experimentDocument.documentType == DocumentType.DOCUMENT_TYPE_COMMENTS) {
                        AssayDocument assayCommentsDoc = new AssayDocument(experimentDocument.properties)
                        assayCommentsDoc.assay = assay
                        assayCommentsDoc.lastUpdated = experimentDocument.lastUpdated ?: new Date()
                        return assayCommentsDoc.validate()
                    }
                    return false
                }
            }

            Boolean assayChanged = false
            AssayDocument assayDescriptionDoc, assayProtocolDoc, assayCommentsDoc
            if (descriptionDoc) {
                assayDescriptionDoc = new AssayDocument(descriptionDoc.properties)
                assayDescriptionDoc.lastUpdated = descriptionDoc.lastUpdated ?: new Date()
                assay.addToAssayDocuments(assayDescriptionDoc)
                assayChanged = true
                assayDescriptions++
//                println("\tDescriptoin document: ${assayDescriptionDoc.id}")
            }
            if (protocolDoc) {
                assayProtocolDoc = new AssayDocument(protocolDoc.properties)
                assayProtocolDoc.lastUpdated = protocolDoc.lastUpdated ?: new Date()
                assay.addToAssayDocuments(assayProtocolDoc)
                assayChanged = true
                assayProtocols++
//                println("\tProtocol document: ${assayProtocolDoc.id}")
            }
            if (commentsDoc) {
                assayCommentsDoc = new AssayDocument(commentsDoc.properties)
                assayCommentsDoc.lastUpdated = commentsDoc.lastUpdated ?: new Date()
                assay.addToAssayDocuments(assayCommentsDoc)
                assayChanged = true
                assayComments++
//                println("\tComments document: ${assayCommentsDoc.id}")
            }
            if (assayChanged) {
                assay.save(failOnError: true, flush: true)
                assaysUpdated++
                println("ADID=${assay?.id}" +
                        "\tDescriptoin document: ${assayDescriptionDoc?.id}" +
                        "\tProtocol document: ${assayProtocolDoc?.id}" +
                        "\tComments document: ${assayCommentsDoc?.id}" +
                        "\tProgress: ${progress++}/${totalAssays}")
            }
//            println("Progress: ${progress++}/${totalAssays}")
        }

        ctx.getBean('sessionFactory').currentSession.flush()
        sw.stop()
        println("finished processing flush() duration: ${sw}\n")
        println("Total assays updated: ${assaysUpdated}\nTotal protocols added: ${assayProtocols}\nTotal Descriptions added: ${assayDescriptions}\nTotal comments added: ${assayComments}")
    }
//      comment below when ready to commit
    status.setRollbackOnly()
}

//Delete everything from ASSAY_DOCUMENT
//                  delete from assay_document where DOCUMENT_TYPE in ('Description', 'Protocol', 'Comments');

//Populate EXPERIMENT_DOCUMENT from DATA_MIG
/**
                    insert into EXPERIMENT_DOCUMENT ed
                        (EXPERIMENT_DOCUMENT_ID, EXPERIMENT_ID, DOCUMENT_NAME, DOCUMENT_TYPE, DOCUMENT_CONTENT, VERSION, DATE_CREATED, LAST_UPDATED, MODIFIED_BY)
                    select EXPERIMENT_DOCUMENT_ID_SEQ.nextval,exp.EXPERIMENT_ID, dm_ad.DOCUMENT_NAME, dm_ad.DOCUMENT_TYPE, DM_AD.DOCUMENT_CONTENT, dm_ad.VERSION, dm_ad.DATE_CREATED, dm_ad.LAST_UPDATED, dm_ad.MODIFIED_BY
                    from    DATA_MIG.EXPERIMENT dm_exp,
                            DATA_MIG.EXTERNAL_REFERENCE dm_extref,
                            DATA_MIG.EXTERNAL_SYSTEM dm_extsys,
                            DATA_MIG.ASSAY_DOCUMENT dm_ad,
                            assay a,
                            experiment exp,
                            external_reference extref,
                            external_system extsys
                    where   DM_EXP.EXPERIMENT_ID = DM_EXTREF.EXPERIMENT_ID
                            and DM_EXTREF.EXTERNAL_SYSTEM_ID = DM_EXTSYS.EXTERNAL_SYSTEM_ID
                            and DM_EXTSYS.SYSTEM_NAME = 'PubChem'
                            and DM_AD.ASSAY_ID = DM_EXP.ASSAY_ID
                            and A.ASSAY_ID = EXP.ASSAY_ID
                            and EXP.EXPERIMENT_ID = EXTREF.EXPERIMENT_ID
                            and EXTREF.EXTERNAL_SYSTEM_ID = EXTSYS.EXTERNAL_SYSTEM_ID
                            and EXTSYS.SYSTEM_NAME = 'PubChem'
                            and EXTREF.EXT_ASSAY_REF = DM_EXTREF.EXT_ASSAY_REF
                            and DM_AD.DOCUMENT_TYPE in ('Description', 'Protocol', 'Comments');
 **/