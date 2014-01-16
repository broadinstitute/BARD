package scenarios
/**
 * This class extends all the test functions from AssayBaseContextSpec and override data values
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssayContextAssayComponentsSpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "assay-components-header"
		cardGroup = "cardHolderAssayComponents"
		editContextGroup = "Assay-Components"
		dbContextType = "Assay Components"
		
		logInSomeUser()
	}

}
