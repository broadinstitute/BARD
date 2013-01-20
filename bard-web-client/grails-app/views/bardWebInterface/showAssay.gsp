<%@ page import="bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <r:require modules="showProjectAssay, twitterBootstrapAffix"/>
    <title>BARD : Assay Definition : ADID ${assayAdapter?.id}</title>
</head>

<body data-spy="scroll" data-target=".bs-docs-sidebar">
<div class="row-fluid">
    <div class="span12 page-header">
        <h1>Assay Definition: ${assayAdapter?.name}
            <small>(ADID: ${assayAdapter?.id})</small>
        </h1>

        <g:saveToCartButton id="${assayAdapter.id}"
                            name="${JavaScriptUtility.cleanup(assayAdapter.name)}"
                            type="${querycart.QueryItemType.AssayDefinition}"/>
        <a class="btn btn-mini" href="${grailsApplication.config.bard.cap.assay}${assayAdapter?.capAssayId}"
           title="Click To Edit Assay Definition In Cap" rel="tooltip">Edit in CAP</a>
    </div>
</div>


<div class="row-fluid">
    <div class="span6">
        <g:render template="assaySummary" model="[assayAdapter: assayAdapter]"/>
    </div>

    <div class="span6">
        <dl>
            <dt>Associated Projects:</dt>
            <dd>
                <ul>
                    <g:each in="${projects}" var="project">
                        <li><g:link controller="bardWebInterface" action="showProject" id="${project.id}"
                                    params='[searchString: "${searchString}"]'>${project.name} <small>(Project ID: ${project.id})</small></g:link>
                        </li>
                    </g:each>
                </ul>
            </dd>
        </dl>
    </div>
</div>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3 bs-docs-sidebar">
            <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar">
                <li><a href="#assay-bio-info"><i class="icon-chevron-right"></i>Assay and Biology Details</a></li>
                <g:if test="${assayAdapter.targets}">
                    <li><a href="#target-info"><i class="icon-chevron-right"></i>Targets</a></li>
                </g:if>
                <li><a href="#document-info"><i class="icon-chevron-right"></i>Documents</a></li>
                <g:if test="${assayAdapter.documents}">
                    <li><a href="#publication-info"><i class="icon-chevron-right"></i>Publications</a></li>
                </g:if>
                <li><a href="#result-info"><i class="icon-chevron-right"></i>Experiments</a></li>
            </ul>
        </div>

        <div class="span9">
            <section id="assay-bio-info">
                <div class="page-header">
                    <h1>Assay and Biology Details</h1>
                </div>

                <div id="cardView" class="cardView" class="row-fluid">
                    <g:render template="listContexts"
                              model="[contextOwner: assayAdapter, contexts: contexts, subTemplate: 'show']"/>
                </div>
                <g:render template="showContextItems" model="[contextOwner: assayInstance, contexts: assayInstance.groupContexts()]"/>

            </section>
            <g:if test="${assayAdapter.targets}">
                <g:render template="targets" model="['targets': assayAdapter.targets]"/>
            </g:if>
            <section id="document-info">
                <div class="page-header">
                    <h1>Documents
                        <small>(${[(assayAdapter.protocol ? 'protocol' : 'no protocol'),
                                (assayAdapter.description ? 'description' : 'no description'),
                                (assayAdapter.comments ? 'comments' : 'no comments')].join(', ')})</small></h1>
                </div>

                <g:render template="assayDocuments" model="['assayAdapter': assayAdapter]"/>

            </section>

            <g:if test="${assayAdapter.documents}">
                <g:render template="publications" model="['documents': assayAdapter.documents]"/>
            </g:if>
            <section id="result-info">
                <div class="page-header">
                    <h1>Experiments (${experiments.size()})</h1>
                </div>
                <g:render template="experiments"
                          model="[experiments: experiments, showAssaySummary: false]"/>
            </section>
        </div>

    </div>
</div>
</body>
</html>