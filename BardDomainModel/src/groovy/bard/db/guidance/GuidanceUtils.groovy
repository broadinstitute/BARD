package bard.db.guidance

/**
 * Created by ddurkin on 1/29/14.
 */
class GuidanceUtils {

    public static List<Guidance> getGuidance(List<GuidanceRule> guidanceRules){
        final List<Guidance> guidanceList = []
        for (GuidanceRule rule : guidanceRules) {
            guidanceList.add(rule.getGuidance())
        }
        guidanceList.flatten()
    }
}
