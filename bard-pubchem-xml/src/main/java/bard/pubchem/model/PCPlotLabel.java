/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
