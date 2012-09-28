<g:each in="${cardDtoMap}" var="entry">
    <div id="${entry.key}"  class="bs-docs"  style="border: 1px solid #000000; border-color: #a9a9a9; margin: 5px;">


        <div class="row-fluid">
            <h2  class="span12">${entry.key}</h2>
        </div>

        <div class="row-fluid">
            <g:each in="${entry.value}" status="cardIndex" var="card">
                <g:if test="${(cardIndex % 3) == 0 && cardIndex != 0}">
                    </div><div class="row-fluid">
                </g:if>
                <g:render template="cardDto" model="['card': card]"/>
            </g:each>
        </div>

    </div>
</g:each>
