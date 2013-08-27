package bard.core

import bard.core.rest.spring.compounds.Compound;

/**
 * @author Jacob Asiedu
 */
public class Probe implements Serializable {
    // private static final long serialVersionUID = 8196705055192706779L;
    final Long cid;
    final String probeId;
    final String url;
    final String smiles;
    final Long bardProjectId
    final Long capProjectId

    public Probe(Compound compound) {
        this(compound.cid, compound.probeId, compound.url, compound.smiles, compound.bardProjectId, compound.capProjectId)
    }

    public Probe(Long cid, String probeId, String url, String smiles, Long bardProjectId, Long capProjectId) {
        this.cid = cid;
        this.probeId = probeId;
        this.url = url;
        this.smiles = smiles;
        this.bardProjectId = bardProjectId
        this.capProjectId = capProjectId
    }

    public Long getCapProjectId() {
        return this.capProjectId
    }

    public Long getBardProjectId() {
        return this.bardProjectId
    }
    /**
     *
     * @return String
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @return String
     */
    public String getSmiles() {
        return smiles;
    }

    /**
     *
     * @return String
     */
    public String getProbeId() {
        return probeId;
    }

    /**
     *
     * @return Long
     */
    public Long getCid() {
        return cid;
    }

}
