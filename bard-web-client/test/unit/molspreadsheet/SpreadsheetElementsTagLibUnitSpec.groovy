package molspreadsheet

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.experiment.ActivityData

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
        assert results.replaceAll("\\s", "").contains(result)
        where:
        label           | cid   | result
        "image cell"    | 47    | "/bardWebInterface/showCompound/47".toString()
        "image cell"    | 48    | "/bardWebInterface/showCompound/48".toString()
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
        String results = new  SpreadsheetElementsTagLib().activeVrsTestedCell([activeVrsTested: activeVrsTested, cid: 54])
        then:
        assert results.replaceAll("\\s", "")==result
        where:
        label           | activeVrsTested   | result
        "activeVrsTestedCell cell"      | "99/100"            | """<div><spanclass="badgebadge-info">99/100</span></div>""".toString()
        "activeVrsTestedCell cell"      | "1/999"             | """<div><spanclass="badgebadge-info">1/999</span></div>""".toString()
        "bad input handled"             | "garbage"           | """<div><spanclass="badgebadge-info">garbage</span></div>""".toString()
        "spaces give link"              | "15 / 47"           | """<div><spanclass="badgebadge-info"><ahref="/molSpreadSheet/showExperimentDetails?cid=54&transpose=true"style="color:white;text-decoration:underline">15</a>/47</div>""".toString()
    }

    /**
     * All the different types of experimental data going these cells
     */
    void "test exptDataCell"() {
        given:
        //-- the null SpreadSheetActivityStorage test
        SpreadSheetActivityStorage spreadSheetActivityStorage1
        //-- the empty SpreadSheetActivityStorage test
        SpreadSheetActivityStorage spreadSheetActivityStorage2 = new SpreadSheetActivityStorage()
        //--  containing only a hillCurveValueHolder with single point data
        SpreadSheetActivityStorage spreadSheetActivityStorage3 = new SpreadSheetActivityStorage()
        final HillCurveValueHolder hillCurveValueHolder = new HillCurveValueHolder()
        hillCurveValueHolder.identifier = 1
        hillCurveValueHolder.s0 = 1d
        hillCurveValueHolder.slope = 1d
        hillCurveValueHolder.coef = 1d
        hillCurveValueHolder.conc = [1d]
        hillCurveValueHolder.response = [1d]
        spreadSheetActivityStorage3.hillCurveValueHolderList  = [hillCurveValueHolder]
        //--  hillCurveValueHolder with multiple point data is handled differently
        SpreadSheetActivityStorage spreadSheetActivityStorage4 = new SpreadSheetActivityStorage()
        final HillCurveValueHolder hillCurveValueHolder1 = new HillCurveValueHolder()
        hillCurveValueHolder1.identifier = 1
        hillCurveValueHolder1.s0 = 1d
        hillCurveValueHolder1.slope = 1d
        hillCurveValueHolder1.coef = 1d
        hillCurveValueHolder1.conc = [1d,2d]
        hillCurveValueHolder1.response = [1d,2d]
        spreadSheetActivityStorage4.hillCurveValueHolderList  = [hillCurveValueHolder1]
        //--   This is an ill formed  hillCurveValueHolder -- test it too
        SpreadSheetActivityStorage spreadSheetActivityStorage5 = new SpreadSheetActivityStorage()
        final HillCurveValueHolder hillCurveValueHolder2 = new HillCurveValueHolder()
        hillCurveValueHolder2.conc = [1d,2d]
        hillCurveValueHolder2.response = [1d,2d]
        spreadSheetActivityStorage5.hillCurveValueHolderList  = [hillCurveValueHolder1]
        //-- hillCurveValueHolder with Child elements and a typical HillCurveValueHolder
        SpreadSheetActivityStorage spreadSheetActivityStorage6 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage6.childElements = [new ActivityData(),new ActivityData()]
        spreadSheetActivityStorage6.hillCurveValueHolderList  = [hillCurveValueHolder1]
        //-- hillCurveValueHolder with Child elements and a degenerate HillCurveValueHolder -- in this case the child element should not be printed
        SpreadSheetActivityStorage spreadSheetActivityStorage7 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage7.childElements = [new ActivityData(),new ActivityData()]
        spreadSheetActivityStorage7.hillCurveValueHolderList  = [hillCurveValueHolder2]
        // check error case when child elements =null
        SpreadSheetActivityStorage spreadSheetActivityStorage8 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage8.childElements = null
        spreadSheetActivityStorage8.hillCurveValueHolderList  = [hillCurveValueHolder2]


        when:
        String results1 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage1])
        String results2 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage2])
        String results3 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage3])
        String results4 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage4])
        String results5 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage5])
        String results6 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage6])
        String results7 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage7])
        String results8 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage7])

        then:
        results1.contains("Not tested in this experiment")
        results2.replaceAll("\\s", "") == """<tdclass="molSpreadSheet"property="var1"><p></p>""".toString()
        results3.contains("molspreadcell")
        results4.contains("/doseResponseCurve/doseResponseCurve?sinf=&s0=1.0&slope=1.0&hillSlope=1.0")
        results5.contains("/doseResponseCurve/doseResponseCurve?sinf=&s0=")
        results6.contains("<FONT COLOR=\"#000000\"><nobr>")
        (!results7.contains("<FONT COLOR=\"#000000\"><nobr>"))
        results8.contains("doseResponseCurve/doseResponseCurve")
        (!results8.contains("nobr"))
    }


    /**
     * All the different types of experimental data going these cells
     */
    void "test y normalization"() {
        given:
        List<MolSpreadSheetColumnHeader> mssHeaders = []
        List<MolSpreadSheetColumnHeader> mssHeaders2 = []
        List<MolSpreadSheetColumnHeader> mssHeaders3 = []
        List<MolSpreadSheetColumnHeader> mssHeaders4 = []
        List<MolSpreadSheetColumnHeader> mssHeaders5 = []
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        MolSpreadSheetData molSpreadSheetData2 = new MolSpreadSheetData()
        MolSpreadSheetData molSpreadSheetData3 = new MolSpreadSheetData()
        //-- the null SpreadSheetActivityStorage test
        SpreadSheetActivityStorage spreadSheetActivityStorage1
        //-- the empty SpreadSheetActivityStorage test
        SpreadSheetActivityStorage spreadSheetActivityStorage2 = new SpreadSheetActivityStorage()
        //--  containing only a hillCurveValueHolder with single point data
        SpreadSheetActivityStorage spreadSheetActivityStorage3 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage3.eid = 234L
        molSpreadSheetData.columnPointer[234L]=0
        mssHeaders << new MolSpreadSheetColumnHeader()
        mssHeaders << new MolSpreadSheetColumnHeader()
        mssHeaders << new MolSpreadSheetColumnHeader()
        mssHeaders << new MolSpreadSheetColumnHeader()
        mssHeaders << new MolSpreadSheetColumnHeader()
        final HillCurveValueHolder hillCurveValueHolder = new HillCurveValueHolder()
        hillCurveValueHolder.identifier = 1
        hillCurveValueHolder.s0 = 1d
        hillCurveValueHolder.slope = 1d
        hillCurveValueHolder.coef = 1d
        hillCurveValueHolder.conc = [1d]
        hillCurveValueHolder.response = [1d]
        spreadSheetActivityStorage3.hillCurveValueHolderList  = [hillCurveValueHolder]
        //--  hillCurveValueHolder with multiple point data is handled differently
        SpreadSheetActivityStorage spreadSheetActivityStorage4 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage4.eid = 234L
        mssHeaders[4].molSpreadSheetColSubHeaderList = []
        hillCurveValueHolder.identifier = 1
        hillCurveValueHolder.s0 = 1d
        hillCurveValueHolder.slope = 1d
        hillCurveValueHolder.coef = 1d
        hillCurveValueHolder.conc = [1d,2d]
        hillCurveValueHolder.response = [1d,2d]
        spreadSheetActivityStorage4.hillCurveValueHolderList  = [hillCurveValueHolder]
        //--  hillCurveValueHolder with multiple point data is handled differently
        SpreadSheetActivityStorage spreadSheetActivityStorage5 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage5.eid = 234L
        mssHeaders[4].molSpreadSheetColSubHeaderList = [new MolSpreadSheetColSubHeader(minimumResponse: 0d, maximumResponse: 47d)]
        hillCurveValueHolder.identifier = 1
        hillCurveValueHolder.s0 = 1d
        hillCurveValueHolder.slope = 1d
        hillCurveValueHolder.coef = 1d
        hillCurveValueHolder.conc = [1d,2d]
        hillCurveValueHolder.response = [1d,2d]
        spreadSheetActivityStorage5.hillCurveValueHolderList  = [hillCurveValueHolder]
        // with multiple min and max records
        SpreadSheetActivityStorage spreadSheetActivityStorage6 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage6.eid = 234L
        mssHeaders2 << new MolSpreadSheetColumnHeader()
        mssHeaders2 << new MolSpreadSheetColumnHeader()
        mssHeaders2 << new MolSpreadSheetColumnHeader()
        mssHeaders2 << new MolSpreadSheetColumnHeader()
        mssHeaders2 << new MolSpreadSheetColumnHeader()
        mssHeaders2[4].molSpreadSheetColSubHeaderList = [new MolSpreadSheetColSubHeader(minimumResponse: 4d, maximumResponse: 8d), new MolSpreadSheetColSubHeader(minimumResponse: 2d, maximumResponse: 94d), new MolSpreadSheetColSubHeader(minimumResponse: 22d, maximumResponse: 23d)]
        hillCurveValueHolder.identifier = 1
        hillCurveValueHolder.s0 = 1d
        hillCurveValueHolder.slope = 1d
        hillCurveValueHolder.coef = 1d
        hillCurveValueHolder.conc = [1d,2d]
        hillCurveValueHolder.response = [1d,2d]
        spreadSheetActivityStorage6.hillCurveValueHolderList  = [hillCurveValueHolder]
        // with null min and max
        SpreadSheetActivityStorage spreadSheetActivityStorage7 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage7.eid = 234L
        mssHeaders3 << new MolSpreadSheetColumnHeader()
        mssHeaders3 << new MolSpreadSheetColumnHeader()
        mssHeaders3 << new MolSpreadSheetColumnHeader()
        mssHeaders3 << new MolSpreadSheetColumnHeader()
        mssHeaders3 << new MolSpreadSheetColumnHeader()
        mssHeaders3[4].molSpreadSheetColSubHeaderList = [new MolSpreadSheetColSubHeader(minimumResponse: Double.NaN, maximumResponse: Double.NaN)]
        hillCurveValueHolder.identifier = 1
        hillCurveValueHolder.s0 = 1d
        hillCurveValueHolder.slope = 1d
        hillCurveValueHolder.coef = 1d
        hillCurveValueHolder.conc = [1d,2d]
        hillCurveValueHolder.response = [1d,2d]
        spreadSheetActivityStorage7.hillCurveValueHolderList  = [hillCurveValueHolder]
        //--  degenerate case
        SpreadSheetActivityStorage spreadSheetActivityStorage8 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage8.eid = 234L
        spreadSheetActivityStorage8.eid = 234L
        spreadSheetActivityStorage8.eid = 234L
        mssHeaders4 << new MolSpreadSheetColumnHeader()
        mssHeaders4 << new MolSpreadSheetColumnHeader()
        mssHeaders4 << new MolSpreadSheetColumnHeader()
        mssHeaders4 << new MolSpreadSheetColumnHeader()
        mssHeaders4 << new MolSpreadSheetColumnHeader(molSpreadSheetColSubHeaderList:null )
        hillCurveValueHolder.identifier = 1
        hillCurveValueHolder.s0 = 1d
        hillCurveValueHolder.slope = 1d
        hillCurveValueHolder.coef = 1d
        hillCurveValueHolder.conc = [1d,2d]
        hillCurveValueHolder.response = [1d,2d]
        spreadSheetActivityStorage8.hillCurveValueHolderList  = [hillCurveValueHolder]
        // with multiple min and max records which have been deactivated
        SpreadSheetActivityStorage spreadSheetActivityStorage9 = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage9.eid = 235L
        mssHeaders5 << new MolSpreadSheetColumnHeader()
        mssHeaders5 << new MolSpreadSheetColumnHeader()
        mssHeaders5 << new MolSpreadSheetColumnHeader()
        mssHeaders5 << new MolSpreadSheetColumnHeader()
        mssHeaders5 << new MolSpreadSheetColumnHeader()
        mssHeaders5[4].molSpreadSheetColSubHeaderList = [new MolSpreadSheetColSubHeader(minimumResponse: 4d, maximumResponse: 8d), new MolSpreadSheetColSubHeader(minimumResponse: 2d, maximumResponse: 94d), new MolSpreadSheetColSubHeader(minimumResponse: 22d, maximumResponse: 23d)]
        molSpreadSheetData2.columnPointer[235L] =0
        molSpreadSheetData2.experimentNameList[0]="9388"
        molSpreadSheetData2.mapColumnsNormalization["9388"]=false
        hillCurveValueHolder.identifier = 1
        hillCurveValueHolder.s0 = 1d
        hillCurveValueHolder.slope = 1d
        hillCurveValueHolder.coef = 1d
        hillCurveValueHolder.conc = [1d,2d]
        hillCurveValueHolder.response = [1d,2d]
        spreadSheetActivityStorage9.hillCurveValueHolderList  = [hillCurveValueHolder]
        // identical to previous, except this timee min and max not deactivated
        molSpreadSheetData3.columnPointer[235L] =0
        molSpreadSheetData3.experimentNameList[0]="9388"
        molSpreadSheetData3.mapColumnsNormalization["9388"]=true


        when:
        String results1 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage1, mssHeaders: mssHeaders, molSpreadSheetData: molSpreadSheetData])
        String results2 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage2, mssHeaders: mssHeaders, molSpreadSheetData: molSpreadSheetData])
        String results3 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage3, mssHeaders: mssHeaders, molSpreadSheetData: molSpreadSheetData])
        String results4 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage4, mssHeaders: mssHeaders, molSpreadSheetData: molSpreadSheetData])
        String results5 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage5, mssHeaders: mssHeaders2, molSpreadSheetData: molSpreadSheetData])
        String results6 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage6, mssHeaders: mssHeaders2, molSpreadSheetData: molSpreadSheetData])
        String results7 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage7, mssHeaders: mssHeaders3, molSpreadSheetData: molSpreadSheetData])
        String results8 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage8, mssHeaders: mssHeaders4, molSpreadSheetData: molSpreadSheetData])
        String results9 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage9, mssHeaders: mssHeaders5, molSpreadSheetData: molSpreadSheetData2])
        String results10 = new  SpreadsheetElementsTagLib().exptDataCell([colCnt: 1, spreadSheetActivityStorage: spreadSheetActivityStorage9, mssHeaders: mssHeaders5, molSpreadSheetData: molSpreadSheetData3])

        then:
        results1.contains("Not tested in this experiment")
        results2.replaceAll("\\s", "") == """<tdclass="molSpreadSheet"property="var1"><p></p>""".toString()
        results3.contains("molspreadcell")
        results4.contains("&yNormMin=0.0&yNormMax=47.0")
        results5.contains("&yNormMin=2.0&yNormMax=94.0")
        results6.contains("&yNormMin=2.0&yNormMax=94.0")
        (!results7.contains("yNormMin"))
        (!results8.contains("yNormMin"))
        (!results9.contains("yNormMin"))
        (results10.contains("yNormMin"))
    }


}