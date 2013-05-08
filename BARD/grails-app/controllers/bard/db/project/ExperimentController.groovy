package bard.db.project

import java.io.Serializable;
import java.text.SimpleDateFormat
import bard.db.experiment.Experiment
import bard.db.registration.Assay
import bard.db.experiment.ExperimentService
import bard.db.registration.MeasureTreeService
import grails.converters.JSON
import grails.plugins.springsecurity.Secured

class ExperimentCommand implements Serializable {
	
	
}

@Secured(['isAuthenticated()'])
class ExperimentController {
    ExperimentService experimentService;
    MeasureTreeService measureTreeService

    def create() {
        def assay = Assay.get(params.assayId)

        renderCreate(assay, new Experiment())
    }

    def edit() {
        def experiment = Experiment.get(params.id)

        renderEdit(experiment, experiment.assay)
    }

    def renderEdit(Experiment experiment, Assay assay) {
        renderEditFieldsView("edit", experiment, assay);
    }

    def renderCreate(Assay assay, Experiment experiment) {
        renderEditFieldsView("create", experiment, assay);
    }

    def renderEditFieldsView(String viewName, Experiment experiment, Assay assay) {
        JSON experimentMeasuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experiment, false))
        JSON assayMeasuresAsJsonTree = new JSON(measureTreeService.createMeasureTreeWithSelections(assay, experiment, true))

        render(view: viewName, model: [experiment: experiment, assay: assay, experimentMeasuresAsJsonTree: experimentMeasuresAsJsonTree, assayMeasuresAsJsonTree: assayMeasuresAsJsonTree])
    }

    def update() {
        def experiment = Experiment.get(params.id)
		setEditFormParams(experiment)
        experimentService.updateMeasures(experiment, JSON.parse(params.experimentTree))
        if (!experiment.save(flush: true)) {
            renderEdit(experiment, experiment.assay)
        } else {
            redirect(action: "show", id: experiment.id)
        }
    }

    def save() {
        def assay = Assay.get(params.assayId)

        Experiment experiment = new Experiment()
        experiment.assay = assay
		setEditFormParams(experiment)
        experiment.dateCreated = new Date()
		if(!validateExperiment(experiment)){
			println("validation failed on experiment")
			println("errors:"+experiment.errors)
			renderCreate(assay, experiment)
		}
		else{
			if (!experiment.save(flush: true)) {
				println("errors:"+experiment.errors)
				renderCreate(assay, experiment)
			} else {
				experimentService.updateMeasures(experiment, JSON.parse(params.experimentTree))
				redirect(action: "show", id: experiment.id)
			}
		}       
    }
	
	private boolean validateExperiment(Experiment experiment){
		println "Validating Experiment dates"
		
		Date today = new Date();
		Calendar cal = Calendar.getInstance()
		cal.add(Calendar.YEAR, 1)
		Date oneYearFromToday = cal.getTime()
		if(experiment.holdUntilDate){
			// Checks that the Hold Until Today is before today date
			if(experiment.holdUntilDate.before(today)){
				experiment.errors.reject('experiment.holdUntilDate.incorrectEarlyValue', 'Hold Until Date must be equal or after today')
			}
			// Checks that the Hold Until Today date is not more than 1 year from today
			if(experiment.holdUntilDate.after(oneYearFromToday)){
				experiment.errors.reject('experiment.holdUntilDate.incorrecMoreThan1YearFromToday', 'Hold Until Date is more than 1 year from today')
			}
		}
		if(experiment.runDateFrom && experiment.runDateTo){
			//Checks that Run Date To is not before Run From Date
			if(experiment.runDateFrom.after(experiment.runDateTo)){
				experiment.errors.reject('experiment.holdUntilDate.incorrecRunDateFrom', 'Run Date To cannot be before Run From Date')
			}				
		}
		return !experiment.hasErrors()
	}
	
	private def setEditFormParams(Experiment experiment){		
		experiment.properties["experimentName","description","experimentStatus"] = params
		experiment.holdUntilDate = params.holdUntilDate ? new SimpleDateFormat("MM/dd/yyyy").parse(params.holdUntilDate) : null
		experiment.runDateFrom = params.runDateFrom ? new SimpleDateFormat("MM/dd/yyyy").parse(params.runDateFrom) : null
		experiment.runDateTo = params.runDateTo ? new SimpleDateFormat("MM/dd/yyyy").parse(params.runDateTo) : null


	}

    def show() {
        def experimentInstance = Experiment.get(params.id)
        if (!experimentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'experiment.label', default: 'Experiment'), params.id])
            return
        }

        JSON measuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experimentInstance, false))

        JSON assayMeasuresAsJsonTree = new JSON(measureTreeService.createMeasureTree(experimentInstance.assay, false))

        [instance: experimentInstance, measuresAsJsonTree: measuresAsJsonTree, assayMeasuresAsJsonTree: assayMeasuresAsJsonTree]
    }
}
