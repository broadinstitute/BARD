package test.groovy.scenarios


/**
 * This class extends all the test functions from AssayBaseContextSpec and override data values
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssayContextAssayBiologySpec extends AssayBaseContextSpec{
	@Override
	def setup() {
		section = "biology-header"
		cardGroup = "cardHolderBiology"
		editContextGroup = "Biology"
		dbContextType = "Biology"

		logInSomeUser()
	}

}
