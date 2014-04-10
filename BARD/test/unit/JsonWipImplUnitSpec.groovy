import adf.JsonTransform
import au.com.bytecode.opencsv.CSVWriter
import spock.lang.Specification

/**
 * Created by ddurkin on 3/20/14.
 */
class JsonWipImplUnitSpec extends Specification {


    void test(){
        JsonTransform jsonWip = new JsonTransform()
        jsonWip.run()
        expect:
        1==1

    }
}
