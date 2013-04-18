package bard.db.experiment

public enum HierarchyType {

    IS_RELATED_TO("is related to"),
    IS_CALCULATED_FROM("is calculated from")
	
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
		[IS_RELATED_TO, IS_CALCULATED_FROM]
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
