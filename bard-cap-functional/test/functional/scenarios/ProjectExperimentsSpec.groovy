package scenarios

import pages.CapSearchPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import spock.lang.Ignore
import base.BardFunctionalSpec

import common.Constants
import common.TestDataReader
import common.Constants.NavigateTo
import common.Constants.SearchBy

//@Stepwise
@Ignore
class ProjectExperimentsSpec extends BardFunctionalSpec {

	def testData = TestDataReader.getTestData()

	def setup() {
		logInSomeUser()

		when: "User is at Home page, Navigating to Search Project By Id page"
		at HomePage
		navigateTo(NavigateTo.PROJECT_BY_ID)

		then: "User is at Search Project by Id page"
		at CapSearchPage

		when: "User is trying to search some project"
		at CapSearchPage
		capSearchBy(SearchBy.PROJECT_ID, testData.ProjectID)

		then: "User is at View Project Definition page"
		at ViewProjectDefinitionPage
	}

	def "Test Add Experiment By Experiment Name into Project"() {
		when:"User is navigated to add experiment"
		at ViewProjectDefinitionPage
//		assert experimentBtns.addExperimentBtn
//		experimentBtns.addExperimentBtn.click()
//		then:"Adding New Experiment"
//		at EditProjectPage
//		assert associateExpriment.titleBar
//		associateExpriment.experimentBy.addExperimentBy(EXPERIMENTNAMEFLD)
//		associateExpriment.experimentBy.addExperimentByValue(EXPERIMENTNAMEFLD) << exprimentName
//		waitFor { associateExpriment.popupList }
//		associateExpriment.popupList[0].click()
//		String exprimentName = associateExpriment.availableExperiments.exprimentsList[0].text()
//		associateExpriment.availableExperiments.exprimentsList.value(exprimentName).click()
//		def experimentId = exprimentName.takeWhile { it != '-' }
//		associateExpriment.stageSelect.stageLink.click()
//		associateExpriment.stageSelect.stageField << stageValue
//		waitFor(15, 5) { associateExpriment.stageSelect.resultPopup }
//		associateExpriment.stageSelect.resultPopup.click()
//		assert associateExpriment.addExprimentBtn
//		associateExpriment.addExprimentBtn.click()
		def addByValue = "3141"
		then:""
		addExperiment(addByValue, stageVal)
		waitFor(20, 5){	exprimentCanvas.addedExperiment(experimentId) }
		def exAdded = exprimentCanvas.addedExperiment(experimentId)
		assert exAdded
		when:"Navigating to Home Page"
		at ViewProjectDefinitionPage
		capHeaders.bardLogo.click()
		then:"User is at Home page"
		at HomePage
	}
	/*
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
	 */
}