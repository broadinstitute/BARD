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

<%@page defaultCodec="none"%>
<%@ page import="bardqueryapi.IDSearchType" %>

<r:require module="idSearch"/>
<div class="modal hide" id="idModalDiv">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">×</a>

        <h3>Enter a Comma separated list of IDs</h3>
    </div>

    <div class="modal-body">
        <textarea class="field span9" id="idSearchString" name="idSearchString" rows="15"></textarea>
    </div>

    <div class="modal-footer">
        <g:form name="idSearchForm" class="form-inline">
            <div>
                <g:radioGroup name="idSearchType"
                              values="${IDSearchType.values()}"
                              value="${IDSearchType.ALL}"
                              labels="${IDSearchType.values().label}">
                    <label class="radio inline"><%=it.radio%>${it.label}</label>
                </g:radioGroup>
            </div>

            <br>

            <div>
                <a href="#" class="btn" data-dismiss="modal" id="closeButton21">Close</a>
                <a href="#" class="idSearchButton btn btn-primary" data-dismiss="modal">Search</a>
            </div>
        </g:form>
    </div>

</div>
