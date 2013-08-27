package bard.core.rest.spring.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapDictionary extends JsonUtil implements Serializable {
    final private String SYNC_LOCK = ""
    private final Map<Long,Node> dictionaryElementMap = [:]

    @JsonProperty("nodes")
    private List<Node> nodes = new ArrayList<Node>();

    @JsonProperty("nodes")
    public List<Node> getNodes() {
        return this.nodes;
    }

    @JsonProperty("nodes")
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
    public void loadElements(){
        if(!this.dictionaryElementMap){
            synchronized (SYNC_LOCK) {
                for(Node dictionaryElement : this.nodes){
                    this.dictionaryElementMap.put(dictionaryElement.elementId,dictionaryElement)
                }
            }
        }
    }
    public Map<Long,Node> getDictionaryElementMap(){
        return this.dictionaryElementMap
    }
    public Node findDictionaryElement(Long dictionaryId){
        loadElements();
        return this.dictionaryElementMap.get(dictionaryId)
    }

}

