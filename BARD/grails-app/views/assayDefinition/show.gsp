<%@ page import="bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,assayshow,twitterBootstrapAffix,xeditable,richtexteditorForEdit,assaysummary,canEditWidget"/>
    <meta name="layout" content="basic"/>
    <title>Assay Definition</title>
</head>

<body>
<g:hiddenField name="assayId" id="assayId" value="${assayInstance?.id}"/>

<g:if test="${message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${message}</strong>
                    </p>
                </div>
            </div>
        </div>
    </div>
</g:if>

<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${flash.message}</strong>
                    </p>
                </div>
            </div>
        </div>
    </div>
</g:if>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">

        </div>

        <div class="span9">
            <div class="pull-left">
                <g:if test="${assayInstance?.id}">
                    <h4>View Assay Definition (ADID: ${assayInstance?.id})</h4>
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
                    <g:link action="cloneAssay" id="${assayInstance?.id}" class="btn"><g:img
                            uri="/images/clone-icon.png"/> Clone Assay Definition</g:link>
                </g:if>
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
            <ul class="nav nav-list">
                <li><a href="#assay-design-header"><i class="icon-chevron-right"></i>Assay Design</a></li>
                <li><a href="#assay-readout-header"><i class="icon-chevron-right"></i>Assay Readout</a></li>
                <li><a href="#assay-components-header"><i class="icon-chevron-right"></i>Assay Components
                </a></li>
                <li><a href="#unclassified-header"><i class="icon-chevron-right"></i>Unclassified</a></li>
            </ul>
        </li>
        <li><a href="#experiments-header"><i class="icon-chevron-right"></i>Experiments</a>
            <ul class="nav nav-list">
                <li><a href="#experimental-variables-header"><i class="icon-chevron-right"></i>Experimental Variables
                </a>
                </li>
            </ul>
        </li>
        %{--<li><a href="#measures-header"><i class="icon-chevron-right"></i>Measures</a></li>--}%
        <li><a href="#documents-header"><i class="icon-chevron-right"></i>Documents</a>
            <ul class="nav nav-list">
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
            <g:render template='editSummary' model="['assay': assayInstance, canedit: editable, assayOwner: assayOwner]"/>
        </div>
    </div>
</section>
<br/>
<section id="biology-header">

    <h3 class="sect">Biology <g:link target="dictionary" controller="element" action="showTopLevelHierarchyHelp"><i
            class="icon-question-sign"></i></g:link></h3>


    <div class="row-fluid">
        <div id="cardHolderBiology" class="span12">
            <g:render template="/context/biology"
                      model="[contextOwner: assayInstance, biology: assayInstance.groupBiology(), subTemplate: 'show', renderEmptyGroups: false]"/>

        </div>
    </div>
    <br/>

    <div class="row-fluid">
        <g:if test="${!uneditable}">
            <g:if test="${editable == 'canedit'}">
                <div class="span12">

                    <g:link action="editContext" id="${assayInstance?.id}"
                            params="[groupBySection: ContextType.BIOLOGY.id.encodeAsURL()]"
                            class="btn"><i class="icon-pencil"></i> Edit Biology</g:link>

                </div>
            </g:if>
        </g:if>
    </div>
</section>
<br/>
<section id="assay-protocol-header">
    <h3 class="sect">Assay Protocol <g:link target="dictionary" controller="element" action="showTopLevelHierarchyHelp"><i
            class="icon-question-sign"></i></g:link></h3>

    <div class="row-fluid">
        <div id="cardHolderAssayProtocol" class="span12">
            <g:render template="/context/currentCard"
                      model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayProtocol(), subTemplate: 'show', renderEmptyGroups: false]"/>
        </div>
    </div>
    <br/>

    <div class="row-fluid">
        <g:if test="${!uneditable}">
            <g:if test="${editable == 'canedit'}">
                <div class="span12">
                    <g:link action="editContext" id="${assayInstance?.id}"
                            params="[groupBySection: ContextType.ASSAY_PROTOCOL.id.encodeAsURL()]"
                            class="btn"><i class="icon-pencil"></i> Edit Assay Protocol</g:link>
                </div>
            </g:if>
        </g:if>
    </div>
</section>
<br/>
<section id="assay-design-header">

    <h4 class="subsect">Assay Design <g:link target="dictionary" controller="element" action="showTopLevelHierarchyHelp"><i
            class="icon-question-sign"></i></g:link></h4>


    <div class="row-fluid">
        <div id="cardHolderAssayDesign" class="span12">
            <g:render template="/context/currentCard"
                      model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayDesign(), subTemplate: 'show', renderEmptyGroups: false]"/>

        </div>
    </div>
    <br/>

    <div class="row-fluid">
        <g:if test="${!uneditable}">
            <g:if test="${editable == 'canedit'}">
                <div class="span12">
                    <g:link action="editContext" id="${assayInstance?.id}"
                            params="[groupBySection: ContextType.ASSAY_DESIGN.id.encodeAsURL()]"
                            class="btn"><i class="icon-pencil"></i> Edit Assay Design</g:link>
                </div>
            </g:if>
        </g:if>
    </div>
    <section id="assay-readout-header">

        <h4 class="subsect">Assay Readout <g:link target="dictionary" controller="element" action="showTopLevelHierarchyHelp"><i
                class="icon-question-sign"></i></g:link></h4>


        <div class="row-fluid">
            <div id="cardHolderAssayReadout" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayReadout(), subTemplate: 'show', renderEmptyGroups: false]"/>

            </div>
        </div>
        <br/>

        <div class="row-fluid">
            <g:if test="${!uneditable}">
                <g:if test="${editable == 'canedit'}">
                    <div class="span12">
                        <g:link action="editContext" id="${assayInstance?.id}"
                                params="[groupBySection: ContextType.ASSAY_READOUT.id.encodeAsURL()]"
                                class="btn"><i class="icon-pencil"></i> Edit Assay Readout</g:link>
                    </div>
                </g:if>
            </g:if>
        </div>
    </section>
    <br/>
    <section id="assay-components-header">

        <h4 class="subsect">Assay Components <g:link target="dictionary" controller="element" action="showTopLevelHierarchyHelp"><i
                class="icon-question-sign"></i></g:link></h4>


        <div class="row-fluid">
            <div id="cardHolderAssayComponents" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayComponents(), subTemplate: 'show', renderEmptyGroups: false]"/>

            </div>
        </div>
        <br/>

        <div class="row-fluid">
            <g:if test="${!uneditable}">
                <g:if test="${editable == 'canedit'}">
                    <div class="span12">
                        <g:link action="editContext" id="${assayInstance?.id}"
                                params="[groupBySection: ContextType.ASSAY_COMPONENTS.id.encodeAsURL()]"
                                class="btn"><i class="icon-pencil"></i> Edit Assay Component</g:link>
                    </div>
                </g:if>
            </g:if>
        </div>
    </section>
    <br/>
    <section id="unclassified-header">
        <h4 class="subsect">Unclassified</h4>

        <div class="row-fluid">
            <div id="cardHolderUnclassified" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupUnclassified(), subTemplate: 'show', renderEmptyGroups: false]"/>

            </div>
        </div>
        <br/>
        <g:if test="${assayInstance.groupUnclassified()}">
            <div class="row-fluid">
                <g:if test="${!uneditable}">
                    <g:if test="${editable == 'canedit'}">
                        <div class="span12">
                            <g:link action="editContext" id="${assayInstance?.id}"
                                    params="[groupBySection: ContextType.UNCLASSIFIED.id.encodeAsURL()]"
                                    class="btn"><i class="icon-pencil"></i> Edit Unclassified</g:link>
                        </div>
                    </g:if>
                </g:if>
            </div>
        </g:if>
    </section>
    <br/>
</section>
<br/>


<section id="experiments-header">

    <h3 class="sect">Experiments</h3>


    <div class="row-fluid">
        <g:render template="showExperiments" model="['assay': assayInstance]"/>
    </div>
    <section id="experimental-variables-header">

        <h4 class="subsect">Experimental Variables <g:link target="dictionary" controller="element"
                                           action="showTopLevelHierarchyHelp"><i
                    class="icon-question-sign"></i></g:link></h4>

        <div class="row-fluid">
            <div id="cardHolderExperimentalVariables" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupExperimentalVariables(), subTemplate: 'show', renderEmptyGroups: false]"/>

            </div>
        </div>
        <br/>
        <g:if test="${assayInstance.groupExperimentalVariables()}">
            <div class="row-fluid">
                <g:if test="${!uneditable}">
                    <g:if test="${editable == 'canedit'}">
                        <div class="span12">
                            <g:link action="editContext" id="${assayInstance?.id}"
                                    params="[groupBySection: ContextType.EXPERIMENT.id.encodeAsURL()]"
                                    class="btn"><i class="icon-pencil"></i> Edit Experimental Variables</g:link>
                        </div>
                    </g:if>
                </g:if>
            </div>
        </g:if>
    </section>
</section>
<br/>

%{--<br/>--}%
%{--<section id="measures-header">--}%

    %{--<h3 class="sect">Measures<g:link target="dictionary" controller="element" action="showTopLevelHierarchyHelp"><i--}%
            %{--class="icon-question-sign"></i></g:link></h3>--}%


    %{--<div class="row-fluid">--}%
        %{--<g:render template="measuresView"--}%
                  %{--model="['measures': assayInstance.measures, 'measureTreeAsJson': measureTreeAsJson, editable: editable]"/>--}%
    %{--</div>--}%
%{--</section>--}%
<br/>
<g:render template="/document/documents"
          model="[documentKind: DocumentKind.AssayDocument, owningEntity: assayInstance, canedit: editable, sectionNumber: '6.']"/>
</div>
</div>
</div>
</g:if>

</body>
</html>