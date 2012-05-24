package db.registration

public enum AttributeType {
	
	Fixed("Fixed"),
	List("List"),
	Range("Range"),
	Number("Number")
	
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
		[Fixed, AttributeType.List, AttributeType.Range, AttributeType.Number]
	}
		  
}
