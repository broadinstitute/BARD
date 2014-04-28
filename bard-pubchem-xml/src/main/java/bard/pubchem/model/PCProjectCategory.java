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

import java.util.HashMap;
import java.util.Map;

public class PCProjectCategory {

	private static Map<String, Integer> projectIds = new HashMap();
	private static Map<String, String> projectNames = new HashMap();
	static {
		PCProjectCategory[] categories = new PCProjectCategory[] { new PCProjectCategory(1, "mlscn", "MLSCN"),
				new PCProjectCategory(2, "mlpcn", "MLPCN"), new PCProjectCategory(3, "mlscn-ap", "MLSCN (Assay Provider)"),
				new PCProjectCategory(4, "mlpcn-ap", "MLPCN (Assay Provider)"),
				new PCProjectCategory(7, "literature-extracted", "Literature (Extracted)"),
				new PCProjectCategory(8, "literature-author", "Literature (Author)"), new PCProjectCategory(10, "rnaigi", "RNAi Global Initiative"),
				new PCProjectCategory(6, "assay-vendor", "Assay Vendor"), new PCProjectCategory(255, "other", "Other") };
		for (PCProjectCategory category : categories)
			projectNames.put(category.getKey(), category.getName());
		for (PCProjectCategory category : categories)
			projectIds.put(category.getKey(), category.getId());
	}

	public static String getProjectCategoryFormatted(String category) {
		return projectNames.get(category);
	}
	public static Integer getProjectCategoryId(String category) {
		return projectIds.get(category);
	}

	private Integer id;

	private String key;

	private String name;

	public PCProjectCategory(Integer id, String key, String name) {
		super();
		this.id = id;
		this.key = key;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}
}
