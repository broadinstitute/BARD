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
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class PCAssayPanel implements Serializable {

	private String activityOutcomeMethod = "";
	private PCAssay assay = null;
	private String comment = "";
	private String description = "";
	private Integer id = -1;
	private String name = "";
	private Integer panelNumber = -1;
	private String protocol = "";

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PCAssayPanel))
			return false;
		PCAssayPanel other = (PCAssayPanel) obj;
		if (panelNumber == null) {
			if (other.panelNumber != null)
				return false;
		} else if (!panelNumber.equals(other.panelNumber))
			return false;
		if (assay == null) {
			if (other.assay != null)
				return false;
		} else if (!assay.equals(other.assay))
			return false;
		return true;
	}

	public String getActivityOutcomeMethod() {
		return activityOutcomeMethod;
	}

	public PCAssay getAssay() {
		return assay;
	}

	public String getComment() {
		return comment;
	}

	public String getDescription() {
		return description;
	}

	public XRef getGene() {
		List<PCAssayXRef> list = new ArrayList<PCAssayXRef>();
		CollectionUtils.select(assay.getAssayXRefs(), new Predicate() {
			public boolean evaluate(Object object) {
				PCAssayXRef xref = (PCAssayXRef) object;
				if (xref.getPanel() != null)
					if (xref.getPanel().getPanelNumber() == getPanelNumber() && xref.getXRef() != null
							&& "gene".equals(xref.getXRef().getDatabase()))
						return true;
				return false;
			}
		}, list);
		return list.size() > 0 ? list.get(0).getXRef() : null;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getPanelNumber() {
		return panelNumber;
	}

	public String getProtocol() {
		return protocol;
	}

	public XRef getTarget() {
		List<PCAssayXRef> list = new ArrayList<PCAssayXRef>();
		CollectionUtils.select(assay.getAssayXRefs(), new Predicate() {
			public boolean evaluate(Object object) {
				PCAssayXRef xref = (PCAssayXRef) object;
				if (xref.getPanel() != null)
					if (xref.getPanel().getPanelNumber() == getPanelNumber() && xref.isTarget() == true)
						return true;
				return false;
			}
		}, list);
		return list.size() > 0 ? list.get(0).getXRef() : null;
	}

	public XRef getTaxonomy() {
		List<PCAssayXRef> list = new ArrayList<PCAssayXRef>();
		CollectionUtils.select(assay.getAssayXRefs(), new Predicate() {
			public boolean evaluate(Object object) {
				PCAssayXRef xref = (PCAssayXRef) object;
				if (xref.getPanel() != null)
					if (xref.getPanel().getPanelNumber() == getPanelNumber())
						if (xref.getXRef() != null && "taxonomy".equals(xref.getXRef().getDatabase()))
							return true;
				return false;
			}
		}, list);
		return list.size() > 0 ? list.get(0).getXRef() : null;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((panelNumber == null) ? 0 : panelNumber.hashCode());
		result = prime * result + ((assay == null) ? 0 : assay.hashCode());
		return result;
	}

	public void setActivityOutcomeMethod(String activityOutcomeMethod) {
		this.activityOutcomeMethod = activityOutcomeMethod;
	}

	public void setAssay(PCAssay assay) {
		this.assay = assay;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public void setPanelNumber(Integer panelNumber) {
		this.panelNumber = panelNumber;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
