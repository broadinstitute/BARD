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

package bard.taglib

class InPlaceEditTagLib {
    def textInPlaceEdit = { attrs, body ->
        def domId = attrs.id
        def bean = attrs.bean
        def field = attrs.field
        def fieldValue = attrs.bean[field]
        def action = attrs.action
        def params = attrs.params
        if (!action) {
            action = "update${field.capitalize()}"
        }
        def dataUrl = createLink([controller: attrs.controller, action: action])
        def dataOriginalTitle = "Edit ${field}"

        out << "<span href='#' id='widget_${domId}' " +
                "data-type='text' " +
                "data-name='${params.contextClass}' " +
                "data-toggle='manual' " +
                "data-mode='inline' " +
                "data-inputclass='input-xxlarge' "+
                "data-value='${fieldValue.encodeAsHTML()}' " +
                "data-pk='${bean.id}' " +
                "data-url='${dataUrl}' " +
                "data-placeholder='Empty' " +
                "data-original-title='${dataOriginalTitle}' >"
        out << fieldValue.encodeAsHTML()
        out << "</span> <a href='javascript:' class='context-card-edit icon-pencil documentPencil' title='Click to edit' data-id='widget_${domId}'></a>"
// TODO: Commenting out the following javascript because it would not load when we use a template
// r.script() {
//            out << "\$('#widget_${domId}').editable({\n" +
//                    "   params: function(params){  \n" +
//                    "       var p = {id: params.pk, value: params.value};\n"
//
//            // add the extra parameters to the submission
//            if (params) {
//                params.each { k, v ->
//                    out << "p['${k}'] = '${v}';\n"
//                }
//            }
//
//            out << "       return p;\n" +
//                    "   }\n" +
//                    "})\n;\n\n"
//        }
    }
}
