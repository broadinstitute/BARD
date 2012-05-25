package barddataexport.experiment

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

class ResultExportServiceIntegrationTests {
    ResultExportService resultExportService

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testGenerateResult() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        final BigDecimal experimentId = 1

        resultExportService.generateResult(sql, xml, experimentId)
        println writer.toString()
    }

    @Test
    void testGenerateResults() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        final BigDecimal experimentId = 1

        resultExportService.generateResults(sql, xml, experimentId, 600)


        def file = new File("resultsFull.xml")
        file.write(writer.toString())
        println "******************Done*****************"
    }

    @Test
    void testGenerateResultStatus() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)

        resultExportService.generateResultStatus(sql, xml, 2)

        println writer.toString()
    }

    @Test
    void testGenerateResultContextItem() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        resultExportService.generateResultContextItem(sql, xml, 1)
        println writer.toString()
    }

    @Test
    void testGenerateResultHierarchy() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        resultExportService.generateResultHierarchy(sql, xml, 41)
        println writer.toString()
    }

    @Test
    void testGenerateResultLinks() {
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        resultExportService.generateResultLinks(xml, 561, 382, "http:/test/1", "http://results/1")
        println writer.toString()
    }

    @Test
    void testGenerateResultsLinks() {
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        resultExportService.generateResultsLinks(xml, "http:/test/1", 6000)
        println writer.toString()
    }
}
