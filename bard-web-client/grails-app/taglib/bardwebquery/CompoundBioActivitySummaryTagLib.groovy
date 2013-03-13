package bardwebquery

class CompoundBioActivitySummaryTagLib {
    def assayDescription = { attrs, body ->

        out << "<p>" << attrs.name << "</p>"
    }

    def projectDescription = { attrs, body ->

        out << "<p>" << attrs.name << "</p>"
    }

    def experimentDescription = { attrs, body ->

        out << "<p>" << attrs.name << "</p>"
    }
}
