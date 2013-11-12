package scenarios

import pages.HomePage

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssayContextAssayBiologySpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "biology-header"
//		cardGroup = "cardHolderBiology"
		editContextGroup = "Biology"
		dbContextType = "Biology"

		logInSomeUser()
	}

}
