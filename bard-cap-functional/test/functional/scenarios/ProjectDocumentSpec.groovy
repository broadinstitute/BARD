package scenarios

import pages.DocumentPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.Constants
import common.TestData
import common.Constants.DocumentAs

import db.Project

/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 * Last Updated: 13/10/30
 */
@Unroll
class ProjectDocumentSpec extends BardFunctionalSpec {
	def setup() {
		logInSomeUser()

	}

	def "Test Project Document Add of type #TestName"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Project.getProjectDocuments(TestData.projectId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Navigating to Create Document Page"
		navigateToCreateDocument(documentHeaders(docHeader))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument(testData.documentName, testData.documentUrl)
		}else{
			createDocument(testData.documentName, testData.documentContent)
		}

		then:"At View Project Page, Verify that Document is added"
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Project.getProjectDocuments(TestData.projectId, docType)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(docHeader))
			def dbDocumentsAfterDelete = Project.getProjectDocuments(TestData.projectId, docType)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
			assert uiDocumetnsAfterDelete.size() < uiDocumetnsAfter.size()
			assert dbDocumentsAfterDelete.size() < dbDocumentsAfter.size()
		}
		report "$TestName"
		
		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
	}

	def "Test Project Document Add of type #TestName with empty values"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Project.getProjectDocuments(TestData.projectId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and: "Navigating to Create Document Page"
		navigateToCreateDocument(documentHeaders(docHeader))

		when:"At Create Document Page, Creating Description Document"
		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument("", testData.documentUrl)
		}else{
			createDocument("", testData.documentContent)
		}

		then:"At View Project Page, Verify that Document is added"
		at ViewProjectDefinitionPage
		assert !isDocument(documentHeaders(docHeader), testData.documentName)

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Project.getProjectDocuments(TestData.projectId, docType)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		report "$TestName"

		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
	}

	def "Test Project Document Edit of type #TestName"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Project.getProjectDocuments(TestData.projectId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does not exists, add new before updating"
		if(!isDocument(documentHeaders(docHeader), testData.documentName)){
			navigateToCreateDocument(documentHeaders(docHeader))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			if(docType == "publication" || docType == "external url"){
				createDocument(testData.documentName, testData.documentUrl)
			}else{
				createDocument(testData.documentName, testData.documentContent)
			}

			and:"Navigating to View Project Page, and edit document"
			at ViewProjectDefinitionPage
			assert isDocument(documentHeaders(docHeader), testData.documentName)
			if(docType == "publication" || docType == "external url"){
				editDocument(documentHeaders(docHeader), testData.documentName, testData.documentName+Constants.edited)
			}else{
				navigateToEditDocument(documentHeaders(docHeader), testData.documentName)
				at DocumentPage
				createDocument(testData.documentName+Constants.edited, testData.documentContent, true)
			}

		}else{
			if(docType == "publication" || docType == "external url"){
				editDocument(documentHeaders(docHeader), testData.documentName, testData.documentName+Constants.edited)
			}else{
				navigateToEditDocument(documentHeaders(docHeader), testData.documentName)
				at DocumentPage
				createDocument(testData.documentName+Constants.edited, testData.documentContent, true)
			}
		}

		and:"Fetching Document Info from UI and DB for validation"
		to ViewProjectDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName+Constants.edited)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Project.getProjectDocuments(TestData.projectId, docType)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(docHeader), testData.documentName+Constants.edited)){
			deleteDocument(documentHeaders(docHeader), testData.documentName+Constants.edited)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(docHeader))
			def dbDocumentsAfterDelete = Project.getProjectDocuments(TestData.projectId, docType)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "$TestName"

		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
	}

	def "Test Project Document Edit of type #TestName with empty name value"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Project.getProjectDocuments(TestData.projectId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before updating"
		if(!isDocument(documentHeaders(docHeader), testData.documentName)){
			navigateToCreateDocument(documentHeaders(docHeader))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			if(docType == "publication" || docType == "external url"){
				createDocument(testData.documentName, testData.documentUrl)
			}else{
				createDocument(testData.documentName, testData.documentContent)
			}

			and:"Navigating to View Project Page"
			at ViewProjectDefinitionPage
			assert isDocument(documentHeaders(docHeader), testData.documentName)
			if(docType == "publication" || docType == "external url"){
				editDocument(documentHeaders(docHeader), testData.documentName, "")
			}else{
				navigateToEditDocument(documentHeaders(docHeader), testData.documentName)
				at DocumentPage
				createDocument("", testData.documentContent, true)
			}

		}

		and:"Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Project.getProjectDocuments(TestData.projectId, docType)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)

			when:"Document is cleaned up, Fetching Document Info from UI and DB for validation"
			def uiDocumetnsAfterDelete = getUIDucuments(documentHeaders(docHeader))
			def dbDocumentsAfterDelete = Project.getProjectDocuments(TestData.projectId, docType)

			then:"Verifying Document Info with UI & DB"
			assert uiDocumetnsAfterDelete.size() == dbDocumentsAfterDelete.size()
			assert uiDocumetnsAfterDelete.sort() == dbDocumentsAfterDelete.sort()
		}
		report "$TestName"


		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
	}


	def "Test Project Document Delete of type #TestName"(){
		given:"Navigate to Show Project page"
		to ViewProjectDefinitionPage

		when:"At View Project Page, Fetching Document Info from UI and DB for validation"
		at ViewProjectDefinitionPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Project.getProjectDocuments(TestData.projectId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		and:"If document does nto exists, add new before deleting"
		if(!isDocument(documentHeaders(docHeader), testData.documentName)){
			navigateToCreateDocument(documentHeaders(docHeader))

			and:"At Create Document Page, Creating New Document"
			at DocumentPage
			if(docType == "publication" || docType == "external url"){
				createDocument(testData.documentName, testData.documentUrl)
			}else{
				createDocument(testData.documentName, testData.documentContent)
			}

			and:"Navigating to View Project Page"
			at ViewProjectDefinitionPage
			assert isDocument(documentHeaders(docHeader), testData.documentName)
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}else{
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}

		and:"Fetching Document Info from UI and DB for validation"
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Project.getProjectDocuments(TestData.projectId, docType)

		and:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		report "$TestName"

		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
	}
}