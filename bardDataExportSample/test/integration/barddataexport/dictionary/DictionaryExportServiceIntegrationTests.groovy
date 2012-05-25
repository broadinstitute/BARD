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
        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        def driver = "oracle.jdbc.driver.OracleDriver"
        def user = "bard_qa"
        def password = "bard_qa"
        Sql sql = Sql.newInstance(url, user, password, driver)
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateDescriptors(sql, xml, DescriptorType.ASSAY_DESCRIPTOR, false)
        println(writer.toString())

    }

    @Test
    void testGenerateBiologyDescriptors() {
        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        def driver = "oracle.jdbc.driver.OracleDriver"
        def user = "bard_qa"
        def password = "bard_qa"
        Sql sql = Sql.newInstance(url, user, password, driver)
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateDescriptors(sql, xml, DescriptorType.BIOLOGY_DESCRIPTOR, false)
        println(writer.toString())

    }

    @Test
    void testGenerateInstanceDescriptors() {
        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        def driver = "oracle.jdbc.driver.OracleDriver"
        def user = "bard_qa"
        def password = "bard_qa"
        Sql sql = Sql.newInstance(url, user, password, driver)
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateDescriptors(sql, xml, DescriptorType.INSTANCE_DESCRIPTOR, false)
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
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateResultTypes(xml)
        println writer.toString()
    }

    @Test
    void testGenerateStages() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateStages(xml)
        println writer.toString()
    }

    @Test
    void testGenerateDictionary() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        dictionaryExportService.generateDictionary(xml)
        def file = new File("dictionary.xml")
        file.write(writer.toString())
        println "******************Done*****************"
    }
}
