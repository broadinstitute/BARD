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

public class PCOutcomeMethod {

	private static Map<String, Integer> outcomeIds = new HashMap();
	private static Map<String, String> outcomeNames = new HashMap();
	static {
		PCOutcomeMethod[] outcomes = new PCOutcomeMethod[] { new PCOutcomeMethod(0, "other", "Other"),
				new PCOutcomeMethod(1, "screening", "Screening"), new PCOutcomeMethod(2, "confirmatory", "Confirmatory"),
				new PCOutcomeMethod(3, "summary", "Summary"), };

		for (PCOutcomeMethod outcome : outcomes)
			outcomeNames.put(outcome.getKey(), outcome.getName());
		for (PCOutcomeMethod outcome : outcomes)
			outcomeIds.put(outcome.getKey(), outcome.getId());
	}

	public static String getActivityOutcomeMethodFormatted(String method) {
		return outcomeNames.get(method);
	}
	public static Integer getActivityOutcomeMethodId(String method) {
		return outcomeIds.get(method);
	}

	private Integer id;

	private String key;

	private String name;

	public PCOutcomeMethod(Integer id, String key, String name) {
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
