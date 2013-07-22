package modules

import geb.Module
import geb.navigator.Navigator

class LoadingModule extends Module {
	static content = {
		editableFormLoading(wait: true, required: false) { $(".editableform-loading") }
		loading(wait: true, required: false) { $("div#spinner") }
	}
}