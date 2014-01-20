package main.groovy.modules

import geb.Module
import geb.navigator.Navigator

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/07/07
 */
class EditableFormModule extends Module {
	static content = {
		editableform(wait: true, required: false) { $("form.form-inline.editableform") }
		editableButtons(wait: true, required: false) { editableform.find("div.editable-buttons") }
		editableInput(wait: true, required: false) { editableform.find("div.editable-input") }
		buttons(wait: true, required: false) { module EditIconModule, editableButtons }
		selectInput(wait: true, required: false) { editableInput.find(".input-medium")}
		selectDay(wait: true, required: false) { editableInput.find(".day")}
		selectMonth(wait: true, required: false) { editableInput.find(".month")}
		selectYear(wait: true, required: false) { editableInput.find(".year")}
		inputTextArea(wait: true, required: false) { editableInput.find("textarea")}
		inputField(wait: true, required: false) { editableInput.find("input")}
	}
}