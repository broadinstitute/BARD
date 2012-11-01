<%@ page import="bard.core.ExperimentValues" %>
<div>
    <g:each var="experiment" in="${experiments}" status="i">
        <div>
            <h4>
                ${experiment.name} <small>(Experiment ID: ${experiment.id})</small>
            </h4>

            <p><span class="label label-info">${experiment.getRole()}</span>
                ${experiment.getValue(ExperimentValues.ExperimentCompoundCountValue).value} compound(s) /
                ${experiment.getValue(ExperimentValues.ExperimentSubstanceCountValue).value} substance(s) tested
            </p>

            <r:script>
                $('.viewDescription').popover();
                //                $('.popover').live('mouseover', function () {
                //                    var popoverStyle = $(this).attr('style')
                //                    $(this).attr('style', popoverStyle + 'width: auto;')
                //                });
                $('.popover').live('mouseleave', function () {
                    $('.viewDescription').popover('hide')
                });
            </r:script>

            <p><span>
                <g:link controller="bardWebInterface" action="showExperiment" id="${experiment.id}"
                        params='[searchString: "${searchString}"]'>View Results</g:link>
                <g:if test="${showAssaySummary}">
                    <div><g:render template="assaySummary" model="[assayAdapter: experiment.getAssay()]"/></div>
                </g:if>
                |
                <a class="viewDescription" href="javascript:;" data-content="${experiment.description}"
                   data-placement="right" rel="popover"
                   data-original-title="Description">View Description</a>
            </span>
            </p>
        </div>
    </g:each>
</div>