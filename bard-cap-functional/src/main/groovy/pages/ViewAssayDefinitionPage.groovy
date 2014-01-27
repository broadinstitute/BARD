package pages

import common.TestData


/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class ViewAssayDefinitionPage extends CapScaffoldPage{
	static url="assayDefinition/show/"+TestData.assayId
	static at = { waitFor {$("h4").text().contains("View Assay Definition")}}

	static content = {

	}
}