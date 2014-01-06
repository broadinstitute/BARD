package scenarios

import pages.HomePage

/**
 * This class extends all the test functions from AssayBaseContextSpec and override data values
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
