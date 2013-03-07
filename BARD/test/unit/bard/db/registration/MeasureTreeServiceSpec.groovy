package bard.db.registration

import bard.db.dictionary.Element

import grails.buildtestdata.mixin.Build
import grails.converters.JSON
import spock.lang.Specification
import spock.lang.Unroll
import grails.test.mixin.*
import grails.test.mixin.support.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(MeasureTreeService)
@Build([Assay, Element, Measure, AssayContext, AssayContextMeasure])
@Mock([Assay, Element, Measure, AssayContext, AssayContextMeasure])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class MeasureTreeServiceSpec extends Specification {

    void 'test rendering json measurements'() {
        given: 'a measurement with a child'
        Measure child = Measure.build(resultType: Element.build(label: "child"))
        Measure parent = Measure.build(resultType: Element.build(label: "parent"), childMeasures: [child] as Set)
        child.parentMeasure = parent
        Assay assay = Assay.build(measures: [parent, child] as Set)

        when: 'we render this as json'
        List result = service.createMeasureTree(assay, false)

        then:
        result == [[key:2, title:"parent", children: [[key:1, title: "child", children: [], expand:true]], expand:true]]
    }

    private static String sortedRootMeasuresAB = '''[
                                                    [key:2, title:a, children:[], expand:true],
                                                    [key:1, title:b, children:[], expand:true]
                                                   ]'''
    private static String sortedRootMeasuresABC = '''[
                                                       [key:3, title:a, children:[], expand:true],
                                                       [key:2, title:B, children:[], expand:true],
                                                       [key:1, title:c, children:[], expand:true]
                                                      ]'''


    void 'test sorting of root measures in json measurements'() {
        given: 'a measurement with a child'
        Assay assay = Assay.build()

        for (String label in measureLabels) {
            assay.addToMeasures(Measure.build(resultType: Element.build(label: label)))
        }

        when: 'we render this as json'
        def result = service.createMeasureTree(assay, false)

        then:
        result.toString().replaceAll(/\s/, '') == expectedJson.replaceAll(/\s/, '')

        where:
        measureLabels   | expectedJson
        ['b', 'a']      | sortedRootMeasuresAB
        ['c', 'B', 'a'] | sortedRootMeasuresABC
    }

    private static String sortedChildMeasuresAB = '''[
                                                        [key:1,
                                                        title:parent,
                                                        children:[
                                                                    [key:3,title:a,children:[], expand:true],
                                                                    [key:2,title:b,children:[], expand:true]
                                                                   ]   , expand:true
                                                         ]
                                                      ]'''
    private static String sortedChildMeasuresABC = '''[
                                                        [key:1,
                                                        title:parent,
                                                        children:[
                                                                    [key:4,title:a,children:[], expand:true],
                                                                    [key:3,title:B,children:[], expand:true],
                                                                    [key:2,title:c,children:[], expand:true]
                                                                   ]     , expand:true
                                                         ]
                                                      ]'''

    void 'test sorting of child measures in json measurements'() {
        given:
        Measure parent = Measure.build(resultType: Element.build(label: "parent"))
        Assay assay = Assay.build()

        for (String label in measureLabels) {
            parent.addToChildMeasures(Measure.build(resultType: Element.build(label: label)))
        }
        assay.addToMeasures(parent)


        when: 'we render this as json'
        def result = service.createMeasureTree(assay, false)

        then:
        result.toString().replaceAll(/\s/, '') == expectedJson.replaceAll(/\s/, '')

        where:
        measureLabels   | expectedJson
        ['b', 'a']      | sortedChildMeasuresAB
        ['c', 'B', 'a'] | sortedChildMeasuresABC
    }
}
