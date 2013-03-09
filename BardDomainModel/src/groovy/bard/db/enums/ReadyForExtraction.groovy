package bard.db.enums

public enum ReadyForExtraction implements IEnumUserType {
    NOT_READY("Not Ready"),
    READY("Ready"),
    STARTED("Started"),
    COMPLETE("Complete")

    final String id

    private ReadyForExtraction(String id) {
        this.id = id
    }

    String getId() {
        return id
    }
    /**
     * Indicates whether a client can change
     * the current status of a resource to the given value.
     * The data export API is one such client, and is only allowed
     * to set specific values
     * @return
     */
    static boolean isAllowed(ReadyForExtraction readyForExtraction) {
        boolean isAllowed = false
        switch (readyForExtraction) {
            case READY:
            case STARTED:
            case COMPLETE:
                isAllowed = true
            default:
                // no op
            break
        }
        isAllowed
    }

    static ReadyForExtraction byId(String id) {
        ReadyForExtraction readyForExtraction = values().find { it.id == id }
        if (readyForExtraction) {
            return readyForExtraction
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}