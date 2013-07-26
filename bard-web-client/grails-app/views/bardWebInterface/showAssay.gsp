<!DOCTYPE html>
<%@ page import="bard.core.rest.spring.assays.BardAnnotation; bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <r:require modules="showProjectAssay,twitterBootstrapAffix"/>
    <title>BARD : Assay Definition : ADID ${assayAdapter?.capAssayId}</title>
</head>

<body>
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
                <g:if test="${!assayAdapter?.annotations?.findOrphanAssayContexts()?.isEmpty()}">
                    <li><a href="#unclassified-header"><i class="icon-chevron-right"></i>3.4 Unclassified</a></li>
                </g:if>
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
                <li>Search external sites for related:
                    <g:render template="externalSearchLinks" model="[contexts: assayAdapter.annotations.contexts]"/>
                </li>
            </ul>
        </div>

        <div class="span9">
            <h2>View Assay Definition (ADID: ${assayAdapter?.capAssayId})</h2>
            <g:saveToCartButton id="${assayAdapter.id}"
                                name="${JavaScriptUtility.cleanup(assayAdapter.name)}"
                                type="${querycart.QueryItemType.AssayDefinition}"/>
            <a class="btn" href="${grailsApplication.config.bard.cap.assay}${assayAdapter?.capAssayId}"
               title="Click To View Assay Definition In CAP" rel="tooltip">View in CAP</a>

            <section id="summary-header">
                <h3>1. Overview</h3>

                <div class="row-fluid">
                    <div id="showSummary" class="span12">
                        <g:render template="assaySummary" model="[assayAdapter: assayAdapter]"/>
                    </div>
                </div>
            </section>

            <section id="biology-header">
                <h3>2. Biology <a target="dictionary" href="https://github.com/broadinstitute/BARD/wiki/BARD-hierarchy-top-level-concept-definitions"><i class="icon-question-sign"></i></a></h3>

                <div class="row-fluid">
                    <div class="cardView" class="row-fluid">
                        <g:render template="listContexts" model="[contexts: assayAdapter?.annotations?.findAssayContextsContainingKeys('biology')]"/>
                    </div>
                </div>

            </section>

            <section id="assay-protocol-header">
                <h3>3. Assay Protocol <a target="dictionary" href="https://github.com/broadinstitute/BARD/wiki/BARD-hierarchy-top-level-concept-definitions"><i class="icon-question-sign"></i></a></h3>

                <div class="row-fluid">
                    <div id="cardHolderAssayProtocol" class="span12">
                        <dl class="dl-horizontal dl-horizontal-wide">
                            <dt>Assay Format:</dt>
                            <dd>${assayAdapter?.minimumAnnotation?.assayFormat}</dd>
                            <dt>Assay Type:</dt>
                            <dd>${assayAdapter?.minimumAnnotation?.assayType}</dd>
                        </dl>
                    </div>
                </div>

                <section id="assay-design-header">
                    <h4>3.1 Assay Design <a target="dictionary" href="https://github.com/broadinstitute/BARD/wiki/BARD-hierarchy-top-level-concept-definitions"><i class="icon-question-sign"></i></a> </h4>

                    <div class="row-fluid">
                        <div class="cardView" class="row-fluid">
                            <g:render template="listContexts" model="[contexts: assayAdapter?.annotations?.findAssayContextsContainingKeys('assay method','assay parameter','assay footprint')]"/>
                        </div>
                    </div>
`
                </section>

                <section id="assay-readout-header">
                    <h4>3.2 Assay Readout <a target="dictionary" href="https://github.com/broadinstitute/BARD/wiki/BARD-hierarchy-top-level-concept-definitions"><i class="icon-question-sign"></i></a></h4>

                    <div class="row-fluid">
                        <div class="cardView" class="row-fluid">
                            <g:render template="listContexts" model="[contexts: assayAdapter?.annotations?.findAssayContextsContainingKeys('readout','detection', 'wavelength')]"/>
                        </div>
                    </div>
                </section>

                <section id="assay-components-header">
                    <h4>3.3 Assay Components<a target="dictionary" href="https://github.com/broadinstitute/BARD/wiki/BARD-hierarchy-top-level-concept-definitions"><i class="icon-question-sign"></i></a></h4>

                    <div class="row-fluid">
                        <div id="cardView" class="cardView" class="row-fluid">
                            <g:render template="listContexts" model="[contexts: assayAdapter?.annotations?.findAssayContextsContainingKeys('assay component role')]"/>
                        </div>
                    </div>
                </section>

                <g:if test="${!assayAdapter?.annotations?.findOrphanAssayContexts()?.isEmpty()}">
                    <section id="unclassified-header">
                        <h4>3.4 Unclassified</h4>

                        <div class="row-fluid">
                            <div class="cardView" class="row-fluid">
                                <g:render template="listContexts" model="[contexts: assayAdapter?.annotations?.findOrphanAssayContexts()]"/>
                            </div>
                        </div>
                    </section>
                </g:if>

            </section>


            <section id="experiments-header">
                <h3>4. Experiments</h3>

                <div class="row-fluid">
                    <g:render template="experiments"
                              model="[experiments: experiments, projects: projects]"/>
                </div>

                <section id="experimental-variables-header">
                    <h4>4.1 Experimental Variables <a target="dictionary" href="https://github.com/broadinstitute/BARD/wiki/BARD-hierarchy-top-level-concept-definitions"><i class="icon-question-sign"></i></a></h4>

                    <div class="row-fluid">
                        <div class="cardView" class="row-fluid">
                            <g:render template="listContexts" model="[contexts: assayAdapter?.annotations?.findAssayContextsForExperimentalVariables()]"/>
                        </div>
                    </div>
                </section>
            </section>


            <section id="measures-header">
                <h3>5. Measures<a target="dictionary" href="https://github.com/broadinstitute/BARD/wiki/BARD-hierarchy-top-level-concept-definitions"><i class="icon-question-sign"></i></a></h3>
                <g:displayMeasures measures="${assayAdapter?.annotations?.measures}" />
            </section>

            <g:render template="assayDocuments" model="['assayAdapter': assayAdapter]"/>

        </div>
    </div>

</body>
</html>