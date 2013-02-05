package bard.taglib

import bard.db.dictionary.Element
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextMeasure
import bard.db.registration.Measure
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 1/11/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Assay, Element, Measure, AssayContext, AssayContextMeasure])
@TestFor(TreeRenderTagLib)
@Unroll
class TreeRenderTagLibUnitSpec extends Specification {


    void 'test rendering json measurements'() {
        given: 'a measurement with a child'
        Measure child = Measure.build(resultType: Element.build(label: "child"))
        Measure parent = Measure.build(resultType: Element.build(label: "parent"), childMeasures: [child] as Set)

        when: 'we render this as json'
        def result = tagLib.renderMeasuresAsJSONTree([measures: [parent]])

        then:
        result.toString() == '''[{"key":2,
                                  "title":"parent",
                                  "children":[
                                        {"key":1,
                                        "title":"child",
                                        "children":[]
                                        }
                                   ]
                                 }]'''.replaceAll(/\s/, '')
    }

    private static String sortedRootMeasuresAB = '''[
                                                    { "key":2, "title":"a", "children":[] },
                                                    { "key":1, "title":"b", "children":[] }
                                                   ]'''
    private static String sortedRootMeasuresABC = '''[
                                                       { "key":3, "title":"a", "children":[] },
                                                       { "key":2, "title":"B", "children":[] },
                                                       { "key":1, "title":"c", "children":[] }
                                                      ]'''


    void 'test sorting of root measures in json measurements'() {
        given: 'a measurement with a child'
        Assay assay = Assay.build()

        for (String label in measureLabels) {
            assay.addToMeasures(Measure.build(resultType: Element.build(label: label)))
        }


        when: 'we render this as json'
        def result = tagLib.renderMeasuresAsJSONTree([measures: assay.getRootMeasuresSorted()])

        then:
        result.toString() == expectedJson.replaceAll(/\s/, '')

        where:
        measureLabels   | expectedJson
        ['b', 'a']      | sortedRootMeasuresAB
        ['c', 'B', 'a'] | sortedRootMeasuresABC
    }

    private static String sortedChildMeasuresAB = '''[
                                                        {"key":1,
                                                        "title":"parent",
                                                        "children":[
                                                                    {"key":3,"title":"a","children":[]},
                                                                    {"key":2,"title":"b","children":[]}
                                                                   ]
                                                         }
                                                      ]'''
    private static String sortedChildMeasuresABC = '''[
                                                        {"key":1,
                                                        "title":"parent",
                                                        "children":[
                                                                    {"key":4,"title":"a","children":[]},
                                                                    {"key":3,"title":"B","children":[]},
                                                                    {"key":2,"title":"c","children":[]}
                                                                   ]
                                                         }
                                                      ]'''

    void 'test sorting of child measures in json measurements'() {
        given:
        Measure parent = Measure.build(resultType: Element.build(label: "parent"))

        for (String label in measureLabels) {
            parent.addToChildMeasures(Measure.build(resultType: Element.build(label: label)))
        }


        when: 'we render this as json'
        def result = tagLib.renderMeasuresAsJSONTree([measures: [parent]])

        then:
        result.toString() == expectedJson.replaceAll(/\s/, '')

        where:
        measureLabels   | expectedJson
        ['b', 'a']      | sortedChildMeasuresAB
        ['c', 'B', 'a'] | sortedChildMeasuresABC
    }

}
