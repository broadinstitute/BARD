<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.ActivityOutcome; bardqueryapi.NormalizeAxis; bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>EID ${tableModel.additionalProperties.capExptId}: Experimental Results</title>
    <r:require modules="experimentData, histogram, bootstrap, compoundOptions, cbas"/>

</head>

<body>
<div class="row-fluid" id="showExperimentDiv">
    <g:if test="${tableModel?.data}">
        <g:render template="facets"
                  model="['facets': facets, 'formName': FacetFormType.ExperimentFacetForm, 'total': tableModel.additionalProperties?.total]"/>
        <g:hiddenField name="experimentId" id='experimentId' value="${params?.id}"/>
    </g:if>

<div class="span9">
    <a class="btn"
       href="mailto:${grailsApplication.config.bard.users.email}?Subject=Question about results for EID: ${tableModel.additionalProperties.capExptId}"
       target="_top"><i class="icon-envelope"></i> Ask a question about this Experiment Results</a>
    <br/>
    <g:if test="${tableModel?.data}">
        <div id="experimentalResults">
            <g:render template='experimentResultData'
                      model='[tableModel: tableModel, innerBorder: true, totalNumOfCmpds: totalNumOfCmpds, preview:preview]'/>
        </div>

    </g:if>
    <g:else>
        <p class="text-info"><i
                class="icon-warning-sign"></i> No results found for this experiment ${tableModel?.additionalProperties?.id}
        </p>
    </g:else>
</div>
</div>
</body>
</html>