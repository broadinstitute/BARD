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
}
