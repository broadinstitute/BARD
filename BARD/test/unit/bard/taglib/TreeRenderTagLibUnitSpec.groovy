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

    void 'test rendering json measurements'() {
        given: 'a measurement with a child'
        Measure child = Measure.build(resultType: Element.build(label: "child"))
        Measure parent = Measure.build(resultType: Element.build(label: "parent"), childMeasures: [child] as Set)

        when: 'we render this as json'
        def result = renderMeasuresAsJSONTree([measures:[parent]])

        then:
        result.replace("\n", "") == "[{\"key\":2,\"title\":\"parent\",\"children\":[{\"key\":1,\"title\":\"child\",\"children\":[]}]}]"
    }
}
