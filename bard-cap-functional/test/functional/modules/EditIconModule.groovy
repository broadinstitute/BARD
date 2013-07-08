package modules

import geb.Module
import geb.navigator.Navigator

class EditIconModule extends Module {
	static content = {
		editIconPencil { $(".icon-pencil.documentPencil") }
		iconPlus { $(".icon-plus") }
		iconTrash { $(".icon-trash") }
		iconPencil { $(".icon-pencil") }
	}
}