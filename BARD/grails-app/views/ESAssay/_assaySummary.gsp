<g:if test="${assayInstance?.aid}">
	<li>
		<span>ID:</span>				
		<span>${assayInstance.aid}</span>					
	</li>
</g:if>
<g:if test="${assayInstance?.name}">
	<li>
		<span">Name:</span>
		<span>${assayInstance.name}</span>				
	</li>				
</g:if>
<g:if test="${assayInstance?.source}">
	<li>
		<span">Source:</span>
		<span>${assayInstance.source}</span>				
	</li>				
</g:if>