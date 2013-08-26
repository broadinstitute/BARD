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
