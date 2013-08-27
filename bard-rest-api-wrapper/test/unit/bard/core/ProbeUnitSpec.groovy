package bard.core

import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.compounds.Compound

@Unroll
class ProbeUnitSpec extends Specification {

    void "test Probe"() {
        given:
        final String smiles = "CC"
        final String probeId = "ML108"
        final Long cid = 444
        final String url = "http://www.bard.com"
        final Long bardProjectId = 2
        final Long capProjectId = 1
        when:
        final Probe probe = new Probe(cid, probeId, url, smiles, bardProjectId, capProjectId)
        then:
        probe.cid == cid
        probe.probeId == probeId
        probe.smiles == smiles
        probe.url == url
        probe.bardProjectId == bardProjectId
        probe.capProjectId == capProjectId
    }
    void "test Probe with Compound"() {
        given:
        final String smiles = "CC"
        final String probeId = "ML108"
        final Long cid = 444
        final String url = "http://www.bard.com"
        final Long bardProjectId = 2
        final Long capProjectId = 1
        Compound compound = new Compound(smiles: smiles, cid: cid, probeId: probeId,url: url, bardProjectId: bardProjectId, capProjectId: capProjectId)
        when:
        final Probe probe = new Probe(compound)
        then:
        probe.cid == cid
        probe.probeId == probeId
        probe.smiles == smiles
        probe.url == url
        probe.bardProjectId == bardProjectId
        probe.capProjectId == capProjectId
    }

}

