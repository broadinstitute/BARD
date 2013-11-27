package bard.db.util

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import grails.buildtestdata.mixin.Build

@Build([BardNews])
@TestFor(BardNewsTagLib)
@Unroll
class BardNewsTagLibUnitSpec extends Specification {

    void "test bardNewsItem"() {
        given:
        BardNews item = new BardNews(
                entryId: 'entryId',
                datePublished: new Date(),
                entryDateUpdated: new Date(),
                title: 'title',
                content: 'content',
                link: 'link',
                authorName: 'John Doe',
                authorEmail: 'noone@nowhere.com',
                authorUri: 'uri',
        )

        when:
        String results = this.tagLib.bardNewsItem([item: item], null)

        then:
        assert results.contains('<table><tr><td>')
    }

    void "test bardNewsItem image #label"() {
        given:
        BardNews item = BardNews.build(content: content)

        when:
        String results = this.tagLib.bardNewsItem([item: item], null)

        then:
        assert results.contains(expectedImage)

        where:
        label                                              | content                                                                                     | expectedImage
        'with width and height'                            | '<img src="image1" width="300" height="300"/>'                                              | 'image1'
        'no width or height'                               | '<img src="image1"/>'                                                                       | 'image1'
        'two image srcs with different widths and heights' | '<img src="image1" width="200" height="200"/> <img src="image2" width="300" height="300"/>' | 'image2'
        'two image srcs flipped'                           | '<img src="image1" width="400" height="400"/> <img src="image2" width="300" height="300"/>' | 'image1'
    }
}
