<r:script>
    $('#measuresTable').dataTable({
        "bJQueryUI":true,
        "sPaginationType":"full_numbers"
    });
</r:script>
<div>
    <g:if test="${assayInstance?.measures}">
        <div>
            <table id="measuresTable" cellpadding="0" cellspacing="0" border="0" class="display"">
                <thead>
                    <tr>
                        <th><g:message code="measure.id" default="ID"/></th>
                        <th><g:message code="measure.resultType.label" default="ResultType"/></th>
                        <th><g:message code="measure.resultType.description" default="Description"/>
                        <th><g:message code="measure.resultType.abbreviation" default="Abbreviation"/>
                        <th><g:message code="measure.resultType.synonyms" default="Synonyms"/>
                        <th><g:message code="measure.entryUnit.label" default="Unit"/></th>
                        <th><g:message code="measure.modifiedBy" default="Modified By"/> </th>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${assayInstance.measures.sort {it.id}}" status="i" var="measureInstance">
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                            <td>${fieldValue(bean: measureInstance, field: "id")}</td>
                            <td>${fieldValue(bean: measureInstance, field: "resultType.label")}</td>
                            <td>${fieldValue(bean: measureInstance, field: "resultType.description")}</td>
                            <td>${fieldValue(bean: measureInstance, field: "resultType.abbreviation")}</td>
                            <td>${fieldValue(bean: measureInstance, field: "resultType.synonyms")}</td>
                            <td>${fieldValue(bean: measureInstance, field: "entryUnit.label")}</td>
                            <td>${fieldValue(bean: measureInstance, field: "modifiedBy")}</td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>
    </g:if>
    <g:else>
        <span>No Measures found</span>
    </g:else>
</div>