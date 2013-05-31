<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<%@ page import="bard.core.rest.spring.assays.BardAnnotation; bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>BioAssay Research Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="core,bootstrap,cardDisplayCSS,jqueryMobile"/>
    <r:layoutResources/>
</head>

<body>
<div data-role="page" id="showCompound">
    <div>
        <g:render template="/layouts/templates/mobileLoginStrip"/>
    </div>

    <div data-role="content" style="text-align: center; margin: 0 auto;">
        <h3>Assay Definition: ${assayAdapter?.title}
            <small>(ADID: ${assayAdapter?.capAssayId})
                <g:if test="${assayAdapter.assayStatus == 'Witnessed'}">
                    <img src="${resource(dir: 'images', file: 'witnessed.png')}"
                         alt="Witnessed" title="Witnessed"/>
                </g:if>
                <g:if test="${assayAdapter.assayStatus == 'Measures Done' || assayAdapter.assayStatus == 'Annotations Done'}">
                    <img src="${resource(dir: 'images', file: 'measures_annotations_done.png')}"
                         alt="Measures or Annotations Done" title="Measures or Annotations Done"/>
                </g:if>
                <g:if test="${assayAdapter.assayStatus == 'Draft' || assayAdapter.assayStatus == 'Retired'}">
                    <img src="${resource(dir: 'images', file: 'draft_retired.png')}"
                         alt="Draft or Retired" title="Draft or Retired"/>
                </g:if>
            </small>
        </h3>


        <div>
            <g:render template="/bardWebInterface/assaySummary" model="[assayAdapter: assayAdapter]"/>
        </div>

        <div>
            <dl class="dl-horizontal dl-horizontal-wide">
                <dt>Associated Projects:</dt>
                <dd>
                    <ul>
                        <g:each in="${projects}" var="project">
                            <li>

                                <g:if test="${searchString}">
                                    <g:link controller="bardWebInterface" action="showProject" id="${project.id}"
                                            params='[searchString: "${searchString}"]'>${project.name}</g:link>
                                </g:if>
                                <g:else>
                                    <g:link controller="bardWebInterface" action="showProject"
                                            id="${project.id}">${project.name}</g:link>
                                </g:else>
                            </li>
                        </g:each>
                    </ul>
                </dd>
            </dl>
        </div>

        <div>
            <g:if test="${BardAnnotation.areAnnotationsEmpty(assayAdapter.annotations)}">
                <section id="assay-bio-info" style="text-align: left;">
                    <div>
                        <h3>Assay and Biology Details</h3>
                    </div>

                    <div id="cardView" class="cardView" style="display: inline-block;">
                        <g:render template="/bardWebInterface/listContexts" model="[annotations: assayAdapter.annotations]"/>
                    </div>

                </section>
            </g:if>

            <g:if test="${assayAdapter.targets}">
                <g:render template="targets" model="['targets': assayAdapter.targets]"/>
            </g:if>
            <section id="document-info" style="text-align: left;">
                <div class="page-header">
                    <h3>Documents
                        <small>(${[(assayAdapter.protocol ? 'protocol' : 'no protocol'),
                                (assayAdapter.description ? 'description' : 'no description'),
                                (assayAdapter.comments ? 'comments' : 'no comments')].join(', ')})</small></h3>
                </div>

                <g:render template="assayDocuments" model="['assayAdapter': assayAdapter]"/>

            </section>

            <g:if test="${assayAdapter.documents}">
                <g:render template="publications" model="['documents': assayAdapter.documents]"/>
            </g:if>
            <section id="result-info" style="text-align: left">
                <div class="page-header">
                    <h3>Experiments</h3>
                </div>
                <g:render template="experiments"
                          model="[experiments: experiments, showAssaySummary: false]"/>
            </section>
        </div>
    </div>
</div>
<r:layoutResources/>
</body>
</html>