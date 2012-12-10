package bard.core.rest.spring.util

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Scores extends JsonUtil {
    Map<String, Double> getScoreMap() {
        return this.getAdditionalProperties()
    }

    Double getScore(final String key) {
        return getScoreMap().get(key)
    }
}