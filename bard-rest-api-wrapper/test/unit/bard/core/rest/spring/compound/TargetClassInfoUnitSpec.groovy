package bard.core.rest.spring.compound

import bard.core.rest.spring.compounds.TargetClassInfo
import bard.core.rest.spring.util.RingNode
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TargetClassInfoUnitSpec extends Specification {


    void "test Copy Constructor"() {
        given:
        String name = "name"
        String ID = "ID"
        String description = "Description"
        String levelIdentifier = "1.0.0.1"
        String source = "Panther"
        int size = 0
        RingNode ringNode = new RingNode(name,ID,description,levelIdentifier,source,size)
        when:
        TargetClassInfo targetClassInfo = new TargetClassInfo(ringNode)
        then:
        assert name ==targetClassInfo.name
        assert ID ==targetClassInfo.id
        assert description == targetClassInfo.description
        assert levelIdentifier==targetClassInfo.levelIdentifier
        assert source == targetClassInfo.source
        assert !targetClassInfo.path
        assert !targetClassInfo.accessionNumber
    }
    void "test generateClassInformation"(){
        given:
        String accessionNumber = "AC001"
        String path="\\target\\protein"
        String name = "name"
        String ID = "ID"
        String description = "Description"
        String levelIdentifier = "1.0.0.1"
        String source = "Panther"
        List<String> data = [ID,name,description,levelIdentifier,source,accessionNumber,path]

        when:
        TargetClassInfo targetClassInfo = TargetClassInfo.generateClassInformation(data)
        then:
        assert name ==targetClassInfo.name
        assert ID ==targetClassInfo.id
        assert description == targetClassInfo.description
        assert levelIdentifier==targetClassInfo.levelIdentifier
        assert source == targetClassInfo.source
        assert  path==targetClassInfo.path
        assert accessionNumber== targetClassInfo.accessionNumber
    }
}

