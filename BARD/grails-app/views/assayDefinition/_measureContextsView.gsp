<div>
				<g:if test="${assayInstance?.assayContexts}">
				<li>
					<g:each in="${assayInstance.assayContexts.sort{it.id}}" var="assayContext">
						<g:if test="$assayContext?.contextName}">
						<li>
							<!--<span id="contextName-label"><g:message code="measureContext.contextName.label" default="Name: " /></span>	-->				
							<span><g:fieldValue bean="${assayContext}" field="contextName"/></span>					
						</li>
						</g:if>									
					</g:each>
				</li>
				</g:if>
				<g:else>
					<span>No Assay Contexts found</span>
				</g:else>
</div>