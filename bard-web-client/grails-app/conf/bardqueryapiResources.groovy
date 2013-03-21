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
        //Polyfill for handling History
        resource url: "js/html5historyapi/history.js"
        resource url: "js/coreShowProjectAssay.js"
        // Stylesheet for context card view
        resource url: "css/card.css"
    }
    handlebars {
        resource url: "/js/handlebars-1.0.rc.2/handlebars.js"
    }
    projectstep {
        dependsOn "handlebars"
        resource url: '/js/projectstep/raphael.js'
        resource url: '/js/projectstep/dracula_graffle.js'
        resource url: '/js/projectstep/dracula_graph.js'
        resource url: '/js/projectstep/dracula_algorithms.js'
        resource url: '/js/projectstep/projectstep.show.js'
    }
    //Adding version allows clients to not cache javascript
    search {
        resource url: "js/search.js"
        resource url = "css/facetDiv.css"
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

    molecularSpreadSheet {
        resource url: "js/molecularSpreadSheet.js"
        resource url: "js/DataTables-1.9.4/jquery.dataTables.js"
        resource url: "css/jquery-dataTables.css"
        resource url: "css/datatables_supplemental.css"
    }
    experimentData {
        //Polyfill for handling History
        resource url: "js/html5historyapi/history.js"
        resource url: "js/experimentalResults.js"
    }
    jqueryMobile {
        dependsOn 'jquery, jquery-ui, jquery-theme'

        resource url: "css/jquery.mobile-1.2.0/jquery.mobile.structure-1.2.0.css"
        resource url: "css/jquery.mobile-1.2.0/jquery.mobile.structure-1.2.0.min.css"
        resource url: "css/jquery.mobile-1.2.0/jquery.mobile.theme-1.2.0.css"
        resource url: "css/jquery.mobile-1.2.0/jquery.mobile.theme-1.2.0.min.css"
        resource url: "css/jquery.mobile-1.2.0/jquery.mobile-1.2.0.css"
        resource url: "css/jquery.mobile-1.2.0/jquery.mobile-1.2.0.min.css"
        resource url: "js/jquery.mobile-1.2.0/jquery.mobile-1.2.0.js"
        resource url: "js/jquery.mobile-1.2.0/jquery.mobile-1.2.0.min.js"
        resource url: "css/bard-mobile.css"
    }
    jqueryMobileInit {
        dependsOn('jqueryMobile')
        resource url: "js/jqueryMobileInit.js"
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
}