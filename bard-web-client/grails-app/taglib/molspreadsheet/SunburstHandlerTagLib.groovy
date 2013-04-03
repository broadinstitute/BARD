package molspreadsheet

class SunburstHandlerTagLib {

    RingManagerService ringManagerService

    def makeSunburst = { attrs, body ->
        RingNode root =  ringManagerService.createStub()
//        out <<   ringManagerService.writeRingTree(root)
//        out <<   "\n"
//        out <<   ringManagerService.defineColors(root)
        out <<        """ var \$data = [{"name":"/", "children": [
                        {"name":"nucleic acid binding", "size":1},
                        {"name":"cytoskeletal protein", "children": [
                                {"name":"actin family cytoskeletal protein", "children": [
                                        {"name":"non-motor actin binding protein", "size":1}
                                ]}
                        ]},
                        {"name":"hydrolase", "children": [
                                {"name":"protease", "children": [
                                        {"name":"serine protease", "size":1}
                                ]}
                        ]},
                        {"name":"transcription factor", "children": [
                                {"name":"transcription cofactor"}
                        ]},
                        {"name":"receptor"}
                ]}]
                  var width = 685,
                  height = 500,
                radius = Math.min(width, height) / 2,
                color = d3.scale.category10().domain(["/",
"nucleic acid binding",
"cytoskeletal protein"
 ]);""".toString()
    }


    def sunburstSection = { attrs, body ->
        if (ringManagerService.classDataExistsForThisCompound(attrs.compoundSummary)) {
            out << """
    <div class="span9">
        <div id="sunburstdiv">
           <script>""".toString()
            out <<   g.makeSunburst(attrs, body)
            out << """
           </script>
           <script type="text/javascript" src="../../js/sunburstPrep.js"></script>
        </div>
    </div>""".toString()
        }
    }



}
