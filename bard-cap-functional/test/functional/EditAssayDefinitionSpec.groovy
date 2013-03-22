import pages.HomePage
import pages.ViewAssayDefinitionPage
import pages.FindAssayByIdPage
import pages.EditAssayContextPage
import pages.CapFunctionalPage
import pages.ScaffoldPage
import spock.lang.Shared;
import spock.lang.Stepwise
import db.Assay

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

	String editAssayName = testData.editAssayName
	String assayStatus = testData.assayStatus
	String designedBy = testData.designedBy
	static String cGroup = "assay protocol> assay component>"
	def assays = new Assay()

	void setupSpec() {
		//browser.config.autoClearCookies = true
		logInSomeUser()
		searchAssayById()
		searchAsssay(testData.assayId)
	}

	def "Test Update Assay Summary"() {
		when: "User is at View Assay Page"
		at ViewAssayDefinitionPage
		def summaryData = [:]
		def summaryUpdatedData = [:]
		def summaryOriginaldData = [:]
		def updateStr = "Updated"
		def summaryName = assaySummary.ddValue[3].text()
		def summaryDesignedBy = assaySummary.ddValue[4].text()
		def summaryStatus = assaySummary.ddValue[5].text()
		summaryData = assays.getAssaySummaryById(testData.assayId)

		then: "Validate assay summary info with database "
		assert assaySummary.ddValue[1].text().equalsIgnoreCase(summaryData.assayVersion)
		assert assaySummary.ddValue[2].text().equalsIgnoreCase(summaryData.shortName)
		assert assaySummary.ddValue[3].text().equalsIgnoreCase(summaryData.assayName)
		assert assaySummary.ddValue[4].text().equalsIgnoreCase(summaryData.designedBy)
		assert assaySummary.ddValue[5].text().equalsIgnoreCase(summaryData.assayStatus)
		assert assaySummary.ddValue[6].text().equalsIgnoreCase(summaryData.assayType)
		
		and: "Update assay summary info"
		editSummay(summaryName+updateStr, assayStatus, designedBy)
		
		when: "Assay summary is updated, Fetch values from database"
		summaryUpdatedData = assays.getAssaySummaryById(testData.assayId)

		then: "Validate updated summary info with database "
		waitFor { assaySummary.ddValue[3].text() == summaryName+updateStr}
		assert assaySummary.ddValue[3].text() == summaryName+updateStr
		assert assaySummary.ddValue[4].text() == designedBy
		assert assaySummary.ddValue[5].text() == assayStatus
		assert assaySummary.ddValue[3].text().equalsIgnoreCase(summaryUpdatedData.assayName)
		assert assaySummary.ddValue[4].text().equalsIgnoreCase(summaryUpdatedData.designedBy)
		assert assaySummary.ddValue[5].text().equalsIgnoreCase(summaryUpdatedData.assayStatus)
		report "UpdateAssaySummary"

		when: "Rolling back changes in assay summary info"
		editSummay(summaryName, summaryStatus, summaryDesignedBy)
		summaryOriginaldData = assays.getAssaySummaryById(testData.assayId)

		then: "Validate that summary info changes are rolled back "
		waitFor { assaySummary.ddValue[3].text() == summaryName}
		assert assaySummary.ddValue[3].text() == summaryName
		assert assaySummary.ddValue[4].text() == summaryDesignedBy
		assert assaySummary.ddValue[5].text() == summaryStatus
		assert assaySummary.ddValue[3].text().equalsIgnoreCase(summaryOriginaldData.assayName)
		assert assaySummary.ddValue[4].text().equalsIgnoreCase(summaryOriginaldData.designedBy)
		assert assaySummary.ddValue[5].text().equalsIgnoreCase(summaryOriginaldData.assayStatus)

		and: "User is at View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Add Assay Context Card"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def dbContexts = []
		def uiContexts = []
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()

		then: "Add New Context Card"
		addNewContextCard(card1)
		waitFor(10, 2) { isCardPresent(card1) }

		when: "Card is added, Fetching card info from database and UI"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()

		then: "Validating card info with UI and database"
		assert isCardPresent(card1)
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		report "Card1Add"

		when: "Card is added and validated"
		at EditAssayContextPage
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage

		when: "Finished editing, Fetch values to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()

		then: "Validating card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
	}

	def "Test Empty Card Dropdown Menu"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage

		then: "Verifing empty card drop down menu"
		verifyEmptyCardsMenu("$card1")
		report "EmptyCardMenu"

		when: "Empty Card menu is validated"
		at EditAssayContextPage
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}

	def "Test Add Assay Context Item"() {
		editAssayContext()

		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def contextItemData = []
		def uiContextItems = []
		contextItemData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		uiContextItems = contextCardItems(card1)
	
		then: "Add context Item to card"
		assert uiContextItems == contextItemData
		cardHolders.cardMenu(card1).click()
		cardHolders.cardDDMenu[2].click()
		defineAttributes(arrtibuteSearch)
		defineValueType(valueType)
		defineValue(arrtibuteSearch, valueTypeQualifier, unitValue, unitSelect)
		reviewAndSave()
		waitFor(20, 3) { isContextItem(card1) }
		assert isContextItem(card1)

		when: "Context item is added, Fetch context info to validate"
		contextItemData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		uiContextItems = contextCardItems(card1)

		then: "Validate context item data with UI and database"
		assert uiContextItems == contextItemData
		report "AddContextItem"

		when: "Context item is added and validated"
		at EditAssayContextPage
		finishEditingBtn.click()

		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}


	def "Test Move Assay Context Item"() {
		editAssayContext()
		
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def card1ItemsData = []
		def card2ItemsData = []
		def uiCardItems = []
		def uiCard2Items = []
		
		then: "Add Context Card 2 for Item Move"
		addNewContextCard(card2)
		waitFor(20, 3) { isCardPresent(card2) }
		assert isCardPresent(card2)
		
		when: "Card2 is added, Fetch cards info to validate"
		card1ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		card2ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card2)
		uiCardItems = contextCardItems(card1)
		uiCard2Items = contextCardItems(card2)
		
		then: "Validate context cards info before moving an item"
		assert uiCardItems == card1ItemsData
		assert uiCard2Items == card2ItemsData
		
		and: "Move context item from one card to another card"
		cardHolders.cardItemMenu(card1).click()
		cardHolders.cardItemMenuDD[1].click()
		waitFor(20, 3){ moveAssayCardItem.selectCardId }
		moveAssayCardItem.newCardHolder(card2)
		moveAssayCardItem.moveBtn.click()
		report "MoveContextItem"
		waitFor(20, 3) { isContextItem(card2) }
		waitFor(20, 3) { !isContextItem(card1) }
		assert !isContextItem(card1)
		assert isContextItem(card2)
		
		when: "Context item is moved to other card, Fetch cards info to validate"
		card1ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		card2ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card2)
		uiCardItems = contextCardItems(card1)
		uiCard2Items = contextCardItems(card2)
		
		then: "Context item is moved, Validate context cards info"
		assert uiCardItems == card1ItemsData
		assert uiCard2Items == card2ItemsData
		
		when: "Context card are moved and validated"
		at EditAssayContextPage
		finishEditingBtn.click()
		
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}
	
	def "Test Delete Assay Context Item"() {
		editAssayContext()
		
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def card1ItemsData = []
		def card2ItemsData = []
		def uiCardItems = []
		def uiCard2Items = []
		card1ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		card2ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card2)
		uiCardItems = contextCardItems(card1)
		uiCard2Items = contextCardItems(card2)
		
		then: "Validate cards info before deleting context item"
		assert uiCardItems == card1ItemsData
		assert uiCard2Items == card2ItemsData
		
		and: "Delete context item from card"
		deleContextItem(card2)
		report "DeleteContextItem"
		waitFor(15, 3) { !isContextItem(card2) }
		assert !isContextItem(card2)
		waitFor(10) { at EditAssayContextPage }
		
		when: "Context item is deleted, Fetch cards info to validate"
		card1ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card1)
		card2ItemsData = assays.getAssayContextItem(testData.assayId, cGroup, card2)
		uiCardItems = contextCardItems(card1)
		uiCard2Items = contextCardItems(card2)
		
		then: "Context item is deleted, validate careds info"
		assert uiCardItems == card1ItemsData
		assert uiCard2Items == card2ItemsData
		
		when:"Context items are deleted and validated"
		finishEditingBtn.click()
		
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
	}
	
	def "Test Edit Assay Context Card"() {
		editAssayContext()
		
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def dbContexts = []
		def uiContexts = []
		def cGroup = "assay protocol> assay component>"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		
		then: "Editing Assay Context Card"
		cardHolders.cardMenu(card1).click()
		cardHolders.cardDDMenu[1].click()
		assert editAssayCards.titleBar.text() ==~ "Edit Card Name"
		editAssayCards.enterCardName.value("")
		editAssayCards.enterCardName << "$cardUpdated"
		editAssayCards.saveBtn.click()
		report "EditContextCard"
		waitFor(25, 2){ isCardPresent(cardUpdated) }
		
		when: "Context card is updated, Fetch updated card info to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()
		
		then: "Context card is updated, validate updated card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		
		when: "Context card is validate with UI and database"
		finishEditingBtn.click()
		
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
		
		when: "Finished editing, Fetch values to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()
		
		then: "Validating card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
	}
	
	def "Test Delete Assay Card1"() {
		editAssayContext()
		
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def dbContexts = []
		def uiContexts = []
		def cGroup = "assay protocol> assay component>"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		
		then: "Delet Assay Context Card1"
		deletAssayCard(cardUpdated)
		waitFor(20, 3) { !isCardPresent(cardUpdated) }
		assert !isCardPresent(cardUpdated)
		
		when: "Card1 is deleted, Fetch cards info to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()
		
		then: "Card1 is deleted, validate card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		report "DeleteContextCard1"
		
		when: "Card1 is deleted and validated"
		at EditAssayContextPage
		finishEditingBtn.click()
		
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
		
		when: "Finished editing, Fetch values to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()
		
		then: "Validate card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
	}

	def "Test Delete Assay Card2"() {
		editAssayContext()
		
		when: "User is at Edit Assay Definition Page"
		at EditAssayContextPage
		def dbContexts = []
		def uiContexts = []
		def cGroup = "assay protocol> assay component>"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		
		then: "Delet Assay Context Card2"
		deletAssayCard(card2)
		waitFor(20, 3) { !isCardPresent(card2) }
		assert !isCardPresent(card2)
		
		when: "Card2 is deleted, Fetch cards info to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()
		
		then: "Card2 is deleted, validate card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
		report "DeleteContextCard2"
		
		when: "Card1 is deleted and validated"
		at EditAssayContextPage
		finishEditingBtn.click()
		
		then: "Navigating to View Assay Page"
		at ViewAssayDefinitionPage
		
		when: "Finished editing, Fetch values to validate"
		dbContexts = assays.getAssayContext(testData.assayId, cGroup)
		uiContexts = UIContexts()
		
		then: "Validate card info with UI and database"
		assert uiContexts.size() == dbContexts.size()
		assert uiContexts.sort() == dbContexts.sort()
	}

	void cleanupSpec() {
	}
}