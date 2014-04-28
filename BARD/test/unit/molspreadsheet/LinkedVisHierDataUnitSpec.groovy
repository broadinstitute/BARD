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

package molspreadsheet

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/27/13
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */

@TestMixin(GrailsUnitTestMixin)
@Unroll
class LinkedVisHierDataUnitSpec  extends Specification {

    void setup() {
    }

    void tearDown() {
        // Tear down logic here
    }


    void "test writeCategorySection"() {
        when:
        LinkedVisHierData linkedVisHierData = new LinkedVisHierData()
        String categorySectionJson =  linkedVisHierData.createCategorySection()

        then:
        def userJson = new JsonSlurper().parseText(categorySectionJson )
        assert  userJson.getClass().name == 'java.util.ArrayList'
    }



    void "test writeHierSection"() {
        when:
        LinkedVisHierData linkedVisHierData = new LinkedVisHierData()
        String hierarchySectionJson =  linkedVisHierData.createHierarchySection()
//        println  hierarchySectionJson

        then:
        def userJson = new JsonSlurper().parseText(hierarchySectionJson )
        assert  userJson.getClass().name == 'java.util.ArrayList'
    }


    void "test writeAssaysSection"() {
        when:
        LinkedVisHierData linkedVisHierData = new LinkedVisHierData()
        String assaysSectionJson =  linkedVisHierData.createAssaysSection()
//        println  assaysSectionJson

        then:
        def userJson = new JsonSlurper().parseText(assaysSectionJson )
        assert  userJson.getClass().name == 'java.util.ArrayList'
    }


    void "test createAssayCrossSection"() {
        when:
        LinkedVisHierData linkedVisHierData = new LinkedVisHierData()
        String assaysSectionJson =  linkedVisHierData.createAssayCrossSection()

        then:
        def userJson = new JsonSlurper().parseText(assaysSectionJson )
        assert  userJson.getClass().name == 'java.util.ArrayList'
    }




    void "test createCombinedListing"() {
        when:
        LinkedVisHierData linkedVisHierData = new LinkedVisHierData()
        String assaysSectionJson =  linkedVisHierData.createCombinedListing()
        println  assaysSectionJson

        then:
        def userJson = new JsonSlurper().parseText(assaysSectionJson )
        assert  userJson.getClass().name == 'java.util.HashMap'
    }




    void "test  Failing indexUniquenessCheck   "() {
        when:
        LinkedVisHierData linkedVisHierData = new LinkedVisHierData()
        int proposedNewIndex  = 2
        List accumulatingIndex = [1, 2, 3]

        then:
        try {
            linkedVisHierData.indexUniquenessCheck(proposedNewIndex,accumulatingIndex,"test-section")
            assert(false)
        } catch(Exception e)   {
            assert(true)
        }
    }






}
