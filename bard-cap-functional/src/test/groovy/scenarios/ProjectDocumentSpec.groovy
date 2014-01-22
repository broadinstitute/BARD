package scenarios

import pages.DocumentPage
import pages.ViewProjectDefinitionPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.Constants
import common.TestData

import db.Project

/**
 * This class includes all the possible test functions for document section of Project.
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
@Unroll
class ProjectDocumentSpec extends BardFunctionalSpec {
	def setup() {
		logInSomeUser()
	}

	def "Test Add and Delete #TestName Document in Project"(){
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
		
		when:"Navigating to Create Document Page and Create new document"
		navigateToCreateDocument(documentHeaders(docHeader))
		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument(testData.documentName, testData.documentUrl)
		}else{
			createDocument(testData.documentName, testData.documentContent)
		}
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Project.getProjectDocuments(TestData.projectId, docType)
		
		then:"At View Project Page, Verify that Document is added"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()
		
		and:"Cleaning up documents"
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		
		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
	}

	def "Test Add #TestName Document in Project with empty values"(){
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
		
		when:"Navigating to Create Document Page and Create new document"
		navigateToCreateDocument(documentHeaders(docHeader))
		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument("", testData.documentUrl)
		}else{
			createDocument("", testData.documentContent)
		}
		at ViewProjectDefinitionPage
		assert !isDocument(documentHeaders(docHeader), testData.documentName)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Project.getProjectDocuments(TestData.projectId, docType)
		
		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
	}

	def "Test Edit #TestName Document in Project"(){
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
		
		when:"If document does not exists, add new before updating"
		assert !isDocument(documentHeaders(docHeader), testData.documentName)
		navigateToCreateDocument(documentHeaders(docHeader))
		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument(testData.documentName, testData.documentUrl)
		}else{
			createDocument(testData.documentName, testData.documentContent)
		}
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		if(docType == "external url"){
			editDocument(documentHeaders(docHeader), testData.documentName, testData.documentName+Constants.edited)
		}else{
			navigateToEditDocument(documentHeaders(docHeader), testData.documentName)
			at DocumentPage
			if(docType == "publication"){
				createDocument(testData.documentName+Constants.edited, testData.documentUrl, true)
			}else{
				createDocument(testData.documentName+Constants.edited, testData.documentContent, true)
			}
		}
		to ViewProjectDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName+Constants.edited)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Project.getProjectDocuments(TestData.projectId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		
		and:"Cleaning up documents"
		while(isDocument(documentHeaders(docHeader), testData.documentName+Constants.edited)){
			deleteDocument(documentHeaders(docHeader), testData.documentName+Constants.edited)
		}
		
		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
	}

	def "Test Edit #TestName Document in Project with empty name value"(){
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

		when:"If document does nto exists, add new before updating"
		assert !isDocument(documentHeaders(docHeader), testData.documentName)
		navigateToCreateDocument(documentHeaders(docHeader))

		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument(testData.documentName, testData.documentUrl)
		}else{
			createDocument(testData.documentName, testData.documentContent)
		}
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		if(docType == "external url"){
			editDocument(documentHeaders(docHeader), testData.documentName, "")
		}else{
			navigateToEditDocument(documentHeaders(docHeader), testData.documentName)
			at DocumentPage
			if(docType == "publication"){
				createDocument(testData.documentName+Constants.edited, testData.documentUrl, true)
			}else{
				createDocument(testData.documentName+Constants.edited, testData.documentContent, true)
			}
		}
		at ViewProjectDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Project.getProjectDocuments(TestData.projectId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		
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