package molspreadsheet

import bard.core.rest.spring.util.RingNode

class SunburstHandlerTagLib {

    RingManagerService ringManagerService

    def makeMicroSunburst = { attrs, body ->
        // for now we leave in test data.  Remove these when real data come along
        Boolean includeHits = session."actives"
        Boolean includeNonHits = session."inactives"
        RingNode root =   ringManagerService.convertCompoundIntoSunburst (2382353L , includeHits, includeNonHits )
        //       RingNode root =   ringManagerService.convertCompoundSummaryIntoSunburst (attrs.compoundSummary, true, true )
        out << ringManagerService.writeRingTree(root,false)
        out << "\n"
        out << ringManagerService.placeSunburstOnPage(274,200,root)
    }

    def makeMacroSunburst = { attrs, body ->
        // for now we leave in test data.  Remove these when real data come along
        Boolean includeHits = session."actives"
        Boolean includeNonHits = session."inactives"
        RingNode root =   ringManagerService.convertCompoundIntoSunburst (2382353L , includeHits, includeNonHits )
        //       RingNode root =   ringManagerService.convertCompoundSummaryIntoSunburst (attrs.compoundSummary, true, true )
        out << ringManagerService.writeRingTree(root,true)
        out << "\n"
        out << ringManagerService.placeSunburstOnPage(1000,800,root)
    }


    def sunburstSection = { attrs, body ->
//        if (ringManagerService.classDataExistsForThisCompound(attrs.compoundSummary)) {
            out << """
    <div class="span9">
        <div id="sunburstdiv">
           <script>""".toString()
            out <<   g.makeMicroSunburst(attrs, body)
            out << """
           </script>
           <script type="text/javascript" src="../../js/sunburstPrep.js"></script>
        </div>
        <a href="#" id="sunburstdiv_bigwin">View in big window</a>
    </div>""".toString()
//        }
    }



    def bigSunburstSection = { attrs, body ->
//        if (ringManagerService.classDataExistsForThisCompound(attrs.compoundSummary)) {
        out << """
    <div class="span9">
        <div id="sunburstdiv">
           <script>""".toString()
        out <<   g.makeMacroSunburst(attrs, body)
        out << """
           </script>
           <script type="text/javascript" src="../js/sunburstPrep.js"></script>
        </div>
    </div>""".toString()
//        }
    }




}
