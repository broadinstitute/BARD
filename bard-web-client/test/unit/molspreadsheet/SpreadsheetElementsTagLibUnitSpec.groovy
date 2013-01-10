package molspreadsheet

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 *   These are the tags that are used to build the different cells within a molecular spreadsheet.  The same methods
 *   can be used in both the transposed and un-transposed version of the spreadsheet.
 */
@TestFor(SpreadsheetElementsTagLib)
@Unroll
class SpreadsheetElementsTagLibUnitSpec  extends Specification {

    MolSpreadSheetCell molSpreadSheetCell
    HillCurveValueHolder hillCurveValueHolder

    void setup() {
        molSpreadSheetCell = new MolSpreadSheetCell("0.123", MolSpreadSheetCellType.numeric)
        hillCurveValueHolder = new HillCurveValueHolder()
    }

    void tearDown() {

    }

    /**
     * Images of molecular structures go in these cells
     */
    void "test imageCell with real structures"() {
    when:
        String results = this.tagLib.imageCell([cid: 47,sid: 48,smiles: "c1cccc1"])
    then:
        assert results.contains("compound-info-dropdown")
        // TODO try to get this code working: assert this.view == "/tagLibTemplates/compoundOptions"
    }


    /**
     * Images of molecular structures go in these cells too
     */
    void "test imageCell with no structure"() {
        when:
        String results = new  SpreadsheetElementsTagLib().imageCell([cid: 47,sid: 48,smiles: 'Unknown smiles'])
        then:
        assert results=="Unknown smiles"
    }

    /**
     *  CIDs and associated links
     */
    void "test cidCell #label"() {
        when:
        String results = new  SpreadsheetElementsTagLib().cidCell([cid: cid])
        then:
        assert results.replaceAll("\\s", "")==result
        where:
        label           | cid   | result
        "image cell"    | 47    | """<ahref="/bardWebInterface/showCompound/47">47</a>""".toString()
        "image cell"    | 48    | """<ahref="/bardWebInterface/showCompound/48">48</a>""".toString()
    }

    /**
     * All the promiscuity information with their associated Ajax calls
     */
    void "test promiscuityCell #label"() {
        when:
        String results = new  SpreadsheetElementsTagLib().promiscuityCell([cid: cid])
        then:
        assert results.replaceAll("\\s", "")==result
        where:
        label           | cid   | result
        "image cell"    | 47    | """<divclass="promiscuity"href="/bardWebInterface/promiscuity?cid=47"id="47_prom"></div>""".toString()
        "image cell"    | 48    | """<divclass="promiscuity"href="/bardWebInterface/promiscuity?cid=48"id="48_prom"></div>""".toString()
    }

    /**
     * active versus tested text information
     */
    void "test activeVrsTestedCell #label"() {
        when:
        String results = new  SpreadsheetElementsTagLib().activeVrsTestedCell([activeVrsTested: activeVrsTested])
        then:
        assert results.replaceAll("\\s", "")==result
        where:
        label           | activeVrsTested   | result
        "activeVrsTestedCell cell"    | "99/100"            | """<div><spanclass="badgebadge-info">99/100</span></div>""".toString()
        "activeVrsTestedCell cell"    | "1/999"             | """<div><spanclass="badgebadge-info">1/999</span></div>""".toString()
    }

    /**
     * All the different types of experimental data going these cells
     */
    void "test exptDataCell"() {
        given:
        SpreadSheetActivityStorage spreadSheetActivityStorage1  // null
        SpreadSheetActivityStorage spreadSheetActivityStorage2 = new SpreadSheetActivityStorage() // empty
        SpreadSheetActivityStorage spreadSheetActivityStorage3 = new SpreadSheetActivityStorage() // containing only a hillCurveValueHolder with single point data
        final HillCurveValueHolder hillCurveValueHolder = new HillCurveValueHolder()
        hillCurveValueHolder.identifier = 1
        hillCurveValueHolder.s0 = 1d
        hillCurveValueHolder.slope = 1d
        hillCurveValueHolder.coef = 1d
        hillCurveValueHolder.conc = [1d]
        hillCurveValueHolder.response = [1d]
        spreadSheetActivityStorage3.hillCurveValueHolderList  = [hillCurveValueHolder]
        SpreadSheetActivityStorage spreadSheetActivityStorage4 = new SpreadSheetActivityStorage()   // hillCurveValueHolder with multiple point data is handled differently
        final HillCurveValueHolder hillCurveValueHolder1 = new HillCurveValueHolder()
        hillCurveValueHolder1.identifier = 1
        hillCurveValueHolder1.s0 = 1d
        hillCurveValueHolder1.slope = 1d
        hillCurveValueHolder1.coef = 1d
        hillCurveValueHolder1.conc = [1d,2d]
        hillCurveValueHolder1.response = [1d,2d]
        spreadSheetActivityStorage4.hillCurveValueHolderList  = [hillCurveValueHolder1]
        SpreadSheetActivityStorage spreadSheetActivityStorage5 = new SpreadSheetActivityStorage() // This is an ill formed  hillCurveValueHolder -- test it too
        final HillCurveValueHolder hillCurveValueHolder2 = new HillCurveValueHolder()
        hillCurveValueHolder2.conc = [1d,2d]
        hillCurveValueHolder2.response = [1d,2d]
        spreadSheetActivityStorage5.hillCurveValueHolderList  = [hillCurveValueHolder2]

        when:
        String results1 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage1])
        String results2 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage2])
        String results3 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage3])
        String results4 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage4])
        String results5 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage5])

        then:
        results1=="<td class=\"molSpreadSheet\" property=\"var1\">\n                          Not tested in this experiment\n                      </td>"
        results2.replaceAll("\\s", "") == """<tdclass="molSpreadSheet"property="var1"><p></p>""".toString()
        results3.replaceAll("\\s", "") == """<tdclass="molSpreadSheet"property="var1"><p><divdata-detail-id="drc_null_1"class="btnbtn-link"data-original-title="1"data-html="true"data-trigger="hover">1</div></p>""".toString()
        results4.replaceAll("\\s", "") == """<tdclass="molSpreadSheet"property="var1"><p><divdata-detail-id="drc_null_1"class="drc-popover-linkbtnbtn-link"data-original-title="1"data-html="true"data-trigger="hover">1</div></p><divclass='popover-content-wrapper'id="drc_null_1"style="display:none;"><divclass="center-aligned"><imgalt="null"title="SubstanceId:null"src="/doseResponseCurve/doseResponseCurve?sinf=&s0=1.0&ac50=1.0&hillSlope=1.0&concentrations=1.0&concentrations=2.0&activities=1.0&activities=2.0&yAxisLabel=1"/></div></div>""".toString()
        results5.replaceAll("\\s", "") == """<tdclass="molSpreadSheet"property="var1"><p></p><divclass='popover-content-wrapper'id="drc_null_1"style="display:none;"><divclass="center-aligned"><imgalt="null"title="SubstanceId:null"src="/doseResponseCurve/doseResponseCurve?sinf=&s0=&ac50=&hillSlope=&concentrations=1.0&concentrations=2.0&activities=1.0&activities=2.0&yAxisLabel="/></div></div>""".toString()
    }




}