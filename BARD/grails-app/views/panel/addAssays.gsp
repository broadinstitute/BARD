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

<%@ page import="bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Add Assay(s) To Panel</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <h3>Add Assay(s) To Panel</h3>
    </div>
    <g:if test="${draftAssays || retiredAssays}">
        <div class="row-fluid">
            <div class="span12">
                <div class="ui-widget">
                    <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                        <span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>

                        <p><strong>Only approved assay definitions can be added to a panel.</strong></p>
                        <g:if test="${draftAssays}">
                            The following assay definitions are still draft:
                            <g:each in="${draftAssays}" var="draftAssay">
                                ${draftAssay.id}
                            </g:each>
                            <br/><br/>
                        </g:if>
                        <g:if test="${retiredAssays}">
                            The following assay definitions are retired:
                            <g:each in="${retiredAssays}" var="retiredAssay">
                                ${retiredAssay.id}
                            </g:each>
                        </g:if>

                    </div>
                </div>
            </div>
        </div>
    </g:if>
    <br/>
    <br/>

    <div class="row-fluid">
        <div class="span12">
            <g:form class="form-horizontal" action="addAssays" controller="panel">
                <g:hiddenField name="id" value="${associatePanelCommand?.id}"/>
                <div class="control-group ${hasErrors(bean: associatePanelCommand, field: 'assayIds', 'error')}">
                    <label class="control-label" for="assayIds">
                        <g:message code="panel.addAssay.label"/>:</label>

                    <div class="controls">
                        <g:textArea id="assayIds" name="assayIds" value="${associatePanelCommand?.assayIds}"
                                    required=""/>
                        <span class="help-inline"><g:fieldError field="assayIds" bean="associatePanelCommand"/></span>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <g:link controller="panel" action="myPanels"
                                class="btn">Cancel</g:link>
                        <input type="submit" class="btn btn-primary" value="Add To Panel">
                    </div>
                </div>

            </g:form>
        </div>
    </div>
</div>

</body>
</html>
