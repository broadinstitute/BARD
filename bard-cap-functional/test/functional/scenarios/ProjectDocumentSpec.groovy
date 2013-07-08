package scenarios

import pages.CapSearchPage
import pages.DocumentPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import base.BardFunctionalSpec

import common.Constants
import common.TestDataReader
import common.Constants.DocumentAs
import common.Constants.NavigateTo
import common.Constants.SearchBy

import db.Project

//@Stepwise
class ProjectDocumentSpec extends BardFunctionalSpec {

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

	def "Test Project Document Add of type Description"(){
		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsBefore = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Description)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Description))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS,testData.documentDescriptionName, testData.documentDescriptionContent)

		then:"At View Project Page, Verify that Document is added"
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Description), testData.documentDescriptionName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Description))
		def dbDocumentsAfter= Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Description)

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
			def dbDocumentsAfterDelete = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Description)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
	}

	def "Test Project Document Add of type Protocol"(){
		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsBefore = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Protocol)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Protocol))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS, testData.documentProtocolName, testData.documentProtocolContent)

		then:"At View Project Page, Verify that Document is added"
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Protocol), testData.documentProtocolName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Protocol))
		def dbDocumentsAfter= Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Protocol)

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
			def dbDocumentsAfterDelete = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Protocol)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
	}

	def "Test Project Document Add of type Comments"(){
		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsBefore = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Comment)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Comment))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS, testData.documentCommentsName, testData.documentCommentsContent)

		then:"At View Project Page, Verify that Document is added"
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Comment), testData.documentCommentsName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Comment))
		def dbDocumentsAfter= Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Comment)

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
			def dbDocumentsAfterDelete = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Comment)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
	}

	def "Test Project Document Add of type Publications"(){
		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsBefore = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Publication)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Publication))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.URL, testData.documentPublicationName, testData.documentPublicationContent)

		then:"At View Project Page, Verify that Document is added"
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Publication), testData.documentPublicationName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Publication))
		def dbDocumentsAfter= Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Publication)

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
			def dbDocumentsAfterDelete = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Publication)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
	}

	def "Test Project Document Add of type External URLS"(){
		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsBefore = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Urls)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Urls))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.URL, testData.documentURLName, testData.documentURLContent)

		then:"At View Project Page, Verify that Document is added"
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Urls), testData.documentURLName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Urls))
		def dbDocumentsAfter= Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Urls)

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
			def dbDocumentsAfterDelete = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Urls)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
	}

	def "Test Project Document Add of type Others"(){
		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsBefore = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Other)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Nagating to Create Document Page"
		navigateToCreateDocument(documentHeaders(Constants.documentHeader.Other))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		createDocument(DocumentAs.CONTENTS,testData.documentOtherName, testData.documentOtherContent)

		then:"At View Project Page, Verify that Document is added"
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(Constants.documentHeader.Other), testData.documentOtherName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(Constants.documentHeader.Other))
		def dbDocumentsAfter= Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Other)

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
			def dbDocumentsAfterDelete = Project.getProjectDocuments(testData.ProjectID, Constants.documentType.Other)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
	}
	
	/*	
	 def "Test Project Document Edit of type Description"(){
	 when:"At View Project Page, Fetching Document Info from UI and DB for validation"
	 at ViewProjectDefinitionPage
	 //		String dococumentType = projectDocumentType.description
	 //		String documentSection = documentSection(dococumentType)
	 //		def uiDocumetnsBefore = getUIDucuments(documentSection)
	 //		def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 def docName = "description"
	 def name = "Description Test Document"
	 def nameEdit = "Description Test Document Edit"
	 def content = "Description Test Document - Descripiton"
	 then: "Nagating to Create Document Page"
	 //		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 editDocument(documentSections(docName), editableForm, name, nameEdit)
	 //		when:"At Create Document Page"
	 //		at DocumentPage
	 //		
	 //		createDocument(name, content)
	 ////		addEditDocument(Document.ADD, dococumentType, descriptionName, descriptionContent)
	 //		
	 and:"At View Project Page, Fetching Document Info from UI and DB for validation"
	 at ViewProjectDefinitionPage
	 //		when: "Document is added, Fetch document info to validate"
	 //		at ViewProjectDefinitionPage
	 //		def uiDocumetnsAfter = getUIDucuments(documentSection)
	 //		def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 //
	 //		then:"Validate UI document info with Database"
	 //		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
	 //		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Project Document Delete of type Description"(){
	 when:"At View Project Page, Fetching Document Info from UI and DB for validation"
	 at ViewProjectDefinitionPage
	 //		String dococumentType = projectDocumentType.description
	 //		String documentSection = documentSection(dococumentType)
	 def docName = "description"
	 def uiDocumetns = getUIDucuments(documentHeaders(docName))
	 println uiDocumetns
	 //		def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 def name = "Description Test Document Edit"
	 def nameEdit = "Description Test Document Edit"
	 def content = "Description Test Document - Descripiton"
	 then: "Nagating to Create Document Page"
	 //		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 deleteDocument(documentHeaders(docName), name)
	 //		when:"At Create Document Page"
	 //		at DocumentPage
	 //
	 //		createDocument(name, content)
	 ////		addEditDocument(Document.ADD, dococumentType, descriptionName, descriptionContent)
	 //
	 and:"At View Project Page, Fetching Document Info from UI and DB for validation"
	 at ViewProjectDefinitionPage
	 //		when: "Document is added, Fetch document info to validate"
	 //		at ViewProjectDefinitionPage
	 //		def uiDocumetnsAfter = getUIDucuments(documentSection)
	 //		def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 //
	 //		then:"Validate UI document info with Database"
	 //		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
	 //		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 */
	/*
	 def "Test Create New Document of type Protocol in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.protocol
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.ADD)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.ADD, dococumentType, protocolName, protocolContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Create New Document of type Comments in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.comments
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.ADD)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.ADD, dococumentType, commentsName, commentsContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Create New Document of type Publication in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.publication
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.ADD)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.ADD, dococumentType, publicationName, publicationContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Create New Document of type External URL in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.externalURL
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.ADD)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.ADD, dococumentType, externalURLName, externalURLContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Create New Document of type Other in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.other
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.ADD)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.ADD, dococumentType, otherName, otherContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Edit Decription Type Document to Protocol type Document in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String fromDocumentType = projectDocumentType.description
	 String toDocumentType = projectDocumentType.protocol
	 String documentSectionFrom = documentSection(fromDocumentType)
	 String documentSectionTo = documentSection(toDocumentType)
	 def uiDocumetnsFromSection = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSection = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSection = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSection = projects.getProjectDocuments(projectID, toDocumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsFromSection.size() == dbDocumentsFromSection.size()
	 assert uiDocumetnsFromSection.sort() == dbDocumentsFromSection.sort()
	 assert uiDocumetnsToSection.size() == dbDocumentsToSection.size()
	 assert uiDocumetnsToSection.sort() == dbDocumentsToSection.sort()
	 navigateToAddEditDeleteDocument(Document.EDIT, documentSectionFrom, descriptionName)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.EDIT, toDocumentType, protocolName, protocolContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsFromSectionAfter = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSectionAfter = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSectionAfter = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSectionAfter = projects.getProjectDocuments(projectID, toDocumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsFromSectionAfter.size() <  uiDocumetnsFromSection.size()
	 assert uiDocumetnsToSectionAfter.size() >  uiDocumetnsToSection.size()
	 assert uiDocumetnsFromSectionAfter.size() == dbDocumentsFromSectionAfter.size()
	 assert uiDocumetnsFromSectionAfter.sort() == dbDocumentsFromSectionAfter.sort()
	 assert uiDocumetnsToSectionAfter.size() == dbDocumentsToSectionAfter.size()
	 assert uiDocumetnsToSectionAfter.sort() == dbDocumentsToSectionAfter.sort()
	 }
	 def "Test Edit Protocol Type Document to Comments type Document in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String fromDocumentType = projectDocumentType.protocol
	 String toDocumentType = projectDocumentType.comments
	 String documentSectionFrom = documentSection(fromDocumentType)
	 String documentSectionTo = documentSection(toDocumentType)
	 def uiDocumetnsFromSection = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSection = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSection = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSection = projects.getProjectDocuments(projectID, toDocumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsFromSection.size() == dbDocumentsFromSection.size()
	 assert uiDocumetnsFromSection.sort() == dbDocumentsFromSection.sort()
	 assert uiDocumetnsToSection.size() == dbDocumentsToSection.size()
	 assert uiDocumetnsToSection.sort() == dbDocumentsToSection.sort()
	 navigateToAddEditDeleteDocument(Document.EDIT, documentSectionFrom, protocolName)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.EDIT, toDocumentType, commentsName, commentsContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsFromSectionAfter = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSectionAfter = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSectionAfter = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSectionAfter = projects.getProjectDocuments(projectID, toDocumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsFromSectionAfter.size() <  uiDocumetnsFromSection.size()
	 assert uiDocumetnsToSectionAfter.size() >  uiDocumetnsToSection.size()
	 assert uiDocumetnsFromSectionAfter.size() == dbDocumentsFromSectionAfter.size()
	 assert uiDocumetnsFromSectionAfter.sort() == dbDocumentsFromSectionAfter.sort()
	 assert uiDocumetnsToSectionAfter.size() == dbDocumentsToSectionAfter.size()
	 assert uiDocumetnsToSectionAfter.sort() == dbDocumentsToSectionAfter.sort()
	 }
	 def "Test Edit Comments Type Document to Publication type Document in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String fromDocumentType = projectDocumentType.comments
	 String toDocumentType = projectDocumentType.publication
	 String documentSectionFrom = documentSection(fromDocumentType)
	 String documentSectionTo = documentSection(toDocumentType)
	 def uiDocumetnsFromSection = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSection = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSection = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSection = projects.getProjectDocuments(projectID, toDocumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsFromSection.size() == dbDocumentsFromSection.size()
	 assert uiDocumetnsFromSection.sort() == dbDocumentsFromSection.sort()
	 assert uiDocumetnsToSection.size() == dbDocumentsToSection.size()
	 assert uiDocumetnsToSection.sort() == dbDocumentsToSection.sort()
	 navigateToAddEditDeleteDocument(Document.EDIT, documentSectionFrom, commentsName)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.EDIT, toDocumentType, publicationName, publicationContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsFromSectionAfter = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSectionAfter = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSectionAfter = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSectionAfter = projects.getProjectDocuments(projectID, toDocumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsFromSectionAfter.size() <  uiDocumetnsFromSection.size()
	 assert uiDocumetnsToSectionAfter.size() >  uiDocumetnsToSection.size()
	 assert uiDocumetnsFromSectionAfter.size() == dbDocumentsFromSectionAfter.size()
	 assert uiDocumetnsFromSectionAfter.sort() == dbDocumentsFromSectionAfter.sort()
	 assert uiDocumetnsToSectionAfter.size() == dbDocumentsToSectionAfter.size()
	 assert uiDocumetnsToSectionAfter.sort() == dbDocumentsToSectionAfter.sort()
	 }
	 def "Test Edit Publication Type Document to External URL type Document in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String fromDocumentType = projectDocumentType.publication
	 String toDocumentType = projectDocumentType.externalURL
	 String documentSectionFrom = documentSection(fromDocumentType)
	 String documentSectionTo = documentSection(toDocumentType)
	 def uiDocumetnsFromSection = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSection = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSection = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSection = projects.getProjectDocuments(projectID, toDocumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsFromSection.size() == dbDocumentsFromSection.size()
	 assert uiDocumetnsFromSection.sort() == dbDocumentsFromSection.sort()
	 assert uiDocumetnsToSection.size() == dbDocumentsToSection.size()
	 assert uiDocumetnsToSection.sort() == dbDocumentsToSection.sort()
	 navigateToAddEditDeleteDocument(Document.EDIT, documentSectionFrom, publicationName)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.EDIT, toDocumentType, externalURLName, externalURLContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsFromSectionAfter = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSectionAfter = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSectionAfter = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSectionAfter = projects.getProjectDocuments(projectID, toDocumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsFromSectionAfter.size() <  uiDocumetnsFromSection.size()
	 assert uiDocumetnsToSectionAfter.size() >  uiDocumetnsToSection.size()
	 assert uiDocumetnsFromSectionAfter.size() == dbDocumentsFromSectionAfter.size()
	 assert uiDocumetnsFromSectionAfter.sort() == dbDocumentsFromSectionAfter.sort()
	 assert uiDocumetnsToSectionAfter.size() == dbDocumentsToSectionAfter.size()
	 assert uiDocumetnsToSectionAfter.sort() == dbDocumentsToSectionAfter.sort()
	 }
	 def "Test Edit External URL Type Document to Other type Document in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String fromDocumentType = projectDocumentType.externalURL
	 String toDocumentType = projectDocumentType.other
	 String documentSectionFrom = documentSection(fromDocumentType)
	 String documentSectionTo = documentSection(toDocumentType)
	 def uiDocumetnsFromSection = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSection = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSection = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSection = projects.getProjectDocuments(projectID, toDocumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsFromSection.size() == dbDocumentsFromSection.size()
	 assert uiDocumetnsFromSection.sort() == dbDocumentsFromSection.sort()
	 assert uiDocumetnsToSection.size() == dbDocumentsToSection.size()
	 assert uiDocumetnsToSection.sort() == dbDocumentsToSection.sort()
	 navigateToAddEditDeleteDocument(Document.EDIT, documentSectionFrom, externalURLName)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.EDIT, toDocumentType, otherName, otherContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsFromSectionAfter = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSectionAfter = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSectionAfter = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSectionAfter = projects.getProjectDocuments(projectID, toDocumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsFromSectionAfter.size() <  uiDocumetnsFromSection.size()
	 assert uiDocumetnsToSectionAfter.size() >  uiDocumetnsToSection.size()
	 assert uiDocumetnsFromSectionAfter.size() == dbDocumentsFromSectionAfter.size()
	 assert uiDocumetnsFromSectionAfter.sort() == dbDocumentsFromSectionAfter.sort()
	 assert uiDocumetnsToSectionAfter.size() == dbDocumentsToSectionAfter.size()
	 assert uiDocumetnsToSectionAfter.sort() == dbDocumentsToSectionAfter.sort()
	 }
	 def "Test Edit Other Type Document to Description type Document in a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String fromDocumentType = projectDocumentType.other
	 String toDocumentType = projectDocumentType.description
	 String documentSectionFrom = documentSection(fromDocumentType)
	 String documentSectionTo = documentSection(toDocumentType)
	 def uiDocumetnsFromSection = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSection = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSection = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSection = projects.getProjectDocuments(projectID, toDocumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsFromSection.size() == dbDocumentsFromSection.size()
	 assert uiDocumetnsFromSection.sort() == dbDocumentsFromSection.sort()
	 assert uiDocumetnsToSection.size() == dbDocumentsToSection.size()
	 assert uiDocumetnsToSection.sort() == dbDocumentsToSection.sort()
	 navigateToAddEditDeleteDocument(Document.EDIT, documentSectionFrom, otherName)
	 when:"User is at add/edit documentpage"
	 at DocumentPage
	 addEditDocument(Document.EDIT, toDocumentType, descriptionName, descriptionContent)
	 then:"User is navigated back to view project page"
	 at ViewProjectDefinitionPage
	 when: "Document is added, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsFromSectionAfter = getUIDucuments(documentSectionFrom)
	 def dbDocumentsFromSectionAfter = projects.getProjectDocuments(projectID, fromDocumentType)
	 def uiDocumetnsToSectionAfter = getUIDucuments(documentSectionTo)
	 def dbDocumentsToSectionAfter = projects.getProjectDocuments(projectID, toDocumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsFromSectionAfter.size() <  uiDocumetnsFromSection.size()
	 assert uiDocumetnsToSectionAfter.size() >  uiDocumetnsToSection.size()
	 assert uiDocumetnsFromSectionAfter.size() == dbDocumentsFromSectionAfter.size()
	 assert uiDocumetnsFromSectionAfter.sort() == dbDocumentsFromSectionAfter.sort()
	 assert uiDocumetnsToSectionAfter.size() == dbDocumentsToSectionAfter.size()
	 assert uiDocumetnsToSectionAfter.sort() == dbDocumentsToSectionAfter.sort()
	 }
	 def "Test Delete Description type Document from a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.description
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.DELETE, documentSection, descriptionName)
	 when:"Document is deleted, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() < uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Delete Protocol type Document from a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.protocol
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.DELETE, documentSection, protocolName)
	 when:"Document is deleted, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() < uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Delete Comments type Document from a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.comments
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.DELETE, documentSection, commentsName)
	 when:"Document is deleted, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() < uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Delete Publication type Document from a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.publication
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.DELETE, documentSection, publicationName)
	 when:"Document is deleted, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() < uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Delete External URL type Document from a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.externalURL
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.DELETE, documentSection, externalURLName)
	 when:"Document is deleted, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() < uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 def "Test Delete Other type Document from a project"(){
	 when: "User is at Home page, Navigate to Find Project By Id page"
	 at HomePage
	 navigateToFindProjectById()
	 then: "Search the project By Id"
	 at FindProjectByIdPage
	 searchProject(projectID)
	 when:"User is at view project detail page"
	 at ViewProjectDefinitionPage
	 String dococumentType = projectDocumentType.other
	 String documentSection = documentSection(dococumentType)
	 def uiDocumetnsBefore = getUIDucuments(documentSection)
	 def dbDocumentsBefore = projects.getProjectDocuments(projectID, dococumentType)
	 then: "Nagating to Add/Edit document page"
	 assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()
	 navigateToAddEditDeleteDocument(Document.DELETE, documentSection, otherName)
	 when:"Document is deleted, Fetch document info to validate"
	 at ViewProjectDefinitionPage
	 def uiDocumetnsAfter = getUIDucuments(documentSection)
	 def dbDocumentsAfter = projects.getProjectDocuments(projectID, dococumentType)
	 then:"Validate UI document info with Database"
	 assert uiDocumetnsAfter.size() < uiDocumetnsBefore.size()
	 assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
	 }
	 */
	/*
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
	 */
}