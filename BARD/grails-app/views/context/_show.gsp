<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for showing summary for both project and assay def --%>

<div>
<g:if test="${contexts}">
    <li>
    <g:each in="${contexts.sort{it.id}}" var="context">
        <g:if test="$context?.contextName}">
            <li>
                <g:message code="context.contextName.label" default="Name: " />
                <span><g:fieldValue bean="${context}" field="contextName"/></span>
            </li>
        </g:if>
    </g:each>
    </li>
</g:if>
<g:else>
    <span>No Contexts found</span>
</g:else>
</div>