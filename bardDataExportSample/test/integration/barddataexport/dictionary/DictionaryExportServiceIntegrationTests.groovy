package barddataexport.dictionary

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

class DictionaryExportServiceIntegrationTests {
    DictionaryExportService dictionaryExportService

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testGenerateAssayDescriptors() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateDescriptors(xml, DescriptorType.ASSAY_DESCRIPTOR)
        println(writer.toString())

    }

    @Test
    void testGenerateBiologyDescriptors() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateDescriptors(xml, DescriptorType.BIOLOGY_DESCRIPTOR)
        println(writer.toString())

    }

    @Test
    void testGenerateInstanceDescriptors() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateDescriptors(xml, DescriptorType.INSTANCE_DESCRIPTOR)
        println(writer.toString())

    }

    @Test
    void testGenerateElements() {
        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        def driver = "oracle.jdbc.driver.OracleDriver"
        def user = "bard_qa"
        def password = "bard_qa"
        Sql sql = Sql.newInstance(url, user, password, driver)
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateElements(sql, xml)
        println writer.toString()
    }

    @Test
    void testGenerateElementHierarchies() {
        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        def driver = "oracle.jdbc.driver.OracleDriver"
        def user = "bard_qa"
        def password = "bard_qa"
        Sql sql = Sql.newInstance(url, user, password, driver)
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateElementHierarchies(sql, xml)
        println writer.toString()
    }

    @Test
    void testGenerateResultTypes() {
        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        def driver = "oracle.jdbc.driver.OracleDriver"
        def user = "bard_qa"
        def password = "bard_qa"
        Sql sql = Sql.newInstance(url, user, password, driver)
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateResultTypes(sql, xml)
        println writer.toString()
    }

    @Test
    void testGenerateStages() {
        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        def driver = "oracle.jdbc.driver.OracleDriver"
        def user = "bard_qa"
        def password = "bard_qa"
        Sql sql = Sql.newInstance(url, user, password, driver)

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateStages(sql, xml)
        println writer.toString()
    }

    @Test
    void testGenerateDictionary() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateDictionary(xml)
        def file = new File("dictionaryFull.xml")
        file.write(writer.toString())
        println "******************Done*****************"
    }

//    @Test
//    void testStuff() {
//
//        def writer = new StringWriter()
//        def xml = new MarkupBuilder(writer)
//        String RESULT_MEDIA_TYPE = "application/vnd.bard.cap+xml;type=result"
//        String EXPERIMENT_MEDIA_TYPE = "application/vnd.bard.cap+xml;type=experiment"
//        final BigDecimal experimentId = 1
//        xml.results(count: 2000000) {
//
//            for (i in 0..2000000) {
//                final BigDecimal resultId = new BigDecimal(i)
//                final String resultHref = grailsLinkGenerator.link(mapping: 'result', absolute: true, params: [id: resultId]).toString()
//
//                xml.link(rel: 'related', title: 'Result', type: "${RESULT_MEDIA_TYPE}",
//                        href: resultHref) {
//                }
//
//            }
//            final String experimentHref = grailsLinkGenerator.link(mapping: 'result', absolute: true, params: [id: experimentId]).toString()
//            xml.link(rel: 'up', title: 'Result', type: "${EXPERIMENT_MEDIA_TYPE}",
//                    href: experimentHref) {
//            }
//        }
//
//        def file = new File("results.xml")
//        file.write(writer.toString())
//        println "******************Done*****************"
//    }
}
