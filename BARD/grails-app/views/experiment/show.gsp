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

<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils; bard.db.enums.Status; bard.db.dictionary.Element; bard.db.model.AbstractContextItem; bard.db.model.AbstractContext; bard.db.enums.Status; bard.db.enums.ContextType; bard.db.registration.DocumentKind; bard.db.model.AbstractContextOwner; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix,xeditable,experimentsummary,canEditWidget,richtexteditorForEdit, sectionCounter, card,histogram"/>
    <meta name="layout" content="basic"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>EID ${instance?.id}: ${instance?.permittedToSeeEntity() ? instance?.experimentName : 'Experiment Pending'}</title>

</head>

<body>

<g:if test="${flash.error}">
    <div class="row-fluid">
        <div class="span12">
            <div class="alert alert-error">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>${flash.error}</strong>
            </div>
        </div>
    </div>
</g:if>
<g:if test="${flash.success}">
    <div class="row-fluid">
        <div class="span12">
            <div class="alert alert-success">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>${flash.success}</strong>
            </div>
        </div>
    </div>
</g:if>

<g:if test="${instance?.id}">
    <g:if test="${instance?.permittedToSeeEntity()}">
        <g:render template="showExperiment"
                  model="[instance: instance, editable: editable, contextItemSubTemplate: contextItemSubTemplate, contextIds: contextIds, isAdmin: isAdmin]"/>
    </g:if>
    <g:else>
        <g:render template="/layouts/templates/handleDraftEntities" model="[entity: 'Experiment']"/>
    </g:else>
</g:if>

</body>
</html>
