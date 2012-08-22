<g:if test="${assayInstance?.protocol}">
	<li>
		<span>Protocol</span><br>				
		<span><p>${assayInstance.protocol}</p></span>					
	</li>
</g:if>		
<g:if test="${assayInstance?.description}">
	<li>
		<span>Description</span><br>				
		<span><p>${assayInstance.description}</p></span>					
	</li>
</g:if>
<g:if test="${assayInstance?.comments}">
	<li>
		<span>Comments</span><br>				
		<span><p>${assayInstance.comments}</p></span>					
	</li>
</g:if>