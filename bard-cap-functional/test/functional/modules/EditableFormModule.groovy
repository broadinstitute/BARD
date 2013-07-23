package modules

import geb.Module
import geb.navigator.Navigator

class EditableFormModule extends Module {
	static content = {
		editableform(wait: true, required: false) { $("form.form-inline.editableform") }
		editableButtons(wait: true, required: false) { editableform.find("div.editable-buttons") }
		editableInput(wait: true, required: false) { editableform.find("div.editable-input") }
		buttons(wait: true, required: false) { module EditIconModule, editableButtons }
//		submitButton(wait: true, required: false) { editableButtons.find("button", type:"submit")}
//		cancelButton(wait: true, required: false) { editableButtons.find("button", type:"button")}
		selectInput(wait: true, required: false) { editableInput.find(".input-medium")}
		inputField(wait: true, required: false) { editableInput.find("input", type:"text")}
//		errorMessage(wait: true, required: false) { editableform.find("div.editable-error-block.help-block") }
	}
}