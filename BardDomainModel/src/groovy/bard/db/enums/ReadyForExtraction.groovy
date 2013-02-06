package bard.db.enums;

public enum ReadyForExtraction implements IEnumUserType {
    NOT_READY("Not Ready"),
    READY("Ready"),
    STARTED("Started"),
    COMPLETE("Complete");

    private final String id;

    private ReadyForExtraction(String id) {
        this.id = id;
    }

    public String getId() {
        return id
    }

}