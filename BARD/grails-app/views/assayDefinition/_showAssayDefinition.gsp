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

<%@ page import="bard.db.enums.Status; bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">

        </div>

        <div class="span9">
            <div class="pull-left">
                <g:if test="${assayInstance?.id}">
                    <h4>View Assay Definition (ADID: ${assayInstance?.id})</h4>
                    <g:if test="${assayInstance.ncgcWarehouseId}">
                        <g:saveToCartButton id="${assayInstance?.id}"
                                            name="${bardqueryapi.JavaScriptUtility.cleanup(assayInstance?.assayName)}"
                                            type="${querycart.QueryItemType.AssayDefinition}"/>
                    </g:if>
                </g:if>
            </div>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span3">

        </div>

        <div class="span9">
            <div class="pull-left">
                <g:if test="${assayInstance?.id}">
                    <sec:ifLoggedIn>
                        <g:link action="cloneAssay" id="${assayInstance?.id}" class="btn"><g:img
                                uri="/images/clone-icon.png"/> Clone Assay Definition</g:link>
                    </sec:ifLoggedIn>
                </g:if>
                <g:render template="../layouts/templates/askAQuestion" model="['entity': 'Assay Definition']"/>
            </div>
        </div>
    </div>
</div>
<g:if test="${assayInstance?.id}">
<div class="container-fluid">
<div class="row-fluid">
<div class="span3 bs-docs-sidebar heading-numbering">
    <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar">
        <li><a href="#summary-header"><i class="icon-chevron-right"></i>Overview</a></li>
        <li><a href="#biology-header"><i class="icon-chevron-right"></i>Biology</a></li>
        <li><a href="#assay-protocol-header"><i class="icon-chevron-right"></i>Assay Protocol</a>
            <ul class="nav nav-list bs-docs-sidenav" style="padding-left: 0; margin: 0;">
                <li><a href="#assay-design-header"><i class="icon-chevron-right"></i>Assay Design</a></li>
                <li><a href="#assay-readout-header"><i class="icon-chevron-right"></i>Assay Readout</a></li>
                <li><a href="#assay-components-header"><i class="icon-chevron-right"></i>Assay Components
                </a></li>
                <li><a href="#unclassified-header"><i class="icon-chevron-right"></i>Unclassified</a></li>
            </ul>
        </li>
        <li><a href="#experiments-header"><i class="icon-chevron-right"></i>Experiments</a>
            <ul class="nav nav-list bs-docs-sidenav" style="padding-left: 0; margin: 0;">
                <li><a href="#experimental-variables-header"><i class="icon-chevron-right"></i>Experimental Variables
                </a>
                </li>
            </ul>
        </li>
        <g:if test="${assayInstance.panelAssays}">
            <li><a href="#panels-header"><i class="icon-chevron-right"></i>Panels</a></li>
        </g:if>
        <li><a href="#documents-header"><i class="icon-chevron-right"></i>Documents</a>
            <ul class="nav nav-list bs-docs-sidenav" style="padding-left: 0; margin: 0;">
                <li><a href="#documents-description-header"><i class="icon-chevron-right"></i>Descriptions</a>
                </li>
                <li><a href="#documents-protocol-header"><i class="icon-chevron-right"></i>Protocols</a></li>
                <li><a href="#documents-comment-header"><i class="icon-chevron-right"></i>Comments</a></li>
                <li><a href="#documents-publication-header"><i class="icon-chevron-right"></i>Publications</a>
                </li>
                <li><a href="#documents-urls-header"><i class="icon-chevron-right"></i>External URLS</a></li>
                <li><a href="#documents-other-header"><i class="icon-chevron-right"></i>Others</a></li>
            </ul>
        </li>
    </ul>
</div>

<div class="span9">
<section id="summary-header">
    <h3 class="sect">Overview</h3>

    <div class="row-fluid">
        <div id="msg" class="alert hide"></div>

        <div id="showSummary">
            <g:render template='editSummary'
                      model="['assay': assayInstance, canedit: editable, assayOwner: assayOwner]"/>
        </div>
    </div>
</section>
<br/>
<g:render template="/common/guidance" model="[guidanceList: assayInstance.guidance]"/>
<section id="biology-header">

    <h3 class="sect">Biology <g:link target="dictionary" controller="element" action="showTopLevelHierarchyHelp"><i
            class="icon-question-sign"></i></g:link></h3>
        <div class="row-fluid">
            <div id="cardHolderBiology" class="span12">
                <g:render template="/context/biology"
                          model="[contextOwner: assayInstance, biology: assayInstance.groupBiology(), subTemplate: contextItemSubTemplate, renderEmptyGroups: false]"/>

            </div>
        </div>
</section>
<br/>
<section id="assay-protocol-header">
    <h3 class="sect">Assay Protocol <g:link target="dictionary" controller="element"
                                            action="showTopLevelHierarchyHelp"><i
                class="icon-question-sign"></i></g:link></h3>
        <div class="row-fluid">

            <div id="cardHolderAssayProtocol" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayProtocol(), subTemplate: contextItemSubTemplate, renderEmptyGroups: false]"/>
            </div>
        </div>
    <section id="assay-design-header">

        <h4 class="subsect">Assay Design <g:link target="dictionary" controller="element"
                                                 action="showTopLevelHierarchyHelp"><i
                    class="icon-question-sign"></i></g:link></h4>

            <div class="row-fluid">
                <div id="cardHolderAssayDesign" class="span12">
                    <g:render template="/context/currentCard"
                              model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayDesign(), subTemplate: contextItemSubTemplate, renderEmptyGroups: false]"/>

                </div>
            </div>
    </section>
    <section id="assay-readout-header">

        <h4 class="subsect">Assay Readout <g:link target="dictionary" controller="element"
                                                  action="showTopLevelHierarchyHelp"><i
                    class="icon-question-sign"></i></g:link></h4>

            <div class="row-fluid">
                <div id="cardHolderAssayReadout" class="span12">
                    <g:render template="/context/currentCard"
                              model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayReadout(), subTemplate: contextItemSubTemplate, renderEmptyGroups: false]"/>

                </div>
            </div>
    </section>
    <section id="assay-components-header">

        <h4 class="subsect">Assay Components <g:link target="dictionary" controller="element"
                                                     action="showTopLevelHierarchyHelp"><i
                    class="icon-question-sign"></i></g:link></h4>

            <div class="row-fluid">
                <div id="cardHolderAssayComponents" class="span12">
                    <g:render template="/context/currentCard"
                              model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayComponents(), subTemplate: contextItemSubTemplate, renderEmptyGroups: false]"/>

                </div>
            </div>
    </section>
    <section id="unclassified-header">
        <h4 class="subsect">Unclassified</h4>
            <div class="row-fluid">
                <div id="cardHolderUnclassified" class="span12">
                    <g:render template="/context/currentCard"
                              model="[contextOwner: assayInstance, currentCard: assayInstance.groupUnclassified(), subTemplate: contextItemSubTemplate, renderEmptyGroups: false]"/>

                </div>
            </div>
            <br/>
    </section>
</section>


<section id="experiments-header">

    <h3 class="sect">Experiments</h3>


    <div class="row-fluid">
        <g:render template="showExperiments"
                  model="['assay': assayInstance, 'experimentsActiveVsTested': experimentsActiveVsTested, 'maxNumProjectsInExperiments': assayInstance.getMaxNumProjectsInExperiments()]"/>
    </div>
    <section id="experimental-variables-header">

        <h4 class="subsect">Experimental Variables <g:link target="dictionary" controller="element"
                                                           action="showTopLevelHierarchyHelp"><i
                    class="icon-question-sign"></i></g:link></h4>

        <div class="row-fluid">
            <div id="cardHolderExperimentalVariables" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupExperimentalVariables(), subTemplate: contextItemSubTemplate, renderEmptyGroups: false]"/>

            </div>
        </div>
    </section>
</section>
<g:if test="${assayInstance.panelAssays}">
    <br/>

    <br/>
    <section id="panels-header">

        <h3 class="sect">Panels</h3>


        <div class="row-fluid">
            <g:render template="panelsView"
                      model="['panelInstances': assayInstance.panelAssays, assay: assayInstance, editable: editable]"/>
        </div>
    </section>
</g:if>
<br/>

<br/>
<g:render template="/document/documents"
          model="[documentKind: DocumentKind.AssayDocument, owningEntity: assayInstance, canedit: editable]"/>
</div>
</div>
</div>
</g:if>
