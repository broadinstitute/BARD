<g:set var="cardIdCounter" value="${0}"/>
<g:set var="itemId" value="${0}"/>
<table>
<tr><td>
    <g:each in="${cardDtoList}" status="cardIndex" var="card">
        <g:set var="cardIdCounter" value="${cardIdCounter + 1}"/>
        <g:if test="${(cardIndex % 3) == 0 && cardIndex != 0}">
            </td></tr><tr><td>
        </g:if>
        <g:render template="cardDto" model="['card': card, 'cardId': cardIdCounter]"/>
    </g:each>
</td></tr>
</table>