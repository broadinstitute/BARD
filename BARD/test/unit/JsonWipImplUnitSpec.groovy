import spock.lang.Specification

/**
 * Created by ddurkin on 3/20/14.
 */
class JsonWipImplUnitSpec extends Specification {


    void test(){
        JsonWipImpl jsonWip = new JsonWipImpl()
        def result =  jsonWip.run()
        result.printlnResultSetPipeline()
        expect:
        1==1

    }
}
