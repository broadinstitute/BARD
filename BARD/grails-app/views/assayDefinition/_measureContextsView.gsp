<div>
				<g:if test="${assayInstance?.measureContexts}">
				<li>
					<g:each in="${assayInstance.measureContexts.sort{it.id}}" var="measureContext">
						<g:if test="${measureContext?.contextName}">
						<li>
							<!--<span id="contextName-label"><g:message code="measureContext.contextName.label" default="Name: " /></span>	-->				
							<span><g:fieldValue bean="${measureContext}" field="contextName"/></span>					
						</li>
						</g:if>									
					</g:each>
				</li>
				</g:if>
				<g:else>
					<span>No Measure Contexts found</span>
				</g:else>
</div>