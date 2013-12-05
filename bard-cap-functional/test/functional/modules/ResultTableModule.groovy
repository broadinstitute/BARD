package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class ResultTableModule extends Module {
	static content = {
		resultList(wait: true, required:false) { $("ul li") }
		result(wait: true, required:false) {value-> $("ul li div", text:contains(value)) }
		deleteResultType(wait: true, required:false) {value-> result(value).next().find(".icon-trash.deleteMeasuresIcon") }
		editResultType(wait: true, required:false) {value-> result(value).next().find(".icon-pencil.editMeasuresIcon") }
		trashIcon(wait: true, required:false) { resultList.find(".icon-trash.deleteMeasuresIcon") }
	}
}