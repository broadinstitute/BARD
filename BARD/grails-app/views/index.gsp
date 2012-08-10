<!doctype html>
<html>
<head>
    <r:require modules="bootstrap"/>
    <title>CAP - Catalog of Assay Protocols</title>
</head>

<body>
<br/>
<br/>
<div class="row">
    <div class="span2"><a href="${grailsApplication.config.bard.home.page}"><img src="${resource(dir: 'images', file: 'bardLogo.png')}" alt="To BARD Home" title="To BARD Home"/></a></div>
    <div class="span6"><h1 style="text-align: center">CAP - Catalog of Assay Protocols</h1><br/><br/>
        <div class="span6">
            <p>CAP allows you to define assays, which can subsequently be used to upload data into the BioAssay Research Database (BARD).
            There are several ways you can create a new assay: <br/>
            You may wish to create a new assay from scratch.<br/>
            You can find an existing assay to clone and then modify**  <br/>
            You may wish to view favorite assays that you previously created or found, to clone and modify** <br/> <br/>

            You might find it helpful to [read about assay terminology in more detail] <br/><br/>

            ** If you find an existing assay that matches your experiment, no need to clone and modify and create a new assay!
            You can proceed to upload data against it immediately. <br/>
           </p>
            <br/><br/>
            <p>
                <a href="#"><img src="${resource(dir: 'images', file: 'lightBulbIcon.png')}" alt="Create New Assay" title="Create New Assay"/> Create a new assay from scratch</a> <br/><br/>
                <a href="#"><img src="${resource(dir: 'images', file: 'magnifyingGlassIcon.png')}" alt="Find Exiting Assay" title="Find Exiting Assay"/> Find an existing assay to clone</a><br/> <br/>
                <a href="#"><img src="${resource(dir: 'images', file: 'starIcon.png')}" alt="View Favorite Assay" title="View Favorite Assay"/> View favorite assays</a><br/><br/>
                <a href="#"><img src="${resource(dir: 'images', file: 'openBookIcon.png')}" alt="Read about assay terminology" title=" Read about assay terminology"/> Read about assay terminology in more detail</a><br/><br/>
            </p>
            <br/>
            <br/>
            <p> <b>Background</b><br/> <br/>
                For our purposes, an assay describes an experiment carried out to test the effect of a perturbagen on a biological entity, measuring one or more readouts facilitated by an assay design and assay format, and recording the results of one or more endpoints that quantify or qualify the extent of perturbation.
                <br/><br/>
                For example:  "When I run my assay I screen a compound collection in duplicate.  This run is a primary assay.  Next time I run the assay I will screen the cherry pick in duplicate 8-point dose as a confirmatory assay.
                These assays are all for a screening project with a biological goal related to a disease or biological process."
                <br/><br/>
                The Catalog of Assay Protocols (CAP) serves to catalog assays using a structured, well-defined language so that scientists can make meaningful comparisons between their assays and experiments.  These well-defined assays form the foundation for the BioAssay Research Database (BARD), allowing scientists to carrying out data analysis and visualization of the experimental results for the assays.

            </p>
        </div>
    </div>
    <div class="span2"><a href="${grailsApplication.config.bard.home.page}"><img src="${resource(dir: 'images', file: 'bardLogo.png')}" alt="CAP - Catalog of Assay Protocols"/></a></div>
</div>
<r:layoutResources />
<r:require modules="bootstrap"/>
</body>
</html>
