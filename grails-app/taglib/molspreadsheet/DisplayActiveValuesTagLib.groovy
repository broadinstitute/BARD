package molspreadsheet

class DisplayActiveValuesTagLib {


        /**
         * Takes the text in the body and converts line breaks to paragraphs and href's to links.
         */
    def activeHeader = { attrs, body ->
        def txt = body()
        txt?.eachLine { line ->
            if (line instanceof  HillCurveValueHolder )
            if (line) {
                if (line instanceof  HillCurveValueHolder )
                out << "<h1>" << HillCurveValueHolder << "</h1>"
            }
        }
    }

    def activeContent = { attrs, body ->
        def txt = body()
        txt?.eachLine { line ->
            if (line) {
                out << "<h1>" << line << "</h1>"
            }
        }
    }

//    HillCurveValueHolder hillCurveValueHolder = body()


}
