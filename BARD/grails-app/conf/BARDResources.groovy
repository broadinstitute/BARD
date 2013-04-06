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
		resource url: '/css/dl-horizontal-fix.css'
	}
    contextItem {
        dependsOn('select2')
        resource url:'/js/cap/contextItem.js'
        // adding the card and bootstrap-plus.css to get the current styling
        // but may want to simplify the styling going forward
        resource url:'/css/card.css'
        resource url:'/css/bootstrap-plus.css'
    }
	datatables {
		resource url: '/js/DataTables-1.9.3/media/js/jquery.dataTables.js'
	}
    dynatree {
        dependsOn 'jquery, jquery-ui'
        resource url: '/js/dynatree-1.2.2/jquery.dynatree.js'
        resource url: '/js/dynatree-1.2.2/skin/ui.dynatree.css'
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

    handlebars {
        resource url: "/js/handlebars-1.0.rc.2/handlebars.js"
    }

    projectstep {
        dependsOn "handlebars"
        //resource url: '/js/projectstep/arbor.js'
        //resource url: '/js/projectstep/projectstep.show.arbor.js'
        resource url: '/js/projectstep/projectstep.edit.js'
        resource url: '/js/projectstep/raphael.js'
        resource url: '/js/projectstep/dracula_graffle.js'
        resource url: '/js/projectstep/dracula_graph.js'
        resource url: '/js/projectstep/dracula_algorithms.js'
        resource url: '/js/projectstep/projectstep.show.js'
        resource url: '/js/projectstep/rcolor.js'
        resource url: '/js/projectstep/viz.js'
    }

    summary{
        resource url: '/js/cap/editSummary.js'		
    }

    projectsummary{
        resource url: '/js/cap/editProjectSummary.js'
    }

	images {
	}

    select2 {
        dependsOn 'jquery'
        resource url: "/js/select2-release-3.2/select2.css"
        resource url: "/js/select2-release-3.2/select2.js"
    }

    accessontology {
        resource url: "/js/cap/accessOntology.js"
    }

    richtexteditor{
        resource url: "/js/nicedit/nicEdit.js"
        resource url: "/js/cap/editDocument.js"
        resource url: "/images/nicedit/nicEditorIcons.gif"
    }
}