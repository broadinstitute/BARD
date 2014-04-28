/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package modules

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
