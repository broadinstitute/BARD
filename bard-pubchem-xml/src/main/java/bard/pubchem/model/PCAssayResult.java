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

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.list.GrowthList;

// indexes are only created on "create", not "update"
public class PCAssayResult implements Serializable {
	private List<String> allValues = GrowthList.decorate(new ArrayList<String>());
	private PCAssay assay = null;
	private Long CID = null;
	private String comments = "";
	private Long id = -1L;
	private String outcome = "";
	private Integer rankScore = null;
	private Long SID = null;
	private String URL = "";
	private String Xref = "";

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PCAssayResult))
			return false;
		PCAssayResult other = (PCAssayResult) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public List<String> getAllValues() {
		return allValues;
	}

	public PCAssay getAssay() {
		return assay;
	}

	// @Index(name = "idx_pcassay_result_cid") // define at class level instead.
	// Include both and you get 2 indexes!
	public Long getCID() {
		return CID;
	}

	public String getComments() {
		return comments;
	}

	public Long getId() {
		return id;
	}

	public String getOutcome() {
		return outcome;
	}

	public Integer getRankScore() {
		return rankScore;
	}

	// @Index(name = "idx_pcassay_result_sid") // define at class level instead.
	// Include both and you get 2 indexes!
	public Long getSID() {
		return SID;
	}

	public String getURL() {
		return URL;
	}

	public Object getValue(PCAssayColumn column) {
		Object obj = ConvertUtils.convert(getAllValues().get(column.getTID() - 1), column.getTypeClass());
		return obj;
	}

	public String getXref() {
		return Xref;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setAllValues(List<String> allValues) {
		this.allValues = allValues;
	}

	public void setAssay(PCAssay assay) {
		this.assay = assay;
	}

	public void setCID(Long cid) {
		CID = cid;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public void setRankScore(Integer rankScore) {
		this.rankScore = rankScore;
	}

	public void setSID(Long sid) {
		SID = sid;
	}

	public void setURL(String url) {
		URL = url;
	}

	public void setXref(String xref) {
		Xref = xref;
	}

	public String toString() {
		return this.getAllValues().toString();
	}
}
