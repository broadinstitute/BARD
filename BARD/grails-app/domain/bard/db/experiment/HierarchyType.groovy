package bard.db.experiment

public enum HierarchyType {
	
	CHILD("Child"),
	DERIVES("Derives")
	
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
		[CHILD, DERIVES]
	}
}
