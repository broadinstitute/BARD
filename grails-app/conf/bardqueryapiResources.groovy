modules = {
    overrides {
        'jquery-theme' {
            resource id:'theme', url:'/css/flick/jquery-ui-1.8.20.custom.css'
        }
    }
    core {
        dependsOn 'jquery, jquery-ui, jquery-theme, underscore, backbone'
        resource url:'/css/main.css'
        //resource url:'/css/mobile.css'
        resource url:'/js/application.js'
    }
    images {
    }
    backbone_grid {
        dependsOn 'jquery, jquery-ui, jquery-theme, underscore, backbone'
        resource url:'/css/main.css'
        resource url:'/js/bbgrid.js'
        resource url:'/js/backbone.table.js'
        resource url:'/css/bbgrid.css'
    }
    backbone_ex {
        resource url:'/js/backbone_example.js'
    }
    backbone {
        resource url: '/backbone.js'
    }
    underscore {
        resource url: '/underscore.js'
    }

}