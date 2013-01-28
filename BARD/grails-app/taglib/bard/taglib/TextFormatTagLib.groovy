package bard.taglib

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 1/24/13
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
class TextFormatTagLib {
    def renderWithBreaks = { attrs, body ->
        def text = attrs.text;
        def lines = text.split("\n")
        for(line in lines) {
            out << "<p>"
            out << line.encodeAsHTML()
            out << "</p>"
        }
    }
}