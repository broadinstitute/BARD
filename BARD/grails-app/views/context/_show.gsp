<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for showing summary for both project and assay def --%>

<div id="cardView" class="cardView" class="row-fluid">

    <div class="span12">

        <div class="row-fluid">
            <div id="cardHolder" class="span12">
                <g:each in="${contexts}" var="entry">
                    <div id="${entry.key}"  class="roundedBorder card-group ${entry.key.replaceAll(/( |> )/, '-')}">

                        <div class="row-fluid">
                            <h5 class="span12">${entry.key}</h5>
                        </div>

                        <div class="row-fluid">
                            <g:each in="${entry.value}" status="i" var="context">
                                <g:if test="${(i % 2) == 0 && i != 0}">
                                    </div><div class="row-fluid">
                                </g:if>
                                <g:render template="../contextItem/show" model="['context': context]"/>
                            </g:each>
                        </div>

                    </div>
                </g:each>
            </div>
        </div>

    </div>
</div>