package bard.db.dictionary
import spock.lang.Specification
import spock.lang.Unroll
import grails.test.mixin.TestFor


/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 7/13/12
 * Time: 9:57 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(TreeRoot)
class TreeRootSpec  extends Specification {

    ArrayList<TreeRoot> existingTreeRoot = [new TreeRoot(treeName : 'tree name', element: createElement(),dateCreated:  new Date())]
    TreeRoot validTreeRoot = new TreeRoot(treeName : 'tree name', element: createElement(),dateCreated:  new Date())



    private Element createElement() {
        new Element(label: 'existingLabel')
    }


    @Unroll
    void "test constraints on treeName"() {
        given:
        mockForConstraintsTests(TreeRoot, existingTreeRoot)

        when:
        TreeRoot treeRoot = validTreeRoot
        treeRoot.treeName = name
        treeRoot.validate()

        then:
        TestUtils.assertFieldValidationExpectations(treeRoot, 'treeName', valid, errorCode)

        where:
        name                                        | valid | errorCode
        null                                        | false | 'nullable'
        TestUtils.createString(30+1)                | false | 'maxSize'
        TestUtils.createString(30)                  | true  | null
    }



    @Unroll
    void "test constraints on element"() {
        given:
        mockForConstraintsTests(TreeRoot, existingTreeRoot)

        when:
        TreeRoot treeRoot = validTreeRoot
        treeRoot.element = name
        treeRoot.validate()

        then:
        TestUtils.assertFieldValidationExpectations(treeRoot, 'element', valid, errorCode)

        where:
        name            | valid | errorCode
        null            | false | 'nullable'
        createElement() | true  | null
    }




    @Unroll
    void "test constraints on relationshipType"() {
        given:
        mockForConstraintsTests(TreeRoot, existingTreeRoot)

        when:
        TreeRoot treeRoot = validTreeRoot
        treeRoot.relationshipType = name
        treeRoot.validate()

        then:
        TestUtils.assertFieldValidationExpectations(treeRoot, 'relationshipType', valid, errorCode)

        where:
        name                                        | valid | errorCode
        null                                        | true  | null
        TestUtils.createString(20+1)                | false | 'maxSize'
        TestUtils.createString(20)                  | true  | null
    }


    @Unroll
    void "test constraints on dateCreated"() {
        given:
        mockForConstraintsTests(TreeRoot, existingTreeRoot)

        when:
        TreeRoot treeRoot = validTreeRoot
        treeRoot.modifiedBy = name
        treeRoot.validate()

        then:
        TestUtils.assertFieldValidationExpectations(treeRoot, 'dateCreated', valid, errorCode)

        where:
        name                     | valid | errorCode
        new Date()               | true  |  null
        null                     | true  |  null
    }


    @Unroll
    void "test constraints on lastUpdated"() {
        given:
        mockForConstraintsTests(TreeRoot, existingTreeRoot)

        when:
        TreeRoot treeRoot = validTreeRoot
        treeRoot.lastUpdated = name
        treeRoot.validate()

        then:
        TestUtils.assertFieldValidationExpectations(treeRoot, 'lastUpdated', valid, errorCode)

        where:
        name                     | valid | errorCode
        new Date()               | true  |  null
        null                     | true  |  null
    }


    @Unroll
    void "test constraints on modifiedBy"() {
        given:
        mockForConstraintsTests(TreeRoot, existingTreeRoot)

        when:
        TreeRoot treeRoot = validTreeRoot
        treeRoot.modifiedBy = name
        treeRoot.validate()

        then:
        TestUtils.assertFieldValidationExpectations(treeRoot, 'modifiedBy', valid, errorCode)

        where:
        name                           | valid | errorCode
        null                           | true  | null
        "joe"                          | true  | null
    }





    @Unroll
    void "test constraints on version"() {
        given:
        mockForConstraintsTests(TreeRoot, existingTreeRoot)

        when:
        TreeRoot treeRoot = validTreeRoot
        treeRoot.version = name
        treeRoot.validate()

        then:
        TestUtils.assertFieldValidationExpectations(treeRoot, 'version', valid, errorCode)

        where:
        name                           | valid | errorCode
        null                           | true  | null
        47L                            | true  | null

    }


}
