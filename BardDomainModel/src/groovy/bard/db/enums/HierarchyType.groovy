package bard.db.enums

public enum HierarchyType implements IEnumUserType {

    SUPPORTED_BY("supported by"),
    CALCULATED_FROM("calculated from")


//	static list(){
//		[SUPPORTED_BY, CALCULATED_FROM]
//	}


    final String id;

    private HierarchyType(String id) {
        this.id = id
    }

    String getId() {
        return id
    }

    static HierarchyType byId(String id) {
        HierarchyType hierarchyType = values().find { it.id == id }
        if (hierarchyType) {
            return hierarchyType
        }
        throw new EnumNotFoundException("No enum found for id: $id")
    }

}
