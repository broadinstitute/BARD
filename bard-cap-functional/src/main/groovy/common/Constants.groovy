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

package common

/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class Constants {
	//Enumerations
	enum ContextItem{ ADD, UPDATE, DELETE }
	enum ExpectedValueType{ ELEMENT, ONTOLOGY, NUMERIC, FREE, NONE }
	enum DocumentAs{ CONTENTS, URL}
	enum NavigateTo { ASSAY_BY_ID, ASSAY_BY_NAME, PROJECT_BY_ID, PROJECT_BY_NAME }
	enum SearchBy { ASSAY_ID, ASSAY_NAME, PROJECT_ID, PROJECT_NAME }

	// constants
	final static int WAIT_INTERVAL = 30;
	final static int R_INTERVAL = 0.5;
	final static String edited = "-Edited";
	final static String IS_EMPTY = "Empty";
	
	final static int index_0 = 0;
	final static int index_1 = 1;
	final static int index_2 = 2;
	final static int index_3 = 3;

	final static def ValueConstraints = [Free:"Free",List:"List", Range:"Range"]
	
	final static def documentHeader = [Description:"description",Protocol:"protocol",Comment:"comment",Publication:"publication",Urls:"urls",Other:"other"]
	final static def documentType = [Description:"description",Protocol:"protocol",Comment:"comments",Publication:"publication",Urls:"external url",Other:"other"]

}
