package pages

import common.TestData


/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class ViewProjectDefinitionPage extends CapScaffoldPage{
	static url="project/show/"+TestData.projectId
	static at = { $("h2").text().contains("Project") }

	static content = {
	}

}