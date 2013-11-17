<%@ page import="bard.db.enums.ExperimentStatus; bard.db.enums.ContextType; bard.db.registration.DocumentKind; bard.db.model.AbstractContextOwner; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix"/>
    <meta name="layout" content="howto"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>How to search</title>

</head>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>how to Visualize Data</h2></article>
            <aside class="span2"></aside>
        </div>

    </div>
</div>


<article class="hero-block">
    <div class="container-fluid">
        <div class="lowkey-hero-area">
            <div class="row-fluid">
                <aside class="span2"></aside>
                <article class="span8">

                    <h3>
                        Overview
                        </h3>

                    <p>
                        BARD offers you a variety of tools for the visualization of assay definitions, compounds and projects of interest. These flexible tools will give you a variety of ways to drilldown deeply into details, further refine your results, uncover and explore connections, and generate new hypotheses.
                        </p>

                    <aside class="calloutbox">

                    <h3>
                        How to Get the Most from BARD
                        </h3>

                    <p>
                        Be sure to invest some time reading <a href="../about/howToGetTheMostFromBard">How to Get the Most from BARD</a>. You’ll see sample workflows that demonstrate how these visualization tools work in concert with each other and other features throughout the system to unlock the full potential of the data.
                        </p>
                    </aside>

                    <p>
                        If you are satisfied that you have found what you’re looking for, these tools will enable you to create fine-tuned visualizations that highlight aspects of your selected data that are most relevant to your work. Depending upon the tool, you will have options of how to capture those visualizations, even export the data itself, for offline analysis, reference and presentation.
                        </p>


                    <p>
                        When you have multiple compounds:
                        </p>

                    <h3>
                        Molecular Spreadsheet – Compare the Activity for a Group of Compounds
                        </h3>

                    <p>
                        This is a table-based visualization of the experimental results for a group of compounds. The cells of the spreadsheet display summary result information for each experiment, as well as an indication of the activity outcome (active, inactive, inconclusive, not tested in this experiment). To use this flexible tool, click the VISUALIZE button at the bottom of your query cart and select it.
                        </p>

                    <section class="shiftedSection">
                        <dl  class='unindentedDefinition'>
                            <dt>View Summary Result Details</dt>
                            <dd>Hover over underlined summary results to view detailed information such as dose response curves.</dd>
                            <br/>
                            <dt>Sort Columns</dt>
                            <dd>Simply click on the column’s header.</dd>
                            <br/>
                            <dt>Transpose the Table</dt>
                            <dd>Improve readability when you have few compounds and many assay definitions.</dd>
                            <br/>
                            <dt>De-normalize the Y-Axis of Dose Response Graphs</dt>
                            <dd>The y-axis of all dose response graphs are normalized to the same range. Click DENORMALIZE in the column header to undo this default.</dd>
                            <br/>
                            <dt>Results from active assays</dt>
                            <dd>When no assays or projects are specified through the query cart then results are displayed from assays only when at least one of those results is marked as active. Click "Show Inactive Results" to display assays from which no results were marked as active.</dd>
                            <br/>
                            <dt>Focus Results</dt>
                            <dd>Constrain compounds to specific projects or assay definitions by adding those projects and assay definitions to your query cart.</dd>
                            <br/>
                            <dt>Export Data</dt>
                            <dd>Generate a PDF, Microsoft Excel spreadsheet or a CSV file.</dd>
                        </dl>
                    </section>

                    <h3>
                        Desktop Client – Manipulate Quantities of Data and Protect Proprietary Compounds
                        </h3>

                    <p>
                        This high-performance application enables you to work with large quantities of BARD data. It also provides a secure environment to upload your proprietary structures via an SDF file to safely research the performance of similar structures.
                        </p>

                    <section class="shiftedSection">
                        <dl  class='unindentedDefinition'>
                            <dt>Simple to Use </dt>
                            <dd>Download the desktop client from the homepage or from your query cart by clicking VISUALIZE. It will launch via Java WebStart and display the contents of your query cart as a collection.</dd>
                            <br/>
                            <dt>Search Securely</dt>
                            <dd>A one-directional hashing algorithm ensures that searches against BARD will not reveal your proprietary information.</dd>
                            <br/>
                            <dt>Learn More</dt>
                            <dd>To make the most of this powerful tool, see the Desktop Client User Manual.</dd>

                        </dl>
                    </section>

                    <p>
                        When you have a single compound:
                        </p>

                    <h3>
                        Show Experimental Results – Quickly See Assay Definitions that Show Activity
                        </h3>
                    <p>
                        This specialized version of the molecular spreadsheet will display all of an assay's results when at least one of those results is marked as active. Inactive results are also available, and may be displayed by clicking the checkbox "Show Inactive Results". You can access this tool by clicking the “i” information icon next to a structure drawing and selecting “Show Experimental Results”.
                        </p>

                    <p>
                    Note that all of the capabilities associated with the regular molecular spreadsheet are available through the “Show Experimental Results” page. </p>


                    <h3>
                        Compound Bioactivity Summary – See Details of a Compound's Bioactivity
                        </h3>

                    <p>
                        This tool allows you to quickly see all the experimental results that the compound tested active in. You can access it by clicking the “i” information icon next to a structure drawing, or by clicking the "Compound Bioactivity Summary" link near the top of a compound’s detail page.
                        </p>

                    <section class="shiftedSection">
                        <dl class='unindentedDefinition'>
                            <dt>Group by Assay </dt>
                            <dd>Show how a compound performed across repeated tests using the same protocol.</dd>
                            <br/>
                            <dt>Group by Project</dt>
                            <dd>Show which projects pursued that compound and how far down the screening pipeline it went.</dd>
                            <br/>
                            <dt>De-normalize the Y-Axis</dt>
                            <dd>The y-axis of all graphs are normalized to the same range. Click DENORMALIZE in the column header to undo this default.</dd>
                            <br/>
                            <dt>Hide Single Point Data</dt>
                            <dd>Hide all experiments that only tested at a single concentration point.</dd>
                            <br/>
                            <dt>Filter by Result Type</dt>
                            <dd>See only experiments with certain result types by checking the appropriate boxes.</dd>
                        </dl>
                    </section>

                    <h3>
                        Linked Hierarchy Visualization – Better Understand a Compound’s Activity
                        </h3>
                    <p>
                        This is an interactive visualization of the assay definitions that a compound was tested in. It uses annotations and hierarchical metadata to provide insight into the activity trends for a compound to help you understand if it's active against a particular class of targets or shows activity in a particular type of assay. This visualization displays four pie charts that show the distribution of values for GO Biological Process, Panther Target Class, Assay Type, and Assay Format. Access this tool by clicking the “i” information icon next to a structure drawing, or by clicking the "Linked Hierarchy Visualization" link near the top of a compound’s detail page.
                        </p>

                    <section class="shiftedSection">
                        <dl class='unindentedDefinition'>
                            <dt>Filter by Pie Chart</dt>
                            <dd>Click on any pie chart wedge to filter the table of assay definitions below to show only those that match the annotation for the selected wedge. You can choose to filter by multiple dimensions at once by clicking on multiple pie segments.</dd>
                            <br/>
                            <dt>Expand to Sunburst Visualization</dt>
                            <dd>Click “Drill Down” to expand the pie chart for Protein Class, Assay Type or Assay Format into a sunburst visualization. This will show a hierarchical view with the broadest categories closest to the center and more detailed ones further out. The arc length of each segment is proportional to the number of assay definitions testing the compound that match that annotation value. The color of the segment is proportional to the percentage of assay definitions where the compound tested active, a darker color indicating more activity.</dd>
                            <br/>
                            <dt>See More Details</dt>
                            <dd>Hover over a segment with your mouse to see activity. Click a segment to drill down even deeper, making that segment the center of the sunburst. Click the center of the sunburst to zoom back out. "Click to contract" will return you to the pie chart view.</dd>
                        </dl>
                    </section>
                    <p>
                        When you have an assay definition or experiment:
                        </p>

                    <h3>
                        Experimental Results – See and Filter All of an Experiment’s Results
                        </h3>

                    <p>
                        See all the results for a single experiment grouped by up to ten substances (PubChem SID) at once. You will see numeric values, text, and graphs showing dose response curves. You will also see histograms for key result types. To use this visualization tool, click “Show experiment” on the detail page of an assay definition or project, then choose "Result Summary" from the table of contents, then “View all results for this experiment.”
                        </p>

                    <section class="shiftedSection">
                        <dl class='unindentedDefinition'>
                            <dt>Show All or Only Active Results</dt>
                            <dd>Only active results are displayed, but there is an option on the left to show all results.</dd>
                            <br/>
                            <dt>De-normalize the Y-Axis</dt>
                            <dd>The y-axis of all graphs are normalized to the same range. Click DENORMALIZE in the column header to undo this default.</dd>
                        </dl>
                    </section>

                    <h3>
                        Project Steps Graph – Understand a Project’s Flow
                    </h3>

                    <p>
                        This visualization shows you all of the experiments in a project and the order in which they were run, as well as the experiment stage – the intended purpose of the experiment in the context of the project (primary, confirmatory, compound toxicity screen, etc.). To use this visualization, navigate to the detail page for a project and select "Experiments and Steps" from the table of contents.
                        </p>

                    <section class="shiftedSection">
                        <dl class='unindentedDefinition'>
                            <dt>View Details</dt>
                            <dd>Click one of the boxes on the graph for more information about the experiment.</dd>
                            <br/>
                            <dt>Navigation</dt>
                            <dd>Use the + or – buttons to zoom in or out, and click and drag to move the diagram around.</dd>
                        </dl>
                    </section>

                    <h3>
                        Capturing Your Data
                        </h3>
                    <p>
                        BARD makes extensive use of functionality common on every computer for you to capture your search results for offline analysis, reference and presentation.
                        </p>

                    <section class="shiftedSection">
                        <dl class='unindentedDefinition'>
                            <dt>Bookmark</dt>
                            <dd>The detail page for any assay definition, compound or project can be bookmarked in your browser for fast retrieval. However, you cannot bookmark dynamic pages, such as your query cart and pages that list your search results.</dd>
                            <br/>
                            <dt>Export Data</dt>
                            <dd>The molecular spreadsheet enables you to export your data as a PDF, Microsoft Excel spreadsheet or Comma-Separated Values (CSV) file. Look for more export functionality coming to BARD in 2014.</dd>
                            <br/>
                            <dt>Print</dt>
                            <dd>Given the dynamic complexities of BARD, the easiest way to print any page reliably is by first making a screen capture, then printing that.</dd>
                        </dl>
                    </section>

                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>
