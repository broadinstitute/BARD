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

package pages

import modules.ButtonsModule
import modules.ErrorInlineModule



/**
 * @author Muhammad.Rafique
 * Date Created: 13/02/07
 * Date Updated: 13/10/30
 */
class DocumentPage extends CommonFunctionalPage{
	static url=""
	static at =  { waitFor{$("#documentType")} }

	static content = {
		documentName { $("#documentName") }
		documentContent() { $("#documentContent") }
		//		documentURL { $("input#documentContent", type:"url") }
		cancelBtn { module ButtonsModule, $("div.control-group"), buttonName:"Cancel" }
		createBtn { module ButtonsModule, $("div.control-group"), buttonName:"Create" }
		updateBtn { module ButtonsModule, $("div.control-group"), buttonName:"Update" }
		validations { module ErrorInlineModule }

	}

	def createDocument(def name, def content, boolean isUpdate = false){
		def validationErrorMessage = "Name is a required field"
		assert documentName
		//		assert createBtn.inputSubmitButton
		assert cancelBtn.button
		assert documentContent
		if(name==""){
			fillDocumentForm(name, content, isUpdate)
//			assert validationError(validations.helpInline, validationErrorMessage)
			cancelBtn.button.click()
		}else{
			fillDocumentForm(name, content, isUpdate)
		}
	}

	def fillDocumentForm(def name, def content, def urlType=false, def isUpdate){
		documentName.value(name)
		documentContent.value(content)
		if(isUpdate){
			updateBtn.inputSubmitButton.click()
		}else{
			createBtn.inputSubmitButton.click()
		}
	}
}
