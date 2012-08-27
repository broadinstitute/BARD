<div>
				
				<g:if test="${assayInstance?.assayDocuments}">
				<li>
					<g:each in="${assayInstance.assayDocuments.sort{it.id}}" var="assayDocument">
						<g:if test="${assayDocument?.documentName}">
						<li>
							<span id="documentName-label"><g:message code="assayDocument.documentName.label" default="Name: " /></span>					
							<span><g:fieldValue bean="${assayDocument}" field="documentName"/></span>					
						</li>
						</g:if>
						
						<g:if test="${assayDocument?.dateCreated}">
						<li>
							<span id="dateCreated-label" class="property-label"><g:message code="assayDocumenty.dateCreated.label" default="Date Created:" /></span>
							<span aria-labelledby="dateCreated-label"><g:formatDate date="${assayDocument?.dateCreated}" /></span>
						</li>
						</g:if>
						
						<g:if test="${assayDocument?.documentType}">
						<li>
							<span id="documentType-label"><g:message code="assayDocument.documentType.label" default="Type: " /></span>					
							<span><g:fieldValue bean="${assayDocument}" field="documentType"/></span>					
						</li>
						</g:if>
			
						<g:if test="${assayDocument?.documentContent}">
						<li>
							<!--<span><g:message code="assayDocument.documentContent.label" default="Content:" /></span><br/> -->
							%{--<span>${assayDocument.content}</span>	 --}%
							<span><g:fieldValue bean="${assayDocument}" field="documentContent"/></span>				
							%{-- <span>${assayDocument.getContent()}</span> --}%
						</li>
						</g:if>
						<br/>
					</g:each>
				</li>
				</g:if>
				<g:else>
					<span>No documents found</span>
				</g:else>
</div>