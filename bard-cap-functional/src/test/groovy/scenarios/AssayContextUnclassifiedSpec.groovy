package scenarios
/**
 * This class extends all the test functions from AssayBaseContextSpec and override data values
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
