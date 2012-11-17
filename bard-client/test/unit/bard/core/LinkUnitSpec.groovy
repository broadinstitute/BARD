package bard.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class LinkUnitSpec extends Specification {
    @Shared String name1 = "name1"
    @Shared String name2 = "name2"
    @Shared boolean isDirectedTrue = true
    @Shared boolean isDirectedFalse = false
    @Shared Link link1 = new Link(new Compound(name1), new Compound(name2), isDirectedTrue)
    @Shared Link link2 = new Link(new Compound(name1), new Compound(name2), isDirectedFalse)
    @Shared Link link3 = new Link(new Compound(name1), new Compound(name2))

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        Link currentLink = link
        then:
        currentLink.source.name == name1
        currentLink.target.name == name2
        currentLink.isDirected() == isDirected
        where:
        label                               | link  | isDirected
        "3 arg ctor With Directed == true"  | link1 | isDirectedTrue
        "3 arg ctor with Directed == false" | link2 | isDirectedFalse
        "2 arg constructor with datasource" | link3 | isDirectedFalse
    }

    void "test Constructors with Exception"() {
        when:
        new Link(source, target)
        then:
        thrown(IllegalArgumentException)
        where:
        label                       | source              | target
        "Target is Null"            | new Compound(name1) | null
        "Source is Null"            | null                | new Compound(name2)
        "Target and Source == null" | null                | null
    }

}

