package bard.db.enums

import groovy.transform.InheritConstructors
import groovy.transform.ToString;

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

    static ReadyForExtraction byId(String id) {
        ReadyForExtraction readyForExtraction = values().find{ it.id == id}
        if (readyForExtraction){
            return readyForExtraction
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}