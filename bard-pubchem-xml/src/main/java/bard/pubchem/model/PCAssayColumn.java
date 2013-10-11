/*
 * Copyright 2010 The Scripps Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bard.pubchem.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PCAssayColumn implements Serializable {

	private boolean activeConcentration;
	private PCAssay assay = null;
	private Integer curvePlotLabel;
	private String description = "";
	private Integer id = -1;
	private String name = "";
	private PCAssayPanel panel = null;
	private String panelReadoutType;
	private Double testedConcentration;
	private String testedConcentrationUnit;
	private Integer TID;
	private String type;

	private Class<?> typeClass = null;

	private String unit;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PCAssayColumn))
			return false;
		PCAssayColumn other = (PCAssayColumn) obj;
		if (TID == null) {
			if (other.TID != null)
				return false;
		} else if (!TID.equals(other.TID))
			return false;
		if (assay == null) {
			if (other.assay != null)
				return false;
		} else if (!assay.equals(other.assay))
			return false;
		return true;
	}

	public PCAssay getAssay() {
		return assay;
	}

	public Integer getCurvePlotLabel() {
		return curvePlotLabel;
	}

	public String getDescription() {
		return description;
	}

	public String getEndpointType() {
		String[] patterns = new String[] { "([AEIC]C50)", "(MIC)", "(Ki)", "(Potency)", "(Kd)" };
		for (String pattern : patterns) {
			Pattern rxPattern = Pattern.compile(pattern);
			Matcher matcher = rxPattern.matcher(getName());
			if (matcher.find()) {
				return matcher.group(1);
			} else {
				matcher = rxPattern.matcher(getDescription());
				if (matcher.find()) {
					return matcher.group(1);
				}
			}
		}
		return null;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public PCAssayPanel getPanel() {
		return panel;
	}

	public String getPanelReadoutType() {
		return panelReadoutType;
	}

	public Integer getPanelReadoutTypeId() {
		return PCPanelReadout.getReadoutTypeId(getPanelReadoutType());
	}

	public Double getTestedConcentration() {
		return testedConcentration;
	}

	public String getTestedConcentrationUnit() {
		return testedConcentrationUnit;
	}

	public Integer getTID() {
		return TID;
	}

	public String getType() {
		return type;
	}

	public Class<?> getTypeClass() {
		if (null == typeClass) {
			if ("float".equals(getType()))
				typeClass = Double.class;
			else if ("string".equals(getType()))
				typeClass = String.class;
			else if ("int".equals(getType()))
				typeClass = Integer.class;
			else if ("bool".equals(getType()))
				typeClass = Boolean.class;
		}
		return typeClass;
	}

	public Integer getTypeId() {
		return PCResultType.getResultTypeId(getType());
	}

	public String getUnit() {
		return unit;
	}

	public Integer getUnitId() {
		return PCResultUnit.getResultTypeId(getUnit());
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((TID == null) ? 0 : TID.hashCode());
		result = prime * result + ((assay == null) ? 0 : assay.hashCode());
		return result;
	}

	public boolean isActiveConcentration() {
		return activeConcentration;
	}

	public void setActiveConcentration(boolean activeConcentration) {
		this.activeConcentration = activeConcentration;
	}

	public void setAssay(PCAssay assay) {
		this.assay = assay;
	}

	public void setCurvePlotLabel(Integer curvePlotLabel) {
		this.curvePlotLabel = curvePlotLabel;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPanel(PCAssayPanel panel) {
		this.panel = panel;
	}

	public void setPanelReadoutType(String panelReadoutType) {
		this.panelReadoutType = panelReadoutType;
	}

	public void setTestedConcentration(Double testedConcentration) {
		this.testedConcentration = testedConcentration;
	}

	public void setTestedConcentrationUnit(String testedConcentrationUnit) {
		this.testedConcentrationUnit = testedConcentrationUnit;
	}

	public void setTID(Integer tid) {
		TID = tid;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}