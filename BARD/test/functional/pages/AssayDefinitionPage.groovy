package pages

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/21/13
 * Time: 10:52 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayDefinitionPage extends ScaffoldPage {
    static url = "assayDefinition/show"

    static at = {
        title ==~ /View Assay Definition/
    }

    static content = {
        editSummaryButton { $("#editSummaryButton") }
        editSummaryForm { $("#editSummaryForm") }
        submitEditSummary { $("#submitEditSummary") }
    }

    void editSummary(String name, String designedBy) {
//        assert ! editSummaryForm.isDisplayed()
        editSummaryButton.click()
        assert editSummaryForm.isDisplayed()

        editSummaryForm.assayName = name
        editSummaryForm.designedBy = designedBy
        submitEditSummary.click()
    }
}

