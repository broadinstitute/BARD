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
        currentDataSource.getURL() == expectedUrl
        where:
        label               | dataSource                         | expectedName | expectedVersion | expectedUrl
        "3 arg constructor" | new DataSource(name, version, url) | name         | version         | url
        "2 arg constructor" | new DataSource(name, version)      | name         | version         | null
        "1 arg constructor" | new DataSource(name)               | name         | "*"             | null
    }

    void "test Constructors with Exception"() {
        when:
        new DataSource(dsname, dsversion, url)
        then:
        thrown(IllegalArgumentException)
        where:
        label                 | dsname | dsversion | url
        "name is null"        | null   | version   | url
        "version is null"     | name   | null      | url
        "name==version==null" | null   | null      | url
    }

    void "test setters"() {
        given:
        DataSource dataSource = new DataSource()
        when:
        dataSource.setDescription("description")
        dataSource.setName(name)
        dataSource.setURL(url)
        dataSource.setVersion(version)

        then:
        assert dataSource.getName() == name
        assert dataSource.getDescription() == "description"
        assert dataSource.getURL() == url
        assert dataSource.getVersion() == version
    }

    void "test hashCode"() {
        given:
        DataSource dataSource = new DataSource(name, version, url)
        when:
        int hashCode = dataSource.hashCode()
        then:
        assert hashCode == 334838
     }

    void "test equals compare same instance"() {
        when:
        boolean bool = dataSource.equals(dataSource)
        then:
        assert bool
        where:
        label               | dataSource
        "Compare to itself" | new DataSource(name, version, url)
    }

    void "test equals #label"() {
        when:
        boolean bool = dataSource.equals(other)
        then:
        assert bool == expectedResults
        where:
        label                                        | dataSource                         | other                            | expectedResults
        "Compare to a different instance, but equal" | new DataSource(name, version, url) | new DataSource(name, "*", url)   | true
        "Compare to a different instance, not equal" | new DataSource(name, version, url) | new DataSource(name, "2.1", url) | false
    }

    void "test equals compare to a different type"() {
        when:
        boolean bool = dataSource.equals("other")
        then:
        assert !bool
        where:
        label                         | dataSource
        "Compare to a different type" | new DataSource(name, version, url)
    }

}

