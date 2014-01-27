package scenarios

import pages.DocumentPage
import pages.ViewAssayDefinitionPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.TestData

import db.Assay
/**
 * This class holds all the test functions of Assay Document section
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
@Unroll
class AssayDocumentSpec extends BardFunctionalSpec {

	def setup() {
		logInSomeUser()
	}

	def "Test Add and Delete #TestName Document in Assay"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage

		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		when: "Nagating to Create Document Page and Create new document"
		navigateToCreateDocument(documentHeaders(docHeader))
		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument(testData.documentName, testData.documentUrl)
		}else{
			createDocument(testData.documentName, testData.documentContent)
		}
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		assert uiDocumetnsAfter.size() > uiDocumetnsBefore.size()
		assert dbDocumentsAfter.size() > dbDocumentsBefore.size()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		report "$TestName"

		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| TestData.documentHeader.Description	| TestData.documentType.Description		| TestData.documents
		"Protocol"		| TestData.documentHeader.Protocol		| TestData.documentType.Protocol		| TestData.documents
		"Comment"		| TestData.documentHeader.Comment		| TestData.documentType.Comment			| TestData.documents
		"Publication"	| TestData.documentHeader.Publication	| TestData.documentType.Publication		| TestData.documents
		"ExternalUrl"	| TestData.documentHeader.Urls			| TestData.documentType.Urls			| TestData.documents
		"Other"			| TestData.documentHeader.Other			| TestData.documentType.Other			| TestData.documents
	}

	def "Test Add #TestName Document in Assay with empty values"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage

		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		when: "Nagating to Create Document Page and Create new document"
		navigateToCreateDocument(documentHeaders(docHeader))
		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument("", testData.documentUrl)
		}else{
			createDocument("", testData.documentContent)
		}
		at ViewAssayDefinitionPage
		assert !isDocument(documentHeaders(docHeader), testData.documentName)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		report "$TestName"

		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| TestData.documentHeader.Description	| TestData.documentType.Description		| TestData.documents
		"Protocol"		| TestData.documentHeader.Protocol		| TestData.documentType.Protocol		| TestData.documents
		"Comment"		| TestData.documentHeader.Comment		| TestData.documentType.Comment			| TestData.documents
		"Publication"	| TestData.documentHeader.Publication	| TestData.documentType.Publication		| TestData.documents
		"ExternalUrl"	| TestData.documentHeader.Urls			| TestData.documentType.Urls			| TestData.documents
		"Other"			| TestData.documentHeader.Other			| TestData.documentType.Other			| TestData.documents
	}

	def "Test Edit #TestName Document in Assay"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage

		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, docType)

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
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		if(docType == "external url"){
			editDocument(documentHeaders(docHeader), testData.documentName, testData.documentName+TestData.edited)
		}else{
			navigateToEditDocument(documentHeaders(docHeader), testData.documentName)
			at DocumentPage
			if(docType == "publication"){
				createDocument(testData.documentName+TestData.edited, testData.documentUrl, true)
			}else{
				createDocument(testData.documentName+TestData.edited, testData.documentContent, true)
			}
		}
		to ViewAssayDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName+TestData.edited)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(docHeader), testData.documentName+TestData.edited)){
			deleteDocument(documentHeaders(docHeader), testData.documentName+TestData.edited)
		}
		report "$TestName"

		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| TestData.documentHeader.Description	| TestData.documentType.Description		| TestData.documents
		"Protocol"		| TestData.documentHeader.Protocol		| TestData.documentType.Protocol		| TestData.documents
		"Comment"		| TestData.documentHeader.Comment		| TestData.documentType.Comment			| TestData.documents
		"Publication"	| TestData.documentHeader.Publication	| TestData.documentType.Publication		| TestData.documents
		"ExternalUrl"	| TestData.documentHeader.Urls			| TestData.documentType.Urls			| TestData.documents
		"Other"			| TestData.documentHeader.Other			| TestData.documentType.Other			| TestData.documents
	}

	def "Test Edit #TestName Document in Assay with empty name value"(){
		given:"Navigate to Show Assay page"
		to ViewAssayDefinitionPage

		when:"At View Assay Page, Fetching Document Info from UI and DB for validation"
		at ViewAssayDefinitionPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Assay.getAssayDocuments(TestData.assayId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		when:"If document does nto exists, add new before updating"
		assert !isDocument(documentHeaders(TestName), testData.documentName)
		navigateToCreateDocument(documentHeaders(docHeader))
		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument(testData.documentName, testData.documentUrl)
		}else{
			createDocument(testData.documentName, testData.documentContent)
		}
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		if(docType == "external url"){
			editDocument(documentHeaders(docHeader), testData.documentName, "")
		}else{
			navigateToEditDocument(documentHeaders(docHeader), testData.documentName)
			at DocumentPage
			if(docType == "publication"){
				createDocument("", testData.documentUrl, true)
			}else{
				createDocument("", testData.documentContent, true)
			}
		}
		at ViewAssayDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(TestName), testData.documentName)){
			deleteDocument(documentHeaders(TestName), testData.documentName)
		}
		report "$TestName"

		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| TestData.documentHeader.Description	| TestData.documentType.Description		| TestData.documents
		"Protocol"		| TestData.documentHeader.Protocol		| TestData.documentType.Protocol		| TestData.documents
		"Comment"		| TestData.documentHeader.Comment		| TestData.documentType.Comment			| TestData.documents
		"Publication"	| TestData.documentHeader.Publication	| TestData.documentType.Publication		| TestData.documents
		"ExternalUrl"	| TestData.documentHeader.Urls			| TestData.documentType.Urls			| TestData.documents
		"Other"			| TestData.documentHeader.Other			| TestData.documentType.Other			| TestData.documents
	}


}