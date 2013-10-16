package bard.db.guidance;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 10/10/13
 * Time: 11:25 AM
 * Interface that individual GuidanceRules should implement
 */
public interface GuidanceRule {

    /**
     * @return a list of Guidance objects containing messages for the end user
     */
    public List<Guidance> getGuidance();
}
