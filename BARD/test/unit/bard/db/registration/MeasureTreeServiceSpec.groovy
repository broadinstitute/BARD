package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.AssayContextExperimentMeasure
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(MeasureTreeService)
@Build([Assay, Element, ExperimentMeasure, Experiment,AssayContext, AssayContextExperimentMeasure])
@Mock([Assay, Element, ExperimentMeasure,Experiment, AssayContext, AssayContextExperimentMeasure])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class MeasureTreeServiceSpec extends Specification {

    void 'test rendering json measurements'() {
        given: 'a measurement with a child'
        ExperimentMeasure child = ExperimentMeasure.build(resultType: Element.build(label: "child"))
        ExperimentMeasure parent = ExperimentMeasure.build(resultType: Element.build(label: "parent"), childMeasures: [child] as Set)
        child.parent = parent
        Assay assay = Assay.build()
        Experiment experiment = Experiment.build(assay: assay, experimentMeasures: [parent, child] as Set)
        when: 'we render this as json'
        List result = service.createMeasureTree(experiment, false)

        then:
        result == [[key:2, title:"parent", children:[[key:1, title:"child", children:[], expand:true, relationship:null,measureId:1]], expand:true, relationship:null, measureId:2]]
    }

    private static String sortedRootMeasuresAB = '''[
                                                    [key:2, title:a, children:[], expand:true,relationship:null,measureId:2],
                                                    [key:1, title:b, children:[], expand:true,relationship:null,measureId:1]
                                                   ]'''
    private static String sortedRootMeasuresABC = '''[
                                                       [key:3, title:a, children:[], expand:true,relationship:null,measureId:3],
                                                       [key:2, title:B, children:[], expand:true,relationship:null,measureId:2],
                                                       [key:1, title:c, children:[], expand:true,relationship:null,measureId:1]
                                                      ]'''


    void 'test sorting of root measures in json measurements'() {
        given: 'a measurement with a child'
        Assay assay = Assay.build()
        Experiment experiment = Experiment.build(assay:assay)
        for (String label in measureLabels) {
            experiment.addToExperimentMeasures(ExperimentMeasure.build(resultType: Element.build(label: label)))
        }

        when: 'we render this as json'
        def result = service.createMeasureTree(experiment, false)

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
                                                                    [key:3,title:a,children:[], expand:true,relationship:null,measureId:3],
                                                                    [key:2,title:b,children:[], expand:true,relationship:null, measureId:2]
                                                                   ]   , expand:true,relationship:null, measureId:1
                                                         ]
                                                      ]'''
    private static String sortedChildMeasuresABC = '''[
                                                        [key:1,
                                                        title:parent,
                                                        children:[
                                                                    [key:4,title:a,children:[], expand:true,relationship:null,measureId:4],
                                                                    [key:3,title:B,children:[], expand:true,relationship:null,measureId:3],
                                                                    [key:2,title:c,children:[], expand:true,relationship:null,measureId:2]
                                                                   ]     , expand:true,relationship:null,measureId:1
                                                         ]
                                                      ]'''

    void 'test sorting of child measures in json measurements #measureLabels'() {
        given:
        ExperimentMeasure parent = ExperimentMeasure.build(resultType: Element.build(label: "parent"))
        Assay assay = Assay.build()
        Experiment experiment = Experiment.build(assay: assay)
        for (String label in measureLabels) {
            parent.addToChildMeasures(ExperimentMeasure.build(resultType: Element.build(label: label)))
        }
        experiment.addToExperimentMeasures(parent)


        when: 'we render this as json'
        def result = service.createMeasureTree(experiment, false)

        then:
        result.toString().replaceAll(/\s/, '') == expectedJson.replaceAll(/\s/, '')

        where:
        measureLabels   | expectedJson
        ['b', 'a']      | sortedChildMeasuresAB
        ['c', 'B', 'a'] | sortedChildMeasuresABC
    }
}
