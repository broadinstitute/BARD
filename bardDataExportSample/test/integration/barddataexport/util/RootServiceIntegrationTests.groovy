package barddataexport.util

import barddataexport.experiment.ResultExportService
import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

class RootServiceIntegrationTests {
    RootService rootService

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testGenerateRoot() {
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        rootService.generateRootElement(xml)
        println writer.toString()
    }


}
