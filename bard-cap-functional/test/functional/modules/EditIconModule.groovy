package modules

import geb.Module
import geb.navigator.Navigator

class EditIconModule extends Module {
	static content = {
		editIconPencil(wait: true, required: false) { $(".icon-pencil.documentPencil") }
		iconPlus(wait: true, required: false) { $(".icon-plus") }
		iconTrash(wait: true, required: false) { $(".icon-trash") }
		iconPencil(wait: true, required: false) { $(".icon-pencil") }
		iconOk(wait: true, required: false) { $(".icon-ok.icon-white") }
		iconRemove(wait: true, required: false) { $(".icon-remove") }
	}
}