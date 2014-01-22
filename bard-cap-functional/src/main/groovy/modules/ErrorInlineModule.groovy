package modules

import geb.Module
import geb.Page
import geb.navigator.Navigator

class ErrorInlineModule extends Module {
	def searchBtns
	static content = {
		cotrolGroup(wait: true, required: false) { $("div.control-group.error") }
		helpInline(wait: true, required: false) { cotrolGroup.find(".help-inline") }
		helpBlock(wait: true, required: false) { cotrolGroup.find(".editable-error-block.help-block") }
		alertError(wait: true, required: false) { $("div.alert.alert-error") }
	}
}