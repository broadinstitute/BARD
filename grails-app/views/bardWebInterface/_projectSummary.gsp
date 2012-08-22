<g:if test="${projectInstance?.id}">
	<li>
		<span>ID:</span>				
		<span>${projectInstance.id}</span>
	</li>
</g:if>
<g:if test="${projectInstance?.name}">
	<li>
		<span>Name:</span>
		<span>${projectInstance.name}</span>
	</li>				
</g:if>
<g:if test="${projectInstance?.category}">
    <li>
         <span>Category:</span>
         <span>${projectInstance.category}</span>
	</li>
</g:if>
<g:if test="${projectInstance?.role}">
    <li>
        <span>Role:</span>
        <span>${projectInstance.role}</span>
    </li>
</g:if>
<g:if test="${projectInstance?.type}">
    <li>
        <span>Type:</span>
        <span>${projectInstance.type}</span>
    </li>
</g:if>
<g:each var="otherProjectProperty" in="${projectInstance.values}">
<li>
<span>${otherProjectProperty.id}:</span>
<span>${otherProjectProperty.value}</span>
</li>
</g:each>
