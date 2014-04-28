/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
