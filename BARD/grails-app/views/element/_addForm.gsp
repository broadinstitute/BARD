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

<div class="span6 offset1">
    <g:form class="form-horizontal" action="saveTerm" id="saveTerm" name="saveTerm">
        <g:hiddenField name="currentElement.id" id="currentElement.id"
                       value="${termCommand?.parentElementId ?: ''}"/>
        <g:hiddenField name="parentElementId" id="parentElementId"
                       value="${termCommand?.parentElementId ?: ''}"/>
        <g:hiddenField name="parentLabel" id="parentLabel"
                       value="${termCommand?.parentLabel ?: ''}"/>
        <g:hiddenField name="parentDescription" id="parentDescription"
                       value="${termCommand?.parentDescription ?: ''}"/>
        <g:render template="addTermForm2nd"/>
        <div class="control-group">
            <label>
                <h4>5. Choose to save your proposed term.</h4>
            </label>
        </div>

        <div class="control-group">
            <div class="controls">
                <input type="submit" class="btn btn-primary" value="Save" id="saveBtn" ${termCommand?.success ? 'disabled=true' : ''}>
                <a href="javascript:closeWindow();">Close this window to cancel</a>
              </div>
        </div>
    </g:form>
</div>
