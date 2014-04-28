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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCAssay implements Serializable {

	private Integer activeCidCount; // eSummary
	private Integer activePanelCidCount; // eSummary
	private Integer activePanelSidCount; // eSummary
	private Integer activeSidCount; // eSummary
	private String activityOutcomeMethod = "";
	private Integer AID = -1;
	private String assayType = "";
	private List<PCAssayXRef> assayXRefs = new ArrayList<PCAssayXRef>();
	private List<PCAssayColumn> columns = new ArrayList<PCAssayColumn>();
	private String comment = "";
	private Map<String, String> comments = new HashMap<String, String>();
	private Date depositDate = null;
	private String description = "";
	private String extRegId = null;
	private String grantNumber = "";
	private Boolean hasScore = Boolean.FALSE;
	private Date holdUntilDate = null;
	private Integer id = -1;
	private Integer inactiveCidCount;
	private Integer inactiveSidCount;
	private Integer inconclusiveCidCount;
	private Integer inconclusiveSidCount;
	private Boolean isPanel = Boolean.FALSE;
	private Date lastDataChange = null;
	private Date modifyDate = null;
	private String name = null;
	private Integer numberOfCidsWithMicroMolActivity;
	private Integer numberOfCidsWithNanoMolActivity;
	private Integer numberOfSidsWithMicroMolActivity;
	private Integer numberOfSidsWithNanoMolActivity;
	private Boolean onHold = Boolean.FALSE;
	private String panelDescription = "";
	private String panelName = "";
	private List<PCAssayPanel> panels = new ArrayList<PCAssayPanel>();
	private Integer probeCidCount = null;
	private Integer probeSidCount = null;
	private String projectCategory = "";
	private String protocol = "";
	private Integer readoutCount = null;
	private Integer revision = null;
	private String sourceName = "";
	private Integer targetCount = null;
	private Integer totalCidCount = null;
	private Integer totalSidCount = null;
	private Integer unspecifiedCidCount = null;
	private Integer unspecifiedSidCount = null;
	private Integer version = null;
	private Boolean versionChanged = Boolean.FALSE;
	private List<PCAssayResult> results = new ArrayList<PCAssayResult>();

	private List<PCPlotLabel> plotLabels = new ArrayList<PCPlotLabel>();

	public List<PCPlotLabel> getPlotLabels() {
		return plotLabels;
	}

	public void setPlotLabels(List<PCPlotLabel> plotLabels) {
		this.plotLabels = plotLabels;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PCAssay))
			return false;
		PCAssay other = (PCAssay) obj;
		if (AID == null) {
			if (other.AID != null)
				return false;
		} else if (!AID.equals(other.AID))
			return false;
		return true;
	}

	public List<PCAssayResult> getResults() {
		return results;
	}

	public void setResults(List<PCAssayResult> results) {
		this.results = results;
	}

	public Integer getActiveCidCount() {
		return activeCidCount;
	}

	public PCAssayColumn getActiveColumn() {
		for (PCAssayColumn column : getColumns()) {
			if (column.isActiveConcentration())
				return column;
		}
		return null;
	}

	public Integer getActivePanelCidCount() {
		return activePanelCidCount;
	}

	public Integer getActivePanelSidCount() {
		return activePanelSidCount;
	}

	public Integer getActiveSidCount() {
		return activeSidCount;
	}

	public String getActivityOutcomeMethod() {
		return activityOutcomeMethod;
	}

	public String getActivityOutcomeMethodFormatted() {
		return PCOutcomeMethod.getActivityOutcomeMethodFormatted(getActivityOutcomeMethod());
	}

	public Integer getActivityOutcomeMethodId() {
		return PCOutcomeMethod.getActivityOutcomeMethodId(getActivityOutcomeMethod());
	}

	public Integer getAID() {
		return AID;
	}

	public String getAssayType() {
		return assayType;
	}

	public List<PCAssayXRef> getAssayXRefs() {
		return assayXRefs;
	}

	public Map<String, String> getCategorizedComments() {
		return comments;
	}

	public List<PCAssayColumn> getColumns() {
		return columns;
	}

	public String getComment() {
		return comment;
	}

	public Date getDepositDate() {
		return depositDate;
	}

	public String getDescription() {
		return description;
	}

	public String getExtRegId() {
		return extRegId;
	}

	public String getGrantNumber() {
		return grantNumber;
	}

	public Boolean getHasScore() {
		return hasScore;
	}

	public Date getHoldUntilDate() {
		return holdUntilDate;
	}

	public Integer getId() {
		return id;
	}

	public Integer getInactiveCidCount() {
		return inactiveCidCount;
	}

	public Integer getInactiveSidCount() {
		return inactiveSidCount;
	}

	public Integer getInconclusiveCidCount() {
		return inconclusiveCidCount;
	}

	public Integer getInconclusiveSidCount() {
		return inconclusiveSidCount;
	}

	public Date getLastDataChange() {
		return lastDataChange;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public String getName() {
		return name;
	}

	public Integer getNumberOfCidsWithMicroMolActivity() {
		return numberOfCidsWithMicroMolActivity;
	}

	public Integer getNumberOfCidsWithNanoMolActivity() {
		return numberOfCidsWithNanoMolActivity;
	}

	public Integer getNumberOfSidsWithMicroMolActivity() {
		return numberOfSidsWithMicroMolActivity;
	}

	public Integer getNumberOfSidsWithNanoMolActivity() {
		return numberOfSidsWithNanoMolActivity;
	}

	public Boolean getOnHold() {
		return onHold;
	}

	public String getPanelDescription() {
		return panelDescription;
	}

	public String getPanelName() {
		return panelName;
	}

	public List<PCAssayPanel> getPanels() {
		return panels;
	}

	public Integer getProbeCidCount() {
		return probeCidCount;
	}

	public Integer getProbeSidCount() {
		return probeSidCount;
	}

	public String getProjectCategory() {
		return projectCategory;
	}

	// @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	// @JoinColumn(name = "pcassay_xref_id")
	// @org.hibernate.annotations.Cascade(value =
	// org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	// @org.hibernate.annotations.IndexColumn(name = "pcassay_xref_position")
	public String getProjectCategoryFormatted() {
		return PCProjectCategory.getProjectCategoryFormatted(getProjectCategory());
	}

	public Integer getProjectCategoryId() {
		return PCProjectCategory.getProjectCategoryId(getProjectCategory());
	}

	public String getProtocol() {
		return protocol;
	}

	public PCAssayColumn getQualifierColumn() {
		PCAssayColumn activeCol = getActiveColumn();
		if (activeCol != null) {
			for (PCAssayColumn column : getColumns()) {
				if (column.getTID() != null && 1 == (activeCol.getTID() - column.getTID())
						&& column.getName().toLowerCase().contains("qualifier"))
					return column;
			}
		}
		return null;
	}

	public Integer getReadoutCount() {
		return readoutCount;
	}

	public Integer getRevision() {
		return revision;
	}

	public String getSourceName() {
		return sourceName;
	}

	public Integer getTargetCount() {
		return targetCount;
	}

	public List<PCAssayColumn> getTestedColumns() {
		List<PCAssayColumn> testedCols = new ArrayList<PCAssayColumn>();
		for (PCAssayColumn col : getColumns()) {
			if (col.getTestedConcentration() != null) {
				testedCols.add(col);
			}
		}
		Collections.sort(testedCols, new java.util.Comparator<PCAssayColumn>() {
			public int compare(PCAssayColumn c1, PCAssayColumn c2) {
				return c1.getTID().compareTo(c2.getTID());
			}
		});
		return testedCols;
	}

	public Integer getTotalCidCount() {
		return totalCidCount;
	}

	public Integer getTotalSidCount() {
		return totalSidCount;
	}

	public Integer getUnspecifiedCidCount() {
		return unspecifiedCidCount;
	}

	public Integer getUnspecifiedSidCount() {
		return unspecifiedSidCount;
	}

	public Integer getVersion() {
		return version;
	}

	public Boolean getVersionChanged() {
		return versionChanged;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((AID == null) ? 0 : AID.hashCode());
		return result;
	}

	public Boolean isPanel() {
		return isPanel;
	}

	public void setActiveCidCount(Integer activeCidCount) {
		this.activeCidCount = activeCidCount;
	}

	public void setActivePanelCidCount(Integer activePanelCidCount) {
		this.activePanelCidCount = activePanelCidCount;
	}

	public void setActivePanelSidCount(Integer activePanelSidCount) {
		this.activePanelSidCount = activePanelSidCount;
	}

	public void setActiveSidCount(Integer activeSidCount) {
		this.activeSidCount = activeSidCount;
	}

	public void setActivityOutcomeMethod(String activityOutcomeMethod) {
		this.activityOutcomeMethod = activityOutcomeMethod;
	}

	public void setAID(Integer aid) {
		AID = aid;
	}

	public void setAssayType(String assayType) {
		this.assayType = assayType;
	}

	public void setAssayXRefs(List<PCAssayXRef> assayXRefs) {
		this.assayXRefs = assayXRefs;
	}

	public void setCategorizedComments(Map<String, String> comments) {
		this.comments = comments;
	}

	public void setColumns(List<PCAssayColumn> columns) {
		this.columns = columns;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}

	public void setDescription(String assayDescription) {
		this.description = assayDescription;
	}

	public void setExtRegId(String extRegId) {
		this.extRegId = extRegId;
	}

	public void setGrantNumber(String grantNumber) {
		this.grantNumber = grantNumber;
	}

	public void setHasScore(Boolean hasScore) {
		this.hasScore = hasScore;
	}

	public void setHoldUntilDate(Date holdUntilDate) {
		this.holdUntilDate = holdUntilDate;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	// @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	// @JoinColumn(name = "aid", referencedColumnName = "aid")
	// public List<PCAssayResult> getResults() {
	// return results;
	// }
	//
	// public void setResults(List<PCAssayResult> results) {
	// this.results = results;
	// }

	public void setInactiveCidCount(Integer inactiveCidCount) {
		this.inactiveCidCount = inactiveCidCount;
	}

	public void setInactiveSidCount(Integer inactiveSidCount) {
		this.inactiveSidCount = inactiveSidCount;
	}

	public void setInconclusiveCidCount(Integer inconclusiveCidCount) {
		this.inconclusiveCidCount = inconclusiveCidCount;
	}

	public void setInconclusiveSidCount(Integer inconclusiveSidCount) {
		this.inconclusiveSidCount = inconclusiveSidCount;
	}

	public void setLastDataChange(Date lastDataChange) {
		this.lastDataChange = lastDataChange;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public void setName(String assayName) {
		this.name = assayName;
	}

	public void setNumberOfCidsWithMicroMolActivity(Integer numberofCidsWithMicroMActivity) {
		this.numberOfCidsWithMicroMolActivity = numberofCidsWithMicroMActivity;
	}

	public void setNumberOfCidsWithNanoMolActivity(Integer numberofCidsWithNanoMActivity) {
		this.numberOfCidsWithNanoMolActivity = numberofCidsWithNanoMActivity;
	}

	public void setNumberOfSidsWithMicroMolActivity(Integer numberofSidsWithMicroMActivity) {
		this.numberOfSidsWithMicroMolActivity = numberofSidsWithMicroMActivity;
	}

	public void setNumberOfSidsWithNanoMolActivity(Integer numberofSidsWithNanoMActivity) {
		this.numberOfSidsWithNanoMolActivity = numberofSidsWithNanoMActivity;
	}

	public void setOnHold(Boolean onHold) {
		this.onHold = onHold;
	}

	public void setPanel(Boolean isPanel) {
		this.isPanel = isPanel;
	}

	public void setPanelDescription(String panelDescription) {
		this.panelDescription = panelDescription;
	}

	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}

	public void setPanels(List<PCAssayPanel> panels) {
		this.panels = panels;
	}

	public void setProbeCidCount(Integer probeCidCount) {
		this.probeCidCount = probeCidCount;
	}

	public void setProbeSidCount(Integer probeSidCount) {
		this.probeSidCount = probeSidCount;
	}

	public void setProjectCategory(String projectCategory) {
		this.projectCategory = projectCategory;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setReadoutCount(Integer readoutCount) {
		this.readoutCount = readoutCount;
	}

	public void setRevision(Integer revision) {
		this.revision = revision;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public void setTargetCount(Integer targetCount) {
		this.targetCount = targetCount;
	}

	public void setTotalCidCount(Integer totalCidCount) {
		this.totalCidCount = totalCidCount;
	}

	public void setTotalSidCount(Integer totalSidCount) {
		this.totalSidCount = totalSidCount;
	}

	public void setUnspecifiedCidCount(Integer unspecifiedCidCount) {
		this.unspecifiedCidCount = unspecifiedCidCount;
	}

	public void setUnspecifiedSidCount(Integer unspecifiedSidCount) {
		this.unspecifiedSidCount = unspecifiedSidCount;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void setVersionChanged(Boolean versionChanged) {
		this.versionChanged = versionChanged;
	}
}
