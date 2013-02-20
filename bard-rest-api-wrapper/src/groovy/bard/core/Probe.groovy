package bard.core;

/**
 * @author Jacob Asiedu
 */
public class Probe implements Serializable {
    // private static final long serialVersionUID = 8196705055192706779L;
    final String cid;
    final String probeId;
    final String url;
    final String smiles;
    final String bardProjectId

    public Probe(String cid, String probeId, String url, String smiles, String bardProjectId) {
        this.cid = cid;
        this.probeId = probeId;
        this.url = url ;
        this.smiles = smiles;
        this.bardProjectId = bardProjectId
    }
    public String getBardProjectId(){
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
    public String getCid() {
        return cid;
    }

}
