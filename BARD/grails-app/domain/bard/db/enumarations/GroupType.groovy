package bard.db.enumarations

public enum GroupType {
	
	PROJECT("Project"),
	CAMPAIGN("Campaign"),
	PANEL("Panel"),
	STUDY("Study")
	
	final String value
	
	GroupType(String value){
		this.value = value
	}
	
	String toString() {
		value
	}
	
	String getKey() {
		name()
	}
	
	static list(){
		[PROJECT, CAMPAIGN, PANEL, STUDY]
	}
}
