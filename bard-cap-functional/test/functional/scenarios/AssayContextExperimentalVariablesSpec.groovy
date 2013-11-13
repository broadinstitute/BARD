package scenarios

import pages.CapSearchPage
import pages.HomePage
import pages.ViewAssayDefinitionPage
import spock.lang.Ignore;
import common.Constants.NavigateTo
import common.Constants.SearchBy

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssayContextExperimentalVariablesSpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "experimental-variables-header"
		editContextGroup = "Experimental-Variables"
		dbContextType = "Experimental Variables"
		
		logInSomeUser()
	}

}
