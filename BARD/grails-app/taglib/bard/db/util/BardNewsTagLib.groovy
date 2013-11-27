package bard.db.util

import org.apache.commons.lang.builder.CompareToBuilder

import java.util.regex.Matcher

class BardNewsTagLib {
    def bardNewsItem = { attrs, body ->
        BardNews item = attrs.item
        List<String> images = item.content.findAll(/<\s*img[^>]+\/>/)
        List<Img> imgs = []
        images.each { String image ->
            String src
            Matcher srcMtchr = image =~ /src="([^"]+)"/
            if (srcMtchr) {
                src = srcMtchr?.getAt(0)?.getAt(1)
            }
            String width
            Matcher widthMtchr = image =~ /width="(\d+)"/
            if (widthMtchr) {
                width = widthMtchr?.getAt(0)?.getAt(1)
            }
            String height
            Matcher heightMtchr = image =~ /height="(\d+)"/
            if (heightMtchr) {
                height = heightMtchr?.getAt(0)?.getAt(1)
            }
            imgs << new Img(src: src, width: width?.toInteger(), height: height?.toInteger())
        }
        Img biggestImage = imgs?.max(Img.imgComparator)

        out << "<table>"
        out << "<tr>"
        out << "<td>${(item.entryDateUpdated ?: item.datePublished)?.format('MM/dd/yyyy  hh:mm:ss')}</td>"
        out << "<td>${item.authorName}</td>"
        out << "</tr>"
        out << "<tr>"
        out << """<td colspan="2"><a href="${createLink([controller: "bardNews", action: "show", id: item.id])}">${item.title}</a></td>"""
        out << "</tr>"
        if (biggestImage) {
            out << "<tr>"
            out << """<td colspan="2" align="center"><img src="${biggestImage.src}" width="200" height="200"></td>"""
            out << "</tr>"
        }
        out << "</table>"
    }
}

class Img {
    String src
    Integer width
    Integer height

    static Comparator imgComparator = [compare: { Img a, Img b ->
        Integer aArea = a?.width * a?.height
        Integer bArea = b?.width * b?.height
        aArea.equals(bArea) ? 0 : aArea < bArea ? -1 : 1
    }] as Comparator
}