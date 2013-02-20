package bard.core

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ProbeUnitSpec extends Specification {

    void "test Probe"() {
        given:
        final String smiles = "CC"
        final String probeId = "ML108"
        final String cid = "444"
        final String url = "http://www.bard.com"
        final String bardProjectId = "bardProjectId"
        when:
        final Probe probe = new Probe(cid, probeId, url, smiles,bardProjectId )
        then:
        probe.cid == cid
        probe.probeId == probeId
        probe.smiles == smiles
        probe.url == url
        probe.bardProjectId == bardProjectId
    }


}

