<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.ActivityOutcome; bardqueryapi.NormalizeAxis; bard.core.interfaces.ExperimentValues" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>EID ${tableModel?.additionalProperties?.capExptId}: Preview of Experimental Results</title>
    <r:require modules="experimentData, histogram, bootstrap, compoundOptions, cbas"/>

</head>

<body>
<div class="row-fluid" id="showExperimentDiv">
    <div class="span2">

    </div>

    <div class="span9">
        <g:if test="${tableModel?.data}">
            <div id="experimentalResults">
                <g:render template='experimentResultData'
                          model='[tableModel: tableModel, innerBorder: true, totalNumOfCmpds: totalNumOfCmpds, preview:true]'/>
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