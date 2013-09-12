modules = {
    overrides {
        'jquery-theme' {
            resource id: 'theme', url: '/css/flick/jquery-ui-1.8.20.custom.css'          
        }
        'bootstrap' {
            resource id: 'bootstrap-css', url: '/js/jquery-ui-bootstrap/bootstrap/bootstrap.css'
        }
    }
    core {
        dependsOn 'jquery, jquery-ui, jquery-theme, jquery-validation-ui, datatables, jqueryform'
        resource url: '/css/datatables/demo_table_jui.css'
        resource url: '/js/application.js'
        resource url: "css/bard.css"
        resource url: '/css/dl-horizontal-fix.css'
        resource url: '/js/persona/include.js'
        resource url: 'js/persona/signin.js'
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
    grailspagination {
        resource url: '/css/grailspagination.css'
    }
    dictionaryPage {
        resource url: "js/html5historyapi/history.js"
        resource url: '/js/jquery-table-sorter/jquery.tablesorter.min.js'
        resource url: '/js/jquery-table-sorter/theme.default.css'
    }
    dynatree {
        dependsOn 'jquery, jquery-ui'
        resource url: '/js/dynatree-1.2.2/jquery.dynatree.js'
        resource url: '/js/dynatree-1.2.2/skin/ui.dynatree.css'
    }
    showProjectAssay {
        dependsOn("cardDisplayCSS")
        //Polyfill for handling History
        resource url: "js/html5historyapi/history.js"
        resource url: "js/coreShowProjectAssay.js"
        // Stylesheet for context card view
    }
    jqueryform {
        resource url: '/js/jquery.form.js'
    }
    cardDisplayCSS {
        resource url: "css/card.css"
    }
    assaycards {
        dependsOn('card')
        resource url: '/js/cap/assay.cards.js'
    }
    assayshow {
        dependsOn(['bootstrapplus', 'card', 'sectionCounter'])
        resource url: '/js/dynatree-1.2.2/jquery.dynatree.js'
        resource url: '/js/cap/assay.show.js'
        resource url: '/css/measures-dynatree.css'
    }
    d3Library {
        resource url: "js/lib/d3.min.js"
    }
    sectionCounter {
        resource url: '/css/sectioncounter.css'
    }
    handlebars {
        resource url: "/js/handlebars-1.0.rc.2/handlebars.js"
    }
    dcLibrary {
        resource url: "js/lib/dc.js"
        resource url: "js/lib/crossfilter.js"
        resource url: "css/dc.css"
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
        resource url: '/css/projectstep.css'
    }
    //Adding version allows clients to not cache javascript
    search {
        resource url: "js/search.js"
        resource url: "css/facetDiv.css"
        resource url: "css/searchResults.css"
    }
    idSearch {
        resource url: "js/idSearchDialog.js"
    }
    canEditWidget {
        resource url: "/css/caneditwidget.css"
    }
    compoundOptions {
        resource url: "js/compoundOptions.js"
    }
    assaysummary {
        resource url: '/js/cap/editSummary.js'
    }
    structureSearch {
        resource url: "js/structureSearchDialog.js"
    }
    createProject {
        resource url: '/js/cap/createProject.js'
    }
    autocomplete {
        resource url: "js/autocomplete.js"
    }
    projectsummary {
        dependsOn('card')
        resource url: '/js/cap/editProjectSummary.js'
    }
    cart {
        resource url: "js/cart.js", disposition: 'head'
        resource url: "css/cart.css"
    }
    experimentsummary {
        resource url: '/js/cap/editExperimentSummary.js'
    }
    promiscuity {
        resource url: "js/promiscuity.js"
        resource url: "css/promiscuity.css"
    }
    substances {
        resource url: "js/substances.js"
    }
    select2 {
        dependsOn 'jquery'
        resource url: "/js/select2-release-3.2/select2.css"
        resource url: "/js/select2-release-3.2/select2.js"
    }
    dataTables {
        resource url: "js/DataTables-1.9.4/jquery.dataTables.js"
        resource url: "css/jquery-dataTables.css"
    }
    accessontology {
        resource url: "/js/cap/accessOntology.js"
    }
    molecularSpreadSheet {
        dependsOn "dataTables"
        resource url: "js/molecularSpreadSheet.js"
        resource url: "css/datatables_supplemental.css"
    }
    experimentData {
        //Polyfill for handling History
        resource url: "js/html5historyapi/history.js"
        resource url: "js/experimentalResults.js"
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
    jqueryMobile {
        dependsOn 'jqueryMobilePreInit'
        resource url: "jquery.mobile-1.3.1/jquery.mobile-1.3.1.css"
//        resource url: "jquery.mobile-1.3.1/jquery.mobile-1.3.1.min.css"
        resource url: "jquery.mobile-1.3.1/jquery.mobile.structure-1.3.1.css"
//        resource url: "jquery.mobile-1.3.1/jquery.mobile.structure-1.3.1.min.css"
        resource url: "jquery.mobile-1.3.1/jquery.mobile.theme-1.3.1.css"
//        resource url: "jquery.mobile-1.3.1/jquery.mobile.theme-1.3.1.min.css"
        resource url: "css/bard-mobile.css"
//        resource url: "jquery.mobile-1.3.1/jquery.mobile-1.3.1.min.js"
        resource url: "jquery.mobile-1.3.1/jquery.mobile-1.3.1.js"
        resource url: "js/jqueryMobilePostInit.js"
    }
    jqueryMobilePreInit {
        dependsOn 'jquery, jquery-ui, jquery-theme'
        resource url: "js/jqueryMobilePreInit.js"
    }
    twitterBootstrapAffix {
        resource url: "/css/twitterBootstrapAffix.css"
        resource url: "/js/jquery-ui-bootstrap/bootstrap/js/twitterBootstrapAffix.js"
    }
    jsDrawEditor {
        resource url: "js/jsDraw/jsDrawEditor.js"
        resource url: "css/jsDrawEditor.css"
    }
    addAllItemsToCarts {
        resource url: "js/addAllItemsToCart.js"
    }
    cbas {
        dependsOn 'bootstrap'
        resource url: "css/cbas.css"
    }
    sunburst {
        dependsOn 'bootstrap,jquery,d3Library,dcLibrary'

        resource url: "js/sunburst/linkedVis.js"
        resource url: "js/sunburst/createALegend.js"
        resource url: "js/sunburst/linkedVisualizationModule.js"
        resource url: "js/sunburst/sharedStructures.js"
        resource url: "js/sunburst/createASunburst.js"
        resource url: "css/sunburst.css"
    }
    assaycompare {
        dependsOn 'core,bootstrap,bootstrapplus,card'
        resource url: '/js/cap/assaycompare.js'
    }
    histogram {
        dependsOn 'bootstrap,jquery,d3Library'

        resource url: "js/histogram/experimentalResultsHistogram.js"
        resource url: "css/experimentalResultHistogram.css"
    }
}