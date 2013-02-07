package bard.util.db;

public class ColumnResultType {

	public String column_name;
	public String column_description;
	public String result_type;
	public String stats_modifier;
	public String attribute_1;
	
	public String getColumn_name() {
		return column_name;
	}
	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}
	public String getColumn_description() {
		return column_description;
	}
	public void setColumn_description(String column_description) {
		this.column_description = column_description;
	}
	public String getResult_type() {
		return result_type;
	}
	public void setResult_type(String result_type) {
		this.result_type = result_type;
	}
	public String getStats_modifier() {
		return stats_modifier;
	}
	public void setStats_modifier(String stats_modifier) {
		this.stats_modifier = stats_modifier;
	}
	public String getAttribute_1() {
		return attribute_1;
	}
	public void setAttribute_1(String attribute_1) {
		this.attribute_1 = attribute_1;
	}
}