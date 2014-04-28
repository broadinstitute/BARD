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

package querycart

import grails.test.mixin.TestFor
import org.apache.commons.lang.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll
import org.apache.commons.lang.StringUtils
import bardqueryapi.IQueryService
import bardqueryapi.QueryService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(CartProject)
@Unroll
class CartProjectUnitSpec extends Specification {

    void setup() {
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test constructor with integer"() {
        given:
        int projectId = 2
        final String projectName = "ProjectSearchResult title"
        when:
        CartProject cartProject = new CartProject(projectName, 1, projectId)

        then:
        assert cartProject.name == 'ProjectSearchResult title'
        assert cartProject.externalId == projectId
        assert cartProject.internalId == 1
    }

    void "test toString #label"() {
        given:
        CartProject cartProject = new CartProject(projectName, 1, 5)

        when:
        String projectAsString = cartProject.toString()

        then:
        assert projectAsString == expectedTitle

        where:
        label                                     | projectName  | expectedTitle
        "Empty ProjectSearchResult Name"          | ""           | ""
        "Null String as ProjectSearchResult Name" | ""           | ""
        "With ProjectSearchResult Name"           | "Some Title" | "Some Title"
        "Null ProjectSearchResult Name"           | null         | ""

    }

    void "Test equals #label"() {
        when:
        final boolean equals = cartProject.equals(otherCartProject)

        then:
        equals == equality
        where:
        label               | cartProject                          | otherCartProject                     | equality
        "Other is null"     | new CartProject()                    | null                                 | false
        "Different classes" | new CartProject()                    | 20                                   | false
        "Equality"          | new CartProject("Some Title", 1, 24) | new CartProject("Some Title", 1, 24) | true


    }

    void "Test hashCode #label"() {
        when:
        int code1 = cartProject1.hashCode()
        int code2 = cartProject2.hashCode()

        then:
        assert (code1 == code2) == expectation

        where:
        label                  | cartProject1                         | cartProject2                         | expectation
        "Empty classes"        | new CartProject()                    | new CartProject()                    | true
        "Diff ids, diff names" | new CartProject("Test 1", 1, 5)      | new CartProject("Test 2", 2, 3)      | false
        "Same ids, diff names" | new CartProject("Test 1", 1, 3)      | new CartProject("Test 2", 1, 3)      | true
        "Diff ids, same names" | new CartProject("Some Title", 2, 22) | new CartProject("Some Title", 1, 25) | false
        "Same ids, same names" | new CartProject("Some Title", 2, 22) | new CartProject("Some Title", 2, 22) | true

    }

    void "test adding ellipses when the project name is too long"() {
        given:
        mockForConstraintsTests(CartProject)
        final String name = RandomStringUtils.randomAlphabetic(stringLength)
        String truncatedName = StringUtils.abbreviate(name, CartCompound.MAXIMUM_NAME_FIELD_LENGTH)

        CartProject cartProject = new CartProject(name, projectId, projectId)

        when:
        cartProject.validate()

        then:
        assert cartProject.name == truncatedName

        where:
        projectId | stringLength
        47        | 4001
        47        | 80000
        47        | 25
        2         | 0
    }

    void "test shopping cart project element"() {
        when:
        CartProject cartProject = new CartProject("my project name", 19, 20)
        assertNotNull(cartProject)

        then:
        assert cartProject.name == 'my project name'
        assertNull cartProject.shoppingItem
    }




    void "test constraints on CartProject #label"() {
        setup:
        mockForConstraintsTests(CartProject)

        when:
        CartProject cartProject = new CartProject(projectName, 19, 20)
        cartProject.validate()

        then:
        cartProject.hasErrors() == !valid

        where:
        label                                  | projectName                | valid
        "ProjectSearchResult Name is null"     | ""                         | false
        "ProjectSearchResult Name is not null" | "Some ProjectSearchResult" | true
    }
}
