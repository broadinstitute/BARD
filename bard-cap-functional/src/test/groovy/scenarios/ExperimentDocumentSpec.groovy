package scenarios

import pages.DocumentPage
import pages.ViewExperimentPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.TestData

import db.Experiment

/**
 * This class includes all the test functions related to experiment document section
 * @author Muhammad.Rafique
 * Date Created: 2013/12/18
 */
@Unroll
class ExperimentDocumentSpec extends BardFunctionalSpec {
	def setup() {
		logInSomeUser()
	}

	def "Test Add and Delete #TestName Document in Experiment"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Page, Fetching Document Info from UI and DB for validation"
		at ViewExperimentPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Experiment.getExperimentDocuments(TestData.experimentId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		when: "Navigating to Create Document Page and Create new document"
		navigateToCreateDocument(documentHeaders(docHeader))
		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument(testData.documentName, testData.documentUrl)
		}else{
			createDocument(testData.documentName, testData.documentContent)
		}
		at ViewExperimentPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Experiment.getExperimentDocuments(TestData.experimentId, docType)

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
		"Descriptoin"	| TestData.documentHeader.Description	| TestData.documentType.Description	| TestData.documents
		"Protocol"		| TestData.documentHeader.Protocol		| TestData.documentType.Protocol		| TestData.documents
		"Comment"		| TestData.documentHeader.Comment		| TestData.documentType.Comment		| TestData.documents
		"Publication"	| TestData.documentHeader.Publication	| TestData.documentType.Publication	| TestData.documents
		"ExternalUrl"	| TestData.documentHeader.Urls			| TestData.documentType.Urls			| TestData.documents
		"Other"			| TestData.documentHeader.Other		| TestData.documentType.Other			| TestData.documents
	}

	def "Test Add #TestName Document in Experiment with empty values"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Page, Fetching Document Info from UI and DB for validation"
		at ViewExperimentPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Experiment.getExperimentDocuments(TestData.experimentId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsBefore.size() == dbDocumentsBefore.size()
		assert uiDocumetnsBefore.sort() == dbDocumentsBefore.sort()

		when: "Navigating to Create Document Page and Create new document"
		navigateToCreateDocument(documentHeaders(docHeader))
		at DocumentPage
		if(docType == "publication" || docType == "external url"){
			createDocument("", testData.documentUrl)
		}else{
			createDocument("", testData.documentContent)
		}
		at ViewExperimentPage
		assert !isDocument(documentHeaders(docHeader), testData.documentName)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Experiment.getExperimentDocuments(TestData.experimentId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()
		report "$TestName"

		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| TestData.documentHeader.Description	| TestData.documentType.Description	| TestData.documents
		"Protocol"		| TestData.documentHeader.Protocol		| TestData.documentType.Protocol		| TestData.documents
		"Comment"		| TestData.documentHeader.Comment		| TestData.documentType.Comment		| TestData.documents
		"Publication"	| TestData.documentHeader.Publication	| TestData.documentType.Publication	| TestData.documents
		"ExternalUrl"	| TestData.documentHeader.Urls			| TestData.documentType.Urls			| TestData.documents
		"Other"			| TestData.documentHeader.Other		| TestData.documentType.Other			| TestData.documents
	}

	def "Test Edit #TestName Document in Experiment"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Page, Fetching Document Info from UI and DB for validation"
		at ViewExperimentPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Experiment.getExperimentDocuments(TestData.experimentId, docType)

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
		at ViewExperimentPage
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
		to ViewExperimentPage
		assert isDocument(documentHeaders(docHeader), testData.documentName+TestData.edited)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Experiment.getExperimentDocuments(TestData.experimentId, docType)

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
		"Descriptoin"	| TestData.documentHeader.Description	| TestData.documentType.Description	| TestData.documents
		"Protocol"		| TestData.documentHeader.Protocol		| TestData.documentType.Protocol		| TestData.documents
		"Comment"		| TestData.documentHeader.Comment		| TestData.documentType.Comment		| TestData.documents
		"Publication"	| TestData.documentHeader.Publication	| TestData.documentType.Publication	| TestData.documents
		"ExternalUrl"	| TestData.documentHeader.Urls			| TestData.documentType.Urls			| TestData.documents
		"Other"			| TestData.documentHeader.Other		| TestData.documentType.Other			| TestData.documents
	}

	def "Test Edit #TestName Document in Experiment with empty name value"(){
		given:"Navigate to Show Experiment page"
		to ViewExperimentPage

		when:"At View Experiment Page, Fetching Document Info from UI and DB for validation"
		at ViewExperimentPage
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		def uiDocumetnsBefore = getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsBefore = Experiment.getExperimentDocuments(TestData.experimentId, docType)

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
		at ViewExperimentPage
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
		at ViewExperimentPage
		assert isDocument(documentHeaders(docHeader), testData.documentName)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Experiment.getExperimentDocuments(TestData.experimentId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(docHeader), testData.documentName)){
			deleteDocument(documentHeaders(docHeader), testData.documentName)
		}
		report "$TestName"


		where:
		TestName		| docHeader								| docType								| testData
		"Descriptoin"	| TestData.documentHeader.Description	| TestData.documentType.Description	| TestData.documents
		"Protocol"		| TestData.documentHeader.Protocol		| TestData.documentType.Protocol		| TestData.documents
		"Comment"		| TestData.documentHeader.Comment		| TestData.documentType.Comment		| TestData.documents
		"Publication"	| TestData.documentHeader.Publication	| TestData.documentType.Publication	| TestData.documents
		"ExternalUrl"	| TestData.documentHeader.Urls			| TestData.documentType.Urls			| TestData.documents
		"Other"			| TestData.documentHeader.Other		| TestData.documentType.Other			| TestData.documents
	}
}