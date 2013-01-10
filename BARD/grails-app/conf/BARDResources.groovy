modules = {
//	overrides {
//		'jquery-theme' {
//			resource id:'theme', url:'/css/flick/jquery-ui-1.8.20.custom.css'
//		}
//	}
	overrides {
		'jquery-theme' {
			resource id:'theme', url:'/js/jquery-ui-bootstrap/css/custom-theme/jquery-ui-1.8.16.custom.css'
		}
		'bootstrap' {
			resource id:'bootstrap-css', url:'/js/jquery-ui-bootstrap/bootstrap/bootstrap.css'
		}
	}
	core {
		dependsOn 'jquery, jquery-ui, jquery-theme, jquery-validation-ui, datatables, jqueryform'
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
	assaycards {
		resource url: '/js/cap/assay.cards.js'
	}
	assayshow {
		resource url: '/js/cap/assay.show.js'
	}
    projectstep {
        resource url: '/js/projectstep/arbor.js'
        resource url: '/js/projectstep/projectstep.show.js'
        resource url: '/js/projectstep/projectstep.edit.js'
    }
	images {
	}
}