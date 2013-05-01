package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import test.TestUtils

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 4/24/13
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ElementController)
@Build(Element)
class ElementControllerUnitSpec extends Specification {
    private static final String pathDelimeter = "/"

    private static final EXPECTED_ADD_TERN_VIEW = "/element/addTerm"


    void "test label constraints #desc TermCommand: '#valueUnderTest'"() {
        final String field = 'label'
        given:
        Element parentElement = Element.build()
        Element.build(label: "Label")
        TermCommand termCommand =
            new TermCommand(parentId: parentElement.id, description: "description", abbreviation: "abbr", synonyms: "abc,efg", comments: "comments")

        when: 'a value is set for the field under test'
        termCommand[(field)] = valueUnderTest
        termCommand.validate()
//        if (termCommand.hasErrors()) {
//            termCommand.errors?.allErrors.each { xyz ->
//                if (xyz != null) {
//                    println "Field:${xyz?.getField()}| Error: ${messageSource?.getMessage(xyz, null)}"
//                }
//            }
//        }
        then: 'verify valid or invalid for expected reason'
        TestUtils.assertFieldValidationExpectations(termCommand, field, valid, errorCode)


        where:
        desc                          | valueUnderTest                                     | valid | errorCode
        'null '                       | null                                               | false | 'nullable'
        'blank value'                 | ''                                                 | false | 'blank'
        'blank value'                 | '  '                                               | false | 'blank'
        'too long'                    | TestUtils.createString(Element.LABEL_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit'            | TestUtils.createString(Element.LABEL_MAX_SIZE)     | true  | null
        'existing element with label' | "Label"                                            | false | 'unique'
    }

    void "test description constraints #desc TermCommand: '#valueUnderTest'"() {
        final String field = 'description'
        given:
        Element parentElement = Element.build()
        TermCommand termCommand =
            new TermCommand(parentId: parentElement.id, label: "label", abbreviation: "abbr", synonyms: "abc,efg", comments: "comments")

        when: 'a value is set for the field under test'
        termCommand[(field)] = valueUnderTest
        termCommand.validate()

        then: 'verify valid or invalid for expected reason'
        TestUtils.assertFieldValidationExpectations(termCommand, field, valid, errorCode)


        where:
        desc               | valueUnderTest                                           | valid | errorCode
        'null '            | null                                                     | false | 'nullable'
        'blank value'      | ''                                                       | false | 'blank'
        'blank value'      | '  '                                                     | false | 'blank'
        'too long'         | TestUtils.createString(Element.DESCRIPTION_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | TestUtils.createString(Element.DESCRIPTION_MAX_SIZE)     | true  | null
    }

    void "test comments constraints #desc TermCommand: Comments '#valueUnderTest'"() {
        final String field = 'comments'
        given:
        Element parentElement = Element.build()
        TermCommand termCommand =
            new TermCommand(parentId: parentElement.id, label: "label", description: "description", abbreviation: "abbr", synonyms: "abc,efg")

        when: 'a value is set for the field under test'
        termCommand[(field)] = valueUnderTest
        termCommand.validate()

        then: 'verify valid or invalid for expected reason'
        TestUtils.assertFieldValidationExpectations(termCommand, field, valid, errorCode)


        where:
        desc               | valueUnderTest                                           | valid | errorCode
        'null '            | null                                                     | false | 'nullable'
        'blank value'      | ''                                                       | false | 'blank'
        'blank value'      | '  '                                                     | false | 'blank'
        'too long'         | TestUtils.createString(Element.DESCRIPTION_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | TestUtils.createString(Element.DESCRIPTION_MAX_SIZE)     | true  | null
    }

    void "test ParentId constraints #desc TermCommand: '#valueUnderTest'"() {
        final String field = 'parentId'
        given:
        TermCommand termCommand =
            new TermCommand(label: "label", description: "description", abbreviation: "abbr", synonyms: "abc,efg", comments: "comments")

        when: 'a value is set for the field under test'
        termCommand[(field)] = valueUnderTest
        termCommand.validate()

        then: 'verify valid or invalid for expected reason'
        TestUtils.assertFieldValidationExpectations(termCommand, field, valid, errorCode)


        where:
        desc                      | valueUnderTest | valid | errorCode
        'null '                   | null           | false | 'nullable'
        'ParentID does not exist' | 111            | false | 'termCommand.parentId.mustexist'
    }

    void "add New Term Page"() {

        when:
        this.controller.addTerm()
        then:
        assert EXPECTED_ADD_TERN_VIEW == view
    }

    void "Save Term Page ParentId #desc"() {
        given:
        ElementService elementService = Mock(ElementService.class)
        this.controller.elementService = elementService
        TermCommand termCommand =
            new TermCommand(parentId: valueUnderTest.call(), label: "Label", description: "description", abbreviation: "abbr", synonyms: "abc,efg", comments: "comments")
        when:
        this.controller.saveTerm(termCommand)
        then:
        assert EXPECTED_ADD_TERN_VIEW == view
        TestUtils.assertFieldValidationExpectations(termCommand, "parentId", valid, errorCode)
        where:
        desc                      | valueUnderTest         | valid | errorCode
        'null '                   | { null }               | false | 'nullable'
        'ParentID does not exist' | { 111 }                | false | 'termCommand.parentId.mustexist'
        'ParentID  exist'         | { Element.build().id } | true  | null
    }

    void "Save Term Page label #desc"() {
        given:
        Element parentElement = Element.build()
        Element.build(label: "Label")
        ElementService elementService = Mock(ElementService.class)
        this.controller.elementService = elementService
        TermCommand termCommand =
            new TermCommand(parentId: parentElement.id, label: valueUnderTest,
                    description: "description", abbreviation: "abbr",
                    synonyms: "abc,efg", comments: "comments")
        when:
        this.controller.saveTerm(termCommand)
        then:
        assert EXPECTED_ADD_TERN_VIEW == view
        TestUtils.assertFieldValidationExpectations(termCommand, "label", valid, errorCode)


        where:
        desc                           | valueUnderTest | valid | errorCode
        'null '                        | null           | false | 'nullable'
        'Label already exist'          | "Label"        | false | 'unique'
        'Label does not already exist' | "Label2"       | true  | null
    }

    void "simple test findElementsFromPathString"() {
        setup:
        final int num = 4

        StringBuilder builder = new StringBuilder()
        List<Element> expected = new ArrayList<Element>(num)
        for (int i = 0; i < num; i++) {
            Element e = Element.build()
            expected.add(e)
            builder.append(pathDelimeter).append(e.label)
        }
        builder.append(pathDelimeter)


        when:
        NewElementAndPath result = ElementController.findElementsFromPathString(builder.toString(), pathDelimeter)


        then:
        expected.size() - 1 == result.newPathElementList.size()
        for (int i = 0; i < expected.size() - 1; i++) {
            expected.get(i) == result.newPathElementList.get(i)
        }

        expected.get(expected.size() - 1).label == result.newElementLabel
    }


    void "test empty path"() {
        setup:
        String pathString = "$pathDelimeter$pathDelimeter"
        when:
        ElementController.findElementsFromPathString(pathString, pathDelimeter)

        then:
        thrown(EmptyPathException)
    }


    void "test invalid element label in path"() {
        setup:
        final int num = 4

        StringBuilder builder = new StringBuilder()
        Set<Element> elementSet = new HashSet<Element>()

        for (int i = 0; i < num / 2; i++) {
            Element e = Element.build()
            elementSet.add(e)
            builder.append(pathDelimeter).append(e.label)
        }

        elementSet.add(Element.build())
        builder.append(pathDelimeter).append("this is my random label - dave lahr")

        for (int i = 0; i < num / 2; i++) {
            Element e = Element.build()
            elementSet.add(e)
            builder.append(pathDelimeter).append(e.label)
        }
        builder.append(pathDelimeter)


        when:
        ElementController.findElementsFromPathString(builder.toString(), pathDelimeter)


        then:
        thrown(UnrecognizedElementLabelException)
    }


    void "test empty element label in middle of path"() {
        setup:
        final int num = 4

        StringBuilder builder = new StringBuilder()
        Set<Element> elementSet = new HashSet<Element>()

        for (int i = 0; i < num / 2; i++) {
            Element e = Element.build()
            elementSet.add(e)
            builder.append(pathDelimeter).append(e.label)
        }

        elementSet.add(Element.build())
        builder.append(pathDelimeter).append(" ")

        for (int i = 0; i < num / 2; i++) {
            Element e = Element.build()
            elementSet.add(e)
            builder.append(pathDelimeter).append(e.label)
        }
        builder.append(pathDelimeter)


        when:
        ElementController.findElementsFromPathString(builder.toString(), pathDelimeter)


        then:
        thrown(EmptyPathSectionException)
    }
}
