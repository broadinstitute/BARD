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

import pages.CapSearchPage
import pages.HomePage
import pages.ViewProjectDefinitionPage
import spock.lang.Ignore
import base.BardFunctionalSpec

import common.Constants.NavigateTo
import common.Constants.SearchBy
import common.TestDataReader;

import db.Project
/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 */

@Ignore
class ProjectSearchSpec extends BardFunctionalSpec{
	def testData = TestDataReader.getTestData()
	String projectId = testData.ProjectID
	String projectSearchName = testData.projectSearchName
	
	def setup() {
		logInSomeUser()
	}

	def "Test Search Project By Project Id"() {
		when: "User is navigating to Find Project page"
		at HomePage
		navigateTo(NavigateTo.PROJECT_BY_ID)

		then: "User is at Find Project page"
		at CapSearchPage

		when: "User is trying to search some project"
		at CapSearchPage
		capSearchBy(SearchBy.PROJECT_ID, projectId)

		then: "User is navigated to View Project Definition page"
		at ViewProjectDefinitionPage

		when: "When user is at View Project, Fetch summary info to validate"
		at ViewProjectDefinitionPage
		def uiSummaryInfo = getUISummaryInfo()
		def dbSummaryInfo = Project.getProjectSummaryById(projectId)

		then: "Validate project summary info with database "
		assert uiSummaryInfo.PID.equalsIgnoreCase(dbSummaryInfo.PID.toString())
		assert uiSummaryInfo.Status.equalsIgnoreCase(dbSummaryInfo.Status)
		assert uiSummaryInfo.Name.equalsIgnoreCase(dbSummaryInfo.Name)
		assert uiSummaryInfo.Description.equalsIgnoreCase(dbSummaryInfo.Description.toString())
		assert uiSummaryInfo.DateCreated.equalsIgnoreCase(dbSummaryInfo.DateCreated)
		assert uiSummaryInfo.LastUpdated.equalsIgnoreCase(dbSummaryInfo.LastUpdated)

		report "SearchProjectById"
	}

	def "Test Search Project By Name"() {
		when: "User is navigating to Find Project page"
		at HomePage
		navigateTo(NavigateTo.PROJECT_BY_NAME)

		then: "User is at Find Project page"
		at CapSearchPage

		when: "User is trying to search some Project with Name"
		at CapSearchPage
		capSearchBy(SearchBy.PROJECT_NAME, projectSearchName)
		def dbSearchCount = Project.getProjectSearchCount(projectSearchName)
		def uiSearchCount = searchResultCount()
		def dbSearchResult = Project.getProjectSearchResults(projectSearchName)
		def uiSearchResult = seachResults()
		
		then: "Validate the search result with db"
		assert uiSearchCount == dbSearchCount
		assert uiSearchResult.sort() == dbSearchResult.sort()

		report "SearchProjectByName"
	}
}
