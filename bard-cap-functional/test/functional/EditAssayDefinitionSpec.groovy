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
	//String assayId = testData.assayId
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
		addEditAssayCards.addNewCardBtn.click()
		waitFor{ addEditAssayCards.titleBar }
		addNewContextCard("$card1")
		report "Card1Add"
		when:
		at EditAssayContextPage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}

	def "Test Add Assay Context Item"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		then: "verify that newly card shows all popup menu items i.e. Edit Card Name, Add Item Wizard, Delete Card etc"
		verifyEmptyCardsMenu("$card1")
		assayCardsHolder.cardMenu("$card1") //click to open context card menu drop down
		assayCardsHolder.cardMenuDD[2].click() //click to open add item wizard
		itemWizard.defineAttributes("$arrtibuteSearch")
		itemWizard.defineValueType("$valueType")
		itemWizard.defineValue("$arrtibuteSearch", "$valueTypeQualifier", "$unitValue")
		itemWizard.reviewnSave()
		itemWizard.closeBtn.click()
		when:
		at EditAssayContextPage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}

	def "Test Move Assay Context Item"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		then: "Adding Context Card 2 for Item Move"
		addEditAssayCards.addNewCardBtn.click()
		waitFor{ addEditAssayCards.titleBar }
		addNewContextCard("$card2")
		assayCardsHolder.cardItemMenu("$card1") //click to open context card menu drop down
		assayCardsHolder.cardItemMenuDD[1].click()
		//waiting for Move Window appear
		waitFor(20, 1){ moveAssayCardItem.selectCardId }
		moveAssayCardItem.newCardHolder("$card2")
		moveAssayCardItem.moveBtn.click()
		waitFor(10, 1) { assayCardsHolder.cardItemsLabel("$card2") }
		//isCardEmpty("New Card 2")
		when:
		at EditAssayContextPage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}
	def "Test Delete Assay Context Item"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		// Move Assay Card Item
		then:
		//	def temp = assayCardsHolder.cardItemsLabel("New Card 2").size()
		assayCardsHolder.cardItemMenu("$card2") //click to open context card menu drop down
		assert assayCardsHolder.cardItemMenuDD[1] // Move Card Item button
		assert assayCardsHolder.cardItemMenuDD[2] // Delete Card Item button
		assayCardsHolder.cardItemMenuDD[2].click()
		//Delete Window appear
		waitFor(10, 1){ deleteAssayCards.deleteBtn }
		deleteAssayCards.deleteBtn.click()
		waitFor(10) { at EditAssayContextPage }
		//	waitFor(10) { assayCardsHolder.cardItemsLabel("New Card 2").size() < temp }
		when:EditAssayContextPage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}
	def "Test Edit Assay Context Card"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		// Edit Assay Card Name
		then:
		assayCardsHolder.cardMenu("$card1") //click to open context card menu drop down
		assayCardsHolder.cardMenuDD[1].click()
		assert editAssayCards.titleBar.text() ==~ "Edit Card Name"
		editAssayCards.enterCardName.value("")
		editAssayCards.enterCardName << "$cardUpdated"
		editAssayCards.saveBtn.click()
		waitFor(25, 2){ assayCardsHolder.cardCaption("$cardUpdated")}
		when:
		at EditAssayContextPage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}
	def "Test Delete Assay Card1"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		// Move Assay Card Item
		then:
		deletAssayCard("$cardUpdated")
		
		when:
		at EditAssayContextPage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}

	def "Test Delete Assay Card2"() {
		editAssayContext()
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		// Move Assay Card Item
		then:
		deletAssayCard("$card2")
		
		when:
		at EditAssayContextPage
		finishEditingBtn.click()
		then:
		at ViewAssayDefinitionPage
	}

	void cleanupSpec() {
		//logoutFromApp()
	}

}