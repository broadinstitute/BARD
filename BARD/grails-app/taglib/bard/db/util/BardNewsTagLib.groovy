package bard.db.util

import org.apache.commons.lang.builder.CompareToBuilder

class BardNewsTagLib {
    def bardNewsItem = { attrs, body ->
        BardNews item = attrs.item
        List<String> images = item.content.findAll(/<\s*img[^>]+\/>/)
        List<Img> imgs = []
        images.each { String image ->
            String src = (image =~ /src="([^"]+)"/)[0][1]
            String width = (image =~ /width="(\d+)"/)[0][1]
            String height = (image =~ /height="(\d+)"/)[0][1]
            imgs << new Img(src: src, width: width.toInteger(), height: height.toInteger())
        }
        Img biggestImage = imgs?.max()

        out << "<table>"
        out << "<tr>"
        out << "<td>${item.entryDateUpdated.format('MM/dd/yyyy  hh:mm:ss')}</td>"
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

class Img implements Comparator<Img> {
    String src
    Integer width
    Integer height

    @Override
    int compare(Img first, Img second) {
        return new CompareToBuilder()
                .append((first.width * first.height), (second.width * second.height))
                .toComparison();
    }
}