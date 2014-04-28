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

modules = {
    overrides {
        'jquery-theme' {
            resource id: 'theme', url: '/css/flick/jquery-ui-1.8.20.custom.css'
        }
    }

    core {
        dependsOn 'jquery, jquery-ui, jquery-theme'
        resource url: '/js/application.js'
        resource url: "css/bard.css"

        resource url: '/js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.accentFolding.js'
        resource url: '/js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.html.js'
        resource url: '/js/jquery-ui-extensions/dialog/jquery.ui.dialog.autoReposition.js'

    }
    images {
    }
    dictionaryPage {
        resource url: "js/html5historyapi/history.js"
        resource url: '/js/jquery-table-sorter/jquery.tablesorter.min.js'
        resource url: '/js/jquery-table-sorter/theme.default.css'
    }
    showProjectAssay {
        dependsOn("cardDisplayCSS")
        //Polyfill for handling History
        resource url: "js/html5historyapi/history.js"
        resource url: "js/coreShowProjectAssay.js"
        // Stylesheet for context card view
    }
    cardDisplayCSS {
        resource url: "css/card.css"
    }
    handlebars {
        resource url: "/js/handlebars-1.0.rc.2/handlebars.js"
    }
    d3Library {
        resource url: "js/lib/d3.min.js"
    }
    dcLibrary {
        resource url: "js/lib/dc.js"
        resource url: "js/lib/crossfilter.js"
        resource url: "css/dc.css"
    }
    projectstep {
        dependsOn "handlebars"
        resource url: '/js/projectstep/raphael.js'
        resource url: '/js/projectstep/dracula_graffle.js'
        resource url: '/js/projectstep/dracula_graph.js'
        resource url: '/js/projectstep/dracula_algorithms.js'
        resource url: '/js/projectstep/projectstep.show.js'
        resource url: '/js/projectstep/rcolor.js'
        resource url: '/js/projectstep/viz.js'
        resource url: '/css/projectstep.css'
    }
    //Adding version allows clients to not cache javascript
    search {
        resource url: "js/search.js"
        resource url: "css/facetDiv.css"
        resource url: "css/searchResults.css"
    }
    idSearch {
        resource url: "js/idSearchDialog.js"
    }
    compoundOptions {
        resource url: "js/compoundOptions.js"
    }
    structureSearch {
        resource url: "js/structureSearchDialog.js"
    }
    autocomplete {
        resource url: "js/autocomplete.js"
    }
    cart {
        resource url: "js/cart.js", disposition: 'head'
        resource url: "css/cart.css"
    }
    promiscuity {
        resource url: "js/promiscuity.js"
        resource url: "css/promiscuity.css"
    }
    substances {
        resource url: "js/substances.js"
    }
    dataTables {
        resource url: "js/DataTables-1.9.4/jquery.dataTables.js"
        resource url: "css/jquery-dataTables.css"
    }

    molecularSpreadSheet {
        dependsOn "dataTables"
        resource url: "js/molecularSpreadSheet.js"
        resource url: "css/datatables_supplemental.css"
    }
    experimentData {
        //Polyfill for handling History
        resource url: "js/html5historyapi/history.js"
        resource url: "js/experimentalResults.js"
    }
    jqueryMobile {
        dependsOn 'jqueryMobilePreInit'
        resource url: "jquery.mobile-1.3.1/jquery.mobile-1.3.1.css"
//        resource url: "jquery.mobile-1.3.1/jquery.mobile-1.3.1.min.css"
        resource url: "jquery.mobile-1.3.1/jquery.mobile.structure-1.3.1.css"
//        resource url: "jquery.mobile-1.3.1/jquery.mobile.structure-1.3.1.min.css"
        resource url: "jquery.mobile-1.3.1/jquery.mobile.theme-1.3.1.css"
//        resource url: "jquery.mobile-1.3.1/jquery.mobile.theme-1.3.1.min.css"
        resource url: "css/bard-mobile.css"
//        resource url: "jquery.mobile-1.3.1/jquery.mobile-1.3.1.min.js"
        resource url: "jquery.mobile-1.3.1/jquery.mobile-1.3.1.js"
        resource url: "js/jqueryMobilePostInit.js"
    }
    jqueryMobilePreInit {
        dependsOn 'jquery, jquery-ui, jquery-theme'
        resource url: "js/jqueryMobilePreInit.js"
    }
    twitterBootstrapAffix {
        resource url: "css/twitterBootstrapAffix.css"
        resource url: "js/twitterBootstrapAffix.js"
    }
    jsDrawEditor {
        resource url: "js/jsDraw/jsDrawEditor.js"
        resource url: "css/jsDrawEditor.css"
    }
    addAllItemsToCarts {
        resource url: "js/addAllItemsToCart.js"
    }
    cbas {
        dependsOn 'bootstrap'
        resource url: "css/cbas.css"
    }
    sunburst {
        dependsOn 'bootstrap,jquery,d3Library,dcLibrary'

        resource url: "js/sunburst/linkedVis.js"
        resource url: "js/sunburst/createALegend.js"
        resource url: "js/sunburst/linkedVisualizationModule.js"
        resource url: "js/sunburst/sharedStructures.js"
        resource url: "js/sunburst/createASunburst.js"
        resource url: "css/sunburst.css"
    }
    histogram {
        dependsOn 'bootstrap,jquery,d3Library'

        resource url: "js/histogram/experimentalResultsHistogram.js"
        resource url: "css/experimentalResultHistogram.css"
    }

}
