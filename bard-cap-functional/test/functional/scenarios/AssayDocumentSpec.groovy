package scenarios
import pages.CapSearchPage
import pages.DocumentPage
import pages.HomePage
import pages.ViewAssayDefinitionPage
import base.BardFunctionalSpec

import common.Constants
import common.TestDataReader
import common.Constants.DocumentAs
import common.Constants.NavigateTo
import common.Constants.SearchBy

import db.Assay
//Updated
//@Stepwise
class AssayDocumentSpec extends BardFunctionalSpec {

	def testData = TestDataReader.getTestData()
	

	def setup() {
		logInSomeUser()

		when: "User is at Home page, Navigating to Search Assay By Id page"
		at HomePage
		navigateTo(NavigateTo.ASSAY_BY_ID)

		then: "User is at Search Assay by Id page"
		at CapSearchPage

		when: "User is trying to search some Assay"
		at CapSearchPage
		capSearchBy(SearchBy.ASSAY_ID, testData.AssayId)

		then: "User is at View Assay Definition page"
		at ViewAssayDefinitionPage
	}

	def "Test Assay Document Add of type Description"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS,testData.documentDescriptionName, testData.documentDescriptionContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddDescription"
	}

	def "Test Assay Document Add of type Protocol"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS, testData.documentProtocolName, testData.documentProtocolContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddProtocol"
	}

	def "Test Assay Document Add of type Comments"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS, testData.documentCommentsName, testData.documentCommentsContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddComments"
	}

	def "Test Assay Document Add of type Publications"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.URL, testData.documentPublicationName, testData.documentPublicationContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddPublications"
	}

	def "Test Assay Document Add of type External URLS"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.URL, testData.documentURLName, testData.documentURLContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddExternalURL"
	}

	def "Test Assay Document Add of type Others"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS,testData.documentOtherName, testData.documentOtherContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddOther"
	}

	def "Test Assay Document Add of type Description with empty values"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS,"", testData.documentDescriptionContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddDescriptionEmpty"
	}

	def "Test Assay Document Add of type Protocol with empty value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS, "", testData.documentProtocolContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddProtocolEmpty"
	}

	def "Test Assay Document Add of type Comments with empty value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS, "", testData.documentCommentsContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddCommentsEmpty"
	}

	def "Test Assay Document Add of type Publications with empty value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.URL, "", testData.documentPublicationContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddPublicationEmpty"
	}

	def "Test Assay Document Add of type External URLS with empty value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.URL, "", testData.documentURLContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddExternalURLEmpty"
	}

	def "Test Assay Document Add of type Others with empty value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS,"", testData.documentOtherContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddOtherEmpty"
	}

	def "Test Assay Document Edit of type Description"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentDescriptionName, testData.documentDescriptionContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)
			editDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName, testData.documentDescriptionName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName, testData.documentDescriptionName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditDescription"
	}

	def "Test Assay Document Edit of type Protocol"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentProtocolName, testData.documentProtocolContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)
			editDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName, testData.documentProtocolName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName, testData.documentProtocolName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditProtocol"
	}

	def "Test Assay Document Edit of type Comments"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentCommentsName, testData.documentCommentsContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)
			editDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName, testData.documentCommentsName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName, testData.documentCommentsName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditComments"
	}

	def "Test Assay Document Edit of type Publications"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, testData.documentPublicationName, testData.documentPublicationContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)
			editDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName, testData.documentPublicationName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName, testData.documentPublicationName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditPublications"
	}

	def "Test Assay Document Edit of type External URLS"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, testData.documentURLName, testData.documentURLContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)
			editDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName, testData.documentURLName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName, testData.documentURLName+Constants.edited)
		}
		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditExternalURL"
	}

	def "Test Assay Document Edit of type Others"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentOtherName, testData.documentOtherContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)
			editDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName, testData.documentOtherName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName, testData.documentOtherName+Constants.edited)
		}
		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditOther"
	}

	def "Test Assay Document Edit of type Description with empty name value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentDescriptionName, testData.documentDescriptionContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)
			editDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName, "")
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditDescriptionEmpty"
	}

	def "Test Assay Document Edit of type Protocol with empty name value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentProtocolName, testData.documentProtocolContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)
			editDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName, "")
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditProtocolEmpty"
	}

	def "Test Assay Document Edit of type Comments with empty name value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentCommentsName, testData.documentCommentsContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)
			editDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName, "")
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditCommentsEmpty"
	}

	def "Test Assay Document Edit of type Publications with empty name value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, testData.documentPublicationName, testData.documentPublicationContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)
			editDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName, "")
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditPublicationsEmpty"
	}

	def "Test Assay Document Edit of type External URLS with empty name value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, testData.documentURLName, testData.documentURLContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)
			editDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName, "")
		}
		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditExternalURLEmpty"
	}

	def "Test Assay Document Edit of type Others with empty name value"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentOtherName, testData.documentOtherContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)
			editDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName, "")
		}
		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditOtherEmpty"
	}

	def "Test Assay Document Delete of type Description"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentDescriptionName, testData.documentDescriptionContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)
			deleteDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName+Constants.edited)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Description)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		report "AssayDocumentDeleteDecription"
	}

	def "Test Assay Document Delete of type Protocol"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentProtocolName, testData.documentProtocolContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName+Constants.edited)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Protocol)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		report "AssayDocumentDeleteProtocol"
	}

	def "Test Assay Document Delete of type Comments"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentCommentsName, testData.documentCommentsContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName+Constants.edited)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Comment)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		report "AssayDocumentDeleteComments"
	}

	def "Test Assay Document Delete of type Publications"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, testData.documentPublicationName, testData.documentPublicationContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName+Constants.edited)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Publication)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		report "AssayDocumentDeletePublications"
	}

	def "Test Assay Document Delete of type External URLS"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, testData.documentURLName, testData.documentURLContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)
		}
		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls+Constants.edited))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Urls+Constants.edited)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		report "AssayDocumentDeleteExternalURL"
	}

	def "Test Assay Document Delete of type Others"(){
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,testData.documentOtherName, testData.documentOtherContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)
			deleteDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)
		}
		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other+Constants.edited))
		def dbDocumentsAfter= Assay.getAssayDocuments(testData.AssayId, Constants.documentType.Other+Constants.edited)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		report "AssayDocumentDeleteOther"
	}

}