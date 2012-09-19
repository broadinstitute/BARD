<div class="row-fluid">
<g:each in="${cardDtoList}" status="cardIndex" var="card">
	<g:if test="${(cardIndex % 3) == 0 && cardIndex != 0}">
		</div><div class="row-fluid">
	</g:if>
 	<g:render template="cardDto" model="['card': card]"/>
</g:each>
</div>