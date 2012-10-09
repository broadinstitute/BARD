package molspreadsheet

import com.metasieve.shoppingcart.ShoppingCartService
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bardqueryapi.*

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/9/12
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */

@Unroll
@TestFor(MolecularSpreadSheetService)
class MolecularSpreadSheetServiceUnitSpec  extends Specification {
    QueryCartService queryCartService
    QueryServiceWrapper queryServiceWrapper
    ShoppingCartService shoppingCartService
    IQueryService queryService

    void setup() {
        this.queryCartService = Mock(QueryCartService)
        this.queryServiceWrapper = Mock(QueryServiceWrapper)
        this.shoppingCartService = Mock(ShoppingCartService)
        this.queryService = Mock(IQueryService)
//        this.service.restClientFactoryService = this.restClientFactoryService
//        this.service.queryHelperService = this.queryHelperService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test extractActivitiesFromExperiment when experimentValue has no children"() {
        given:  "we have an experiment"
        final bard.core.Value experimentalValue  = new bard.core.Value()
        final experimentId = 47 as Long

        when: "we want to pull out the active values"
        SpreadSheetActivity spreadSheetActivity = service.extractActivitiesFromExperiment(experimentalValue,experimentId)

        then: "prove that the active values are available"
        assertNotNull spreadSheetActivity
        assert spreadSheetActivity.experimentId ==  experimentId

    }


    void "test addCurrentActivityToSpreadSheet when experimentValue has no children"() {
        given:  "we have an experiment"
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        final bard.core.Value experimentalValue  = new bard.core.Value()
        experimentalValue.id = identifier
        experimentalValue.metaClass.value = incomingValue

        when: "we want to pull out the active values"
        service.addCurrentActivityToSpreadSheet(spreadSheetActivity,experimentalValue)

        then: "prove that the active values are available"
        assert returnRelevantNumber(identifier,spreadSheetActivity)==returnValue

        where:
        incomingValue   |   identifier  | returnValue
        47 as Double    | "potency"     | 47 as Double
        47 as Long      | "outcome"     | null
        47 as Long      | "eid"         | 47 as Long
        47 as Long      | "cid"         | 47 as Long
        47 as Long      | "sid"         | 47 as Long
    }


    def returnRelevantNumber (String identifier, SpreadSheetActivity spreadSheetActivity){
        def returnValue
        switch (identifier) {
            case "potency":
                returnValue = (Double) spreadSheetActivity.potency
                break
            case "outcome":
                returnValue = spreadSheetActivity.activityOutcome
                break
            case "eid":
                returnValue =  spreadSheetActivity.eid
                break
            case "cid":
                returnValue = spreadSheetActivity.cid
                break
            case "sid":
                returnValue = spreadSheetActivity.sid
                break
            default:
               assert false, "Unexpected Identifier: ${identifier} is unknown"
        }
        returnValue
    }



}
