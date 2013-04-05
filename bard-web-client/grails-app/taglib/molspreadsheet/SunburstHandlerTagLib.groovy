package molspreadsheet

import bard.core.rest.spring.util.RingNode

class SunburstHandlerTagLib {

    RingManagerService ringManagerService

    def makeSunburst = { attrs, body ->
        RingNode root =  ringManagerService.createStub()
////        out <<   ringManagerService.writeRingTree(root)
////        out <<   "\n"
////        out <<   ringManagerService.defineColors(root)
//        out <<        """ var \$data = [{"name":"/", "children": [
//                        {"name":"nucleic acid binding", "size":1},
//                        {"name":"cytoskeletal protein", "children": [
//                                {"name":"actin family cytoskeletal protein", "children": [
//                                        {"name":"non-motor actin binding protein", "size":1}
//                                ]}
//                        ]},
//                        {"name":"hydrolase", "children": [
//                                {"name":"protease", "children": [
//                                        {"name":"serine protease", "size":1}
//                                ]}
//                        ]},
//                        {"name":"transcription factor", "children": [
//                                {"name":"transcription cofactor"}
//                        ]},
//                        {"name":"receptor"}
//                ]}]
//                  var width = 685,
//                  height = 500,
//                radius = Math.min(width, height) / 2,
//                color = d3.scale.category10().domain(["/",
//"nucleic acid binding",
//"cytoskeletal protein"
// ]);""".toString()



        out <<        """ var \$data = [{"name":"/", "children": [
{"name":"receptor", "children": [
{"name":"G-protein coupled receptor", "size":29},
{"name":"protein kinase receptor", "size":4}
]},
{"name":"transcription factor", "children": [
{"name":"nuclear hormone receptor", "size":6},
{"name":"zinc finger transcription factor", "size":3},
{"name":"transcription cofactor", "size":5},
{"name":"basic helix-loop-helix transcription factor", "size":4}
]},
{"name":"nucleic acid binding", "children": [
{"name":"RNA binding protein", "children": [
{"name":"ribonucleoprotein", "size":1},
{"name":"mRNA processing factor", "children": [
{"name":"mRNA splicing factor", "size":2},
{"name":"mRNA polyadenylation factor"}
]},
{"name":"translation factor", "children": [
{"name":"translation elongation factor"},
{"name":"translation initiation factor"}
]}
]},
{"name":"DNA binding protein", "children": [
{"name":"chromatin/chromatin-binding protein", "size":3},
{"name":"DNA-directed DNA polymerase", "size":3},
{"name":"damaged DNA-binding protein"}
]},
{"name":"nuclease", "children": [
{"name":"exodeoxyribonuclease"},
{"name":"endodeoxyribonuclease"}
]},
{"name":"helicase", "children": [
{"name":"DNA helicase", "size":2}
]}
]},
{"name":"cytoskeletal protein", "children": [
{"name":"microtubule family cytoskeletal protein", "children": [
{"name":"non-motor microtubule binding protein", "size":2}
]},
{"name":"actin family cytoskeletal protein", "children": [
{"name":"non-motor actin binding protein", "size":3},
{"name":"actin and actin related protein", "size":5},
{"name":"actin binding motor protein"}
]},
{"name":"intermediate filament"}
]},
{"name":"ligase", "children": [
{"name":"ubiquitin-protein ligase", "size":6}
]},
{"name":"signaling molecule", "children": [
{"name":"cytokine", "children": [
{"name":"tumor necrosis factor family member", "size":1}
]},
{"name":"membrane-bound signaling molecule"},
{"name":"peptide hormone"},
{"name":"growth factor"}
]},
{"name":"transferase", "children": [
{"name":"kinase", "children": [
{"name":"protein kinase", "children": [
{"name":"non-receptor serine/threonine protein kinase", "size":12},
{"name":"tyrosine protein kinase receptor", "size":4},
{"name":"non-receptor tyrosine protein kinase", "size":7},
{"name":"serine/threonine protein kinase receptor"}
]},
{"name":"carbohydrate kinase", "size":2},
{"name":"nucleotide kinase"}
]},
{"name":"acetyltransferase", "size":7},
{"name":"methyltransferase", "children": [
{"name":"DNA methyltransferase"}
]},
{"name":"nucleotidyltransferase", "size":2},
{"name":"acyltransferase"}
]},
{"name":"cell adhesion molecule", "size":5},
{"name":"storage protein"},
{"name":"hydrolase", "children": [
{"name":"phosphatase", "children": [
{"name":"protein phosphatase", "size":6}
]},
{"name":"protease", "children": [
{"name":"serine protease", "size":12},
{"name":"metalloprotease", "size":2},
{"name":"cysteine protease", "size":3}
]},
{"name":"esterase", "size":3},
{"name":"lipase", "children": [
{"name":"phospholipase", "size":1}
]},
{"name":"phosphodiesterase"},
{"name":"deaminase", "size":2},
{"name":"glucosidase"},
{"name":"galactosidase"},
{"name":"glycosidase"},
{"name":"deacetylase"}
]},
{"name":"membrane traffic protein", "children": [
{"name":"membrane trafficking regulatory protein"},
{"name":"vesicle coat protein"}
]},
{"name":"chaperone", "children": [
{"name":"Hsp90 family chaperone"},
{"name":"Hsp70 family chaperone"},
{"name":"chaperonin"}
]},
{"name":"lyase", "children": [
{"name":"hydratase"},
{"name":"dehydratase"}
]},
{"name":"oxidoreductase", "children": [
{"name":"oxidase", "size":2},
{"name":"reductase", "size":3},
{"name":"dehydrogenase", "size":5},
{"name":"hydroxylase", "size":1},
{"name":"oxygenase", "size":3}
]},
{"name":"structural protein", "size":1},
{"name":"enzyme modulator", "children": [
{"name":"G-protein modulator", "children": [
{"name":"guanyl-nucleotide exchange factor", "size":1}
]},
{"name":"G-protein", "children": [
{"name":"small GTPase", "size":2},
{"name":"heterotrimeric G-protein"}
]},
{"name":"kinase modulator", "children": [
{"name":"kinase inhibitor"},
{"name":"kinase activator"}
]},
{"name":"protease inhibitor", "size":3}
]},
{"name":"transporter", "children": [
{"name":"ion channel", "children": [
{"name":"voltage-gated ion channel", "children": [
{"name":"voltage-gated sodium channel"},
{"name":"voltage-gated calcium channel"},
{"name":"voltage-gated potassium channel", "size":5}
]},
{"name":"sodium channel"},
{"name":"calcium channel"},
{"name":"potassium channel", "size":5},
{"name":"anion channel"},
{"name":"ligand-gated ion channel", "children": [
{"name":"cyclic nucleotide-gated ion channel"}
]}
]},
{"name":"cation transporter", "size":1},
{"name":"carbohydrate transporter"},
{"name":"ATP-binding cassette (ABC) transporter", "size":1}
]},
{"name":"transfer/carrier protein", "children": [
{"name":"apolipoprotein", "size":1}
]},
{"name":"calcium-binding protein", "children": [
{"name":"intracellular calcium-sensing protein", "children": [
{"name":"calmodulin", "size":1}
]},
{"name":"annexin"}
]},
{"name":"extracellular matrix protein", "children": [
{"name":"extracellular matrix glycoprotein"}
]},
{"name":"isomerase", "children": [
{"name":"epimerase/racemase"}
]},
{"name":"transmembrane receptor regulatory/adaptor protein"},
{"name":"defense/immunity protein", "children": [
{"name":"antibacterial response protein"},
{"name":"complement component"}
]}
]}]
                  var width = 685,
                  height = 500,
                radius = Math.min(width, height) / 2,
                color = d3.scale.category10().domain(["/",
"nucleic acid binding",
"cytoskeletal protein"
 ]);"""
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
