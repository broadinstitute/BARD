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

public class PCResultUnit {

	private static Map<String, Integer> resultTypeIds = new HashMap();
	static {
		PCResultUnit[] resultTypes = new PCResultUnit[] {new PCResultUnit(1, "ppt"), new PCResultUnit(2, "ppm"), new PCResultUnit(3, "ppb"), 
				new PCResultUnit(4, "mm"), new PCResultUnit(5, "um"), new PCResultUnit(6, "nm"), new PCResultUnit(7, "pm"), new PCResultUnit(8, "fm"), 
				new PCResultUnit(9, "mgml"), new PCResultUnit(10, "ugml"), new PCResultUnit(11, "ngml"), new PCResultUnit(12, "pgml"), new PCResultUnit(13, "fgml"), 
				new PCResultUnit(14, "m"), new PCResultUnit(15, "percent"), new PCResultUnit(16, "ratio"), new PCResultUnit(17, "sec"), new PCResultUnit(18, "rsec"), 
				new PCResultUnit(19, "min"), new PCResultUnit(20, "rmin"), new PCResultUnit(21, "day"), new PCResultUnit(22, "rday"), new PCResultUnit(254, "none"), 
				new PCResultUnit(255, "unspecified")};
		for (PCResultUnit resultType : resultTypes)
			resultTypeIds.put(resultType.getValue(), resultType.getId());
	}

	public static Integer getResultTypeId(String resultType) {
		return resultTypeIds.get(resultType);
	}

	private Integer id;

	private String value;

	public PCResultUnit(Integer id, String value) {
		super();
		this.id = id;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
