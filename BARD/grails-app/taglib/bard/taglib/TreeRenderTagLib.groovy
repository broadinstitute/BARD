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

    /*
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
    */

    private Map createTreeFromMeasure(Measure measure) {
        def key = measure.id;
        def title = measure.displayLabel

        def children = []

        for(m in measure.childrenMeasuresSorted) {
            children.add(createTreeFromMeasure(m))
        }

        return [key: key, title: title, children: children];
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

    def renderMeasuresAsJSONTree = { attrs, body ->
        def measures = attrs.measures;
        def children = []

        for(measure in measures) {
            children.add(createTreeFromMeasure(measure))
        }

        out << new JSON(children).toString()
    }

    def dynaTree = { attrs, body ->
        def id = attrs.id
        def measures = attrs.measures;

        out << r.script() {
            out <<  '               $(function(){\n' +
                    '                    $("#'+id+'").dynatree({\n' +
                    '                        onActivate: function(node) {\n' +
                    '                            $(".measure-detail-card").hide(); \n' +
                    '                            $("#measure-details-"+node.data.key).show(); \n' +
                    '                        },\n' +
                    '                        children: '+g.renderMeasuresAsJSONTree([measures:measures],null)+'\n' +
                    '                    });\n' +
                    '                });' +
                    ''
        }
        out << "<div id=\"${id}\"></div>"
    }
}
