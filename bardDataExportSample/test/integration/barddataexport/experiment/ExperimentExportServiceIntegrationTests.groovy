package barddataexport.experiment

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.Difference
import org.custommonkey.xmlunit.DifferenceListener
import org.custommonkey.xmlunit.XMLUnit
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
    void testGenerateExperimentLinks() {
        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        final BigDecimal projectId = 1
        final BigDecimal assayId = 1
        final BigDecimal experimentId = 1
        xml.links() {
            experimentExportService.generateExperimentLinks(xml, projectId, assayId, experimentId)
        }
        XMLUnit.setIgnoreWhitespace(true)
        def xmlDiff = new Diff(writer.toString(), ExperimentXmlExamples.EXPERIMENT_LINKS)
        assert xmlDiff.similar()
    }

    @Test
    void testGenerateExperiment() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)

        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)
        final BigDecimal experimentId = 1
        final String experimentName = "Some Name"
        final String experimentStatus = "Published'"
        final Date holdUntilDate = new Date()
        final Date runFromDate = new Date()
        final Date runToDate = new Date()
        final String description = "Some description"
        final BigDecimal projectId = 1
        final BigDecimal assayId = 1

        final ExperimentDTO experimentDTO =
            new ExperimentDTO(
                    experimentId,
                    experimentName,
                    experimentStatus,
                    holdUntilDate,
                    runFromDate,
                    runToDate,
                    description,
                    projectId,
                    assayId, 'Ready')
        experimentExportService.generateExperiment(sql, xml, experimentDTO)
        XMLUnit.setIgnoreWhitespace(true)
        def xmlDiff = new Diff(writer.toString(), ExperimentXmlExamples.EXPERIMENT)

        xmlDiff.overrideDifferenceListener(new DifferenceListener() {
            public int differenceFound(Difference difference) {
                if (
                        difference.getControlNodeDetail().node.nodeName == 'runDateFrom' ||
                                difference.getControlNodeDetail().node.nodeName == 'runDateTo' ||
                                difference.getControlNodeDetail().node.nodeName == 'holdUntilDate'
                ) {
                    return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                }
                return RETURN_ACCEPT_DIFFERENCE;
            }

            public void skippedComparison(org.w3c.dom.Node control, org.w3c.dom.Node test) {

            }
        });
        assert xmlDiff.similar()

    }

    @Test
    void testGenerateExperiments() {
        final def url = "jdbc:oracle:thin:@barddb:1521:bardqa"
        final def driver = "oracle.jdbc.driver.OracleDriver"
        final def user = "bard_qa"
        final def password = "bard_qa"
        final Sql sql = Sql.newInstance(url, user, password, driver)

        final def writer = new StringWriter()
        final def xml = new MarkupBuilder(writer)

        experimentExportService.generateExperiments(sql, xml)
        //println writer.toString()
        XMLUnit.setIgnoreWhitespace(true)
        def xmlDiff = new Diff(writer.toString(), ExperimentXmlExamples.EXPERIMENTS)

        xmlDiff.overrideDifferenceListener(new DifferenceListener() {
            public int differenceFound(Difference difference) {
                if (
                        difference.getControlNodeDetail().node.nodeName == 'runDateFrom' ||
                                difference.getControlNodeDetail().node.nodeName == 'runDateTo' ||
                                difference.getControlNodeDetail().node.nodeName == 'holdUntilDate'
                ) {
                    return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                }
                return RETURN_ACCEPT_DIFFERENCE;
            }

            public void skippedComparison(org.w3c.dom.Node control, org.w3c.dom.Node test) {

            }
        });
        assert xmlDiff.similar()

    }

}
class ExperimentXmlExamples {

    static def EXPERIMENTS = '''
<experiments count='3'>
  <experiment numberOfResults='600' experimentName='Dose-response biochemical assay of inhibitors of Rho kinase 2 (Rock2): 1' readyForExtraction='Ready' runDateFrom='2012-03-12T00:00:00.000-04:00' runDateTo='2012-03-12T00:00:00.000-04:00'>
    <experimentStatus>
      <status>Approved</status>
    </experimentStatus>
    <resultContextItems />
    <link rel='related' title='Link to Project' type='application/vnd.bard.cap+xml;type=project' href='http://localhost/api/cap/project/1' />
    <link rel='related' title='Link to Assay' type='application/vnd.bard.cap+xml;type=assay' href='http://localhost/api/cap/assay/1' />
    <link rel='related' title='List Related Results' type='application/vnd.bard.cap+xml;type=results' href='http://localhost/api/data/experiment/1/results' />
    <link rel='edit' title='Use link to edit Experiment' type='application/vnd.bard.cap+xml;type=experiment' href='http://localhost/api/data/experiment/1' />
    <link rel='up' title='List Experiments' type='application/vnd.bard.cap+xml;type=experiments' href='http://localhost/api/data' />
  </experiment>
  <experiment numberOfResults='168' experimentName='A cell-based HTS for delayed death inhibitors of the malarial parasite plastid.: 1' readyForExtraction='Ready' runDateFrom='2012-03-12T00:00:00.000-04:00' runDateTo='2012-03-12T00:00:00.000-04:00'>
    <experimentStatus>
      <status>Approved</status>
    </experimentStatus>
    <resultContextItems>
      <resultContextItem resultContextItemId='611' valueDisplay='100800' valueNum='100800'>
        <attributeId>
          <link rel='related' href='http://localhost/api/dictionary/element/117' type='application/vnd.bard.cap+xml;type=element' />
        </attributeId>
      </resultContextItem>
    </resultContextItems>
    <link rel='related' title='Link to Project' type='application/vnd.bard.cap+xml;type=project' href='http://localhost/api/cap/project/2' />
    <link rel='related' title='Link to Assay' type='application/vnd.bard.cap+xml;type=assay' href='http://localhost/api/cap/assay/2' />
    <link rel='related' title='List Related Results' type='application/vnd.bard.cap+xml;type=results' href='http://localhost/api/data/experiment/2/results' />
    <link rel='edit' title='Use link to edit Experiment' type='application/vnd.bard.cap+xml;type=experiment' href='http://localhost/api/data/experiment/2' />
    <link rel='up' title='List Experiments' type='application/vnd.bard.cap+xml;type=experiments' href='http://localhost/api/data' />
  </experiment>
  <experiment numberOfResults='0' experimentName='Primary biochemical high-throughput screening assay for inhibitors of Rho kinase 2 (Rhok2).' readyForExtraction='Ready' runDateFrom='2007-02-17T00:00:00.000-05:00' runDateTo='2007-02-17T00:00:00.000-05:00'>
    <experimentStatus>
      <status>Approved</status>
    </experimentStatus>
    <description>Rho-Kinase is a serine/threonine kinase that is involved in the regulation of smooth muscle contraction and cytoskeletal reorganization of nonmuscle cells (1). Small-molecule inhibitors of Rho-Kinase, which promote smooth muscle relaxation, may be effective for treatment of cerebral vasospasm (2) and potentially effective for treatment of angina (3), hypertension (4), arteriosclerosis (5), and erectile dysfunction (6).</description>
    <resultContextItems />
    <link rel='related' title='Link to Project' type='application/vnd.bard.cap+xml;type=project' href='http://localhost/api/cap/project/1' />
    <link rel='related' title='Link to Assay' type='application/vnd.bard.cap+xml;type=assay' href='http://localhost/api/cap/assay/1' />
    <link rel='related' title='List Related Results' type='application/vnd.bard.cap+xml;type=results' href='http://localhost/api/data/experiment/3/results' />
    <link rel='edit' title='Use link to edit Experiment' type='application/vnd.bard.cap+xml;type=experiment' href='http://localhost/api/data/experiment/3' />
    <link rel='up' title='List Experiments' type='application/vnd.bard.cap+xml;type=experiments' href='http://localhost/api/data' />
  </experiment>
</experiments>
'''
    static def EXPERIMENT_LINKS = '''
    <links>
    <link rel='related' title='Link to Project' type='application/vnd.bard.cap+xml;type=project' href='http://localhost/api/cap/project/1' />
    <link rel='related' title='Link to Assay' type='application/vnd.bard.cap+xml;type=assay' href='http://localhost/api/cap/assay/1' />
    <link rel='related' title='List Related Results' type='application/vnd.bard.cap+xml;type=results' href='http://localhost/api/data/experiment/1/results' />
    <link rel='edit' title='Use link to edit Experiment' type='application/vnd.bard.cap+xml;type=experiment' href='http://localhost/api/data/experiment/1' />
    <link rel='up' title='List Experiments' type='application/vnd.bard.cap+xml;type=experiments' href='http://localhost/api/data' />
    </links>
    '''
    static def EXPERIMENT = '''
<experiment numberOfResults='600' experimentName='Some Name' readyForExtraction='Ready' holdUntilDate='2012-05-27T12:45:22.310-04:00' runDateFrom='2012-05-27T12:45:22.310-04:00' runDateTo='2012-05-27T12:45:22.310-04:00'>
  <experimentStatus>
    <status>Published'</status>
  </experimentStatus>
  <description>Some description</description>
  <resultContextItems />
  <link rel='related' title='Link to Project' type='application/vnd.bard.cap+xml;type=project' href='http://localhost/api/cap/project/1' />
  <link rel='related' title='Link to Assay' type='application/vnd.bard.cap+xml;type=assay' href='http://localhost/api/cap/assay/1' />
  <link rel='related' title='List Related Results' type='application/vnd.bard.cap+xml;type=results' href='http://localhost/api/data/experiment/1/results' />
  <link rel='edit' title='Use link to edit Experiment' type='application/vnd.bard.cap+xml;type=experiment' href='http://localhost/api/data/experiment/1' />
  <link rel='up' title='List Experiments' type='application/vnd.bard.cap+xml;type=experiments' href='http://localhost/api/data' />
</experiment>
'''

}
