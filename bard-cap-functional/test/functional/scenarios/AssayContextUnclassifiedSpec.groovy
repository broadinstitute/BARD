package scenarios

import pages.HomePage

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssayContextUnclassifiedSpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "unclassified-header"
		editContextGroup = "Unclassified"
		dbContextType = "Unclassified"
		
		logInSomeUser()
	}

}
