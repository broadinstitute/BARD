package bard.db.registration

public enum AttributeType {
	
	FIXED("Fixed"),
	LIST("List"),
	RANGE("Range"),
	NUMBER("Number")
	
	final String value
	
	AttributeType(String value){
		this.value = value
	}
	
	String toString() {
		value
	}
	
	String getKey() {
		name()
	}
	
	static list(){
		[FIXED, LIST, RANGE, NUMBER]
	}
		  
}
