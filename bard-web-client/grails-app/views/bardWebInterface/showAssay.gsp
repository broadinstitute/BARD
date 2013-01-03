<%@ page import="bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <r:require modules="showProjectAssay"/>
    <title>BARD : Assay Definition : ADID ${assayAdapter?.id}</title>
</head>

<body>
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

<div class="row-fluid">
    <div class="span12 accordion" id="accordionParent">

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#assay-bio-info" class="accordion-toggle" data-toggle="collapse"
                   data-parent="#accordionParent" data-target="#assay-bio-info">
                    <i class="icon-chevron-right"></i> Assay and Biology Details
                </a>


                <div id="assay-bio-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:if test="${assayAdapter?.keggDiseaseCategories}">
                            <dl>
                                %{--TODO: Make annother call to get other annotations--}%
                                <dt>Kegg Disease Categories</dt>
                                <g:each in="${assayAdapter.keggDiseaseCategories}" var="annotation">

                                    <dd>${annotation}</dd>
                                </g:each>
                            </dl>
                        </g:if>
                        <g:if test="${assayAdapter?.keggDiseaseNames}">
                            <dl>
                                <dt>Kegg Disease Names</dt>
                                <g:each in="${assayAdapter.keggDiseaseNames}" var="annotation">
                                    <dd>${annotation}</dd>
                                </g:each>
                            </dl>
                        </g:if>
                    </div>
                </div>
            </div>
        </div>
        <g:if test="${assayAdapter.targets}">
            <g:render template="targets" model="['targets': assayAdapter.targets]"/>
        </g:if>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-info" class="accordion-toggle" data-toggle="collapse"
                   data-target="#document-info" data-parent="#accordionParent"><i
                        class="icon-chevron-right"></i> Documents
                    <small>(${[(assayAdapter.protocol ? 'protocol' : 'no protocol'),
                            (assayAdapter.description ? 'description' : 'no description'),
                            (assayAdapter.comments ? 'comments' : 'no comments')].join(', ')})</small>
                </a>

                <div id="document-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="assayDocuments" model="['assayAdapter': assayAdapter]"/>
                    </div>
                </div>
            </div>
        </div>

        <g:if test="${assayAdapter.documents}">
            <g:render template="publications" model="['documents': assayAdapter.documents]"/>
        </g:if>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#result-info" class="accordion-toggle" data-toggle="collapse"
                   data-target="#result-info" data-parent="#accordionParent"><i
                        class="icon-chevron-right"></i> Experiments (${experiments.size()})</a>

                <div id="result-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="experiments" model="[experiments: experiments, showAssaySummary: false]"/>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>
</body>
</html>