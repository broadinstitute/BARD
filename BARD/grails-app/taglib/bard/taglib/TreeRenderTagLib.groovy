package bard.taglib

import bard.db.registration.AssayContext
import bard.db.registration.Measure
import grails.converters.JSON

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 1/11/13
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
class TreeRenderTagLib {
    /* methods for rendering trees */

    class Link {
        String title;
        String link;
    }

    class Tree {
        String key;
        String title;
        List<Link> links = [];
        String xref;
        List<Tree> children = [];
    }

    private Tree createTreeFromMeasure(Measure measure) {
        Tree tree = new Tree();
        tree.key = measure.id;
        tree.title = measure.resultType.label;
        if(measure.statsModifier != null) {
            tree.title += " (" + measure.statsModifier.label + ")"
        }

        for(assayContextMeasure in measure.assayContextMeasures) {
            AssayContext context = assayContextMeasure.assayContext;

            tree.links.add(new Link(title: context.contextName, link: "#card-"+context.id))
        }

        for(m in measure.childMeasures) {
            tree.children.add(createTreeFromMeasure(m))
        }

        return tree;
    }

    def recursiveRenderTree(out, tag, children) {
        out << "<"+tag+">";

        for( child in children ) {
            List linkStrings = child.links.collect { "<a href=\"${it.link}\">${it.title.encodeAsHTML()}</a>" }

            out << "<li>" << child.title.encodeAsHTML() << " " << linkStrings.join(", ")
            if(child.children.size() > 0) {
                recursiveRenderTree(out, tag, child.children)
            }
            out << "</li>\n"
        }

        out << "</"+tag+">";
    }

    /** Render a set of measures as unordered list */
    def renderMeasuresAsTree = { attrs, body ->
        def measures = attrs.measures;
        def children = []

        for(measure in measures) {
            children.add(createTreeFromMeasure(measure))
        }

        recursiveRenderTree(out, "ul", children);
    }
}
