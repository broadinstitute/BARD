package bard.core.rest.spring.etags

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 3/8/13
 * Time: 10:28 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class ETags extends ArrayList<ETag>{
    final Map<String,List<ETag>> etagMap = [:]

    public List<ETag> getByType(String type){
        if(!this.etagMap){
            buildMap()
        }
       return this.etagMap.get(type)
    }
    public Map<String,List<ETag>> buildMap(){
       if(!this.etagMap){
         for(ETag etag: this){
             List<ETag> eTags = this.etagMap.get(etag.type)
             if(!eTags){
                 eTags = new ArrayList<ETag>()
             }
             eTags.add(etag)
             this.etagMap.put(etag.type,eTags)
         }
       }
       return this.etagMap
    }
}
