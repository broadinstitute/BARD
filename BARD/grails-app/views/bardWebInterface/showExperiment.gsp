<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.ActivityOutcome; bardqueryapi.NormalizeAxis; bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Experiment Result : ${tableModel.additionalProperties.capExptId}</title>
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
    <g:if test="${tableModel?.data}">
        <div id="experimentalResults">
            <g:render template='experimentResultData'
                      model='[tableModel: tableModel, innerBorder: true, totalNumOfCmpds: totalNumOfCmpds]'/>
        </div>
        </div>
    </g:if>
    <g:else>
        <p class="text-info"><i
                class="icon-warning-sign"></i> No results found for this experiment ${tableModel?.additionalProperties?.id}
        </p>
    </g:else>
</div>
</body>
</html>