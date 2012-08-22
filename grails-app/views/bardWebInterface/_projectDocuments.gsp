<g:if test="${projectInstance?.protocol}">
	<li>
		<span>Protocol</span><br>				
		<span><p>${projectInstance.protocol}</p></span>
	</li>
</g:if>		
<g:if test="${projectInstance?.description}">
	<li>
		<span>Description</span><br>				
		<span><p>${projectInstance.description}</p></span>
	</li>
</g:if>
<g:if test="${projectInstance?.comments}">
	<li>
		<span>Comments</span><br>				
		<span><p>${projectInstance.comments}</p></span>
	</li>
</g:if>