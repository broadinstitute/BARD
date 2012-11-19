modules = {
    overrides {
        'jquery-theme' {
            resource id:'theme', url:'/css/flick/jquery-ui-1.8.20.custom.css'
        }
    }
    core {
        dependsOn 'jquery, jquery-ui, jquery-theme'
        //resource url:'/css/mobile.css'
        resource url:'/js/application.js'
        resource url:"css/bard.css"

        resource url:'/js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.accentFolding.js'
        resource url:'/js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.html.js'
        resource url:'/js/jquery-ui-extensions/dialog/jquery.ui.dialog.autoReposition.js'

    }
    images {
    }
    //Adding version allows clients to not cache javascript
    search {
        resource url:"js/search.js"
      //  resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
    }
    idSearch {
        resource url:"js/idSearchDialog.js"
    }
    structureSearch {
        resource url:"js/structureSearchDialog.js"
    }
    autocomplete {
        resource url:"js/autocomplete.js"
    }
    cart {
        resource url:"js/cart.js", disposition: 'head'
        resource url:"css/cart.css"
        //resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
    }
    promiscuity {
        resource url:"js/promiscuity.js"
        //resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
        resource url:"css/promiscuity.css"
    }
    activeVrsTested {
        resource url:"js/activeVrsTested.js"
     }
    molecularSpreadSheet {
        resource url:"js/molecularSpreadSheet.js"
        resource url:"js/DataTables-1.9.4/jquery.dataTables.js"
        resource url:"css/jquery-dataTables.css"
        resource url:"css/datatables_supplemental.css"
        //resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
    }
    experimentData {
        resource url:"js/experimentalResults.js"
       // resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?autoUpdate=1&crawling=1&history=1&tracker=trackFunction&state=/&strict=0&wrap=1'
    }
    jqueryMobile {
        resource uri:"js/jquery.mobile-1.2/jquery.mobile.custom.js"
        resource uri:"js/jquery.mobile-1.2/jquery.mobile.custom.min.js"
        resource uri:"css/jquery.mobile-1.2/jquery.mobile.custom.css"
        resource uri:"css/jquery.mobile-1.2/jquery.mobile.custom.min.css"
        resource uri:"css/jquery.mobile-1.2/jquery.mobile.custom.structure.css"
        resource uri:"css/jquery.mobile-1.2/jquery.mobile.custom.structure.min.css"
        resource uri:"css/jquery.mobile-1.2/jquery.mobile.custom.theme.css"
        resource uri:"css/jquery.mobile-1.2/jquery.mobile.custom.theme.min.css"
    }
}