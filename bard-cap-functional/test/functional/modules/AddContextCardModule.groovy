package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class AddContextCardModule extends Module {
	static content = {
		inputCardName(wait: true) { $("input#new_card_name") }
		saveBtn { module ButtonsModule, $("div.ui-dialog-buttonset"), buttonName:"Save" }
		cancelBtn { module ButtonsModule, $("div.ui-dialog-buttonset"), buttonName:"Cancel" }
	}
}