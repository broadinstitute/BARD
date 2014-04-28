/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package scenarios

import pages.DocumentPage
import pages.ViewAssayDefinitionPage
import spock.lang.Unroll
import base.BardFunctionalSpec

import common.Constants
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
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
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
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
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
		to ViewAssayDefinitionPage
		assert isDocument(documentHeaders(docHeader), testData.documentName+Constants.edited)
		def uiDocumetnsAfter= getUIDucuments(documentHeaders(docHeader))
		def dbDocumentsAfter= Assay.getAssayDocuments(TestData.assayId, docType)

		then:"Verifying Document Info with UI & DB"
		assert uiDocumetnsAfter.size() == dbDocumentsAfter.size()
		assert uiDocumetnsAfter.sort() == dbDocumentsAfter.sort()

		and:"Cleaning up documents"
		while(isDocument(documentHeaders(docHeader), testData.documentName+Constants.edited)){
			deleteDocument(documentHeaders(docHeader), testData.documentName+Constants.edited)
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
		"Descriptoin"	| Constants.documentHeader.Description	| Constants.documentType.Description	| TestData.documents
		"Protocol"		| Constants.documentHeader.Protocol		| Constants.documentType.Protocol		| TestData.documents
		"Comment"		| Constants.documentHeader.Comment		| Constants.documentType.Comment		| TestData.documents
		"Publication"	| Constants.documentHeader.Publication	| Constants.documentType.Publication	| TestData.documents
		"ExternalUrl"	| Constants.documentHeader.Urls			| Constants.documentType.Urls			| TestData.documents
		"Other"			| Constants.documentHeader.Other		| Constants.documentType.Other			| TestData.documents
	}


}
