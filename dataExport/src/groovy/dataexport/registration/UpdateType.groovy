package dataexport.registration

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 7/30/12
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public enum UpdateType {
    COMPLETE("Complete"),
    STARTED("Started");

    private final String description

    private UpdateType(String description) {
        this.description = description
    }

    public String getDescription() {
        return this.description
    }
    /**
     * if we are in the started state then transition to complete
     * if we are in the Ready state then transition to started
     * otherwise no-op
     */
    public static UpdateType findNextTransitionUpdateState(final String readyForExtraction) {
        switch (readyForExtraction) {
            case UpdateType.STARTED.description:
            case UpdateType.COMPLETE.description:
                return UpdateType.COMPLETE
            default:
                return UpdateType.STARTED
        }
    }
}