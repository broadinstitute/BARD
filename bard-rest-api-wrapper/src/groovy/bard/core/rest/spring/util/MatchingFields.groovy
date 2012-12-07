package bard.core.rest.spring.util

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchingFields extends JsonUtil {

    Map<String, NameDescription> getMatchingFieldsMap() {
        final Map<String, Map<String, String>> map =
            (Map<String, Map<String, String>>) this.getAdditionalProperties()
        final Map<String, NameDescription> matchingFields = [:]
        final Iterator<String> iterator = map.keySet().iterator()
        while (iterator.hasNext()) {
            final String key = iterator.next()
            Map<String, String> vals = map.get(key)
            //we expect only one value in the list
            final List<String> keys = vals.keySet() as List<String>

            //the key is the field name
            final String name = keys.get(0)
            //the value is the description
            final String description = vals.get(name)
            NameDescription nameDescription = new NameDescription(name: name, description: description)
            matchingFields.put(key, nameDescription)
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