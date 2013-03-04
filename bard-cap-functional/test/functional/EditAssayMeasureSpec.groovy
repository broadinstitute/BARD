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
		//assert measureDetailModule.addChildMeasureBtn
		measureDetailModule.addChildMeasureBtn("$resultTypeTopMeasure") //click to open add child measure window
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
		addEditAssayCards.addNewCardBtn.click()
		waitFor{ addEditAssayCards.titleBar }
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
		//navigate to child measure
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)

		waitFor(10, 5) { measureDetailModule.measuresDetails("$resultTypeChildMeasure").find("form.form-horizontal") }
		measureDetailModule.measuresDetails("$resultTypeChildMeasure").find("form.form-horizontal").find("#assayContextId").value("$contextCard")  //select context to assiciate measure with
		measureDetailModule.measuresDetails("$resultTypeChildMeasure").find("form.form-horizontal").find("button").click()  //click assiciate button

		report "AssociateChildMeasureWithContext"
		when:
		at EditAssayMeasurePage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage

		//assert that measure is associated with context
//		wiatFor(10, 5) { cardsHold.cardcap("$contextCard") }
	
//		assert cardsHolder.cardcap("$contextCard").next().find("a", text:"$resultTypeChildMeasure ($resultValueChildMeasure)")
	}

	def "Test Disassociate Measure from Context"(){
		editMeasureNavigate()

		when: "User is at Edit Assay Measure Page"
		at EditAssayMeasurePage
		then: "Disassociate Child Measure from Context"
		//navigate to child measure
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)
		//		measuresHolder.find("a", text:"$resultTypeTopMeasure ($resultValueTopMeasure)").parent().find("span")[0].click()
		//		measuresHolder.find("ul").find("a", text:"$resultTypeTopMeasure ($resultValueTopMeasure)").click()

		assert measureDetailModule.measuresDetails("$resultTypeChildMeasure").find("form.form-horizontal")
		measureDetailModule.measuresDetails("$resultTypeChildMeasure").find("form.form-horizontal").find("button", text:"Disassociate context from $resultTypeChildMeasure").click()

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
		//navigate to child measure window
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)
		
		assert measureDetailModule.measuresDetails("$resultTypeChildMeasure").find("form")[1].find("button", text:"Click to delete $resultTypeChildMeasure"+" entirely")
		measureDetailModule.measuresDetails("$resultTypeChildMeasure").find("form")[1].find("button", text:"Click to delete $resultTypeChildMeasure"+" entirely").click()

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
		assert measureDetailModule.measuresDetails("$resultTypeTopMeasure").find("form")[1].find("button", text:"Click to delete $resultTypeTopMeasure"+" entirely")

		measureDetailModule.measuresDetails("$resultTypeTopMeasure").find("form")[1].find("button", text:"Click to delete $resultTypeTopMeasure"+" entirely").click()

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
	
	}
}