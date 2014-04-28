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

public class PCAssayXRef implements Serializable {

	private PCAssay assay = null;
	private String comment = "";
	private Long id = -1L;
	private PCAssayPanel panel = null;
	private Boolean target = false;
	private Long taxon = -1L;
	private String taxonName = "";
	private String taxonCommon = "";
	private XRef xRef = null;

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PCAssayXRef))
			return false;
		PCAssayXRef other = (PCAssayXRef) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public PCAssay getAssay() {
		return assay;
	}

	public String getComment() {
		return comment;
	}

	public Long getId() {
		return id;
	}

	public PCAssayPanel getPanel() {
		return panel;
	}

	public Long getTaxon() {
		return taxon;
	}

	public String getTaxonCommon() {
		return taxonCommon;
	}

	public String getTaxonName() {
		return taxonName;
	}

	public XRef getXRef() {
		return xRef;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public Boolean isTarget() {
		return target;
	}

	public void setAssay(PCAssay assay) {
		this.assay = assay;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPanel(PCAssayPanel panel) {
		this.panel = panel;
	}

	public void setTarget(Boolean target) {
		this.target = target;
	}

	public void setTaxon(Long taxon) {
		this.taxon = taxon;
	}

	public void setTaxonCommon(String taxonCommon) {
		this.taxonCommon = taxonCommon;
	}

	public void setTaxonName(String taxonName) {
		this.taxonName = taxonName;
	}

	public void setXRef(XRef ref) {
		xRef = ref;
	}

	public String toString() {
		// return ToStringBuilder.reflectionToString(this);
		return String.format("assay = %s, panel = %s, xref=%s", getAssay().getId(), null, getXRef().getId());
	}

}
