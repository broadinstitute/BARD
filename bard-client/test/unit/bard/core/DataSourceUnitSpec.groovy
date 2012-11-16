package bard.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DataSourceUnitSpec extends Specification {
    @Shared String name = "name"
    @Shared String version = "v1.0"
    @Shared String url = "http://bard.org"

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors"() {
        when:
        DataSource currentDataSource = dataSource
        then:
        currentDataSource.name == expectedName
        currentDataSource.version == expectedVersion
        currentDataSource.url == expectedUrl
        where:
        label               | dataSource                         | expectedName | expectedVersion | expectedUrl
        "3 arg constructor" | new DataSource(name, version, url) | name         | version         | url
        "2 arg constructor" | new DataSource(name, version)      | name         | version         | null
        "1 arg constructor" | new DataSource(name)               | name         | "*"             | null
    }


}

