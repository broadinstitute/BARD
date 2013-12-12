<!DOCTYPE html>
<html>
<g:render template="howToHeader" model="[title:'How to get the most from BARD']"/>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>How To Get the Most From BARD</h2></article>
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
                        Every research project has it’s own unique requirements, and every researcher has their own approach to meeting them. BARD provides you with comprehensive data, but also numerous flexible features and tools. You’ll have the freedom to employ search strategies that make the most of the data to accomplish your goals, no matter what those goals might be:
                    </p>

                    <section class="shiftedSection">
                        <dl class='unindentedDefinition'>
                            <dt>Discovering Connections</dt>
                            <dd>By allowing one search to flow into the next, by taking full advantage of auto-suggest to lead you closer and open new paths, and by using filters to focus your thinking and results, often surprising connections between different data will emerge.</dd>
                            <br/>
                            <dt>Progressive Refinement</dt>
                            <dd>Think of your query cart as a quiet workspace, free of noise and clutter, where only the most interesting assay definitions, compounds and projects come together and your ideas take shape. Your cart will expand and contract as you come and go between searches and drill downs into details using visualization tools such as the molecular spreadsheet.</dd>
                            <br/>
                            <dt>Hypothesis Generation</dt>
                            <dd>Tools such as the Linked Hierarchy Visualization may lead you to new questions and hypotheses. Because BARD has made it possible to examine specific projects in detail, you can now consider the ideas that drive large collections of assays and experiments.</dd>
                        </dl>
                    </section>

                    <p>
                        Below you’ll find three sample workflows, each demonstrating how different search strategies, features and visualization tools can be used alone or in combination to help you get the most from BARD.
                    </p>

                    <p>
                    <h3>
                        Workflow 1: Biology-driven searches
                        </h3>

                    <ol>
                        <li>Type "cholesterol" into the search bar as free text</li>
                        <li>Note that different options appear through the auto suggest mechanism. Choose "cholesterol transport as GO Biological Process Term”</li>
                        <li>Observe the filters on the left side of the screen that allow you to narrow your search result. Select "fluorescence intensity” from "Detection method type" and click "Apply Filters”.</li>
                        <li>Further narrow your search results by selecting “scavenger receptor class b member 1” from "Target protein”.  Click "Apply Filters”.</li>
                        <li>From the assays on the screen click on the blue text that provides the title for "Assay using Dil-labeled EGL to measure lipid transfer in ldlA[SR-BI] cells: 2085-01”</li>
                        <li>Examine some of the information available on this page to describe the chosen assay. Move eventually to section 4 ("Experiments”)</li>
                        <li>Select the experiment with ID 1462 (“Using DiI-HDL to assay lipid transfer in ldlA[SR-BI] cells, DMSO cherry pick confirmation at dose”) by clicking on the title.</li>
                        <li>Examine the record for this experiment. Note that below the histogram in section 3 there is a link titled "View all results for this experiment".  Click that link.</li>
                        <li>Examines some of the molecules that were tested in this experiment. Locate the molecule with CID=3245746. Find the icon marked with a letter I in the lower right-hand corner of the image of the molecular structure for this molecule.  Click on the icon, and note the available options. Select “Bioactivity Summary” and click that option.</li>
                        <li>You should now see a page labeled "Compound Bio Activity Summary” which provides a detailed listing of experimental data for assays in which this molecule was found to be active (including the assay we began this sequence with, ID = 5499).</li>

                    </ol>


                     <p>

                    <h3>
                        Workflow 2: Chemistry-driven searches
                        </h3>
                        <ol>
                            <li>Type “DNA repair” into the search bar and press enter.</li>
                            <li>Note that search results are available on three tabs below: “Assay Definitions”, "Compounds", and "Projects". Select the Compounds tab.</li>
                            <li>Look through the molecular structures returned by the search. About halfway down the page is a structure for the drug “Chlorambucil”.  Click on the icon (marked with an “i”) to the lower right of the molecular structure for Chlorambucil.  Click on the option "Show Experimental Details”.</li>
                            <li>You will see a page labeled "Molecular Spreadsheet” showing experimental results for assays that indicated activity for this compound. Use the ‘back arrow ’ option on your browser to return to the previous page (search results).</li>
                            <li>Return to the Compounds tab.  This time click on the icon below the molecular structure for the first compound (“CAFdA”) and click on the option “Search for Analogs”.  Bard will perform a search to find molecular structures with a similarity coefficients of 90% or greater to CAFdA.</li>
                            <li>On the resulting page of molecular search results click on the checkbox labeled “Save to Cart for analysis” for the first four compounds.  Next, move to the upper right-hand corner of the screen and find a box labeled “QUERY CART”.  This box should indicate that it contains the molecules you selected. Click on the QUERY CART box to open the display.</li>
                            <li>Observe references to the molecules you selected. Find a drop-down box labeled VISUALIZE in the lower portion of the Query Cart box. Click on this box to display its options. Among the two options click the first option, labeled "Molecular Spreadsheet”.</li>
                            <li>You should now see a Molecular Spreadsheet displaying your selected molecules along with all of the assays for which one or more of these molecules demonstrated activity.  Mouse over any underlined results to see a dose response curve. Note that any numerical results can be sorted by clicking on the header, or that the entire table can be exported in a number of formats.</li>
                        </ol>
                    <p>
                    <h3>
                        Workflow 3: Project-driven searches
                    </h3>


                    <ol>
                        <li>Start by selecting the name of a Molecular Libraries probe (“ML162”).  Note that the search brings back both the compounds (on the compounds tab) and a project. Move to the Projects tab.</li>
                        <li>Scan through some of the information available to describe this project. Note in particular the two resulting probes (both ML162 and ML210), and the project annotations that make this record accessible via searches.  Note also the interactive project diagram, which permits an overview of the decision-making process driving the project by showing how one experiment leads to another (note that those experiments and their associated assay links are also presented below the project diagram).  Finally moved back to the top of the screen, click the checkbox “Save to Cart for analysis”, then move to the display for probe ML210 and click “Show Compound Details in BARD”.</li>
                        <li>On the compound page click the small checkbox to “Save to Cart for analysis”.  The query cart will now display all active experimental results for this compound, but only when those experiments were performed as a part of our selected project.  Click on the QUERY CART button in the upper right-hand corner of the screen, then click on the VISUALIZE drop-down box and select the Molecular Spreadsheet to see this display.</li>
                        <li>After viewing the Molecular Spreadsheet click again on the QUERY CART button, and this time click on the name of the selected project ("Screen for RAS-Selective Lethal Compounds and VDAC Ligands”)</li>
                        <li>You will be faced again with the project view page for project ID 907.  This time move to the probe record ML162 and click the icon near the molecular structure.  Choose “Linked Hierarchy Visualization” and click.</li>
                        <li>Upon updating the screen should present a visualization of all assays that were used to test this probe compound. For different parameters describing is assays (Biological process, Assay format, Protein class, and Assay type) are used to identify for different columns that categorize the available assays. The pie charts above each column shows the proportion of the total number of assays that fall into each of these categories. Click on the pie slice labeled “single protein format” in the ‘Assay format’ pie chart and note that the elements in the table has been filtered to include only those whose assay format is “single protein format”.</li>
                        <li>Next click on “signaling molecule” in the “Protein class” pie chart. Note that the table below is again filtered, so that the only records remaining now meet both of the criteria you have set. Note also that the remaining pie charts have been reformed to describe only those data that remain.  Now click the “reset” labels located above each of the two selected pie charts.</li>
                        <li>Now click the “drill down” button located in the header for “Assay type”.  You will now see a hierarchical presentation of the different assay types, showing high-level categories (nearer the center of the “sunburst display” as well as those constituent categories (located radially further from the center) that belong to the higher-level categories. Note that each element in this diagram is color-coded to indicate the ratio of the assays in each category that were active.  Now click on the pie section labeled “viability assay”.</li>
                        <li>Note that the sunburst plot reforms, filtering out any assays that were not in the category you clicked, or else in some subcategory belonging to the clicked category. Furthermore, notice that the pie charts above the sunburst plot have also contracted based on the newly filtered data set.  Now click on the button “click to contract”.</li>
                        <li>Note that the assay type category now contains only the high-level category you selected (viability assay) or its two subcategories (apoptosis assay or cytotoxicity assay).  Note that you can continue to filter the available data by clicking on other pie charts, or else by drilling down into other categories and perhaps interacting with those sunburst diagrams. Understand that this tool allows an exploration of the relationship between assays based on the hierarchical relationships between these different descriptions of their characteristics. This sort of visual analysis may be useful for hypothesis generation, and is a tool that is unique to BARD.</li>

                    </ol>




                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>
