package bard.core.rest.spring.util
import bard.core.rest.spring.compounds.TargetClassInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @deprecated replaced by BiologyEntity
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
public class Target extends JsonUtil {

    @JsonProperty("classes")
    private List<TargetClassification> targetClassifications = new ArrayList<TargetClassification>();
    @JsonProperty("url")
    private String url;
    @JsonProperty("acc")
    private String acc;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("status")
    private String status;
    @JsonProperty("geneId")
    private long geneId;
    @JsonProperty("taxId")
    private long taxId;
    @JsonProperty("resourcePath")
    private String resourcePath;


    @JsonProperty("classes")
    public List<TargetClassification> getTargetClassifications() {
        return targetClassifications
    }

    @JsonProperty("classes")
    public void setTargetClassifications(List<TargetClassification> targetClassifications) {
        this.targetClassifications = targetClassifications
    }

    @JsonProperty("url")
    public String getUrl() {
        return url
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url
    }

    @JsonProperty("acc")
    public String getAcc() {
        return acc;
    }

    @JsonProperty("acc")
    public void setAcc(String acc) {
        this.acc = acc;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public Object getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(Object description) {
        this.description = description;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("geneId")
    public long getGeneId() {
        return geneId;
    }

    @JsonProperty("geneId")
    public void setGeneId(long geneId) {
        this.geneId = geneId;
    }

    @JsonProperty("taxId")
    public long getTaxId() {
        return taxId;
    }

    @JsonProperty("taxId")
    public void setTaxId(long taxId) {
        this.taxId = taxId;
    }

    @JsonProperty("resourcePath")
    public String getResourcePath() {
        return resourcePath;
    }

    @JsonProperty("resourcePath")
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @JsonIgnore
    public List<RingNode> toRingNodeList() {
        List<RingNode> ringNodeList = []
        for (TargetClassification classification : this.getTargetClassifications()) {
            ringNodeList.add(new RingNode(classification.name,
                    classification.id,
                    classification.description,
                    classification.levelIdentifier,
                    classification.source))
        }

        ringNodeList.sort {RingNode ringNodeA, RingNode ringNodeB -> ringNodeA.numberOfLevels() <=> ringNodeB.numberOfLevels() }
        return ringNodeList
    }
    @JsonIgnore
    public static Map<String, RingNode> createRingNodeManager(final Target target){
        final List<RingNode> ringNodeList = target.toRingNodeList()

        Map<String, RingNode> ringNodeMgr = [:]
        ringNodeMgr["1."] = new RingNode("\\", "0", "root", "1", "none")
        for (RingNode ringNode in ringNodeList) {
            RingNode currentRingNode = ringNodeMgr["1."]
            List<String> listOfGroups = ringNode.levelIdentifier.split(/\./)
            String buildingString = ""
            for (String oneGroup in listOfGroups) {
                buildingString += "${oneGroup}."
                if (ringNodeMgr.containsKey(buildingString)) {
                    currentRingNode = ringNodeMgr[buildingString]
                } else {
                    ringNodeMgr[buildingString] = ringNode
                    currentRingNode.children << ringNode
                    break
                }
            }
        }
        return ringNodeMgr
    }
    @JsonIgnore
    public static List<TargetClassInfo> constructTargetInformation(final Target target) {
        Map<String, RingNode> ringNodeMgr =  createRingNodeManager(target)
        List<TargetClassInfo> targetClassInfos = []
        final List<TargetClassification> classifications = target.getTargetClassifications()
        if (classifications) {
             // Everything node (except the root) now gets written out, and tree position is explicitly represented
            ringNodeMgr.each { String key, RingNode ringNode ->
                if (key != '1.') {
                    TargetClassInfo targetClassInfo = new TargetClassInfo(ringNode)
                    targetClassInfo.path = ringNode.writeHierarchyPath(ringNodeMgr)
                    targetClassInfo.accessionNumber=target.acc
                    targetClassInfos.add(targetClassInfo)
                 }
            }
         }
        return targetClassInfos
    }

}
