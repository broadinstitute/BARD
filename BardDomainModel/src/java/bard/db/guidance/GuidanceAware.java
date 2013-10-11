package bard.db.guidance;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/8/13
 * Time: 4:00 PM
 * <p/>
 * Implementers should evaluate their state and the state of owned entities and give feedback to the users.
 * These are meant to allow for offering guidance but not applying hard constraints that would prevent the user from
 * saving and entity.
 * <p/>
 *
 * <p>GuidanceAware object should enlist GuidanceRules to assemble a list of Guidance objects to present to the user</p>
 *
 * The thought is that through guidance we can strongly suggest how users structure their data in a very flexible
 * data structure.
 */
public interface GuidanceAware {

    /**
     * @return a list of Guidance objects containing messages for the end user
     */
    public List<Guidance> getGuidance();
}
