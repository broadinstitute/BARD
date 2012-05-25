package barddataexport.experiment

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test


class ExperimentExportServiceIntegrationTests {
    ExperimentExportService experimentExportService

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testGenerateExperimentStatus() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)

        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        experimentExportService.generateExperimentStatus(sql, xml, 2)
        println writer.toString()
    }

    @Test
    void testGenerateExperimentLinks() {
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        final BigDecimal projectId = 1
        final BigDecimal assayId = 1
        final BigDecimal experimentId = 1

        experimentExportService.generateExperimentLinks(xml, projectId, assayId, experimentId)
        println writer.toString()
    }

    @Test
    void testGenerateExperiment(){
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)

        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        final BigDecimal experimentId =  1
        final String experimentName = "Some Name"
        final BigDecimal experimentStatusId = 2
        final Date holdUntilDate =new Date()
        final Date runFromDate=new Date()
        final Date runToDate=new Date()
        final String description="Some description"
        final BigDecimal projectId =1
        final BigDecimal assayId  =1
        final ExperimentDTO experimentDTO =
            new ExperimentDTO(experimentId,experimentName,experimentStatusId,
                    holdUntilDate,runFromDate,runToDate,description,projectId,assayId)
        experimentExportService.generateExperiment(sql,xml,experimentDTO)
        println writer.toString()
    }
    @Test
    void testGenerateExperiments(){
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)

        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)

        experimentExportService.generateExperiments(sql,xml)
        println writer.toString()
    }

}
