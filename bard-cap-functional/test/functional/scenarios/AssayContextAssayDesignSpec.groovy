package scenarios

import pages.HomePage

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssayContextAssayDesignSpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "assay-design-header"
//		cardGroup = "cardHolderAssayDesign"
		editContextGroup = "Assay-Design"
		dbContextType = "Assay Design"
		
		logInSomeUser()
	}

}
