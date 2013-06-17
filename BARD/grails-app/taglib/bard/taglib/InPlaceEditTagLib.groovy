package bard.taglib

class InPlaceEditTagLib {
    def textInPlaceEdit = { attrs, body ->
        def domId = attrs.id
        def bean = attrs.bean
        def field = attrs.field
        def fieldValue = attrs.bean[field]
        def action = attrs.action
        def params = attrs.params
        if(!action) {
            action = "update${field.capitalize()}"
        }
        def dataUrl = createLink([controller: attrs.controller, action: action])
        def dataOriginalTitle = "Edit ${field}"

        out << "<a href='#' id='${domId}' " +
                "data-type='text' " +
                "data-value='${fieldValue.encodeAsHTML()}' " +
                "data-pk='${bean.id}' " +
                "data-url='${dataUrl}' "+
                "data-placeholder='Empty' " +
                "data-original-title='${dataOriginalTitle}' >"
        out << fieldValue.encodeAsHTML()
        out << "</a><i class=\"icon-pencil\"></i>"

        r.script() {
            out << "\$('#${domId}').editable({\n" +
                    "   mode:'inline', \n" +
                    "   params: function(params){  \n" +
                    "       var p = {id: params.pk, value: params.value};\n"

            // add the extra parameters to the submission
            if(params) {
                params.each { k, v ->
                    out << "p['${k}'] = '${v}';\n"
                }
            }

            out <<  "       return p;\n" +
                    "   }\n" +
                    "})\n; "
        }
    }
}
