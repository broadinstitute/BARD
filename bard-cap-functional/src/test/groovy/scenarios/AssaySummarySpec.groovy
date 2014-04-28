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

import pages.CreateAssayPage
import pages.ViewAssayDefinitionPage
import base.BardFunctionalSpec

import common.Constants
import common.TestData

import db.Assay



/**
 * This class holds all the test functions of Assay Summary section
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class AssaySummarySpec extends BardFunctionalSpec {
	int statusIndex = 1
	int nameIndex = 2
	int designedByIndex = 4
	int definitionTypeIndex = 5

	def setup() {
		logInSomeUser()
	}

	def "Test Create New Assay"() {
		given:"Navigating to Create New Assay page"
		to CreateAssayPage

		when:"User is at Create New Assay Page"
		at CreateAssayPage
		CreateNewAssay(TestData.createAssay)

		then:"Navigate to View Assay page and fetch summary info"
		at ViewAssayDefinitionPage

		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getCreatedAssaySummary(TestData.createAssay.name)

		and:"Validate the created assay summary info with db and ui"
		assert uiSummary.ADID.toString() == dbSummary.ADID.toString()
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
		assert uiSummary.Owner == dbSummary.owner
	}

	def "Test Edit Assay Summary Status"() {
		given: "Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(TestData.assayId)
		def statusOriginal = uiSummary.Status
		def statusEdited = ""
		if(statusOriginal != "Approved"){
			statusEdited = "Approved"
		}else{
			statusEdited = "Retired"
		}
		
		then:"Verify Summary Status before edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)

        when:"Edit/Update Summary Status, Fetch Summary info on UI and DB for validation"
        editSummary(statusIndex, statusEdited, true)
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Status after edit on UI & DB"
		assert uiSummary.Status.equalsIgnoreCase(statusEdited)
		assert uiSummary.Status.equalsIgnoreCase(dbSummary.Status)
	}

	def "Test Edit Assay Summary Name"() {
		given: "Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(TestData.assayId)
		def nameOriginal = uiSummary.Name
		def nameEdited = nameOriginal+Constants.edited

		then:"Verify Summary Name before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

        when:"Edit/Update Summary Name, Fetch Summary info on UI and DB for validation"
        editSummary(nameIndex, nameEdited)
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Name after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameEdited)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)

        when:"Revert Edit/Update Summary Name, Fetch Summary info on UI and DB for validation"
        editSummary(nameIndex, nameOriginal)
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Name after revert on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name)
	}

	def "Test Edit Assay Summary Name with empty value"() {
		given: "Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(TestData.assayId)
		def nameOriginal = uiSummary.Name

		then:"Verify Summary Description before edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())

        when:"Edit/Update Summary Description, Fetch Summary info on UI and DB for validation"
        editSummary(nameIndex, "")
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Description after edit on UI & DB"
		assert uiSummary.Name.equalsIgnoreCase(nameOriginal)
		assert uiSummary.Name.equalsIgnoreCase(dbSummary.Name.toString())
	}

	def "Test Edit Assay Definition Type"() {
		given: "Navigate to Show Assay page"
		to ViewAssayDefinitionPage
		
		when:"User is at View Assay Page, Fetch Summary info on UI and DB for validation"
		at ViewAssayDefinitionPage
		def uiSummary = getUISummaryInfo()
		def dbSummary = Assay.getAssaySummaryById(TestData.assayId)
		def definitionTypeOriginal = uiSummary.DefinitionType
		def definitionTypeEdited = ""
		if(definitionTypeOriginal != "Template"){
			definitionTypeEdited = "Template"
		}else{
			definitionTypeEdited = "Regular"
		}
		then:"Verify Summary Definition Type before edit on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)

        when:"Edit/Update Summary Definition Type, Fetch Summary info on UI and DB for validation"
        editSummary(definitionTypeIndex, definitionTypeEdited, true)
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Definition Type after edit on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(definitionTypeEdited)
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)

        when:"Revert Edit/Update Summary Definition Type, Fetch Summary info on UI and DB for validation"
        editSummary(definitionTypeIndex, definitionTypeOriginal, true)
        at ViewAssayDefinitionPage
		uiSummary = getUISummaryInfo()
		dbSummary = Assay.getAssaySummaryById(TestData.assayId)

		then:"Verify Summary Definition Type after revert on UI & DB"
		assert uiSummary.DefinitionType.equalsIgnoreCase(definitionTypeOriginal)
		assert uiSummary.DefinitionType.equalsIgnoreCase(dbSummary.DefinitionType)
	}

}
