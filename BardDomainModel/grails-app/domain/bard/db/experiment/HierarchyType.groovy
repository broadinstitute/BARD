package bard.db.experiment

public enum HierarchyType {

    SUPPORTED_BY("supported by"),
    CALCULATED_FROM("calculated from")
	
	final String value
	
	HierarchyType(String value){
		this.value = value
	}
	
	String toString() {
		value
	}
	
	String getKey() {
		name()
	}
	
	static list(){
		[SUPPORTED_BY, CALCULATED_FROM]
	}

    static Map<String, HierarchyType> byValue = new HashMap();
    static {
        for(hierarchyType in HierarchyType.values()) {
            byValue.put(hierarchyType.value, hierarchyType)
        }
    }

    static HierarchyType getByValue(String value) {
        return byValue.get(value);
    }
}
