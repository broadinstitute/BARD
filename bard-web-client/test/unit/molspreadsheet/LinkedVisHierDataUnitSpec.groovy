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
//        println  categorySectionJson

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
//        println  assaysSectionJson

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






}
