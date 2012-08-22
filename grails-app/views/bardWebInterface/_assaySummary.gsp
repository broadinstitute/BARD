<g:if test="${assayInstance?.id}">
	<li>
		<span>ID:</span>				
		<span>${assayInstance.id}</span>
	</li>
</g:if>
<g:if test="${assayInstance?.name}">
	<li>
		<span>Name:</span>
		<span>${assayInstance.name}</span>				
	</li>				
</g:if>
<g:if test="${assayInstance?.category}">
    <li>
         <span>Category:</span>
         <span>${assayInstance.category}</span>
	</li>
</g:if>
<g:if test="${assayInstance?.role}">
    <li>
        <span>Role:</span>
        <span>${assayInstance.role}</span>
    </li>
</g:if>
<g:if test="${assayInstance?.type}">
    <li>
        <span>Type:</span>
        <span>${assayInstance.type}</span>
    </li>
</g:if>
<g:each var="otherAssayProperty" in="${assayInstance.values}">
<li>
<span>${otherAssayProperty.id}:</span>
<span>${otherAssayProperty.value}</span>
</li>
</g:each>
