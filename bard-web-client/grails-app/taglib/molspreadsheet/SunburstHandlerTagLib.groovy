package molspreadsheet

import bard.core.rest.spring.util.RingNode

class SunburstHandlerTagLib {

    RingManagerService ringManagerService

    def prepMicroSunburst = { attrs, body ->
        // for now we leave in test data.  Remove these when real data come along
        Boolean includeHits = session."actives"
        Boolean includeNonHits = session."inactives"
        RingNode root =   ringManagerService.convertCompoundIntoSunburst (2382353L , includeHits, includeNonHits )
        //       RingNode root =   ringManagerService.convertCompoundSummaryIntoSunburst (attrs.compoundSummary, true, true )
        out << ringManagerService.writeRingTree(root,false)
        out << "\n"
//        out << ringManagerService.placeSunburstOnPage(274,200,root,4)
        out << ringManagerService.colorMappingOnPage ( )
    }


    def makeMicroSunburst = { attrs, body ->
        out << ringManagerService.placeSunburstOnPage(300,200)
    }




    def prepMacroSunburst = { attrs, body ->
        // for now we leave in test data.  Remove these when real data come along
        Boolean includeHits = session."actives"
        Boolean includeNonHits = session."inactives"
        int typeOfColoring = session."colorOption"  ?: 3
        RingNode root =   ringManagerService.convertCompoundIntoSunburst (2382353L , includeHits, includeNonHits )
        LinkedHashMap extremeValues = root.determineColorMappingRange()
        out << ringManagerService.writeRingTree(root,true,typeOfColoring) // writes $data = [...]
        out << "\n"
        out << "var minimumValue=${extremeValues["minimumValue"].toString()};\n"
        out << "var maximumValue=${extremeValues["maximumValue"].toString()};\n"
        out << ringManagerService.colorMappingOnPage ( extremeValues["minimumValue"].toString(),extremeValues["maximumValue"].toString( ) )// js array mapping values to colors
    }


    def makeMacroSunburst = { attrs, body ->
        out << ringManagerService.placeSunburstOnPage(800,800)
    }


    def sunburstSection = { attrs, body ->
//        if (ringManagerService.classDataExistsForThisCompound(attrs.compoundSummary)) {
            out << """
    <div >
        <div id="sunburstdiv">
           <script>""".toString()
            out <<   g.makeMicroSunburst(attrs, body)
            out << """
           </script>
           """
        out <<   g.makeMicroSunburst(attrs, body)
        out << """
        </div>
        <a  href="#" id="sunburstdiv_bigwin"  style="float: right; font-size: 80%">Sunburst visualization<br/>(sample data only)</a>
    </div>""".toString()
//        }
    }



    def bigSunburstData = { attrs, body ->
//        if (ringManagerService.classDataExistsForThisCompound(attrs.compoundSummary)) {
        out << """
            <script>""".toString()
        out <<   g.prepMacroSunburst(attrs, body)
        out << """
           </script>
           """
//        }
    }


    def bigSunburstSection = { attrs, body ->
//        if (ringManagerService.classDataExistsForThisCompound(attrs.compoundSummary)) {
        out << """
        <div id="sunburstdiv">
            """
        out <<   g.makeMacroSunburst(attrs, body)
        out << """
        </div>
""".toString()
//        }
    }




    def sunburstLegend = { attrs, body ->
//        if (ringManagerService.classDataExistsForThisCompound(attrs.compoundSummary)) {
        out << """
<div id="sunburstlegend" class="legendHolder">
    Color assignment:<br />
    x = active / <br />
    (active + inactive)
    <hr width=100% color=black style="color: #000; height:1px;">

    <script>
        createALegend(120, 200,100,continuousColorScale,'div#sunburstlegend');
    </script>

    <div  style="padding-top: 5px;"></div>

</div>
    """.toString()
//        }
    }





//    def bigSunburstSection = { attrs, body ->
////        if (ringManagerService.classDataExistsForThisCompound(attrs.compoundSummary)) {
//        out << """
//    <div class="span9">
//        <div id="sunburstdiv">
//           <script>""".toString()
//        out <<   g.prepMacroSunburst(attrs, body)
//        out << """
//           </script>
//           <script type="text/javascript" src="../js/sunburstPrep.js"></script>
//        </div>
//    </div>""".toString()
////        }
//    }
//



}
