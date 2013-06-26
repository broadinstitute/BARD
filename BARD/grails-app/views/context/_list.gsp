<div id="errorCardHolder" class="span12"></div>

<div id="cardHolder" class="span12">


    <g:each in="${contexts}" var="entry">
    %{--hack here to limit the showing the--}%
        <g:if test="${entry.value.size() > 0 || (subTemplate.equals("edit"))}">
            <div id="${entry.key}" class="roundedBorder card-group ${entry.key.trim().replaceAll(/( |>)/, '-')}">
                <div class="row-fluid">
                    <h4>${entry.key}</h4>
                </div>
                <g:render template="../contextItem/${subTemplate}GroupHeader"
                          model="[contextOwner: contextOwner, cardSection: entry.key]"/>
                <p>
                    ${entry.description}
                </p>

                <div class="row-fluid">
                    <g:each in="${contextOwner.splitForColumnLayout(entry.value)}" var="contextColumnList">
                        <div class="span6">
                            <g:each in="${contextColumnList}" var="context">
                                <g:render template="../contextItem/${subTemplate}"
                                          model="[contextOwner: contextOwner,
                                                  context: context,
                                                  cardSection: entry.key]"/>
                            </g:each>
                        </div>
                    </g:each>
                </div>
            </div>
        </g:if>
    </g:each>
</div>