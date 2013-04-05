package bard.validation.ext;

/**
 * Captures Id and display name from an External Ontology search.
 * 
 * @author southern
 * 
 */
public class ExternalItem {

	private String display;
	private String id;

	public ExternalItem(String id, String display) {
		super();
		this.id = id;
		this.display = display;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalItem other = (ExternalItem) obj;
		if (display == null) {
			if (other.display != null)
				return false;
		} else if (!display.equals(other.display))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getDisplay() {
		return display;
	}

	public String getId() {
		return id;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public void setId(String id) {
		this.id = id;
	}

}