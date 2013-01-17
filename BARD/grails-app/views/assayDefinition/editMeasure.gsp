<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,assaycards"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <meta name="layout" content="basic"/>
    <title>Edit Measures</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12 well well-small">
            <div class="pull-left">
                <h4>Editing Measures for ${assayInstance?.assayName} (ADID: ${assayInstance?.id})</h4>
            </div>
            <g:if test="${assayInstance?.id}">
                <div class="pull-right">
                    <g:link action="show" id="${assayInstance?.id}"
                            class="btn btn-small btn-primary">Finish Editing</g:link>
                </div>
            </g:if>
    </div>
</div>

<div class="alert">
    <button type="button" class="close" data-dismiss="alert">×</button>
    <strong>Tips:</strong> Edits will be saved immediately.
</div>

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

<g:if test="${assayInstance?.id}">
    <div class="row-fluid">

        <div class="span6">

            <h4>Add a measure</h4>
            <a href="#saveModal" role="button" class="btn" data-toggle="modal">Click to add new measure at the top of the hierarchy</a>
            <div id="saveModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="saveModalLabel" aria-hidden="true">
                <div class="modal-header">
                    <h3 id="saveModalLabel">Add a new measure</h3>
                </div>
                <div class="modal-body">
                    <g:form class="form-horizontal" id="${assayInstance.id}">
                        <div class="control-group">
                            <label class="control-label" for="inputMeasure">Measure</label>
                            <div class="controls">
                                <input type="text" id="inputMeasure" placeholder="Measure name">
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputStatistic">Statistic</label>
                            <div class="controls">
                                <input type="text" id="inputStatistic" placeholder="Statistic name">
                            </div>
                        </div>
                    </g:form>
                </div>
                <div class="modal-footer">
                    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
                    <button class="btn btn-primary">Save</button>
                </div>
            </div>

            <h4>Moving Measures</h4>
            <p>To change the location of a measure in the tree, select the name and drag it to the new location.</p>
            <h3>Measures</h3>
            <r:require module="dynatree"/>
            <g:dynaTree id="measure-tree" measures="${assayInstance.rootMeasures}" editable="true"/>
        </div>

        <div class="span6">
        <%-- Statically render every measurement details as not displayed.  Selecting nodes in tree will display one card --%>
            <div id="measure-details-none" class="measure-detail-card">
                <p>To edit, delete or add children to a measure, first click on the name in the tree below to select it.</p>
            </div>
            <g:each in="${assayInstance.measures}" var="measure">
                <div id="measure-details-${measure.id}" class="measure-detail-card" style="display: none">
                    <h1>Measure: ${measure.resultType?.label}</h1>
                    <p><strong>Statistic:</strong> ${measure.statsModifier?.label}</p>
                    <p><strong>Definition:</strong> ${measure.resultType?.description}</p>

                    <h4>Add child measure</h4>
                    <a href="#saveModal" role="button" class="btn" data-toggle="modal">Click to add new measure under ${measure.resultType?.label}</a>

                    <h4>Add association</h4>
                    <p>To associate this measure with a context, select the context below and click "associate".</p>
                    <g:form class="form-horizontal" id="${assayInstance.id}" action="associateContext">
                        <input type="hidden" name="measureId" value="${measure.id}"/>
                        <g:select from="${assayInstance.assayContexts}" noSelection="${['null':'Select context...']}" optionKey="id" optionValue="preferredName" name="assayContextId"></g:select>
                        <button type="button" class="btn" onclick="this.form.submit()">Associate</button>
                    </g:form>

                    <h3>Associated Contexts:</h3>
                    <g:if test="${measure.assayContextMeasures.empty}">
                        <p>No assay contexts associated with this measure</p>
                    </g:if>
                    <g:else>
                        <g:each in="${measure.assayContextMeasures}" var="assayContextMeasure">
                            <g:set var="context" value="${assayContextMeasure.assayContext}"/>
                            <div class="card roundedBorder card-table-container">
                                <table class="table table-hover">
                                    <caption id="${context.id}" class="assay_context">
                                        <div class="cardTitle">
                                            <p>${context.preferredName}</p>
                                        </div>
                                    </caption>
                                    <tbody>
                                    <g:each in="${context.contextItems}" status="i" var="contextItem">
                                        <tr id="${contextItem.id}" class='context_item_row'>
                                            <td class="attributeLabel">${contextItem.attributeElement?.label}</td>
                                            <td class="valuedLabel">${contextItem.valueDisplay}</td>
                                        </tr>
                                    </g:each>
                                    </tbody>
                                </table>
                                <g:form class="form-horizontal" id="${assayInstance.id}" action="disassociateContext">
                                    <input type="hidden" name="measureId" value="${measure.id}"/>
                                    <input type="hidden" name="assayContextId" value="${context.id}"/>
                                    <button type="button" onclick="this.form.submit()">Disassociate context from ${measure.resultType?.label}</button>
                                </g:form>

                            </div>
                        </g:each>
                    </g:else>

                    <button type="button" class="btn">Click to delete ${measure.resultType?.label} entirely</button>

                </div>
            </g:each>
        </div>
    </div>
</g:if>

</body>
</html>