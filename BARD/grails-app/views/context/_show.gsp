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
                <g:each in="${contexts}" var="context">
                    <div id="${context.contextName}"  class="roundedBorder card-group ${context.contextName}">

                        <div class="row-fluid">
                            <h5 class="span12">${context.contextName}</h5>
                        </div>

                        <div class="row-fluid">
                            <g:render template="../contextItem/show" model="['context': context]"/>
                        </div>

                    </div>
                </g:each>
            </div>
        </div>

    </div>
</div>
