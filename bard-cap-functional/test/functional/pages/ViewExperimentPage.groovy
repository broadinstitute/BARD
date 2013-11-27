package pages

import modules.AddLinkExperimentModule
import modules.ButtonsModule
import common.Constants
import common.TestData;

/**
 * this is show experiment page class, most of the contents are inherited from CapScaffoldPage class to reuse.
 * @author Muhammad.Rafique
 * Date Created: 2013/11/25
 */
class ViewExperimentPage extends CapScaffoldPage{
	static url="experiment/show/"+TestData.experimentId
	static at = { waitFor(Constants.WAIT_INTERVAL, Constants.R_INTERVAL){	$("h4").text().contains("View Experiment")} }

	static content = {
	}

}