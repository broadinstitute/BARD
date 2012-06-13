package barddataexport.cap

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
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
   void testSomething() {
        println("Testing")
    }
//    @Test
//    void testGenerateMeasureContext() {
//
//        def writer = new StringWriter()
//        def xml = new MarkupBuilder(writer)
//        def measureContextId = 1
//        final String contextName = "Context for PI (avg)"
//        capExportService.generateMeasureContext(xml, measureContextId, contextName)
//        println writer.toString()
//
//        XMLUnit.setIgnoreWhitespace(true)
//        def xmlDiff = new Diff(writer.toString(), CapXmlExamples.MEASURE_CONTEXT)
//        assert xmlDiff.similar()
//    }
//
//    @Test
//    void generateMeasureContexts() {
//        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        def driver = "oracle.jdbc.driver.OracleDriver"
//        def user = "bard_qa"
//        def password = "bard_qa"
//        Sql sql = Sql.newInstance(url, user, password, driver)
//        def writer = new StringWriter()
//        def xml = new MarkupBuilder(writer)
//        def assayId = 1
//        capExportService.generateMeasureContexts(sql, xml, assayId)
//
//        XMLUnit.setIgnoreWhitespace(true)
//        def xmlDiff = new Diff(writer.toString(), CapXmlExamples.MEASURE_CONTEXTS)
//        assert xmlDiff.similar()
//    }
//
//    @Test
//    void generateMeasure() {
//        def writer = new StringWriter()
//        def xml = new MarkupBuilder(writer)
//
//        final BigDecimal measureId = 1
//        final BigDecimal measureContextId = 1
//        final BigDecimal resultTypeId = 373
//        final BigDecimal parentMeasureId = null
//        final String entryUnit = 'uM'
//        final MeasureDTO measureDTO = new MeasureDTO(measureId, measureContextId, resultTypeId, parentMeasureId, entryUnit)
//
//        capExportService.generateMeasure(xml, measureDTO)
//        XMLUnit.setIgnoreWhitespace(true)
//        def xmlDiff = new Diff(writer.toString(), CapXmlExamples.MEASURE)
//        assert xmlDiff.similar()
//    }
//
//    @Test
//    void generateMeasures() {
//        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        def driver = "oracle.jdbc.driver.OracleDriver"
//        def user = "bard_qa"
//        def password = "bard_qa"
//        Sql sql = Sql.newInstance(url, user, password, driver)
//        def writer = new StringWriter()
//        def xml = new MarkupBuilder(writer)
//        def assayId = 1
//        capExportService.generateMeasures(sql, xml, assayId)
//        XMLUnit.setIgnoreWhitespace(true)
//        def xmlDiff = new Diff(writer.toString(), CapXmlExamples.MEASURES)
//        assert xmlDiff.similar()
//    }
//
//
//
//    @Test
//    void testGenerateMeasureContextItem() {
//
//        def writer = new StringWriter()
//        def xml = new MarkupBuilder(writer)
//
//        final BigDecimal measureContextItemId = 1
//        final BigDecimal groupMeasureContextItemId = null
//        final BigDecimal measureContextId = null
//        final BigDecimal attributeId = 352
//        final BigDecimal valueId = null
//        final BigDecimal valueNum = null
//        final BigDecimal valueMin = null
//        final BigDecimal valueMax = null
//        final String valueDisplay = 'ATP'
//        final String qualifier = null
//        final String attributeType = 'Fixed'
//        final MeasureContextItemDTO measureContextItemDTO = new MeasureContextItemDTO(measureContextItemId, groupMeasureContextItemId,
//                measureContextId, attributeId, valueId, valueNum, valueMin, valueMax, valueDisplay, qualifier, attributeType)
//
//        capExportService.generateMeasureContextItem(xml, measureContextItemDTO)
//        XMLUnit.setIgnoreWhitespace(true)
//        def xmlDiff = new Diff(writer.toString(), CapXmlExamples.MEASURE_CONTEXT_ITEM)
//        assert xmlDiff.similar()
//    }
//
//    @Test
//    void testGenerateMeasureContextItems() {
//        def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        def driver = "oracle.jdbc.driver.OracleDriver"
//        def user = "bard_qa"
//        def password = "bard_qa"
//        Sql sql = Sql.newInstance(url, user, password, driver)
//        def writer = new StringWriter()
//        def xml = new MarkupBuilder(writer)
//        def assayId = 1
//        capExportService.generateMeasureContextItems(sql, xml, assayId)
//        println writer.toString()
//    }
//
//    @Test
//    void testGenerateExternalAssays() {
//        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        final def driver = "oracle.jdbc.driver.OracleDriver"
//        final def user = "bard_qa"
//        final def password = "bard_qa"
//        final Sql sql = Sql.newInstance(url, user, password, driver)
//        final def writer = new StringWriter()
//        final def xml = new MarkupBuilder(writer)
//        capExportService.generateExternalAssays(sql, xml, 1)
//        println writer.toString()
//    }
//
//    @Test
//    void testGenerateExternalAssay() {
//        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        final def driver = "oracle.jdbc.driver.OracleDriver"
//        final def user = "bard_qa"
//        final def password = "bard_qa"
//        final Sql sql = Sql.newInstance(url, user, password, driver)
//        final def writer = new StringWriter()
//        final def xml = new MarkupBuilder(writer)
//        capExportService.generateExternalAssay(sql, xml, "aid=644", 1)
//        println writer.toString()
//    }
//
//    @Test
//    void testGenerateAssay() {
//        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        final def driver = "oracle.jdbc.driver.OracleDriver"
//        final def user = "bard_qa"
//        final def password = "bard_qa"
//        final Sql sql = Sql.newInstance(url, user, password, driver)
//        final def writer = new StringWriter()
//        final def xml = new MarkupBuilder(writer)
//
//
//        final BigDecimal assayId = 1
//        final String assayVersion = 1
//        final BigDecimal projectId = 1
//        final String assayStatus = 'Active'
//        final String assayName = 'Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2)'
//        final String description = 'Rho-Kinase is a serine/threonine kinase involved in the regulation of smooth muscle contraction and cytoskeletal reorganization of nonmuscle cells (1). Its inhibition is known to promote the smooth muscle relaxation. Thus, small-molecule inhibitors of Rho-Kinase may be effective probes for treatment of cerebral vasospasm (2) and potentially effective for treatment of angina (3), hypertension (4), arteriosclerosis (5), and erectile dysfunction (6).'
//        final String designedBy = 'Scripps Florida'
//        final AssayDTO assayDTO = new AssayDTO(projectId, assayId, assayVersion, assayStatus, assayName, description, designedBy, "Ready")
//        capExportService.generateAssay(sql, xml, assayDTO)
//        println writer.toString()
//    }
//
//    @Test
//    void testGenerateDocuments() {
//        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        final def driver = "oracle.jdbc.driver.OracleDriver"
//        final def user = "bard_qa"
//        final def password = "bard_qa"
//        final Sql sql = Sql.newInstance(url, user, password, driver)
//        final def writer = new StringWriter()
//        final def xml = new MarkupBuilder(writer)
//        final BigDecimal assayId = 1
//        capExportService.generateAssayDocuments(sql, xml, assayId)
//        println writer.toString()
//    }
//
//    @Test
//    void testGenerateDocument() {
//        final def writer = new StringWriter()
//        final def xml = new MarkupBuilder(writer)
//        capExportService.generateAssayDocument(xml, "Some Text", 2)
//        println writer.toString()
//    }
//
//    @Test
//    void testGenerateProjectAssay() {
//        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        final def driver = "oracle.jdbc.driver.OracleDriver"
//        final def user = "bard_qa"
//        final def password = "bard_qa"
//        final Sql sql = Sql.newInstance(url, user, password, driver)
//        final def writer = new StringWriter()
//        final def xml = new MarkupBuilder(writer)
//
//        final BigDecimal projectAssayId = 1
//        final BigDecimal projectId = 1
//        final BigDecimal relatedAssayId = null
//        final BigDecimal assayId = 1
//        final BigDecimal stageId = 224
//        final BigDecimal sequenceNumber = null
//        final BigDecimal promotionThreshold = null
//        final String promotionCriteria = 'The activity score was calculated based on pIC50 values for compounds for which an exact IC50 value was calculated and based on the observed pIC50 range, specifically the maximum lower limit of the pIC50 value as calculated from the lowest concentration for which greater than 50% inhibition is observed. This results in a conservative estimate of the activity score for compounds for which no exact IC50 value is given while maintaining a reasonable rank order of all compounds tested'
//
//
//
//
//        ProjectAssayDTO projectAssayDTO =
//            new ProjectAssayDTO(projectId, projectAssayId,
//                    relatedAssayId, assayId,
//                    stageId, sequenceNumber, promotionThreshold, promotionCriteria)
//        capExportService.generateProjectAssay(sql, xml, projectAssayDTO)
//        println writer.toString()
//    }
//
//    @Test
//    void testGenerateProjectAssays() {
//        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        final def driver = "oracle.jdbc.driver.OracleDriver"
//        final def user = "bard_qa"
//        final def password = "bard_qa"
//        final Sql sql = Sql.newInstance(url, user, password, driver)
//        final def writer = new StringWriter()
//        final def xml = new MarkupBuilder(writer)
//
//        capExportService.generateProjectAssays(sql, xml, 1)
//        println writer.toString()
//    }
//
//    @Test
//    void testGenerateProject() {
//        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        final def driver = "oracle.jdbc.driver.OracleDriver"
//        final def user = "bard_qa"
//        final def password = "bard_qa"
//        final Sql sql = Sql.newInstance(url, user, password, driver)
//        final def writer = new StringWriter()
//        final def xml = new MarkupBuilder(writer)
//        final ProjectDTO projectDTO = new ProjectDTO(1, 'Project', 'Scripps special project #1', null, "Ready")
//        capExportService.generateProject(sql, xml, projectDTO)
//        println writer.toString()
//    }
//
//    @Test
//    void testGenerateProjects() {
//        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
//        final def driver = "oracle.jdbc.driver.OracleDriver"
//        final def user = "bard_qa"
//        final def password = "bard_qa"
//        final Sql sql = Sql.newInstance(url, user, password, driver)
//        final def writer = new StringWriter()
//        final def xml = new MarkupBuilder(writer)
//        capExportService.generateProjects(sql, xml, false)
//        println writer.toString()
//    }
//
//    @Test
//    void testGenerateCap() {
//        final def writer = new StringWriter()
//        final def xml = new MarkupBuilder(writer)
//        capExportService.generateCap(xml)
//        println writer.toString()
//    }
}
class CapXmlExamples {
    static String MEASURE_CONTEXT_ITEM = '''
<measureContextItem measureContextItemId='1' attributeType='Fixed' valueDisplay='ATP'>
  <attributeId>
    <link rel='related' href='http://localhost/api/dictionary/element/352' type='application/vnd.bard.cap+xml;type=element' />
  </attributeId>
</measureContextItem>
'''

static String MEASURES = '''
<measures>
  <measure measureId='1' measureContextRef='1' measureRef='1' entryUnit='%'>
    <resultTypeRef>
      <link rel='related' href='http://localhost/api/dictionary/resultType/373' type='application/vnd.bard.cap+xml;type=resultType' />
    </resultTypeRef>
  </measure>
  <measure measureId='2' measureContextRef='2' measureRef='2' entryUnit='uM'>
    <resultTypeRef>
      <link rel='related' href='http://localhost/api/dictionary/resultType/341' type='application/vnd.bard.cap+xml;type=resultType' />
    </resultTypeRef>
  </measure>
  <measure measureId='3' measureContextRef='2' measureRef='3'>
    <resultTypeRef>
      <link rel='related' href='http://localhost/api/dictionary/resultType/374' type='application/vnd.bard.cap+xml;type=resultType' />
    </resultTypeRef>
  </measure>
  <measure measureId='4' measureContextRef='2' measureRef='4'>
    <resultTypeRef>
      <link rel='related' href='http://localhost/api/dictionary/resultType/375' type='application/vnd.bard.cap+xml;type=resultType' />
    </resultTypeRef>
  </measure>
  <measure measureId='5' measureContextRef='2' measureRef='5' entryUnit='uM'>
    <resultTypeRef>
      <link rel='related' href='http://localhost/api/dictionary/resultType/376' type='application/vnd.bard.cap+xml;type=resultType' />
    </resultTypeRef>
  </measure>
  <measure measureId='6' measureContextRef='2' measureRef='6' entryUnit='uM'>
    <resultTypeRef>
      <link rel='related' href='http://localhost/api/dictionary/resultType/377' type='application/vnd.bard.cap+xml;type=resultType' />
    </resultTypeRef>
  </measure>
  <measure measureId='7' measureContextRef='2' measureRef='7' entryUnit='uM'>
    <resultTypeRef>
      <link rel='related' href='http://localhost/api/dictionary/resultType/378' type='application/vnd.bard.cap+xml;type=resultType' />
    </resultTypeRef>
  </measure>
  <measure measureId='8' measureContextRef='2' measureRef='8'>
    <resultTypeRef>
      <link rel='related' href='http://localhost/api/dictionary/resultType/381' type='application/vnd.bard.cap+xml;type=resultType' />
    </resultTypeRef>
  </measure>
  <measure measureId='9' measureContextRef='2' measureRef='9'>
    <resultTypeRef>
      <link rel='related' href='http://localhost/api/dictionary/resultType/382' type='application/vnd.bard.cap+xml;type=resultType' />
    </resultTypeRef>
  </measure>
</measures>
'''
    static String MEASURE = '''
<measure measureId='1' measureContextRef='1' measureRef='1' entryUnit='uM'>
  <resultTypeRef>
    <link rel='related' href='http://localhost/api/dictionary/resultType/373' type='application/vnd.bard.cap+xml;type=resultType' />
  </resultTypeRef>
</measure>
'''
    static String MEASURE_CONTEXTS = '''
<measureContexts>
  <measureContext measureContextId='1'>
    <contextName>Context for PI (avg)</contextName>
  </measureContext>
  <measureContext measureContextId='2'>
    <contextName>Context for IC50</contextName>
  </measureContext>
</measureContexts>
'''
    static String MEASURE_CONTEXT = '''
<measureContext measureContextId='1'>
  <contextName>Context for PI (avg)</contextName>
</measureContext>
'''
}