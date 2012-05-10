package bard.db.experiment

public enum GroupType {
	
	Project("Project"),
	Campaign("Campaign"),
	Panel("Panel"),
	Study("Study")
	
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
		[GroupType.Project, GroupType.Campaign, GroupType.Panel, GroupType.Study]
	}
}
