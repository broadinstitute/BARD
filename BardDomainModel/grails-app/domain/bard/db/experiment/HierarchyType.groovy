package bard.db.experiment

public enum HierarchyType {
	
	Child("Child"),
	Derives("Derives")
	
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
		[Child, Derives]
	}
}
