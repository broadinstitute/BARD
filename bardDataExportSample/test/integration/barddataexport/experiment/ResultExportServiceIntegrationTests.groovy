package barddataexport.experiment

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.custommonkey.xmlunit.*

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
    void testGenerateResult() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        final BigDecimal experimentId = 1

        resultExportService.generateResult(xml, experimentId)
        println writer.toString()
    }

    @Test
    void testGenerateResultContextItem() {
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        final BigDecimal resultContextItemId = 1
        final BigDecimal groupResultContextItemId = 1
        final BigDecimal valueNum = 1
        final BigDecimal valueMax = 2
        final BigDecimal valueMin = 0
        final BigDecimal attributeId = 1
        final BigDecimal valueId = 1
        final String qualifier = "<"
        final String valueDisplay = "< 1"

        final ResultContextItemDTO resultContextItemDTO =
            new ResultContextItemDTO(resultContextItemId, groupResultContextItemId, valueNum, valueMax, valueMin, attributeId, valueId, qualifier, valueDisplay)
        resultExportService.generateResultContextItem(xml, resultContextItemDTO)

        XMLUnit.setIgnoreWhitespace(true)
        def xmlDiff = new Diff(writer.toString(), ResultXmlExamples.RESULT_CONTEXT_ITEM)
        assert xmlDiff.similar()
    }

    @Test
    void testGenerateResultHierarchy() {
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        resultExportService.generateResultHierarchy(xml, new ResultHierarchyDTO(1, "Some String"))
        XMLUnit.setIgnoreWhitespace(true)
        def xmlDiff = new Diff(writer.toString(), ResultXmlExamples.RESULT_HIERARCHY)
        assert xmlDiff.similar()
    }

    @Test
    void testGenerateResultLinks() {
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        xml.links() {
            resultExportService.generateResultLinks(xml, 561, 382, "http:/test/1", "http://results/1")
        }
        XMLUnit.setIgnoreWhitespace(true)
        def xmlDiff = new Diff(writer.toString(), ResultXmlExamples.RESULT_LINKS)
        assert xmlDiff.similar()
    }

    @Test
    void testGenerateResultsLinks() {
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)

        resultExportService.generateResultsLinks(xml, "http:/test/1", 6000, 1)
        XMLUnit.setIgnoreWhitespace(true)
        def xmlDiff = new Diff(writer.toString(), ResultXmlExamples.RESULTS_LINK)
        xmlDiff.overrideDifferenceListener(new DifferenceListener() {
            public int differenceFound(Difference difference) {
                if (difference.getId()
                        == DifferenceConstants.ATTR_VALUE ) {
                    return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                }
                return RETURN_ACCEPT_DIFFERENCE;
            }

            public void skippedComparison(org.w3c.dom.Node control, org.w3c.dom.Node test){

            }
        });
        assert xmlDiff.similar()
    }

}

class ResultXmlExamples {
    static def RESULT_CONTEXT_ITEM = '''
<resultContextItem resultContextItemId='1' groupResultContextItemId='1' qualifier='&lt;' valueDisplay='&lt; 1' valueNum='1' valueMin='0' valueMax='2'>
  <attributeId>
    <link rel='related' href='http://localhost/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
  </attributeId>
  <valueId>
    <link rel='related' href='http://localhost/api/dictionary/element/1' type='application/vnd.bard.cap+xml;type=element' />
  </valueId>
</resultContextItem>
'''
    static def RESULT_LINKS = '''
<links><link rel='up' title='Experiment' type='application/vnd.bard.cap+xml;type=experiment' href='http:/test/1' />
<link rel='related' title='Result Type' type='application/vnd.bard.cap+xml;type=resultType' href='http://localhost/api/dictionary/resultType/382' />
<link rel='edit' title='Edit Link' type='application/vnd.bard.cap+xml;type=result' href='http://results/1' /> </links>
  '''
    static def RESULTS_LINK = '''
<link rel='collection' title='Experiment' type='application/vnd.bard.cap+xml;type=experiment' href='http:/test/1' />
'''
    static def RESULT_HIERARCHY = '''
<resultHierarchy parentResultId='1' hierarchyType='Some String' />
'''
}
