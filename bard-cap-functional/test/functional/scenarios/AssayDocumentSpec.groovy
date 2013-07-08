package scenarios
import base.BardFunctionalSpec;
import pages.HomePage
import pages.FindAssayByIdPage
import pages.ViewAssayDefinitionPage
import spock.lang.Stepwise

@Stepwise
class AssayDocumentSpec extends BardFunctionalSpec {
	String assayId = testData.assayId
	
	def setupSpec() {
		logInSomeUser()
	}

	def "Open and close create new document window"() {
		when: "User is at home page, Navigate to Find Assay By ID page"
		at HomePage
		navigateToFindAssayById()

		then: "User is at home page, Navigate to Find Assay By ID page"
		at FindAssayByIdPage

		when: "Find assay by id"
		searchAsssay(assayId)

		then: "User is navigated to View Assay Definition page"
		at ViewAssayDefinitionPage

	}
}