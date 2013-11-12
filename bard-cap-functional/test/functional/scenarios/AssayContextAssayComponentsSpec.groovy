package scenarios

import pages.HomePage

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssayContextAssayComponentsSpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "assay-components-header"
//		cardGroup = "cardHolderAssayComponents"
		editContextGroup = "Assay-Components"
		dbContextType = "Assay Components"
		
		logInSomeUser()
	}

}
