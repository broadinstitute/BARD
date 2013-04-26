package bard.db.dictionary

import spock.lang.Specification
import grails.test.mixin.TestFor

import grails.buildtestdata.mixin.Build

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
        expected.size()-1 == result.newPathElementList.size()
        for (int i = 0; i < expected.size()-1; i++) {
            expected.get(i) == result.newPathElementList.get(i)
        }

        expected.get(expected.size()-1).label == result.newElementLabel
    }


    void "test empty path"(){
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

        for (int i = 0; i < num/2; i++) {
            Element e = Element.build()
            elementSet.add(e)
            builder.append(pathDelimeter).append(e.label)
        }

        elementSet.add(Element.build())
        builder.append(pathDelimeter).append("this is my random label - dave lahr")

        for (int i = 0; i < num/2; i++) {
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

        for (int i = 0; i < num/2; i++) {
            Element e = Element.build()
            elementSet.add(e)
            builder.append(pathDelimeter).append(e.label)
        }

        elementSet.add(Element.build())
        builder.append(pathDelimeter).append(" ")

        for (int i = 0; i < num/2; i++) {
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
