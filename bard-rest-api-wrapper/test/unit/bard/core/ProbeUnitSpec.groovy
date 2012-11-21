package bard.core

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ProbeUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Probe"() {
        given:
        final String smiles = "CC"
        final String probeId = "ML108"
        final String cid = "444"
        final String url = "http://www.bard.com"
        when:
        final Probe probe = new Probe(cid, probeId, url, smiles)
        then:
        probe.cid == cid
        probe.probeId == probeId
        probe.smiles == smiles
        probe.url == url

    }


}

