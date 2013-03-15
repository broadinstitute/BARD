<%@ page import="bard.core.rest.spring.assays.Context" %>
<div id="cardHolder" >
    <g:each in="${annotations}" var="annotationSet">
        <div class="roundedBorder card-group">
            <div class="row-fluid">
                <g:each in="${Context.splitForColumnLayout(annotationSet.contexts)}" var="contextColumnList">
                    <div class="span6">
                        <g:each in="${contextColumnList}" var="context">
                            <g:render template="showContextItems" model="[context: context]"/>
                        </g:each>
                    </div>
                </g:each>
            </div>
        </div>
    </g:each>
</div>