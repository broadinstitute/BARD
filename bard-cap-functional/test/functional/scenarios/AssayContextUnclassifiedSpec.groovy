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
		cardGroup = "cardHolderUnclassified"
		editContextGroup = "Unclassified"
		dbContextType = "Unclassified"
		
		logInSomeUser()
	}

}
