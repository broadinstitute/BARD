import pages.HomePage
import pages.FindProjectByIdPage
import pages.ViewProjectDefinitionPage
import pages.ScaffoldPage
import spock.lang.Stepwise
import pages.EditProjectPage

@Stepwise
class EditProjectSpec extends BardFunctionalSpec {
	String exprimentName = testData.exprimentName
	String exprimentIdInput = testData.exprimentId
	String stageValue = testData.stageValue
	String projName = testData.editProjName
	String projDesc = testData.editProjDesc
	
	final static String fromExp = "5477"
	final static String toExp = "75"
	
	final static String EXPERIMENTNAMEFLD = "ExperimentName"
	final static String EXPERIMENTIDFLD = "ExperimentId"
	final static String FROMEXPID = "fromExperimentId"
	final static String TOEXPID = "toExperimentId"
	
	void setupSpec() {
		// pre-condition of each test: user is logged in
		logInSomeUser()
	}

	def "Test Edit Project Summary Information"() {
		given:
		navigateToSearchProjectById()
		searchProject(testData.projectId)
		when:"User is navigated to view project detail page"
		at ViewProjectDefinitionPage
		assert editSummaryBtn
		editSummaryBtn.click()
		then: "User is at Edit summary form"
		at EditProjectPage
		
		waitFor { editProjectSummary.projNameField }
		assert editProjectSummary.projNameField
		assert editProjectSummary.projDescField
		assert editProjectSummary.updateBtn
		assert editProjectSummary.cancelBtn
		editProjectSummary.projNameField.value("")
		editProjectSummary.projNameField <<projName
		editProjectSummary.projDescField.value("")
		editProjectSummary.projDescField <<projDesc
		editProjectSummary.updateBtn.click()

		waitFor(15, 5) { projectSummary.value[1].text() ==~ projName }
		assert projectSummary.value[1].text() ==~ projName
		assert projectSummary.value[2].text() ==~ projDesc
		
		when:"Navigating to Home Page"
		at ViewProjectDefinitionPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}
	
	def "Test Add Experiment By Experiment Name into Project"() {
		given:
		navigateToSearchProjectById()
		searchProject(testData.projectId)
		when:"User is navigated to add experiment"
		at ViewProjectDefinitionPage
		assert experimentBtns.addExperimentBtn
		experimentBtns.addExperimentBtn.click()
		
		then:"Adding New Experiment"
		at EditProjectPage
		assert associateExpriment.titleBar
		
		associateExpriment.experimentBy.addExperimentBy(EXPERIMENTNAMEFLD)
		associateExpriment.experimentBy.addExperimentByValue(EXPERIMENTNAMEFLD) << exprimentName 
		waitFor { associateExpriment.popupList }
		associateExpriment.popupList[0].click()
		String exprimentName = associateExpriment.availableExperiments.exprimentsList[0].text()
		associateExpriment.availableExperiments.exprimentsList.value(exprimentName).click()
		def experimentId = exprimentName.takeWhile { it != '-' }
		associateExpriment.stageSelect.stageLink.click()
		associateExpriment.stageSelect.stageField << stageValue
		waitFor(15, 5) { associateExpriment.stageSelect.resultPopup }
		associateExpriment.stageSelect.resultPopup.click()
		assert associateExpriment.addExprimentBtn
		associateExpriment.addExprimentBtn.click()
		waitFor(20, 5){	exprimentCanvas.addedExperiment(experimentId) }
		def exAdded = exprimentCanvas.addedExperiment(experimentId)
		assert exAdded
		
		when:"Navigating to Home Page"
		at ViewProjectDefinitionPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}
	
	def "Test Add Experiment By Experiment Id into Project"() {
		given:
		navigateToSearchProjectById()
		searchProject(testData.projectId)
		when:"User is navigated to add experiment"
		at ViewProjectDefinitionPage
		assert experimentBtns.addExperimentBtn
		experimentBtns.addExperimentBtn.click()
		
		then:"Adding New Experiment"
		at EditProjectPage
		assert associateExpriment.titleBar
		
		associateExpriment.experimentBy.addExperimentBy(EXPERIMENTIDFLD)
		associateExpriment.experimentBy.addExperimentByValue(EXPERIMENTIDFLD) << exprimentIdInput
		assert associateExpriment.availableExperiments.exprimentsList[0]
		associateExpriment.availableExperiments.exprimentsList[0].click()
		waitFor(15, 5) { associateExpriment.availableExperiments.exprimentsList[0].text() != "Empty"  }
		def exprimentName = associateExpriment.availableExperiments.exprimentsList[0].text()
		associateExpriment.availableExperiments.exprimentsList.value(exprimentName).click()
		def experimentId = exprimentName.takeWhile { it != '-' }
		associateExpriment.stageSelect.stageLink.click()
		associateExpriment.stageSelect.stageField << stageValue
		waitFor(15, 5) { associateExpriment.stageSelect.resultPopup }
		associateExpriment.stageSelect.resultPopup.click()
		assert associateExpriment.addExprimentBtn
		associateExpriment.addExprimentBtn.click()
		waitFor(20, 5){	exprimentCanvas.addedExperiment(experimentId) }
		def exAdded = exprimentCanvas.addedExperiment(experimentId)
		assert exAdded
		
		when:"Navigating to Home Page"
		at ViewProjectDefinitionPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}
	
	def "Test Add Experiment Link"() {
		given:
		navigateToSearchProjectById()
		searchProject(testData.projectId)
		
		when:"User is at View Project Page"
		at ViewProjectDefinitionPage
		assert experimentBtns.linkExperimentBtn
		experimentBtns.linkExperimentBtn.click()
		
		then:"find out the canvas experiment and delete it"
		at EditProjectPage
		
		assert linkExpriment.titleBar
		assert linkExpriment.experimentForm.linkExpFromTo(FROMEXPID)
		assert linkExpriment.experimentForm.linkExpFromTo(TOEXPID)
		assert linkExpriment.linkExprimentBtn
		linkExpriment.experimentForm.linkExpFromTo(FROMEXPID) << fromExp
		linkExpriment.experimentForm.linkExpFromTo(TOEXPID) << toExp
		linkExpriment.linkExprimentBtn.click()
		
		when:"Navigating to Home Page"
		at EditProjectPage
		capHeaders.bardLogo.click()
		
		then:"User is at Home page"
		at HomePage
	}
	
}