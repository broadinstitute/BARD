package adf.imp

import adf.exp.JsonTransform
import adf.imp.Batch
import adf.imp.DatasetParser
import bard.db.dictionary.Element
import bard.db.experiment.JsonSubstanceResults
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * Created by ddurkin on 3/20/14.
 */
@Build([Element])
@Mock([Element])
@TestMixin(GrailsUnitTestMixin)
class ExportImportUnitSpec extends Specification {
    void testRoundTripExportImport() {
        setup:
        Element pubchemOutcome = Element.build(label: "PubChem outcome")
        Element pubchemActivityScore = Element.build(label: "PubChem activity score")
        Element ac50 = Element.build(label: "AC50")
        Element pAC50 = Element.build(label: "pAC50")
        Element hillCoefficient = Element.build(label: "Hill coefficient");
        Element hillS0 = Element.build(label: "Hill s0")
        Element hillSinf = Element.build(label: "Hill sinf")
        Element percentActivity = Element.build(label: "percent activity")
        Element maximum = Element.build(label: "maximum")
        Element numberOfPoints = Element.build(label: "number of points")
        Element screeningConc = Element.build(label: "screening concentration (molar)")

        DatasetImporter importer = new DatasetImporter();

        when:
        JsonTransform jsonWip = new JsonTransform()
        jsonWip.transform(new File('exp-1462-20131027-144131.json.gz'), "dataset_")
        DatasetParser imp = new DatasetParser(["dataset_1.txt", "dataset_2.txt"])
        while (imp.hasNext()) {
            // for each sample
            Batch b = imp.readNext()
            Map<Long, DatasetImporter.DatasetRow> byResultId = importer.indexByResultId(b.datasets)
            List<DatasetImporter.DatasetRow> rootRows = importer.constructTree(byResultId);
            JsonSubstanceResults result = importer.translateRowsToResults(b.sid, rootRows)
//            println(result);
        }

        then:
        Element.findByLabel("PubChem outcome") >> pubchemOutcome
        Element.findByLabel("PubChem activity score") >> pubchemActivityScore
        Element.findByLabel("AC50") >> ac50
        Element.findByLabel("pAC50") >> pAC50
        Element.findByLabel("Hill coefficient") >> hillCoefficient
        Element.findByLabel("Hill s0") >> hillS0
        Element.findByLabel("Hill sinf") >> hillSinf
        Element.findByLabel("percent activity") >> percentActivity
        Element.findByLabel("maximum") >> maximum
        Element.findByLabel("number of points") >> numberOfPoints
        Element.findByLabel("screening concentration (molar)") >> screeningConc

        expect:
        1 == 1
    }

    void testSerialize() {
    }
}
