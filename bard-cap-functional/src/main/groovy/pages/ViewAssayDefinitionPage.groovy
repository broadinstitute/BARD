package main.groovy.pages

import main.groovy.common.TestData

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class ViewAssayDefinitionPage extends CapScaffoldPage{
	static url="assayDefinition/show/"+TestData.assayId
	static at = { waitFor {$("h4").text().contains("View Assay Definition")}}

	static content = {
		//		editAssayMeasureBtn { $("a.btn", text:"Edit Measures") } // Edit Measure btn
	}

	Map<String, String> getUIAssociatedMeasure(def cardName){
		def contextMeasure = [:]
		if(cardHolders.cardTable){
			cardHolders.cardTable.each{ cards ->
				def cardCaption = cards.find("div.cardTitle").find("p")
				if(cardCaption[0].text().equalsIgnoreCase(cardName) && cardCaption[1]){
					def uiMeasure = cardCaption[1].find("a").text()
					def measure = uiMeasure.takeWhile { it != '('}.trim()
					contextMeasure = ['measure':measure, 'context':cardCaption[0].text()]
				}
			}
		}
		return contextMeasure
	}
}