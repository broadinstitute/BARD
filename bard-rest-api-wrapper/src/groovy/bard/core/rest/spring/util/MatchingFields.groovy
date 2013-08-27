package bard.core.rest.spring.util

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchingFields extends JsonUtil {

    Map<String, NameDescription> getMatchingFieldsMap() {
        final Map<String, NameDescription> matchingFields = [:]
        final Map<String, Object> map = this.getAdditionalProperties()
        map.collect { entry1 ->
            String key1 = entry1.key
            LinkedHashMap<String,String> vals = entry1.value
            vals.collect { entry2 ->
               String key2 = entry2.key
               NameDescription nameDescription = new NameDescription(name: key2, description: "")
               matchingFields.put(key1, nameDescription)
                return
            }
        }
        return matchingFields
    }

    NameDescription getNamedField(String key) {
        return getMatchingFieldsMap().get(key)
    }

}
public class NameDescription {
    String name
    String description
}