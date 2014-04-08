import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import bard.db.registration.Assay
import org.springframework.transaction.TransactionStatus
import org.hibernate.Session
import bard.db.audit.BardContextUtils

model = ctx.rdfToBardMetadataService.createModel("test1.n3")
SpringSecurityUtils.reauthenticate("integrationTestUser", "integrationTestUser")
Assay.withTransaction { TransactionStatus status ->
    Assay.withSession { Session session ->
        BardContextUtils.setBardContextUsername(session, 'integrationTestUser')
        assays = ctx.rdfToBardMetadataService.handleAssay(model)
    }
}
