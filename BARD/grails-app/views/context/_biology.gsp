<g:if test="${biology || (subTemplate.equals("edit") && contextOwner instanceof bard.db.registration.Assay)}">
    <div id="cardHolder" class="span12">
        <div class="row-fluid">
            <g:each in="${contextOwner.splitForColumnLayout(biology.value)}" var="contextColumnList">
                <div class="span6">
                    <g:each in="${contextColumnList}" var="context">
                        <g:render template="../contextItem/${subTemplate}"
                                  model="[contextOwner: contextOwner,
                                          context: context,
                                          cardSection: biology.key]"/>
                    </g:each>
                </div>
            </g:each>
        </div>
    </div>
</g:if>