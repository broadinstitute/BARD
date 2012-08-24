<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="searchBox"/>
    <title>Assay Definition : ADID ${assayInstance?.id}</title>
</head>
<body>
<div class="page-header">
    <h1>Assay Definition Detail for ADID </h1>
</div>
<div class="row-fluid">
    <div class="span12">
        <h3 data-toggle="collapse" data-target="#project-info">Projects</h3>
        <div id="project-info">

        </div>
        <h3 data-toggle="collapse" data-target="#assay-bio-info">Assay and Biology Details</h3>
        <div id="assay-bio-info">

        </div>
        <h3 data-toggle="collapse" data-target="#document-info">Documents</h3>
        <div id="document-info">
            <g:render template="assayDocuments" model="['assayInstance': assayInstance]" />
        </div>
        <h3 data-toggle="collapse" data-target="#result-info">Results</h3>
        <div id="result-info">

        </div>
        <h3 data-toggle="collapse" data-target="#registration-info">Registration Info</h3>
        <div id="registration-info">

        </div>
    </div>
</div>
	<div>
	  	<div class="ui-widget"><p><h1>Assay View</h1></p></div>
	  	
		<g:if test="${assayInstance?.id}">
		<div id="accordion">
		
			<h3><a href="#">Summary for Assay ID: ${assayInstance.id}</a></h3>
			<div>
				<g:render template="assaySummary" model="['assayInstance': assayInstance]" />							
			</div>		
	
			<h3><a href="#">Documents</a></h3>
			<div>
				<g:render template="assayDocuments" model="['assayInstance': assayInstance]" />	
			</div>
							
		</div>	<!-- End accordion -->
		</g:if>
	</div><!-- End body div -->
</body>
</html>