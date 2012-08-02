<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
<r:require modules="core"/>
<meta name="layout" content="basic"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
<title>Assay Definition</title>
<r:script>
	$(document).ready(function() {
		$( "#accordion" ).accordion({ autoHeight: false });
	}) 
</r:script>

</head>
<body>
<div>
  	<div class="ui-widget"><p><h1>Assay View</h1></p></div>
  	
	<g:if test="${flash.message}">
		<div class="ui-widget">
		<div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
			<p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
			<strong>${flash.message}</strong>
		</div>
		</div>
	</g:if>
	<g:if test="${assayInstance?.id}">
	<div id="accordion">
	
	<!-- Assay fields -->
		<h3><a href="#">Summary for Assay ID: ${assayInstance?.id}</a></h3>
		<div>
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
			</div>

            <h3><a href="#">Assay and Biology Details</a></h3>
            <div class="cardView">
                <table>
                <tr><td>
                    <g:each in="${cardDtoList}" status="cardIndex" var="card">

                        <g:if test="${(cardIndex % 3) == 0 && cardIndex != 0}">
                            </td></tr><tr><td>
                        </g:if>

                        <div class="card">
                            <table class="gridtable">
                                <caption>${card.title}</caption>
                                <tbody>
                                <g:each in="${card.lines}" status="i" var="line">
                                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                                        <td>${line.attributeLabel}</td>
                                        <td>${line.valueLabel}</td>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </div>
                    </g:each>
                </td></tr>
                </table>
            </div>

    <!-- Assay Documents fields -->
			<h3><a href="#">Documents</a></h3>
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
			
			<!-- Assay-Measure Context fields -->
			<h3><a href="#">Measure Contexts</a></h3>
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
			
			<!-- Assay-Measure fields -->
			<h3><a href="#">Measures</a></h3>
			<div>
				<g:if test="${assayInstance?.measures}">
						<div>
						<table class="gridtable">
							<thead>
								<tr>
									<th><g:message code="measure.id.label" default="ID" /></th>
									<th><g:message code="measure.entryUnit.label" default="Unit" /></th>
									<th><g:message code="measure.element.label.label" default="Label" /></th><!--  -->	
									<th><g:message code="measure.element.description.label" default="Description" />
									<th><g:message code="measure.element.abbreviation.label" default="Abbreviation" />
									<th><g:message code="measure.element.synonyms.label" default="Synonyms" />																							
									<g:sortableColumn property="modifiedBy" title="${message(code: 'measure.modifiedBy.label', default: 'Modified By')}" />																																								
								</tr>
							</thead>
							<tbody>
							<g:each in="${assayInstance.measures.sort{it.id}}" status="i" var="measureInstance">
								<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">	
									<td>${fieldValue(bean: measureInstance, field: "id")}</td>										
									<td>${fieldValue(bean: measureInstance, field: "entryUnit.unit")}</td>			
									<td>${fieldValue(bean: measureInstance, field: "element.label")}</td>%{--  --}%
									<td>${fieldValue(bean: measureInstance, field: "element.description")}</td>
									<td>${fieldValue(bean: measureInstance, field: "element.abbreviation")}</td>
									<td>${fieldValue(bean: measureInstance, field: "element.synonyms")}</td>											
									<td>${fieldValue(bean: measureInstance, field: "modifiedBy")}</td>														
																	
								</tr>
							</g:each>
							</tbody>
						</table>
						</div>
				</g:if>
				<g:else>
					<span>No Measures found</span>
				</g:else>	
			</div>
			<h3><a href="#">Measure Context Items</a></h3>
			<div>
				<g:if test="${assayInstance?.measureContextItems}">
					<div>
						<table class="gridtable">
						<thead>
							<tr>
								<g:sortableColumn property="attributeType" title="${message(code: 'measureContextItem.id.label', default: 'ID')}" />	
								<g:sortableColumn property="attributeType" title="${message(code: 'measureContextItem.attributeType.label', default: 'Attribute Type')}" />							
								<g:sortableColumn property="attributeType" title="${message(code: 'measureContextItem.attributeElement.label.label', default: 'Attribute Element')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'measureContextItem.valueDisplay.label', default: 'Value Display')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'measureContextItem.valueNum.label', default: 'Value')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'measureContextItem.valueMin.label', default: 'Value Min')}" />
								<g:sortableColumn property="valueDisplay" title="${message(code: 'measureContextItem.valueMax.label', default: 'Value Max')}" />
							
							</tr>
						</thead>
						<tbody>
						<g:each in="${assayInstance?.measureContextItems.sort{it.id}}" status="i" var="measureContextItemInstance">
							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
								<td>${fieldValue(bean: measureContextItemInstance, field: "id")}</td>
								<td>${fieldValue(bean: measureContextItemInstance, field: "attributeType")}</td>	
								<td>${fieldValue(bean: measureContextItemInstance, field: "attributeElement.label")}</td>						
								<td>${fieldValue(bean: measureContextItemInstance, field: "valueDisplay")}</td>								
								<td>${fieldValue(bean: measureContextItemInstance, field: "valueNum")}</td>
								<td>${fieldValue(bean: measureContextItemInstance, field: "valueMin")}</td>
								<td>${fieldValue(bean: measureContextItemInstance, field: "valueMax")}</td>
							</tr>
						</g:each>
						</tbody>
					</table>
					</div>
				</g:if>
				<g:else>
					<span>No Measure Contexts Items found</span>
				</g:else>
			</div>
	</div>	<!-- End accordion -->
	</g:if>
</div><!-- End body div -->
</body>
</html>