import bard.db.registration.Assay
import pages.AssayDefinitionPage
import pages.HomePage
import pages.LoginPage

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/21/13
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayDefinitionSpec  extends BardFunctionalSpec {
    def 'test assay definition page' () {
        setup:
        to LoginPage
        at LoginPage
        logIn("integrationTestUser", "integrationTestUser")

        when:
        def assayId = build(Assay)
        to AssayDefinitionPage, assayId

        then:
        at AssayDefinitionPage

        // problem: need hook into ajax somehow
//        when:
//        editSummary("new name", "editor")
//
//        then:
//        Assay assay = Assay.get(assayId)
//        assay.assayName == "new name"
    }
}
