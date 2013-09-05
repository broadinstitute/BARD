<%@ page import="bard.core.rest.spring.assays.Context" %>
<div class="card-group">
    <div class="row-fluid">
        <g:each in="${Context.splitForColumnLayout(contexts)}" var="contextColumnList">
            <div class="span6">
                <g:each in="${contextColumnList}" var="context">
                    <g:render template="showContextItems" model="[context: context]"/>
                </g:each>
            </div>
        </g:each>
    </div>
</div>
