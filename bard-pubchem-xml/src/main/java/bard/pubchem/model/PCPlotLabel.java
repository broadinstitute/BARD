package bard.pubchem.model;

public class PCPlotLabel {

	private Integer id = -1;
	private PCAssay assay = null;
	private Integer plotLabel;
	private String title = null;
	private String concentrationTitle = null;
	private String responseTitle = null;
	private Boolean derivedByEquation;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PCPlotLabel other = (PCPlotLabel) obj;
		if (assay == null) {
			if (other.assay != null)
				return false;
		} else if (!assay.equals(other.assay))
			return false;
		if (plotLabel == null) {
			if (other.plotLabel != null)
				return false;
		} else if (!plotLabel.equals(other.plotLabel))
			return false;
		return true;
	}

	public PCAssay getAssay() {
		return assay;
	}

	public String getConcentrationTitle() {
		return concentrationTitle;
	}

	public Integer getPlotLabel() {
		return plotLabel;
	}

	public Boolean getDerivedByEquation() {
		return derivedByEquation;
	}

	public Integer getId() {
		return id;
	}

	public String getResponseTitle() {
		return responseTitle;
	}

	public String getTitle() {
		return title;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assay == null) ? 0 : assay.hashCode());
		result = prime * result + ((plotLabel == null) ? 0 : plotLabel.hashCode());
		return result;
	}

	public void setAssay(PCAssay assay) {
		this.assay = assay;
	}

	public void setConcentrationTitle(String concentrationTitle) {
		this.concentrationTitle = concentrationTitle;
	}

	public void setPlotLabel(Integer plotLabel) {
		this.plotLabel = plotLabel;
	}

	public void setDerivedByEquation(Boolean derivedByEquation) {
		this.derivedByEquation = derivedByEquation;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setResponseTitle(String responseTitle) {
		this.responseTitle = responseTitle;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}