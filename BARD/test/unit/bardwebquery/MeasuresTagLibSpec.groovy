package bardwebquery

import bard.core.rest.spring.assays.Annotation
import bard.core.rest.spring.assays.Context
import bard.core.rest.spring.assays.Measure
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(MeasuresTagLib)
class MeasuresTagLibSpec extends Specification {

    void "test displayMeasures"() {
        given:
        def template = '<g:displayMeasures measures="${measures}" />'
        def top = new Measure(name: "Top", comps: [new Annotation(display:"Top")])
        def child1 = new Measure(name: "Child1", comps: [new Annotation(display:"Child1")])
        child1.setParent(top)
        top.children.add(child1)
        def child2 = new Measure(name: "Child2", comps: [new Annotation(display:"Child2")])
        child2.setParent(top)
        top.children.add(child2)
        def grandchild = new Measure(name: "GrandChild", comps: [new Annotation(display:"GrandChild")])
        grandchild.setParent(child1)
        child1.children.add(grandchild)
        def context1 = new Context(name:"Context1", id:1)
        def context2 = new Context(name:"Context2", id:2)
        child1.relatedContexts = [context1,context2]

        List<Measure> measures = [top, child1, child2, grandchild]

        when:
        String actualResults = applyTemplate(template, [measures: measures])

        then:
        assert actualResults == '<ul><li>Top</li><ul><li>Child1 (linked to contexts: <a href="#card-1">Context1</a>, <a href="#card-2">Context2</a>)</li><ul><li>GrandChild</li></ul><li>Child2</li></ul></ul>'
    }
}