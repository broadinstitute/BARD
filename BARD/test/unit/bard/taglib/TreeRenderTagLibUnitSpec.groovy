package bard.taglib

import bard.db.dictionary.Element
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextMeasure
import bard.db.registration.Measure
import grails.buildtestdata.mixin.Build
import grails.plugin.spock.TagLibSpec
import org.codehaus.groovy.grails.plugins.codecs.HTMLCodec

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 1/11/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Element, Measure, AssayContext, AssayContextMeasure])
class TreeRenderTagLibUnitSpec extends TagLibSpec {
    void setup() {
        // needed because taglib tests don't load codecs and so encodeAsHTML is missing
        loadCodec(HTMLCodec)
    }

    void 'test rendering nested measurements'() {
        given: 'a measurement with a child'
        Measure child = Measure.build(resultType: Element.build(label: "child"))
        Measure parent = Measure.build(resultType: Element.build(label: "parent"), childMeasures: [child] as Set)

        when: 'we render this as html'
        def result = renderMeasuresAsTree([measures:[parent]])

        then:
        result.replace("\n", "") == "<ul><li>parent <ul><li>child </li></ul></li></ul>"
    }

    void 'test rendering multiple links'() {
        given: 'a measurement associated with two AssayContexts'
        Measure measure = Measure.build(resultType: Element.build(label: "label"))
        AssayContext ac1 = AssayContext.build(contextName: "context1", id: 1)
        AssayContext ac2 = AssayContext.build(contextName: "context2", id: 2)
        measure.assayContextMeasures = [
                AssayContextMeasure.build(assayContext: ac1, measure: measure),
                AssayContextMeasure.build(assayContext: ac2, measure: measure) ] as Set

        when:
        def result = renderMeasuresAsTree([measures:[measure]])

        then:
        result.replace("\n", "") == "<ul><li>label <a href=\"#card-1\">context1</a>, <a href=\"#card-2\">context2</a></li></ul>"
    }
}
