%{-- Copyright (c) 2014, The Broad Institute
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
 --}%

<div class="well well-small">
	Add new numeric value and unit:
</div>
<form id="edit_numeric_item_form" class="form-horizontal" >
	<input type="hidden" id="attributeElementId" name="attributeElementId" value="${assayContextItem.attributeElement?.id}"/>
	<input type="hidden" id="attributeElementUnitId" name="attributeElementUnitId" value="${assayContextItem.attributeElement?.unit?.id}"/>
	<input type="hidden" id="valueUnitLabel" name="valueUnitLabel" value="${assayContextId}"/>
	<div class="control-group">
		<label class="control-label">Numeric Value:</label>
		<div class="controls">
			<input type="text" id="numericValue" name='numericValue' value="${assayContextItem?.valueNum}">
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">Unit:</label>
		<div class="controls">
			<%-- This hidden control becomes the units selection box --%>
            <input type="hidden" id="valueUnitId" name="valueUnitId">
		</div>
	</div>
	<input type="hidden" id="assayContextItemId" name="assayContextItemId" value="${assayContextItem.id}"/>
   	<input type="hidden" id="assayContextId" name="assayContextId" value="${assayContextId}"/>
</form>
