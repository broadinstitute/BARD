package bard.core.rest.spring.util

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchingFields extends JsonUtil {

    Map<String, NameDescription> getMatchingFieldsMap() {
        final Map<String, Map<String, String>> map =
            (Map<String, Map<String, String>>) this.getAdditionalProperties().get("matchingFields")
        final Map<String, NameDescription> matchingFields = [:]
        final Iterator<String> iterator = map.keySet().iterator()
        while (iterator.hasNext()) {
            final String key = iterator.next()
            Map<String, String> vals = map.get(key)
            NameDescription nameDescription = new NameDescription(name: vals.get("name"), description: vals.get("description"))
            matchingFields.put(key, nameDescription)
        }
        return matchingFields
    }
}
public class NameDescription {
    String name
    String description
}