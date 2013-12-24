package scenarios

import pages.HomePage

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssayContextAssayReadoutSpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "assay-readout-header"
		cardGroup = "cardHolderAssayReadout"
		editContextGroup = "Assay-Readout"
		dbContextType = "Assay Readout"
		
		logInSomeUser()
	}

}
