package bard.db.util

import org.apache.commons.lang.builder.CompareToBuilder

import java.util.regex.Matcher

class BardNewsTagLib {
    def bardNewsItem = { attrs, body ->
        BardNews item = attrs.item

        out << "<p>"
        out << item?.authorName
        out << "<span>&nbsp;&nbsp;-&nbsp;&nbsp;"
        out << (item?.entryDateUpdated ?: item.datePublished)?.format('MMM dd yyyy @ hh:mm a')
        out << "&nbsp;&nbsp;-&nbsp;&nbsp;"
        out << """<a href="${item?.link?.trim()}">${item.title}</a></span>"""
        out << "</p>"
    }
}