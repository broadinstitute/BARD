package bard.validation.ext;

/**
 * Captures Id and display name from an External Ontology search.
 * @author southern
 *
 */
public class ExternalItem {

	private String id;
	private String display;
	
	public ExternalItem(String id, String display) {
		super();
		this.id = id;
		this.display = display;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
}