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