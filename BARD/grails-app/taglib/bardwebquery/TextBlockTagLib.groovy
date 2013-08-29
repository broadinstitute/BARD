package bardwebquery

import org.codehaus.groovy.grails.plugins.codecs.HTMLCodec

class TextBlockTagLib {

    /**
     * Takes the text in the body and converts line breaks to paragraphs and href's to links.
     */
    def textBlock = { attrs, body ->
        def txt = body()
        txt?.eachLine { line ->
            if (line) {
                out << "<p>" << linkify(encodeIfRequired(line)) << "</p>"
            }
        }
    }

    protected linkify(str) {
        return str?.replaceAll('([\\S\\+]+://\\S+)', '<a href="$1">$1</a>')
    }

    protected encodeIfRequired(str) {
        return HTMLCodec.shouldEncode() ? str?.encodeAsHTML() : str
    }

}
