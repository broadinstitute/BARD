package bard.db.registration.additemwizard

import bard.db.registration.AssayContextService;
import bard.db.dictionary.*;
import bard.db.registration.*;

/**
 * ajaxflow Controller
 *
 * @author ycruz
 * @package AjaxFlow
 */

class AttributeCommand implements Serializable {
	
	Long elementId
	String path
	String assayContextIdValue
	String attributeId
	String attributeLabel
	
	static constraints = {
		attributeId(nullable: false, blank: false)
	}
}
	
class ValueTypeCommand implements Serializable {
	
	String valueTypeOption
	
	static constraints = {
		valueTypeOption(nullable: false, blank: false)
	}
}
	
class FixedValueCommand implements Serializable {
	
	Long valueId
	String attributeElementId
	String valueQualifier
	String valueUnits
	String valueLabel
	
	static constraints = {
		valueId(nullable: false, blank: false)
	}
}

class AddItemWizardController {
    // the pluginManager is used to check if the Grom
    // plugin is available so we can 'Grom' development
    // notifications to the unified notifications daemon
    // (see http://www.grails.org/plugin/grom)
    def pluginManager

    def assayContextService

    // need to clear the session factory because grails includes the hibernate session in the flow.  I think this is
    // a terrible idea, and I don't fully know why grails does it.  It'd be nice if we could at least disable it.
    // but working around it for now.
    // http://davecurryco.blogspot.com/2010/04/grails-from-crypt-webflows.html
    def transient sessionFactory

    /**
     * index method, redirect to the webflow
     * @void
     */
    def index = {
        // Grom a development message
        if (pluginManager.getGrailsPlugin('grom')) "redirecting into the webflow".grom()

        redirect(action: 'pages')
    }

    def addItemWizard(Long assayId, Long assayContextId, String cardSection) {
        println "addItemWizard -> Assay ID: " + assayId
        render(template: "common/ajaxflow", model: [assayId: assayId, assayContextId: assayContextId, path: cardSection])
    }

    /**
     * WebFlow definition
     * @void
     */
    def pagesFlow = {
        // start the flow
        onStart {
            // Grom a development message
            if (pluginManager.getGrailsPlugin('grom')) "entering the WebFlow".grom()

            // define variables in the flow scope which is availabe
            // throughout the complete webflow also have a look at
            // the Flow Scopes section on http://www.grails.org/WebFlow
            //
            // The following flow scope variables are used to generate
            // wizard tabs. Also see common/_tabs.gsp for more information
            flow.page = 0
            flow.pages = [
                    [title: 'Attribute', description: 'Define attribute'],
                    [title: 'Value Type', description: 'Value type'],
                    [title: 'Define Value', description: 'Define value'],
                    [title: 'Review & Confirm', description: 'Review and save your entries'],
                    [title: 'Done', description: 'Suggestions for Next']
            ]
            flow.cancel = true;
            flow.quickSave = true;
			
			flow.assayContextId = params.assayContextId;
			println "params.assayContextId = " + params.assayContextId;
			
            flow.attribute = null;
            flow.valueType = null;
            flow.fixedValue = null;
			
            flow.itemSaved = false;

            println ("flow: "+flow)
			
            success()
        }

        // first wizard page: Asking for attribute
        pageOne {
            render(view: "_page_one")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial: pages/_page_one.gsp".grom()

                flow.page = 1
                success()
            }
			on("cancel").to "closeWizard"
			on("close").to "closeWizard"
            on("next") { AttributeCommand cmd ->
				if(cmd.hasErrors()){
					flow.attribute = cmd
					return error()
				}
				flow.attribute = cmd
				println "calling closure for AttributeCommand ${cmd.dump()}"
				println "(pageOne - next) flow.attribute.attributeId: " + flow.attribute.attributeId
				def attributeElement = Element.get(flow.attribute.attributeId)	
				println "attributeElement object: ${attributeElement.dump()}"
				flow.attribute.attributeLabel = attributeElement.label
				flow.page = 2
				sessionFactory.currentSession.clear()
				success()
				
//                def attributeElement = Element.get(params.attributeId)
//                flow.attributeName = attribute.label
//                flow.attributeId = params.attributeId
//                flow.page = 2
//
//                sessionFactory.currentSession.clear()
//
//                success()
            }.to "pageTwo"

            on("toPageTwo").to "pageTwo"
            on("toPageThree").to "pageThree"
            on("toPageFour").to "pageFour"
            on("toPageFive").to "save"
        }

        // second wizard page: Asking for value type
        pageTwo {
            render(view: "_page_two")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial: pages/_page_two.gsp".grom()

                flow.page = 2
                success()
            }
			on("cancel").to "closeWizard"
			on("close").to "closeWizard"
            on("next") {ValueTypeCommand cmd ->
					if(cmd.hasErrors()){
						flow.valueType = cmd
						return error()
					}
					flow.valueType = cmd
					flow.page = 3
					
					success()	
                
//				flow.valueTypeOption = params.valueTypeOption
//                flow.page = 3
//                success()
            }.to "pageThree"
            on("previous").to "pageOne"
            on("toPageOne").to "pageOne"
            on("toPageThree").to "pageThree"
            on("toPageFour").to "pageFour"
            on("toPageFive") {
                flow.page = 5
            }.to "save"
        }

        // Third wizard page: Asking for value
        pageThree {
            render(view: "_page_three")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial pages/_page_three.gsp".grom()

                flow.page = 3
                success()
            }
			on("cancel").to "closeWizard"
			on("close").to "closeWizard"
            on("next") {FixedValueCommand cmd ->
				if(cmd.hasErrors()){
					flow.fixedValue = cmd
					return error()
				}
				flow.fixedValue = cmd
				def valueElement = Element.get(flow.fixedValue.valueId)
				flow.fixedValue.valueLabel = valueElement.label
				flow.page = 4
				sessionFactory.currentSession.clear()
				success()
                
//				flow.valueId = params.valueId
//                flow.valueName = Element.get(params.valueId).label
//                flow.valueQualifier = params.valueQualifier
//                flow.valueUnits = params.valueUnits
//                flow.page = 4
//
//                sessionFactory.currentSession.clear()
//
//                success()
            }.to "pageFour"
            on("previous").to "pageTwo"
            on("toPageOne").to "pageOne"
            on("toPageTwo").to "pageTwo"
            on("toPageFour").to "pageFour"
            on("toPageFive") {
                flow.page = 5
            }.to "save"
        }

        // four wizard page: Confirmation page. Allows saving attribute / Value pair
        pageFour {
            render(view: "_page_four")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial pages/_page_four.gsp".grom()

                flow.page = 4
                success()
            }
			on("cancel").to "closeWizard"
			on("close").to "closeWizard"
            on("save") {
                // put some logic in here
                flow.page = 5
            }.to "save"
            on("previous").to "pageThree"
            on("toPageOne").to "pageOne"
            on("toPageTwo").to "pageTwo"
            on("toPageThree").to "pageThree"
            on("toPageFive") {
                flow.page = 5
            }.to "save"
        }

        // save action
        save {
            action {
                // here you can validate and save the
                // instances you have created in the
                // ajax flow.
                try {
                    // Grom a development message
                    if (pluginManager.getGrailsPlugin('grom')) ".persisting instances to the database...".grom()

                    // put your bussiness logic in here
                    println "Preparing to start saving"
                    Element attributeElement = Element.get(flow.attribute.attributeId)
                    Element valueElement = Element.get(flow.fixedValue.valueId)
                    AssayContext assayContext = AssayContext.get(flow.assayContextId)
					println "flow.assayContextId: " + flow.assayContextId
					println "AssayContext assayContext: = " + assayContext

//                    def isSaved = assayContextService.saveItemInCard(assayContext, attribute, flow.valueType.valueTypeOption, value)
					def isSaved = assayContextService.saveItemInCard(assayContext, attributeElement, flow.valueType.valueTypeOption, valueElement)
                    sessionFactory.currentSession.flush()
                    sessionFactory.currentSession.clear()
                    if (isSaved) {
                        println "New item was successfully added to the card"
                        flow.itemSaved = true;
//						AssayContext assayContext = AssayContext.get(flow.attribute.assayContextIdValue)
//						Assay assay = assayContext.assay
//						println "Assay ID: " + assay.id + "  Name: " + assay.assayName
                        success()
                    } else {
                        println "ERROR - unable to add item to the card"
                        flow.page = 4
                        error()
                    }

                } catch (Exception e) {
                    // put your error handling logic in
                    // here
                    sessionFactory.currentSession.clear()
                    println "Exception -> " + e
                    flow.page = 4
                    error()
                }
            }
            on("error").to "error"
            on(Exception).to "error"
            on("success").to "finalPage"
        }

        // render errors
        error {
            render(view: "_error")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial pages/_error.gsp".grom()

                // set page to 4 so that the navigation
                // works (it is disabled on the final page)
                flow.page = 4
            }
			on("cancel").to "closeWizard"
			on("close").to "closeWizard"
            on("next").to "save"
            on("previous").to "pageFour"
            on("toPageOne").to "pageOne"
            on("toPageTwo").to "pageTwo"
            on("toPageThree").to "pageThree"
            on("toPageFour").to "pageFour"
            on("toPageFive").to "save"

        }

        // last wizard page
        finalPage {
            render(view: "_final_page")
            onRender {
                // Grom a development message
                if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial pages/_final_page.gsp".grom()

                success()
            }
			on("cancel").to "closeWizard"
			on("close").to "closeWizard"
			on("addAnotherItem").to "addAnotherItem"
        }
		
		// calls the view with code to close the wizard window
		closeWizard {
			render(view: "_close_wizard")
			onRender {
				// Grom a development message
				if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial pages/_final_page.gsp".grom()

				success()
			}
		}
		
		// calls the view to restartthe wizard an add another item
		addAnotherItem {
			render(view: "_add_another_item")
			onRender {
				// Grom a development message
				if (pluginManager.getGrailsPlugin('grom')) ".rendering the partial pages/_final_page.gsp".grom()

				success()
			}
		}
    }
}
