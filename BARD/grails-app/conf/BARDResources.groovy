modules = {
	overrides {
		'jquery-theme' {
			resource id:'theme', url:'/css/flick/jquery-ui-1.8.20.custom.css'
		}
	}
	core {
		dependsOn 'jquery, jquery-ui, jquery-theme, underscore, backbone'
		//resource url:'/css/main.css'
		//resource url:'/css/mobile.css'
		resource url:'/js/application.js'
		resource url:'/backbone_example.js'
		//resource url: '/modules/assay_cards.js'
	}
	
	backbone {
		resource url: '/js/backbone.js'
	}
	
	underscore {
		resource url: '/js/underscore.js'
	}
	
	images {
		
	}
}