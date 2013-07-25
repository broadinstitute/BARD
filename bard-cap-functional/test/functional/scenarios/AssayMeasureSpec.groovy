package scenarios
import base.BardFunctionalSpec;
import spock.lang.Ignore;
import spock.lang.Stepwise
import pages.ViewAssayDefinitionPage
import pages.EditAssayContextPage
import db.Assay

@Stepwise
@Ignore
class AssayMeasureSpec extends BardFunctionalSpec {
	String resultTypeTopMeasure = testData.resultTypeTopMeasure
	String resultValueTopMeasure = testData.resultValueTopMeasure
	String resultTypeChildMeasure = testData.resultTypeChildMeasure
	String resultValueChildMeasure = testData.resultValueChildMeasure
	String contextCard = testData.cardForAssociation
	def assays = new Assay()
	final static WAIT_INTERVAL = 15
	final static R_INTERVAL = 3
	def setupSpec() {
		logInSomeUser()
		searchAssayById()
		searchAsssay(testData.assayId)
	}

	def "Test Add Assay Top Measure"() {
		editMeasureNavigate()
		
		when: "User is at Edit Assay Measure Page"
		at EditAssayMeasurePage
		def uiMeasures = []
		def dbMeasures = []
		uiMeasures = getUIMeasures()
		dbMeasures = assays.getAssayMeasures(testData.assayId)
		
		then: "Validate assay measures"
		assert uiMeasures.size() == dbMeasures.size()
		assert uiMeasures.sort() == dbMeasures.sort()
		
		and:"Adding New Top Measure"
		addTopMeasureBtn.click()
		newMeasure(resultTypeTopMeasure, resultValueTopMeasure)
		assert isMeasure(resultTypeTopMeasure, resultValueTopMeasure)
		report "AddTopMeasure"
		
		when: "Measure is added, Fetch measures info to validate"
		dbMeasures = assays.getAssayMeasures(testData.assayId)
		uiMeasures = getUIMeasures()
		
		then: "Validate measure info with UI and database"
		assert uiMeasures.size() == dbMeasures.size()
		assert uiMeasures.sort() == dbMeasures.sort()
		
		when: "Measures info is validated, Finish editing"
		finishEditingBtn.click()
		
		then: "Navigate back to view assay page"
		at ViewAssayDefinitionPage
	}

	def "Test Add Assay Child Measure"() {
		editMeasureNavigate()
		
		when: "User is at Edit Assay Measure Page"
		at EditAssayMeasurePage
		def uiMeasures = []
		def dbMeasures = []
		uiMeasures = getUIMeasures()
		dbMeasures = assays.getAssayMeasures(testData.assayId)
		
		then: "Validate assay measures"
		assert uiMeasures.size() == dbMeasures.size()
		assert uiMeasures.sort() == dbMeasures.sort()
		
		and: "Adding New Child Measure"
		measuresHolder.find("a", text:"$resultTypeTopMeasure ($resultValueTopMeasure)").click()	 //highlighting the Top Measure
		measureDetail.addChildMeasureBtn(resultTypeTopMeasure).click()							 //click to open add child measure window
		newMeasure(resultTypeChildMeasure, resultValueChildMeasure)
		assert isMeasure(resultTypeChildMeasure, resultValueChildMeasure)
		report "AddChildMeasure"
		
		when: "Measure is added, Fetch measures info to validate"
		dbMeasures = assays.getAssayMeasures(testData.assayId)
		uiMeasures = getUIMeasures()
		
		then: "Validate measure info with UI and database"
		assert uiMeasures.size() == dbMeasures.size()
		assert uiMeasures.sort() == dbMeasures.sort()
		
		when: "Measures info is validated, Finish editing"
		finishEditingBtn.click()
		
		then: "Navigate back to view assay page"
		at ViewAssayDefinitionPage
	}

	def "Test Associate Measure with Context"() {
		editAssayContext()
		
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		
		then: "Adding context card to associate measure with"
		addNewContextCard(contextCard)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { isCardPresent(contextCard) }
		assert isCardPresent(contextCard)
		
		when: "Card is added, Finish Editing "
		finishEditingBtn.click()
		
		then: "View assay definition page is displayed"
		at ViewAssayDefinitionPage
		
		when: "User is at view assay page, Edit measure"
		editMeasureNavigate()
		
		then: "User is at Edit Assay Measure Page"
		at EditAssayMeasurePage
		
		and: "Associating Child Measure with Context"
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { measureDetail.associateBtn(resultTypeChildMeasure) }
		measureDetail.assayContext(resultTypeChildMeasure).value(contextCard) 	 //select context to assiciate measure with
		measureDetail.associateBtn(resultTypeChildMeasure).click() 				 //click assiciate button
		waitFor(WAIT_INTERVAL, R_INTERVAL) { measuresHolder}
		report "AssociateChildMeasureWithContext"
		
		when: "Measure is associated, Fetch info to validate"
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { measureDetail.associateBtn(resultTypeChildMeasure) }
		def uiAssociatedMeasureContex = getUIAssociatedContext(resultTypeChildMeasure)
		def dbAssociatedMeasureContex = assays.getContextMeasures(testData.assayId, resultTypeChildMeasure)
		
		then: "Validate if measure is associated with context"
		assert uiAssociatedMeasureContex == dbAssociatedMeasureContex
		
		when: "Measures association is validated, Finish editing"
		finishEditingBtn.click()
		
		then: "Navigate back to view assay page"
		at ViewAssayDefinitionPage
		
		when: "At view assay page, Fetch assays measure info to validate"
		at ViewAssayDefinitionPage
		uiAssociatedMeasureContex = getUIAssociatedMeasure(contextCard)
		dbAssociatedMeasureContex = assays.getContextMeasures(testData.assayId, resultTypeChildMeasure)
		
		then: "Validate if measure is associated with context from database and UI"
		assert uiAssociatedMeasureContex == dbAssociatedMeasureContex
	}

	def "Test Disassociate Measure from Context"(){
		editMeasureNavigate()

		when: "User is at Edit Assay Measure Page, Fetch measure info"
		at EditAssayMeasurePage
		def uiAssociatedMeasureContex = [:]
		def dbAssociatedMeasureContex = [:]

		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { measureDetail.associateBtn(resultTypeChildMeasure) }
		uiAssociatedMeasureContex = getUIAssociatedContext(resultTypeChildMeasure)
		dbAssociatedMeasureContex = assays.getContextMeasures(testData.assayId, resultTypeChildMeasure)

		then: "Validate if measure is associated with context"
		assert uiAssociatedMeasureContex == dbAssociatedMeasureContex

		and: "Disassociate Child Measure from Context"
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { measureDetail.associateBtn(resultTypeChildMeasure) }
		measureDetail.disasiciateBtn(resultTypeChildMeasure).click()
		report "DiisassociateChildMeasureFromContext"

		when: "Measure is disassociated, Fetch info to validate"
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)
		waitFor(WAIT_INTERVAL, R_INTERVAL) { measureDetail.associateBtn(resultTypeChildMeasure) }
		uiAssociatedMeasureContex = getUIAssociatedContext(resultTypeChildMeasure)
		dbAssociatedMeasureContex = assays.getContextMeasures(testData.assayId, resultTypeChildMeasure)

		then: "Validate that measure is disassociated from context"
		assert uiAssociatedMeasureContex == dbAssociatedMeasureContex

		when: "Measure is disassociated and validated"
		finishEditingBtn.click()

		then: "User is at view assay definition page"
		at ViewAssayDefinitionPage

		when: "Finished measured edit, Fetch assay measure info to validate"
		uiAssociatedMeasureContex = getUIAssociatedMeasure(contextCard)
		dbAssociatedMeasureContex = assays.getContextMeasures(testData.assayId, resultTypeChildMeasure)

		then: "Validate if measure is associated with context from database and UI"
		assert uiAssociatedMeasureContex == dbAssociatedMeasureContex
	}

	def "Test Delete Child Measure"(){
		editMeasureNavigate()
		
		when:"User is at Edit Assay Measure Page"
		at EditAssayMeasurePage
		def uiMeasures = []
		def dbMeasures = []
		uiMeasures = getUIMeasures()
		dbMeasures = assays.getAssayMeasures(testData.assayId)
		
		then: "Validate assay measures"
		assert uiMeasures.size() == dbMeasures.size()
		assert uiMeasures.sort() == dbMeasures.sort()
		
		and:"Delete the Child Measure"
		navigateToChildMeasure(resultTypeTopMeasure, resultValueTopMeasure, resultTypeChildMeasure, resultValueChildMeasure)
		waitFor(WAIT_INTERVAL, R_INTERVAL) {measureDetail.deleteMeasure(resultTypeChildMeasure)}
		measureDetail.deleteMeasure(resultTypeChildMeasure).click()
		assert !isMeasure(resultTypeChildMeasure, resultValueChildMeasure)
		report "DeleteChildMeasure"
		
		when: "Measure is deleted, Fetch measures info to validate"
		dbMeasures = assays.getAssayMeasures(testData.assayId)
		uiMeasures = getUIMeasures()
		
		then: "Validate measure info with UI and database"
		assert uiMeasures.size() == dbMeasures.size()
		assert uiMeasures.sort() == dbMeasures.sort()
		
		when: "Measures info is validated, Finish editing"
		finishEditingBtn.click()
		
		then: "Navigate back to view assay page"
		at ViewAssayDefinitionPage
	}
	
	def "Test Delete Top Measure"(){
		editMeasureNavigate()
		
		when:"User is at Edit Assay Measure Page"
		at EditAssayMeasurePage
		def uiMeasures = []
		def dbMeasures = []
		uiMeasures = getUIMeasures()
		dbMeasures = assays.getAssayMeasures(testData.assayId)
		
		then: "Validate assay measures"
		assert uiMeasures.size() == dbMeasures.size()
		assert uiMeasures.sort() == dbMeasures.sort()
		
		and: "Delete the Top Measure"
		measuresHolder.find("a", text:"$resultTypeTopMeasure ($resultValueTopMeasure)").click()
		waitFor(WAIT_INTERVAL, R_INTERVAL) {measureDetail.deleteMeasure(resultTypeTopMeasure)}
		measureDetail.deleteMeasure(resultTypeTopMeasure).click()
		assert !isMeasure(resultTypeTopMeasure, resultValueTopMeasure)
		report "DeleteTopMeasure"
		
		when: "Measure is deleted, Fetch measures info to validate"
		dbMeasures = assays.getAssayMeasures(testData.assayId)
		uiMeasures = getUIMeasures()
		
		then: "Validate measure info with UI and database"
		assert uiMeasures.size() == dbMeasures.size()
		assert uiMeasures.sort() == dbMeasures.sort()
		
		when: "Measures info is validated, Finish editing"
		finishEditingBtn.click()
		
		then: "Navigate back to view assay page"
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