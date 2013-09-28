<%@ page import="bardqueryapi.ActivityOutcome; bardqueryapi.GroupByTypes; bardqueryapi.FacetFormType" contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>BARD : Compound Bio-Activity Summary: ${tableModel?.additionalProperties?.id}</title>
    <r:require modules="experimentData, cbas, core"/>
    <script src="http://d3js.org/d3.v3.min.js"></script>
    <r:script>
        function makeBigSunburstWindow() {
            window.open("../bigSunburst/${tableModel?.additionalProperties?.id}", "mywindow", "menubar=0,resizable=1,width=1100,height=950");
        }
        window.onload = function () {
            $('#sunburstdiv_bigwin').click(function () {
                makeBigSunburstWindow();
            })
        }
        $(document).ready(function() {
            $('.linksListPopup').popover({
            delay: { show: 0, hide: 3000 }
            });
        });
    </r:script>
</head>

<body>

<div class="row-fluid">
    <g:if test="${tableModel?.data || tableModel?.additionalProperties?.'experimentWithSinglePointDataOnly'}">
        <div class="row-fluid">
            <div class="span12">
                <g:sunburstSection compoundSummary="${tableModel?.additionalProperties?.compoundSummary}"/>

                <h2>Compound Bio Activity Summary <small>(cid: ${tableModel?.additionalProperties?.id})</small>
                    <g:set var="smiles" value="${tableModel?.additionalProperties?.smiles}"/>
                    <g:if test="${smiles}">
                        <img alt="${smiles}"
                             src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: smiles, width: 300, height: 200])}"
                             style="min-width: ${200}px; min-height: ${133}px"/>
                    </g:if>
                </h2>

                <g:form action="showCompoundBioActivitySummary" id="${params.id}">
                    <g:hiddenField name="compoundId" id='compoundId' value="${params?.id}"/>
                    <div style="text-align: left; vertical-align: middle;">
                        <label for="groupByTypeSelect"
                               style="display: inline; vertical-align: middle;">Group-by:</label>
                        <g:select id="groupByTypeSelect" name="groupByType"
                                  from="${[GroupByTypes.ASSAY, GroupByTypes.PROJECT]}" value="${resourceType}"
                                  style="display: inline; vertical-align: middle;"/>
                        <g:submitButton class="btn btn-primary" name="groupByTypeButton" value="Group"
                                        style="display: inline; vertical-align: middle;"/>
                    </div>
                </g:form>
            </div>
        </div>

        <div class="row-fluid">
            <g:render template="facets"
                      model="['facets': facets, 'formName': FacetFormType.CompoundBioActivitySummaryForm]"/>
            <g:hiddenField name="compoundId" id='compoundId' value="${params?.id}"/>


            <div class="span9">
                <div>
                    <g:if test="${tableModel.additionalProperties.activityOutcome == ActivityOutcome.ACTIVE}">
                        <p class="text-info"><i
                                class="icon-info-sign"></i>Only showing results where the compound is active
                        </p>
                    </g:if>
                </div>

                <div>
                    <g:if test="${tableModel.additionalProperties?.experimentWithSinglePointDataOnly?.size()}">
                        <p class="text-info"><i
                                class="icon-info-sign"></i><span>Please note:</span>
                            <span>there is/are
                            <g:generateLinksList
                                    controller="bardWebInterface"
                                    action="showExperiment"
                                    ids="${tableModel.additionalProperties.experimentWithSinglePointDataOnly*.value*.bardExptId}"/>
                            experiment(s) with single-point data only</span>
                        </p>
                    </g:if>
                    <g:if test="${tableModel.additionalProperties.experimentsWithoutResultData?.size()}">
                        <p class="text-info"><i
                                class="icon-info-sign"></i><span>Please note:</span>
                            <span>there is/are
                            <g:generateLinksList
                                    controller="bardWebInterface"
                                    action="showExperiment"
                                    ids="${tableModel.additionalProperties.experimentsWithoutResultData*.value*.bardExptId}"/>
                            experiment(s) with no data</span>
                        </p>
                    </g:if>
                </div>
                <g:if test="${tableModel?.data}">
                    <div id="compoundBioActivitySummaryDiv">
                        <g:render template="experimentResultRenderer"
                                  model="[tableModel: tableModel, landscapeLayout: false, innerBorder: true]"/>
                    </div>
                </g:if>
            </div>
        </div>
    </g:if>
    <g:else>
        <p class="text-info"><i
                class="icon-warning-sign"></i> None of the assays that tested this compound demonstrated any activity ${tableModel?.additionalProperties?.id}
        </p>
    </g:else>
</div>
</body>
</html>