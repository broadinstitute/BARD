package barddataexport.cap

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

class CapExportServiceIntegrationTests {
    CapExportService capExportService

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testGenerateMeasureContext() {

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def measureContextId = 1
        final String contextName = "Context for PI (avg)"
        capExportService.generateMeasureContext(xml, measureContextId, contextName)
        println writer.toString()
    }

    @Test
    void generateMeasureContexts() {
        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        def driver = "oracle.jdbc.driver.OracleDriver"
        def user = "bard_qa"
        def password = "bard_qa"
        Sql sql = Sql.newInstance(url, user, password, driver)
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def assayId = 1
        capExportService.generateMeasureContexts(sql, xml, assayId)
        println writer.toString()
    }

    @Test
    void generateMeasures() {
        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        def driver = "oracle.jdbc.driver.OracleDriver"
        def user = "bard_qa"
        def password = "bard_qa"
        Sql sql = Sql.newInstance(url, user, password, driver)
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def assayId = 1
        capExportService.generateMeasures(sql, xml, assayId)
        println writer.toString()
    }

    @Test
    void generateMeasure() {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        final BigDecimal measureId = 1
        final BigDecimal measureContextId = 1
        final BigDecimal resultTypeId = 373
        final BigDecimal parentMeasureId = null
        final String entryUnit = 'uM'
        MeasureDTO measureDTO = new MeasureDTO(measureId, measureContextId, resultTypeId, parentMeasureId, entryUnit)
        capExportService.generateMeasure(xml, measureDTO)
        println writer.toString()
    }

    @Test
    void testGenerateMeasureContextItem() {

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        final BigDecimal measureContextItemId = 1
        final BigDecimal groupMeasureContextItemId = null
        final BigDecimal measureContextId = null
        final BigDecimal attributeId = 352
        final BigDecimal valueId = null
        final BigDecimal valueNum = null
        final BigDecimal valueMin = null
        final BigDecimal valueMax = null
        final String valueDisplay = 'ATP'
        final String qualifier = null
        final String attributeType = 'Fixed'
        final MeasureContextItemDTO measureContextItemDTO = new MeasureContextItemDTO(measureContextItemId, groupMeasureContextItemId,
                measureContextId, attributeId, valueId, valueNum, valueMin, valueMax, valueDisplay, qualifier, attributeType)

        capExportService.generateMeasureContextItem(xml, measureContextItemDTO)
        println writer.toString()
    }

    @Test
    void testGenerateMeasureContextItems() {
        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        def driver = "oracle.jdbc.driver.OracleDriver"
        def user = "bard_qa"
        def password = "bard_qa"
        Sql sql = Sql.newInstance(url, user, password, driver)
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        def assayId = 1
        capExportService.generateMeasureContextItems(sql, xml, assayId)
        println writer.toString()
    }

    @Test
    void testGenerateExternalAssays() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        capExportService.generateExternalAssays(sql, xml, 1)
        println writer.toString()
    }

    @Test
    void testGenerateExternalAssay() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        capExportService.generateExternalAssay(sql, xml, "aid=644", 1)
        println writer.toString()
    }

    @Test
    void testGenerateAssay() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)


        final BigDecimal assayId = 1
        final String assayVersion = 1
        final String assayStatus = 'Active'
        final String assayName = 'Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2)'
        final String description = 'Rho-Kinase is a serine/threonine kinase involved in the regulation of smooth muscle contraction and cytoskeletal reorganization of nonmuscle cells (1). Its inhibition is known to promote the smooth muscle relaxation. Thus, small-molecule inhibitors of Rho-Kinase may be effective probes for treatment of cerebral vasospasm (2) and potentially effective for treatment of angina (3), hypertension (4), arteriosclerosis (5), and erectile dysfunction (6).'
        final String designedBy = 'Scripps Florida'
        final AssayDTO assayDTO = new AssayDTO(assayId, assayVersion, assayStatus, assayName, description, designedBy)
        capExportService.generateAssay(sql, xml, assayDTO)
        println writer.toString()
    }

    @Test
    void testGenerateDocuments() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        final BigDecimal assayId = 1
        capExportService.generateProtocolDocuments(sql, xml, assayId)
        println writer.toString()
    }

    @Test
    void testGenerateDocument() {
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        capExportService.generateProtocolDocument(xml, "Some Text", 2)
        println writer.toString()
    }

    @Test
    void testGenerateProjectAssay() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)

        final BigDecimal projectAssayId = 1
        final BigDecimal relatedAssayId = null
        final BigDecimal assayId = 1
        final BigDecimal stageId = 224
        final BigDecimal sequenceNumber = null
        final BigDecimal promotionThreshold = null
        final String promotionCriteria = 'The activity score was calculated based on pIC50 values for compounds for which an exact IC50 value was calculated and based on the observed pIC50 range, specifically the maximum lower limit of the pIC50 value as calculated from the lowest concentration for which greater than 50% inhibition is observed. This results in a conservative estimate of the activity score for compounds for which no exact IC50 value is given while maintaining a reasonable rank order of all compounds tested'

        ProjectAssayDTO projectAssayDTO = new ProjectAssayDTO(projectAssayId, relatedAssayId, assayId, stageId, sequenceNumber, promotionThreshold, promotionCriteria)
        capExportService.generateProjectAssay(sql, xml, projectAssayDTO)
        println writer.toString()
    }

    @Test
    void testGenerateProjectAssays() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)

        capExportService.generateProjectAssays(sql, xml, 1)
        println writer.toString()
    }
    @Test
    void testGenerateProject(){
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        final ProjectDTO projectDTO = new ProjectDTO(1,'Project','Scripps special project #1',null)
        capExportService.generateProject(sql,xml,projectDTO)
        println writer.toString()
    }
    @Test
    void testGenerateProjects(){
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        capExportService.generateProjects(sql,xml,false)
        println writer.toString()
    }
    @Test
    void testGenerateCap(){
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        capExportService.generateCap(xml)
        println writer.toString()
    }
}
