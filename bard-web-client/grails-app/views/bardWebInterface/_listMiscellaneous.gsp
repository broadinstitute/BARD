<%@ page import="bard.core.rest.spring.assays.Comp" %>
<div id="cardHolderMisc" class="span12">
    <g:each in="${annotations}" var="annotationSet">
        <div class="roundedBorder card-group">
            <div class="row-fluid">
                <g:each in="${Comp.splitForColumnLayout(annotationSet.otherAnnotations)}" var="contextColumnList">
                    <div class="span6">
                        <g:each in="${contextColumnList}" var="contextItem">
                            <g:render template="showMiscItems" model="[contextItem: contextItem]"/>
                        </g:each>
                    </div>
                </g:each>
            </div>
        </div>
    </g:each>
</div>