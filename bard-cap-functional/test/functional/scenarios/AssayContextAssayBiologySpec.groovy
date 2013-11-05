package scenarios

import pages.CapSearchPage
import pages.HomePage
import pages.ViewAssayDefinitionPage
import spock.lang.Ignore;
import common.Constants.NavigateTo
import common.Constants.SearchBy

/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 * Last Updated: 13/10/29
 */
@Ignore
class AssayContextAssayBiologySpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "biology"
		cardGroup = "cardHolderBiology"
		editContextGroup = "Biology"
		dbContextType = "Biology"

		logInSomeUser()
	}

}
