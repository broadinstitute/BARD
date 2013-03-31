package bard.core.rest.spring.compound

import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.compounds.TargetClassInfo
import org.apache.commons.lang.StringUtils

@Unroll
class SunburstUnitSpec extends Specification {

    /**
     *
     * http://bard.nih.gov/api/v10/search/compounds?q="dna repair" & expand=true
     *
     */
    void "test serialization to Compounds from free text search"() {
        when:
        File f = new File("target.txt")

        then:

        assert f.exists()
        int index = 1
        f.eachLine { line ->
            println index++
            if(StringUtils.isNotBlank(line)) {
                List<String> spreadSheetData = line.split("\t") as List<String>
                TargetClassInfo targetClassInfo = TargetClassInfo.generateClassInformation(spreadSheetData)

                println(targetClassInfo)
                println "\n\n"
            }
        }

    }


}

