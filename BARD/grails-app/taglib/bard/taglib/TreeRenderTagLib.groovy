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

    /* Render a dynaTree for rendering measures which is configured by the following parameters:
    *
    * measures: the set of root measures to use for the tree
    * editable: if true, will allow drag and drop of node
    * dropCallback: method name to call when a node has been dropped.  Will be passed destNode, sourceNode, hitMode, ui, draggable
    * activateCallback : method name to call when a node has been activated
    *
    * The various callbacks can be specified in the body of the tag.
    */
    def dynaTree = { attrs, body ->
        def id = attrs.id
        def measures = attrs.measures;
        def editable = attrs.editable;
        def activateCallback = attrs.activateCallback;

        def dropCallback = attrs.dropCallback;

        out << r.script() {
            out << body()

            out <<  '$(function(){\n' +
                    ' $("#'+id+'").dynatree({\n' +
                    '  onActivate: function(node) {\n' +
                    '   $(".measure-detail-card").hide(); \n' +
                    '   $("#measure-details-"+node.data.key).show(); \n'
            if(activateCallback) {
                out << activateCallback + "(node);"
            }
            out <<  '  },\n';
            if (dropCallback) {
                out <<  'dnd: { preventVoidMoves: true, \n' +
                        'onDragStart: function(node) { return true; }, \n' +
                        'onDragEnter: function(node, sourceNode) { ' +
                        '  if(node.getParent() == null || node.getParent().getParent() == null) {  ' +
                        '    return ["over","after"];\n' +
                        '  } else { ' +
                        '   return ["over"] ' +
                        '  }}, \n' +
                        'onDrop: function(node, sourceNode, hitMode, ui, draggable) {  '
                out << dropCallback + "(node, sourceNode, hitMode, ui, draggable); \n";
                out <<  ' }\n' +
                        '},'
            }
            out << '  children: '+g.renderMeasuresAsJSONTree([measures:measures],null)+'\n';
            out <<  ' });\n' +
                    '});' +
                    ''
        }
        out << "<div id=\"${id}\"></div>"
    }
}
