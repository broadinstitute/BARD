package webtests

import spock.lang.Stepwise
import webtests.pages.HomePage
import webtests.pages.LoginPage
import webtests.pages.ResultsPage
import webtests.pages.StructureSearchPage

@Stepwise
class SearchSpec extends BardReportingSpec {
    def "Go to home page"() {
        given: "The user is logged in"
        to LoginPage
        logInWithRole('ROLE_USER')

        when: "The user searches for DNA repair"
        at HomePage
        searchBox << "644"
        searchButton.click()

        then: "The results page should appear"
        at ResultsPage
    }

//    def "Go to Structure search page"() {
//        when:
//        structureSearchLink.click()
//        at StructureSearchPage
//        $("form#structureSearchForm a#structureSearchButton.btn.btn-primary").click()
//
//        then:
//        at HomePage
//
//    }

//    def "start a substructure search with no structure"() {
//        when:
//        withFrame(structureModalDialog) {
//            at StructureSearchPage
//            structureSearchButton.click()
//        }
//        then:
//        at ResultsPage
//    }

}