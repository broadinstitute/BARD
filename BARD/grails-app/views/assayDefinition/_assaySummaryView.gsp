<div>
	<dl class="dl-horizontal">
	
		<g:if test="${assayInstance?.id}">
			<dt><g:message code="assay.id.label" default="ID:" /></dt>
	  		<dd><g:fieldValue bean="${assayInstance}" field="id"/></dd>
  		</g:if>
  		
  		<g:if test="${assayInstance?.assayName}">
	  		<dt><g:message code="assay.assayName.label" default="Name:" /></dt>
	  		<dd><g:fieldValue bean="${assayInstance}" field="assayName"/></dd>
  		</g:if>
  		
  		<g:if test="${assayInstance?.assayVersion}">
	  		<dt><g:message code="assay.assayVersion.label" default="Version:" /></dt>
	  		<dd><g:fieldValue bean="${assayInstance}" field="assayVersion"/></dd>
  		</g:if>
  		
  		<g:if test="${assayInstance?.assayStatus}">
	  		<dt><g:message code="assay.assayStatus.label" default="Status:" /></dt>
	  		<dd><g:fieldValue bean="${assayInstance}" field="assayStatus"/></dd>
  		</g:if>
  		
  		<g:if test="${assayInstance?.designedBy}">
	  		<dt><g:message code="assay.designedBy.label" default="Designed By:" /></dt>
	  		<dd><g:fieldValue bean="${assayInstance}" field="designedBy"/></dd>	
  		</g:if>
  		
  		<g:if test="${assayInstance?.dateCreated}">
	  		<dt><g:message code="assay.dateCreated.label" default="Date Created:" /></dt>
	  		<dd><g:formatDate date="${assayInstance?.dateCreated}" /></dd>
  		</g:if>
	</dl>
				%{--
				<g:if test="${assayInstance?.id}">
				<li>
					<span id="assayId-label"><g:message code="assay.id.label" default="ID:" /></span>				
					<span aria-labelledby="assayId-label"><g:fieldValue bean="${assayInstance}" field="id"/></span>					
				</li>
				</g:if>
				<g:if test="${assayInstance?.assayName}">
				<li>
					<span id="assayName-label"><g:message code="assay.assayName.label" default="Name:" /></span>
					<span aria-labelledby="assayName-label"><g:fieldValue bean="${assayInstance}" field="assayName"/></span>					
				</li>
				</g:if>
			
				<g:if test="${assayInstance?.assayVersion}">
				<li>
					<span id="assayVersion-label"><g:message code="assay.assayVersion.label" default="Version:" /></span>					
					<span aria-labelledby="assayVersion-label"><g:fieldValue bean="${assayInstance}" field="assayVersion"/></span>					
				</li>
				</g:if>	
				<g:if test="${assayInstance?.assayStatus}">
				<li>
					<span id="assayStatus-label"><g:message code="assay.assayStatus.label" default="Status:" /></span>					
					<span aria-labelledby="assayStatus-label"><g:fieldValue bean="${assayInstance}" field="assayStatus"/></span>					
				</li>
				</g:if>		
				<g:if test="${assayInstance?.designedBy}">
				<li>
					<span id="designedBy-label" class="property-label"><g:message code="assay.designedBy.label" default="Designed By:" /></span>					
					<span aria-labelledby="designedBy-label"><g:fieldValue bean="${assayInstance}" field="designedBy"/></span>					
				</li>
				</g:if>			
				<g:if test="${assayInstance?.dateCreated}">
				<li>
					<span id="dateCreated-label" class="property-label"><g:message code="assay.dateCreated.label" default="Date Created:" /></span>
					<span aria-labelledby="dateCreated-label"><g:formatDate date="${assayInstance?.dateCreated}" /></span>				
				</li>
				</g:if>
				--}%
</div>