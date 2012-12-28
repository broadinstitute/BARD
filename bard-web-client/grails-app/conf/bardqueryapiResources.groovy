modules = {
    overrides {
        'jquery-theme' {
            resource id: 'theme', url: '/css/flick/jquery-ui-1.8.20.custom.css'
        }
    }
    core {
        dependsOn 'jquery, jquery-ui, jquery-theme'
        //resource url:'/css/mobile.css'
        resource url: '/js/application.js'
        resource url: "css/bard.css"

        resource url: '/js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.accentFolding.js'
        resource url: '/js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.html.js'
        resource url: '/js/jquery-ui-extensions/dialog/jquery.ui.dialog.autoReposition.js'

    }
    images {
    }
    showAssay {
        resource url:"js/html5historyapi/history.js"
        resource url:"js/coreShowProjectAssay.js"
    }

    projects {
        resource url:"js/html5historyapi/history.js"
        resource url: "js/coreShowProjectAssay.js"
    }
    //Adding version allows clients to not cache javascript
    search {
        resource url: "js/search.js"
        resource url = "css/facetDiv.css"
        //  resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
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
        //resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
    }
    promiscuity {
        resource url: "js/promiscuity.js"
        //resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
        resource url: "css/promiscuity.css"
    }
    substances {
        resource url: "js/substances.js"
    }
//    activeVrsTested {
//        resource url:"js/activeVrsTested.js"
//     }
    molecularSpreadSheet {
        resource url: "js/molecularSpreadSheet.js"
        resource url: "js/DataTables-1.9.4/jquery.dataTables.js"
        resource url: "css/jquery-dataTables.css"
        resource url: "css/datatables_supplemental.css"
        //resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
    }
    experimentData {
        resource url: "js/experimentalResults.js"
        // resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?autoUpdate=1&crawling=1&history=1&tracker=trackFunction&state=/&strict=0&wrap=1'
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
        resource url: "js/jqueryMobileInit.js"
    }
}