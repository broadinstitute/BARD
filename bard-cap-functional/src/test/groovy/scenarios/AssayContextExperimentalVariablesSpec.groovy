package scenarios
/**
 * This class extends all the test functions from AssayBaseContextSpec and override data values
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssayContextExperimentalVariablesSpec extends AssayBaseContextSpec{
	@Override
	def setupSpec() {
		section = "experimental-variables-header"
		cardGroup = "cardHolderExperimentalVariables"
		editContextGroup = "Experimental-Variables"
		dbContextType = "Experimental Variables"
	}

}
