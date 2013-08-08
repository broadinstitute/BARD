modules = {
//	overrides {
//		'jquery-theme' {
//			resource id:'theme', url:'/css/flick/jquery-ui-1.8.20.custom.css'
//		}
//	}
    overrides {
        'jquery-theme' {
            resource id: 'theme', url: '/js/jquery-ui-bootstrap/css/custom-theme/jquery-ui-1.8.16.custom.css'
        }
        'bootstrap' {
            resource id: 'bootstrap-css', url: '/js/jquery-ui-bootstrap/bootstrap/bootstrap.css'
        }
    }

    core {
        dependsOn 'jquery, jquery-ui, jquery-theme, jquery-validation-ui, datatables, jqueryform'
        resource url: '/css/datatables/demo_table_jui.css'
        resource url: '/js/application.js'
        resource url: '/css/dl-horizontal-fix.css'
    }
    xeditable {
        resource url: "/js/x-editable/bootstrap-editable.js"
        resource url: "/css/x-editable/bootstrap-editable.css"
        resource url: "/js/x-editable/moment.js"
        resource url: "/js/x-editable/combodate.js"
    }
    bootstrapplus {
        resource url: '/css/bootstrap-plus.css'
    }
    card {
        resource url: '/css/card.css'
    }
    addItem {
        dependsOn(['bootstrapplus', 'card'])

        resource url: '/css/AddItemWizard.css'
    }
    contextItem {
        dependsOn(['select2', 'bootstrapplus', 'card'])
        resource url: '/js/cap/contextItem.js'
    }
    newTerm {
        resource url: '/css/newterm/newTerms.css'
        resource url: '/js/element/newTerm.js'

    }
    datatables {
        resource url: '/js/DataTables-1.9.3/media/js/jquery.dataTables.js'
    }
    grailspagination{
        resource url:'/css/grailspagination.css'
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
        dependsOn('card')
        resource url: '/js/cap/assay.cards.js'
    }

    assayshow {
        dependsOn(['bootstrapplus', 'card'])
        resource url: '/js/dynatree-1.2.2/jquery.dynatree.js'
        resource url: '/js/cap/assay.show.js'
        resource url: '/css/measures-dynatree.css'
        resource url: '/css/sectioncounter.css'
    }

    handlebars {
        resource url: "/js/handlebars-1.0.rc.2/handlebars.js"
    }

    projectstep {
        dependsOn "handlebars"
        resource url: '/js/projectstep/projectstep.edit.js'
        resource url: '/js/projectstep/raphael.js'
        resource url: '/js/projectstep/dracula_graffle.js'
        resource url: '/js/projectstep/dracula_graph.js'
        resource url: '/js/projectstep/dracula_algorithms.js'
        resource url: '/js/projectstep/projectstep.show.js'
        resource url: '/js/projectstep/rcolor.js'
        resource url: '/js/projectstep/viz.js'
        resource url: '/js/projectstep/projectstep.css'
    }
    canEditWidget {
        resource url: "/css/caneditwidget.css"
    }
    assaysummary {
        resource url: '/js/cap/editSummary.js'
    }
    createProject{
        resource url: '/js/cap/createProject.js'
    }
    projectsummary {
        dependsOn('card')
        resource url: '/js/cap/editProjectSummary.js'

    }
    experimentsummary {
        resource url: '/js/cap/editExperimentSummary.js'
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

    richtexteditor {
        resource url: "/css/richtexteditor.css"
        resource url: "/js/nicedit/nicEdit.js"
        resource url: "/images/nicedit/nicEditorIcons.gif"
    }
    richtexteditorForCreate {
        dependsOn 'richtexteditor'
        resource url: "/js/cap/createDocument.js"
    }
    richtexteditorForEdit {
        dependsOn 'richtexteditor'
        resource url: "/js/cap/editDocument.js"
    }
    twitterBootstrapAffix {
        resource url: "/css/twitterBootstrapAffix.css"
        resource url: "/js/jquery-ui-bootstrap/bootstrap/js/twitterBootstrapAffix.js"
    }
    assaycompare{
        dependsOn 'core,bootstrap,bootstrapplus,card'
        resource url:'/js/cap/assaycompare.js'
    }
}