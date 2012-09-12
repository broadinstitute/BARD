package webtests

import geb.spock.GebReportingSpec
import spock.lang.Stepwise

@Stepwise
class SearchSpec extends GebReportingSpec {

    def "Go to home page"() {
        when:
        to HomePage
        then:
        at HomePage
    }

    def "Go to Structure search page"() {
        when:
        structureSearchLink.click()
        then:
        at StructureSearchPage
    }

	def "start a substructure search with no structure"() {
		when:
        //do some assertions here that we are on the page?
       // structureRadioButton.click()
        structureSearchButton.click()
		then:
		at HomePage
	}
//
//	def "check the entered details"() {
//		expect:
//		firstName == "Luke"
//		lastName == "Daley"
//		enabled == true
//	}
//
//	def "edit the details"() {
//		when:
//		editButton.click()
//		then:
//		at EditPage
//		when:
//		enabled = false
//		updateButton.click()
//		then:
//		at ShowPage
//	}
//
//	def "check in listing"() {
//		when:
//		to ListPage
//		then:
//		personRows.size() == 1
//		def row = personRow(0)
//		row.firstName == "Luke"
//		row.lastName == "Daley"
//	}
//
//	def "show person"() {
//		when:
//		personRow(0).showLink.click()
//		then:
//		at ShowPage
//	}
//
//	def "delete user"() {
//		given:
//		def deletedId = id
//		when:
//		withConfirm { deleteButton.click() }
//		then:
//		at ListPage
//		message == "Person $deletedId deleted"
//		personRows.size() == 0
//	}
}