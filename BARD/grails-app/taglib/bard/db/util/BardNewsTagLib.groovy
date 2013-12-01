package bard.db.util

import org.apache.commons.lang.builder.CompareToBuilder

import java.util.regex.Matcher

class BardNewsTagLib {
    def bardNewsItem = { attrs, body ->
        BardNews item = attrs.item

        out << "<table>"
        out << "<tr>"
        out << "<td align=\"left\" style=\"padding-left: 10px;\">${(item?.entryDateUpdated ?: item.datePublished)?.format('MM/dd/yyyy  hh:mm:ss')}</td>"
        out << "<td align=\"right\" style=\"padding-left: 10px;\">${item?.authorName}</td>"
        out << "</tr>"
        out << "<tr>"
        out << """<td colspan="2"><a href="${item?.link?.trim()}">${item.title}</a></td>"""
        out << "</tr>"
        out << "</table>"
    }
}