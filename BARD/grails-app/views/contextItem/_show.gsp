<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for showing summary for both project and assay def --%>

<r:script>
    $('#contextItemsTable').dataTable({
        "bJQueryUI": true,
        "sPaginationType": "full_numbers"
    });
</r:script>
<div>
    <g:if test="${contexts}">
        <div>
            <table id="contextItemsTable" cellpadding="0" cellspacing="0" border="0" class="display">
                <thead>
                <tr>
                    <th>Attribute Element</th>
                    <th>Value Display</th>
                    <th>Value</th>
                    <th>Value Min</th>
                    <th>Value Max</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${contexts}" status="a" var="context">
                    <g:each in="${context?.contextItems}" status="i" var="contextItem">
                        <tr>
                            <td>${fieldValue(bean: contextItem, field: "attributeElement.label")}</td>
                            <td>${fieldValue(bean: contextItem, field: "valueDisplay")}</td>
                            <td>${fieldValue(bean: contextItem, field: "valueNum")}</td>
                            <td>${fieldValue(bean: contextItem, field: "valueMin")}</td>
                            <td>${fieldValue(bean: contextItem, field: "valueMax")}</td>
                        </tr>
                    </g:each>
                </g:each>
                </tbody>
            </table>
        </div>
    </g:if>
    <g:else>
        <span>No Contexts Items found</span>
    </g:else>
</div>
