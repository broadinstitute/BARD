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
        resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
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
        resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
    }
    promiscuity {
        resource url:"js/promiscuity.js"
        resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
        resource url:"css/promiscuity.css"
    }
    molecularSpreadSheet {
        resource url:"js/molecularSpreadSheet.js"
        resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
    }
    experimentData {
        resource url:"js/experimentalResults.js"
        resource url:'/js/jquery.address-1.4/jquery.address-1.4.js?state=/'
    }
}