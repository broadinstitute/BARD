<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,assayshow,twitterBootstrapAffix,xeditable,assaysummary"/>
    <meta name="layout" content="basic"/>
    <title>Assay Definition</title>
</head>

<body>
<g:hiddenField name="assayId" id="assayId" value="${assayInstance?.id}"/>
<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${flash.message}</strong>
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
<div class="span3 bs-docs-sidebar">
    <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar">
        <li><a href="#summary-header"><i class="icon-chevron-right"></i>1. Overview</a></li>
        <li><a href="#biology-header"><i class="icon-chevron-right"></i>2. Biology</a></li>
        <li><a href="#assay-protocol-header"><i class="icon-chevron-right"></i>3. Assay Protocol</a></li>
        <li><a href="#assay-design-header"><i class="icon-chevron-right"></i>3.1 Assay Design</a></li>
        <li><a href="#assay-readout-header"><i class="icon-chevron-right"></i>3.2 Assay Readout</a></li>
        <li><a href="#assay-components-header"><i class="icon-chevron-right"></i>3.3 Assay Components
        </a></li>
        <li><a href="#unclassified-header"><i class="icon-chevron-right"></i>3.4 Unclassified</a></li>

        <li><a href="#experiments-header"><i class="icon-chevron-right"></i>4. Experiments</a></li>
        <li><a href="#experimental-variables-header"><i class="icon-chevron-right"></i>4.1 Experimental Variables</a></li>
        <li><a href="#measures-header"><i class="icon-chevron-right"></i>5. Measures</a></li>
        <li><a href="#documents-header"><i class="icon-chevron-right"></i>6. Documents</a></li>
        <li><a href="#documents-description-header"><i class="icon-chevron-right"></i>6.1 Descriptions</a>
        </li>
        <li><a href="#documents-protocol-header"><i class="icon-chevron-right"></i>6.2 Protocols</a></li>
        <li><a href="#documents-comment-header"><i class="icon-chevron-right"></i>6.3 Comments</a></li>
        <li><a href="#documents-publication-header"><i class="icon-chevron-right"></i>6.4 Publications</a>
        </li>
        <li><a href="#documents-urls-header"><i class="icon-chevron-right"></i>6.5 External URLS</a></li>
        <li><a href="#documents-other-header"><i class="icon-chevron-right"></i>6.6 Others</a></li>
    </ul>
</div>

<div class="span9">
    <section id="summary-header">
        <h3>1. Overview</h3>

        <div class="row-fluid">
            <div id="msg" class="alert hide"></div>

            <div id="showSummary">
                <g:render template='editSummary' model="['assay': assayInstance]"/>
            </div>
        </div>
    </section>
    <br/>
    <section id="biology-header">

        <h3>2. Biology</h3>


        <div class="row-fluid">
            <div id="cardHolderBiology" class="span12">
                <g:render template="/context/biology"
                          model="[contextOwner: assayInstance, biology: assayInstance.groupBiology(), subTemplate: 'show', renderEmptyGroups: false]"/>

            </div>
        </div>
        <br/>

        <div class="row-fluid">
            <g:if test="${!uneditable}">
                <div class="span12">
                    <g:link action="editContext" id="${assayInstance?.id}"
                            class="btn"><i class="icon-pencil"></i> Edit Biology</g:link>
                </div>

            </g:if>
        </div>
    </section>
    <br/>
    <section id="assay-protocol-header">
        <h3>3. Assay Protocol</h3>

        <div class="row-fluid">
            <div id="cardHolderAssayProtocol" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayType(), subTemplate: 'show', renderEmptyGroups: false]"/>
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayFormat(), subTemplate: 'show', renderEmptyGroups: false]"/>

            </div>
        </div>
        <br/>

        <div class="row-fluid">
            <g:if test="${!uneditable}">
                <div class="span12">
                    <g:link action="editContext" id="${assayInstance?.id}"
                            class="btn"><i class="icon-pencil"></i> Edit Asssay Protocol</g:link>
                </div>

            </g:if>
        </div>
    </section>
    <br/>
    <section id="assay-design-header">

        <h4>3.1 Assay Design</h4>


        <div class="row-fluid">
            <div id="cardHolderAssayDesign" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayDesign(), subTemplate: 'show', renderEmptyGroups: false]"/>

            </div>
        </div>
        <br/>

        <div class="row-fluid">
            <g:if test="${!uneditable}">
                <div class="span12">
                    <g:link action="editContext" id="${assayInstance?.id}"
                            class="btn"><i class="icon-pencil"></i> Edit Assay Design</g:link>
                </div>

            </g:if>
        </div>
    </section>
    <br/>
    <section id="assay-readout-header">

        <h4>3.2 Assay Readout</h4>


        <div class="row-fluid">
            <div id="cardHolderAssayReadout" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayReadout(), subTemplate: 'show', renderEmptyGroups: false]"/>

            </div>
        </div>
        <br/>

        <div class="row-fluid">
            <g:if test="${!uneditable}">
                <div class="span12">
                    <g:link action="editContext" id="${assayInstance?.id}"
                            class="btn"><i class="icon-pencil"></i> Edit Assay Readout</g:link>
                </div>

            </g:if>
        </div>
    </section>
    <br/>
    <section id="assay-components-header">

        <h4>3.3 Assay Components</h4>


        <div class="row-fluid">
            <div id="cardHolderAssayComponents" class="span12">
                <g:render template="/context/currentCard"
                          model="[contextOwner: assayInstance, currentCard: assayInstance.groupAssayComponents(), subTemplate: 'show', renderEmptyGroups: false]"/>

            </div>
        </div>
        <br/>

        <div class="row-fluid">
            <g:if test="${!uneditable}">
                <div class="span12">
                    <g:link action="editContext" id="${assayInstance?.id}"
                            class="btn"><i class="icon-pencil"></i> Edit Assay Component</g:link>
                </div>

            </g:if>
        </div>
    </section>
    <br/>
    <section id="unclassified-header">
        <h4>3.4 Unclassified</h4>
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
                    <div class="span12">
                        <g:link action="editContext" id="${assayInstance?.id}"
                                class="btn"><i class="icon-pencil"></i> Edit Unclassified</g:link>
                    </div>

                </g:if>
            </div>
        </g:if>
    </section>
    <br/>

    <section id="experiments-header">

        <h3>4. Experiments</h3>


        <div class="row-fluid">
            <g:render template="showExperiments" model="['assay': assayInstance]"/>
        </div>
    </section>
    <br/>
    <section id="experimental-variables-header">

        <h4>4.1 Experimental Variables</h4>
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
                    <div class="span12">
                        <g:link action="editContext" id="${assayInstance?.id}"
                                class="btn"><i class="icon-pencil"></i> Edit Experimental Variables</g:link>
                    </div>

                </g:if>
            </div>
        </g:if>
    </section>
    <br/>
    <section id="measures-header">

        <h3>5. Measures</h3>


        <div class="row-fluid">
            <g:render template="measuresView"
                      model="['measures': assayInstance.measures, 'measureTreeAsJson': measureTreeAsJson]"/>
        </div>
    </section>
    <br/>
    <g:render template="assayDocuments" model="[assay: assayInstance]"/>
</div>
</div>
</div>
</g:if>

</body>
</html>