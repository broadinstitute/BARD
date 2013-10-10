package scenarios

import pages.CapSearchPage
import pages.HomePage
import pages.ViewAssayDefinitionPage

import common.Constants.NavigateTo
import common.Constants.SearchBy

/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 * Last Updated: 13/10/10
 */
class AssayContextAssayProtocolSpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "assay-protocol"
		cardGroup = "cardHolderAssayProtocol"
		editContextGroup = "Assay-Protocol"
		dbContextType = "Assay Protocol"
//		oldGroup = "assay protocol> assay format>"
		
		logInSomeUser()

//		when: "User is at Home page, Navigating to Search Assay By Id page"
//		at HomePage
//		navigateTo(NavigateTo.ASSAY_BY_ID)
//
//		then: "User is at Search Assay by Id page"
//		at CapSearchPage
//
//		when: "User is trying to search some Assay"
//		at CapSearchPage
//		capSearchBy(SearchBy.ASSAY_ID, testData.AssayId)
//
//		then: "User is at View Assay Definition page"
//		at ViewAssayDefinitionPage
	}

}
