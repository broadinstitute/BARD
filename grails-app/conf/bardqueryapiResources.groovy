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
        resource url:"js/search.js?version=1"
    }
    structureSearch {
        resource url:"js/structureSearchDialog.js?version=1"
    }
    autocomplete {
        resource url:"js/autocomplete.js?version=1"
    }
    cart {
        resource url:"js/cart.js?version=1", disposition: 'head'
        resource url:"css/cart.css"
    }
    promiscuity {
        resource url:"js/promiscuity.js?version=1"
        resource url:"css/promiscuity.css"
    }
    molecularSpreadSheet {
        resource url:"js/molecularSpreadSheet.js?version=1"
    }
}