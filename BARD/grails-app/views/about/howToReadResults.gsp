<%@ page import="bard.db.enums.ExperimentStatus; bard.db.enums.ContextType; bard.db.registration.DocumentKind; bard.db.model.AbstractContextOwner; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix"/>
    <meta name="layout" content="howto"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>BARD How to interpret search results</title>
</head>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>How to Interpret Search Results</h2></article>
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
                        BARD will provide search results that include every database entry that matches at least one parameter of your search. Those results will be grouped into three major data types: assay definitions, compounds and projects.  Each group will have its own tab at the top of the results page, and each tab will indicate the number of matches for that type of data.
                        </p>

                    <p>
                        The largest part of the page will display the individual results of the selected tab, with assay definitions being the default. Each result will offer an overview of key information, for more detailed information you can simply click on either the entry’s name or its ID number.
                        </p>

                    <p>
                        Results may span several pages depending upon your preference settings. So on the lower right of the page is a box that allows you to quickly navigate between pages.
                        </p>


                    <h3>
                        Assay Definitions
                        </h3>

                    <p>
                        The assay definitions tab will be displayed by default when you first arrive at the results page.
                        </p>

                        <section class="shiftedSection">
                            <dl class='unindentedDefinition'>
                                <dt>Text Search</dt>
                                <dd>Results will be every assay definition that includes a match of any of the searched text in any text field or annotation.  </dd>
                                <br/>
                                <dt>Constrained Search</dt>
                                <dd>Results will only include assay definitions from the category of data to which you constrained your search.</dd>
                            </dl>
                        </section>

                    <p>
                        Each result will include key information about each assay definition:  assay format, assay type, detection method type, assay definition ID, name, matched category, and a field to indicate the status of the assay definition’s annotation in the review process. There is also a checkbox for you to capture that result in your query cart.
                        </p>


                    <h3>
                        Compounds
                        </h3>

                    <p>
                        The second tab will contain compounds that result from a text search, constrained search, PubChem CID search or structure search.
                        </p>

                    <section class="shiftedSection">
                        <dl class='unindentedDefinition'>
                            <dt>Text Search</dt>
                            <dd>Results will be compounds where the searched text matches annotations to the compound in a descriptive field, such as Mechanism of Action, or Therapeutic Indication. "Matched Category" will always simply state “Annotation Value.” (Note: It is currently not possible to search for a compound by name.)</dd>
                            <br/>
                            <dt>Constrained Search</dt>
                            <dd>Results will only include compounds from the category of data to which you constrained your search.</dd>
                            <br/>
                            <dt>CID Search</dt>
                            <dd>Results will include all entries that match the searched PubChem Compound ID numbers. “Matched Category” will be left blank.  Note:  partial ID matches are not possible. For example, searching for CID 123 will not return CID 1234.</dd>
                            <br/>
                            <dt>Structure Search</dt>
                            <dd>Results will include matches to a molecular structure or fragment you’ve drawn or pasted into BARD. Each resulting structure and a description of the search will be shown (including a SMILES representation), but the “Matched Category” will not be listed, nor will any filtering options be presented.</dd>
                        </dl>
                    </section>

                    <p>
                        No matter the type of search, each result will offer key information such as: common name of the molecule, PubChem CID and an image of the compound’s structure. In a dropdown box next to the drawing (indicated by an “i” in a circle) you will find links to more information about the molecule, and a tool for searching analogs with an adjustable percentage threshold. Each result will also provide you with a count of every assay definition that has tested the molecule, as well as the number of those in which it was found active – you can click this number to see the results of those assay definitions for a rough assessment of the promiscuity of the compound.
                        </p>

                    <p>
                        To allow for more detailed information about promiscuity there is a Scaffold Promiscuity Analysis of the individual substructures within the molecule. This field will provide you with a collection of substructures contained within that molecule, each with a predicted value of molecular promiscuity and a link to more details.  These calculations are provided by a plug-in written by BARD collaborator Jeremy Yang as part of the <a href="http://pasilla.health.unm.edu/tomcat/badapple/badapple">'badapple’ project at the University of New Mexico</a>. You can click <a href="../about/howToUsePlugins">here</a> to learn more about writing and using BARD plug-ins.
                        </p>

                    <h3>
                        Projects
                        </h3>

                    <p>
                        The third tab will contain projects that result from a text search or PID number search.
                        </p>

                        <section class="shiftedSection">
                            <dl class='unindentedDefinition'>
                                <dt>Text Search</dt>
                                <dd>Results will be projects where the searched text matches annotations, such as the name or description.</dd>
                                <br/>
                                <dt>Constrained Search</dt>
                                <dd>Results will only include projects from the category of data to which you constrained your search.</dd>
                                <br/>
                                <dt>Project ID Search</dt>
                                <dd>The result will be the project matching the searched PID.</dd>
                            </dl>
                        </section>

                    <p>
                        You will be provided with the name of the project, its BARD project ID number and other key information. Each project result will include an overview, list of probes, annotations, description of experiments and steps, as well as links to available documentation.
                    </p>

                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>



</body>
</html>