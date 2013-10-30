<g:if test="${biology || (subTemplate.equals("edit") && contextOwner instanceof bard.db.registration.Assay)}">
    <div id="cardHolder" class="span12">
        <div class="row-fluid">
            <g:each in="${contextOwner.splitForColumnLayout(biology.value)}" var="contextColumnList">
                <div class="span6">
                    <g:each in="${contextColumnList}" var="context">
                        <g:if test="${showCheckBoxes}">
                        %{--for the case where we want to display only the contexts with at least one context item that has a non fixed attribute--}%
                            <g:if test="${context.atLeastOneNonFixedContextItem()}">
                                <g:render template="../contextItem/${subTemplate}"
                                          model="[
                                                  contextOwner: contextOwner,
                                                  context: context,
                                                  cardSection: biology.key,
                                                  showCheckBoxes: showCheckBoxes,
                                                  existingContextIds: existingContextIds
                                          ]"/>
                            </g:if>

                        </g:if>
                        <g:else>
                            <g:render template="../contextItem/${subTemplate}"
                                      model="[
                                              contextOwner: contextOwner,
                                              context: context,
                                              cardSection: biology.key,
                                              showCheckBoxes: showCheckBoxes,
                                              existingContextIds: existingContextIds
                                      ]"/>
                        </g:else>
                    </g:each>
                </div>
            </g:each>
        </div>
    </div>
</g:if>