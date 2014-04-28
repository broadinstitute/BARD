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

package bard.db.registration

/**
 * Naming here can probably be improved, Fixed
 *
 * See https://github.com/broadinstitute/BARD/wiki/Business-rules#attribute_type-and-expected-value-type
 *
 * for how this interacts with expected_value_type
 */
enum AttributeType {
    /**  meaning the value portion of the AssayContextItem is set / fixed when the assayContextItem is created or edited
     */
	Fixed,   //
    /**
     *  Meaning there will be a list of values that will be entered when the assayContextItem is created or enter and used to constrain data at ResultUpload time
     */
	List,
    /**
     * Meaning a numeric range will be set at a  assayContextItem is created or enter
     */
	Range,
    /**
     * Free is a bit tough on naming, it means that the assayContextItem will be created at assay definition time to indicate that
     * at ResultUpload time contextItem with this attribute should be created at the Result and or ExperimentContext level
     *
     * This is only appropriate with expectedValueTypes of 'free text' and 'numeric'
     */
	Free

}
