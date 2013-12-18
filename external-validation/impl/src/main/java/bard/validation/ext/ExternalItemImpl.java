package bard.validation.ext;

/**
 * Captures Id and display name from an External Ontology search.
 * 
 * @author southern
 * 
 */
public class ExternalItemImpl implements ExternalItem {

	private String display;
	private String id;

	public ExternalItemImpl(String id, String display) {
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
			if (other.getDisplay() != null)
				return false;
		} else if (!display.equals(other.getDisplay()))
			return false;
		if (id == null) {
			if (other.getId() != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;
		return true;
	}

	@Override public String getDisplay() {
		return display;
	}

	@Override public String getId() {
		return id;
	}

	@Override public void setDisplay(String display) {
		this.display = display;
	}

	@Override public void setId(String id) {
		this.id = id;
	}
	
	public String toString() {
		return String.format("%s[id=%s, name=%s]", getClass().getName(), getId(), getDisplay());
	}
}