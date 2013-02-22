import pages.AssayDefinitionPage

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/21/13
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayDefinitionSpec  extends BardFunctionalSpec {
    def 'test assay definition page' () {
        when:
        to AssayDefinitionPage, "4250"

        then:
        at AssayDefinitionPage
    }

}
