package scenarios
import pages.CapSearchPage
import pages.DocumentPage
import pages.HomePage
import pages.ViewAssayDefinitionPage
import spock.lang.Ignore;
import base.BardFunctionalSpec
import common.Constants
import common.TestData;
import common.TestDataReader
import common.Constants.DocumentAs
import common.Constants.NavigateTo
import common.Constants.SearchBy
import db.Assay
/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 * Last Updated: 13/10/07
 */
@Ignore
class AssayDocumentSpec extends BardFunctionalSpec {

//	def TestData = TestDataReader.getTestData()
	def setup() {
		logInSomeUser()

//		when: "User is at Home page, Navigating to Search Assay By Id page"
//		at HomePage
//		navigateTo(NavigateTo.ASSAY_BY_ID)
//
//		then: "User is at Search Assay by Id page"
//		at CapSearchPage
//
//		when: "User is trying to search some Assay"
//		at CapSearchPage
//		capSearchBy(SearchBy.ASSAY_ID, TestData.assayId)
//
//		then: "User is at View Assay Definition page"
//		at ViewAssayDefinitionPage
	}

	def "Test Assay Document Add of type Description"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS,TestData.documentDescriptionName, TestData.documentDescriptionContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddDescription"
	}

	def "Test Assay Document Add of type Protocol"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS, TestData.documentProtocolName, TestData.documentProtocolContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddProtocol"
	}

	def "Test Assay Document Add of type Comments"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS, TestData.documentCommentsName, TestData.documentCommentsContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddComments"
	}

	def "Test Assay Document Add of type Publications"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.URL, TestData.documentPublicationName, TestData.documentPublicationContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddPublications"
	}

	def "Test Assay Document Add of type External URLS"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.URL, TestData.documentURLName, TestData.documentURLContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddExternalURL"
	}

	def "Test Assay Document Add of type Others"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS,TestData.documentOtherName, TestData.documentOtherContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddOther"
	}

	def "Test Assay Document Add of type Description with empty values"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS,"", TestData.documentDescriptionContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddDescriptionEmpty"
	}

	def "Test Assay Document Add of type Protocol with empty value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS, "", TestData.documentProtocolContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddProtocolEmpty"
	}

	def "Test Assay Document Add of type Comments with empty value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS, "", TestData.documentCommentsContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddCommentsEmpty"
	}

	def "Test Assay Document Add of type Publications with empty value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.URL, "", TestData.documentPublicationContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddPublicationEmpty"
	}

	def "Test Assay Document Add of type External URLS with empty value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.URL, "", TestData.documentURLContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddExternalURLEmpty"
	}

	def "Test Assay Document Add of type Others with empty value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS,"", TestData.documentOtherContent)

		then:"At View Assay Page, Verify that Document is added"
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "AssayDocumentAddOtherEmpty"
	}

	def "Test Assay Document Edit of type Description"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentDescriptionName, TestData.documentDescriptionContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)
			editDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName, TestData.documentDescriptionName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName, TestData.documentDescriptionName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		to ViewAssayDefinitionPage
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditDescription"
	}

	def "Test Assay Document Edit of type Protocol"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentProtocolName, TestData.documentProtocolContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)
			editDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName, TestData.documentProtocolName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName, TestData.documentProtocolName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		to ViewAssayDefinitionPage
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditProtocol"
	}

	def "Test Assay Document Edit of type Comments"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentCommentsName, TestData.documentCommentsContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)
			editDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName, TestData.documentCommentsName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName, TestData.documentCommentsName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		to ViewAssayDefinitionPage
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditComments"
	}

	def "Test Assay Document Edit of type Publications"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, TestData.documentPublicationName, TestData.documentPublicationContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)
			editDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName, TestData.documentPublicationName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName, TestData.documentPublicationName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		to ViewAssayDefinitionPage
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditPublications"
	}

	def "Test Assay Document Edit of type External URLS"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, TestData.documentURLName, TestData.documentURLContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)
			editDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName, TestData.documentURLName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName, TestData.documentURLName+Constants.edited)
		}
		and:"Fetching Document Info from UI and DB for validation"
		to ViewAssayDefinitionPage
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditExternalURL"
	}

	def "Test Assay Document Edit of type Others"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentOtherName, TestData.documentOtherContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)
			editDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName, TestData.documentOtherName+Constants.edited)
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName, TestData.documentOtherName+Constants.edited)
		}
		
		and:"Fetching Document Info from UI and DB for validation"
		to ViewAssayDefinitionPage
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName+Constants.edited)){
			deleteDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditOther"
	}

	def "Test Assay Document Edit of type Description with empty name value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentDescriptionName, TestData.documentDescriptionContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)
			editDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName, "")
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditDescriptionEmpty"
	}

	def "Test Assay Document Edit of type Protocol with empty name value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentProtocolName, TestData.documentProtocolContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)
			editDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName, "")
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditProtocolEmpty"
	}

	def "Test Assay Document Edit of type Comments with empty name value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentCommentsName, TestData.documentCommentsContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)
			editDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName, "")
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditCommentsEmpty"
	}

	def "Test Assay Document Edit of type Publications with empty name value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, TestData.documentPublicationName, TestData.documentPublicationContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)
			editDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName, "")
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditPublicationsEmpty"
	}

	def "Test Assay Document Edit of type External URLS with empty name value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, TestData.documentURLName, TestData.documentURLContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)
			editDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName, "")
		}
		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditExternalURLEmpty"
	}

	def "Test Assay Document Edit of type Others with empty name value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentOtherName, TestData.documentOtherContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)
			editDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName, "")
		}else{
			editDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName, "")
		}
		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)){
			deleteDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
			def dbDocumentsAfterDelete = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "AssayDocumentEditOtherEmpty"
	}

	def "Test Assay Document Delete of type Description"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentDescriptionName, TestData.documentDescriptionContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName)
			deleteDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName+Constants.edited)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Description), TestData.documentDescriptionName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Description)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		report "AssayDocumentDeleteDecription"
	}

	def "Test Assay Document Delete of type Protocol"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentProtocolName, TestData.documentProtocolContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName)
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName+Constants.edited)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Protocol), TestData.documentProtocolName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Protocol)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		report "AssayDocumentDeleteProtocol"
	}

	def "Test Assay Document Delete of type Comments"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentCommentsName, TestData.documentCommentsContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName)
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName+Constants.edited)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Comment), TestData.documentCommentsName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Comment)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		report "AssayDocumentDeleteComments"
	}

	def "Test Assay Document Delete of type Publications"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, TestData.documentPublicationName, TestData.documentPublicationContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName)
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName+Constants.edited)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Publication), TestData.documentPublicationName+Constants.edited)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Publication)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		report "AssayDocumentDeletePublications"
	}

	def "Test Assay Document Delete of type External URLS"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.URL, TestData.documentURLName, TestData.documentURLContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Urls), TestData.documentURLName)
		}
		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls+Constants.edited))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Urls+Constants.edited)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		report "AssayDocumentDeleteExternalURL"
	}

	def "Test Assay Document Delete of type Others"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)){
			navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			createDocument(DocumentAs.CONTENTS,TestData.documentOtherName, TestData.documentOtherContent)

			and:"Navigating to View Assay Page"
			at ViewAssayDefinitionPage
			assert isDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)
			deleteDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)
		}else{
			deleteDocument(documentHeaders(Constants.documentHeader.Other), TestData.documentOtherName)
		}
		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other+Constants.edited))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, Constants.documentType.Other+Constants.edited)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		report "AssayDocumentDeleteOther"
	}

}