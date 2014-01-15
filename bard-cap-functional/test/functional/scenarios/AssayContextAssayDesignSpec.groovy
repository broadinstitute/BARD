package scenarios

import org.junit.Ignore
import pages.HomePage

/**
 * This class extends all the test functions from AssayBaseContextSpec and override data values
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
@Ignore
class AssayContextAssayDesignSpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "assay-design-header"
		cardGroup = "cardHolderAssayDesign"
		editContextGroup = "Assay-Design"
		dbContextType = "Assay Design"
		
		logInSomeUser()
	}

}
