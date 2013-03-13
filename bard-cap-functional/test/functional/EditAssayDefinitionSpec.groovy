import java.util.Map;

import geb.waiting.Wait;

import org.apache.tools.ant.taskdefs.WaitFor;
import pages.HomePage
import pages.ViewAssayDefinitionPage
import pages.FindAssayByIdPage
import pages.EditAssayContextPage
import pages.ScaffoldPage
import spock.lang.Shared;
import spock.lang.Stepwise

@Stepwise
class EditAssayDefinitionSpec extends BardFunctionalSpec {
	String card1 = testData.card1
	String card2 = testData.card2
	String cardUpdated = testData.cardUpdated
	String arrtibuteSearch = testData.arrtibuteSearch
	String valueType = testData.valueType
	String valueTypeQualifier = testData.valueTypeQualifier
	String unitValue = testData.unitValue
	String unitSelect = testData.unitSelect

	void setupSpec() {
		//browser.config.autoClearCookies = true
		logInSomeUser()
		searchAssayById()
		searchAsssay(testData.assayId)
	}

	def "Test Add Assay Context Card"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		then: "Adding New Context Card"
		
		addNewContextCard("$card1")
		
		report "Card1Add"
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		finishEditingBtn.click()
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Empty Card Dropdown Menu"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		then: "Verifing empty card drop down menu"
		verifyEmptyCardsMenu("$card1")
		report "EmptyCardMenu"
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		finishEditingBtn.click()
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}
	
	def "Test Add Assay Context Item"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage

		then: "Add context Item to card"
		cardHolders.cardMenu("$card1").click()
		cardHolders.cardDDMenu[2].click()
		defineAttributes("$arrtibuteSearch")
		defineValueType("$valueType")
		defineValue("$arrtibuteSearch", "$valueTypeQualifier", "$unitValue", "$unitSelect")
		reviewAndSave()
		report "AddContextItem"
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		waitFor {finishEditingBtn }
		Thread.sleep(2000)
		finishEditingBtn.click()
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Move Assay Context Item"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		then: "Adding Context Card 2 for Item Move"
		addNewContextCard("$card2")
		cardHolders.cardItemMenu("$card1").click()
		cardHolders.cardItemMenuDD[1].click()
		waitFor(20, 3){ moveAssayCardItem.selectCardId }
		moveAssayCardItem.newCardHolder("$card2")
		moveAssayCardItem.moveBtn.click()
		report "MoveContextItem"
		waitFor(20, 3) { cardHolders.cardItems("$card2") }
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		finishEditingBtn.click()
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Delete Assay Context Item"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		then: "Deleting Assay Context Item"
		cardHolders.cardItemMenu("$card2").click()
		assert cardHolders.cardItemMenuDD[1]
		assert cardHolders.cardItemMenuDD[2]
		cardHolders.cardItemMenuDD[2].click()
		waitFor(10, 1){ deleteAssayCards.deleteBtn }
		deleteAssayCards.deleteBtn.click()
		report "DeleteContextItem"
		waitFor(10) { at EditAssayContextPage }
		when:"User is at Edit Assay Definition Page"
		finishEditingBtn.click()
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Edit Assay Context Card"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		then: "Editing Assay Context Card"
		cardHolders.cardMenu("$card1").click()
		cardHolders.cardDDMenu[1].click()
		assert editAssayCards.titleBar.text() ==~ "Edit Card Name"
		editAssayCards.enterCardName.value("")
		editAssayCards.enterCardName << "$cardUpdated"
		editAssayCards.saveBtn.click()
		report "EditContextCard"
		waitFor(25, 2){ cardHolders.cardName("$cardUpdated") }
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		finishEditingBtn.click()
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Delete Assay Card1"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		then: "Deleting Assay Context Card"
		deletAssayCard("$cardUpdated")
		report "DeleteContextCard1"
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		finishEditingBtn.click()
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Delete Assay Card2"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		then: "Editing Assay Context Card"
		
		deletAssayCard("$card2")
		report "DeleteContextCard2"
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		finishEditingBtn.click()
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	void cleanupSpec() {
	}

}