modules = {
	overrides {
		'jquery-theme' {
			resource id:'theme', url:'/css/flick/jquery-ui-1.8.20.custom.css'
		}
	}
	core {
		dependsOn 'jquery, jquery-ui, jquery-theme, datatables, jqueryform'
		//resource url:'/css/main.css'
		//resource url:'/css/mobile.css'
		resource url:'/css/datatables/demo_table_jui.css'
		resource url:'/js/application.js'
	}
	datatables {
		resource url: '/js/DataTables-1.9.3/media/js/jquery.dataTables.js'
	}
	jqueryform {
		resource url: '/js/jquery.form.js'
	}
	images {
	}
}