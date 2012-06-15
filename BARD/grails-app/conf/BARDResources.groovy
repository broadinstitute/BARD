modules = {
	overrides {
		'jquery-theme' {
			resource id:'theme', url:'/css/flick/jquery-ui-1.8.20.custom.css'
		}
	}
	core {
		dependsOn 'jquery, jquery-ui, jquery-theme'
		//resource url:'/css/main.css'
		//resource url:'/css/mobile.css'
		resource url:'/js/application.js'
	}
	images {
	}
}