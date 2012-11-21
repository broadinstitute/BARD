package webtests

import spock.lang.Stepwise
import webtests.pages.HomePage
import webtests.pages.LoginPage
import webtests.pages.ResultsPage
import webtests.pages.StructureSearchPage

@Stepwise
class SearchFunctionalSpec extends BardFunctionalSpec {
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

    def "Go to Structure search page"() {
        when:
        structureSearchLink.click()
        at StructureSearchPage
        $('#searchForm').submit()

        then:
        at HomePage

    }
}