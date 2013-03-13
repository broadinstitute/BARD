import spock.lang.Stepwise;
import pages.ViewAssayDefinitionPage
import pages.EditAssayContextPage

@Stepwise
class EditAssayMeasureSpec extends BardFunctionalSpec {
	String resultTypeTopMeasure = testData.resultTypeTopMeasure
	String resultValueTopMeasure = testData.resultValueTopMeasure
	String resultTypeChildMeasure = testData.resultTypeChildMeasure
	String resultValueChildMeasure = testData.resultValueChildMeasure
	String contextCard = testData.cardForAssociation
	//String contextCard = testData.card2
	void setupSpec() {
		logInSomeUser()
		searchAssayById()
		searchAsssay(testData.assayId)
	}

	def "Test Add Assay Top Measure"() {
		editMeasureNavigate()
		when: "User is at Edit Assay Measure Page"
		at EditAssayMeasurePage
		then: "Adding New Top Measure"
		addTopMeasureBtn.click()
		newMeasure("$resultTypeTopMeasure", "$resultValueTopMeasure")
		report "AddTopMeasure"
		when:
		at EditAssayMeasurePage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}

	def "Test Add Assay Child Measure"() {
		editMeasureNavigate()
		when: "User is at Edit Assay Measure Page"
		at EditAssayMeasurePage

		then: "Adding New Child Measure"
		measuresHolder.find("a", text:"$resultTypeTopMeasure ($resultValueTopMeasure)").click() //highlighting the Top Measure
		measureDetailModule("$resultTypeTopMeasure").addChildMeasureBtn("$resultTypeTopMeasure").click() //click to open add child measure window
		newMeasure("$resultTypeChildMeasure", "$resultValueChildMeasure")
		report "AddChildMeasure"
		when:
		at EditAssayMeasurePage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}

	def "Test Associate Measure with Context"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		then: "Adding Context Card 2 for Item Move"
		
		addNewContextCard("$contextCard")

		when:
		at EditAssayContextPage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage

		editMeasureNavigate()
		when: "User is at Edit Assay Measure Page"
		at EditAssayMeasurePage

		then: "Associating Child Measure with Context"
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)

		waitFor(10, 5) { measureDetailModule("$resultTypeChildMeasure").measureForm }
		measureDetailModule("$resultTypeChildMeasure").assayContext.value("$contextCard")  //select context to assiciate measure with
		measureDetailModule("$resultTypeChildMeasure").associateBtn.click()  //click assiciate button

		report "AssociateChildMeasureWithContext"
		when:
		at EditAssayMeasurePage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage

	}

	def "Test Disassociate Measure from Context"(){
		editMeasureNavigate()

		when: "User is at Edit Assay Measure Page"
		at EditAssayMeasurePage
		then: "Disassociate Child Measure from Context"
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)
		assert measureDetailModule("$resultTypeChildMeasure").measureForm
		measureDetailModule("$resultTypeChildMeasure").disasiciateBtn("$resultTypeChildMeasure").click()

		report "DiisassociateChildMeasureFromContext"
		when:
		at EditAssayMeasurePage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}

	def "Test Delete Child Measure"(){
		editMeasureNavigate()

		when:"User is at Edit Assay Measure Page"
		at EditAssayMeasurePage
		then:"Deleting Child Measure"
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)
		assert measureDetailModule("$resultTypeChildMeasure").deleteMeasure("$resultTypeChildMeasure")
		measureDetailModule("$resultTypeChildMeasure").deleteMeasure("$resultTypeChildMeasure").click()

		report "DeleteChildMeasure"
		when:
		at EditAssayMeasurePage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}

	def "Test Delete Top Measure"(){
		editMeasureNavigate()

		when:"User is at Edit Assay Measure Page"
		at EditAssayMeasurePage
		then: "Deleting Top Measure"
		measuresHolder.find("a", text:"$resultTypeTopMeasure ($resultValueTopMeasure)").click()
		assert measureDetailModule("$resultTypeTopMeasure").deleteMeasure("$resultTypeTopMeasure")
		measureDetailModule("$resultTypeTopMeasure").deleteMeasure("$resultTypeTopMeasure").click()

		report "DeleteTopMeasure"
		when:
		at EditAssayMeasurePage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}

	def "Test Delete Assay Cards"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage

		then: "Deleting the previously added card"
		deletAssayCard("$contextCard")
		report "DeleteAssayCard"
	}

}