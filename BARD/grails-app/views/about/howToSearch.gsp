<%@ page import="bard.db.enums.ExperimentStatus; bard.db.enums.ContextType; bard.db.registration.DocumentKind; bard.db.model.AbstractContextOwner; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix"/>
    <meta name="layout" content="howto"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>How to search</title>
    <style>
    .shiftedSection {
        padding-left: 25px
    }
    .calloutbox {
        background-color: #eee;
        border: 2px solid #000;
        padding: 10px;
        -moz-border-radius: 20px;
        -webkit-border-radius: 20px;
        -khtml-border-radius: 20px;
        border-radius: 20px;
    }
    </style>
</head>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>How to Search</h2></article>
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
                        While the bioassay data made available by BARD are vast and complex, searching those data is simple and flexible. To begin, just start typing to perform a free-text search. (Note: case is ignored in all searches.) By the time you type the third character, BARD’s auto-suggest feature will begin guiding you to the most relevant data. If one of the suggestions is close to what you’re looking for, you can select it to perform a constrained search for your input exclusively within the data category shown in bold. If you prefer a more open-ended exploration, simply click “SEARCH BARD” to perform an unconstrained search across all data categories. As an alternative to searching by text you can search by one or more ID numbers. You can even search by molecular structure by drawing or pasting a structure.
                    </p>

                    <p>
                        With BARD, one simple search box is your gateway to the high-quality bioassay data you need to accelerate your research.
                    </p>

                    <h3>
                        Free-Text Search
                    </h3>

                    <p>
                    BARD is powered by state-of-the-art database management systems as well as a high-speed, free-text SOLR search engine that scours every entry in the system. What you get are reliably comprehensive results organized by assay definitions, compounds and projects.
                    </p>

                    <p>
                    The goal of BARD is to help you find what you are looking for as quickly as possible, from the simplest search possible. Here are some of the categories of data that are searchable:
                    </p>

                    <section class="shiftedSection">
                        <ul>
                            <li>Target name</li>
                            <li>Description</li>
                            <li>GO biological process term </li>
                            <li>GO molecular function term</li>
                            <li>KEGG disease category</li>
                            <li>KEGG disease name</li>
                            <li>MeSH term</li>
                            <li>Panther class description </li>
                            <li>Title  </li>
                            <li>Protocol </li>
                            <li>Readout technologies (e.g. fluorescence, bioluminescence) </li>
                            <li>Cell lines  </li>
                            <li>Assay components </li>
                        </ul>
                    </section>

                    <h3>
                        Auto-Suggest
                    </h3>

                    <p>
                        Almost as soon as you start typing BARD will start giving you results. As you enter the third character of your search, a drop-down list of possible matches will appear. As you continue to type, that list will shorten to focus on the most relevant results, perhaps even the exact result you are seeking.
                        </p>


                    <IMG style="margin: 25px auto 25px;" SRC="${resource(dir: 'images/bardHomepage', file: 'autosuggest_screen_capture.png')}" ALIGN="top">

                    <p>
                    You’ll notice that in bold type at the end of each auto-suggest match is the data category in which the match occurs. So even if auto-suggest doesn’t display exactly what you are looking for, you’ll have some idea if the term you are searching is bringing you any closer to what you need.

                </p>


                    <h3>
                    Unconstrained and Constrained Searches
                    </h3>

                    <p>
                        If none of the auto-suggest matches or categories shown in bold type are what you are searching for, you can simply click the “SEARCH BARD” button to perform an unconstrained search of all categories. This will likely generate more results, which might be useful if you wish to perform a more open-ended exploration of your search term.
                        </p>

                    <p>
                        If you do select one of the auto-suggest matches, you will initiate a constrained search where the results will be limited to the category shown in bold type. For example, if you were to type the search term “ras” then clicked on the auto-suggest line “ras protein signal transduction as GO Biological Process”, your results will be exclusively of entries where “ras protein signal transduction" is the GO Biological Process targeted by a particular assay definition or project.
                        </p>

                    <p>

                        </p>
                    </p>You’ll notice that when you select one of the auto-suggest matches to perform a constrained search, BARD changes the text in the search box. So for our above example, the search box would read: gobp_term:“ras protein signal transduction". You can use this formula – category:“search term” – to perform constrained searches by typing them directly into the search box. There are many more advanced search formulas, listed below.

                    <h3>
                    Search by One or More ID Numbers
                    </h3>

                <p>
                    Every assay definition, project, and compound has a searchable ID number in BARD. For example, the assay definition named “Additional SAR compounds tested via Multiplex dose response to identify specific small-molecule inhibitors of Ras and Ras-related GTPases specifically Rac1 wildtype” has been assigned the ADID number 4159. So if you wish to revisit this specific entry, instead of typing its long name, you can just type “4159”. Partial ID searches are not possible, thus typing in “41” or “415” would not  find the above assay definition.
                    </p>

                    <p>
                        You can also search multiple ID numbers of the same type of data (assay definition, project and compound) simply by separating them with commas. For a more convenient way to search multiple IDs, click “IDs” near the search box to open a larger search box.  In this box, you can paste a carriage return-separated list of ID numbers copied from a spreadsheet.
                    </p>

                    <aside class="calloutbox">
                        <h3>
                         About ID Numbers
                        </h3>

                        <p>
                            Most of the ID numbers you’ll see are assigned by BARD based upon the entry’s data type, the exception being the CID numbers, which BARD shares with PubChem.
                            </p>


                        <dl>
                            <dt>ADID</dt><dd>Assay Definition ID</dd>
                            <br/>
                            <dt>CID</dt><dd>PubChem Compound ID</dd>
                            <br/>
                            <dt>PID</dt><dd>Project ID</dd>
                            <br/>
                            <dt>EID</dt><dd>Experiment ID (available for ID search in 2014)</dd>
                            <br/>
                            <dt>PLID</dt><dd>Assay Definition ID</dd>
                            <br/>
                            <dt>ADID</dt><dd>Panel ID (available for ID search in 2014)</dd>
                            </dl>
                    </aside>


                    <h3>
                        Search by Molecular Structure
                        </h3>

                    <p>
                        To search by molecular structure, click “Structure” near the search box. This will open a structure editor where you can paste or draw a structure to search. To learn more about this full-featured editor’s many capabilities, visit <a href="http://www.scilligence.com/web/jsdraw.aspx">http://www.scilligence.com/web/jsdraw.aspx</a>.
                        </p>

                <IMG style="margin: 25px auto 25px;" SRC="${resource(dir: 'images/bardHomepage', file: 'mol_struct_edit_screenshot.png')}" ALIGN="top">

                    <p>
                    With BARD, you can perform a variety molecular structure searches: substructure, superstructure, exact structure, and similarity. Similarity searches require a numeric parameter describing the degree of similarity required for matches, and can be set  between 0 and 100%. For more details on BARD’s molecular structure search, please visit <a href="http://tripod.nih.gov/?p=427">http://tripod.nih.gov/?p=427</a>.
                    </p>

                    <h3>
                        Advanced Search Formulas
                        </h3>

                    <p>
                        As you can see below, there are a wide variety of search formulas available. Advanced users can use these to quickly target specific categories within a particular type of data.
                    </p>

                    <table class='table-striped table-bordered'>
                        <thead>
                           <th>Data Type</th>
                           <th>Category</th>
                           <th>Use Quotes for Term?</th>
                           <th>Description</th>
                           <th>Example Formula</th>
                        </thead>
                        <tbody>
                            <tr><td>All</td>	<td>name</td>	<td>Y</td>	<td>Search for an entity with the given string in its name</td>	<td>name:"ras"</td></tr>
                            <tr><td>Assay Definition</td>	<td>accession_gene</td>	<td>Y</td> <td></td><td></td></tr>
                            <tr><td>Assay Definition</td>	<td>accession_process</td>	<td>Y</td> <td></td><td></td></tr>
                            <tr><td>Assay Definition</td>	<td>accession_protein</td>	<td>Y</td><td></td><td></td></tr>
                            <tr><td>Assay Definition</td>	<td>ADID</td>		<td>Search for Assay Definitions by one or more ADIDs</td>	<td>ADID:726,919</td><td></td></tr>
                            <tr><td>Assay Definition</td>	<td>assay_format</td>	<td>Y</td>	<td>Find by assay format</td>	<td>assay_format:"single protein format"</td> </tr>
                            <tr><td>Assay Definition</td>	<td>assay_type</td>	<td>Y</td>	<td>Find by assay type</td>	<td>assay_type:"direct enzyme activity assay"</td> </tr>
                            <tr><td>Assay Definition</td>	<td>bardAssayId</td>	<td>Y</td>	<td>Find by the ID used by the REST API	<td>bardAssayId:"2775"</td> </tr>
                            <tr><td>Assay Definition</td>	<td>comment</td>	<td>Y</td>	<td>Search for matches using the comment field</td><td></td></tr>
                            <tr><td>Assay Definition</td>	<td>detection_method_type</td>	<td>Y</td>	<td>Find by detection method type</td><td></td></tr>
                            <tr><td>Assay Definition</td>	<td>protocol</td>	<td>Y</td>	<td>Search for matches using the protocol field</td><td></td> </tr>
                            <tr><td>Assay Definition</td>	<td>target_name_gene</td>	<td>Y</td><td></td><td></td> </tr>
                            <tr><td>Assay Definition</td>	<td>target_name_process</td>	<td>Y</td><td></td><td></td></tr>
                            <tr><td>Assay Definition</td>	<td>target_name_protein</td>	<td>Y</td><td></td><td></td></tr>
                            <tr><td>Assay Definition</td>	<td>title</td>	<td>Y</td>	<td>Search for matches using the title field</td><td></td> </tr>
                            <tr><td>Assay Definition, Compound</td>	<td>target_name</td>	<td>Y</td><td></td><td></td> </tr>
                            <tr><td>Assay Definition, Compound</td>	<td>timestamp</td>	<td>Y</td> <td></td><td></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>accession</td>	<td>Y</td> <td></td><td></td>  </tr>
                            <tr><td>Assay Definition, Project</td>	<td>ak_dict_label</td>	<td>Y</td> <td></td><td></td> </tr>
                            <tr><td>Assay Definition, Project</td>	<td>biology</td>	<td>Y</td><td></td><td></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>biology_dict_label</td>	<td>Y</td> <td></td><td></td> </tr>
                            <tr><td>Assay Definition, Project</td>	<td>description</td>	<td>Y</td> <td></td><td></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>gobp_id</td>	<td>Y</td> <td></td><td></td> </tr>
                            <tr><td>Assay Definition, Project</td>	<td>gobp_term</td>	<td>Y</td> <td></td><td></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>gocc_id</td>	<td>Y</td> <td></td><td></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>gocc_term</td>	<td>Y</td> <td></td><td></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>gomf_id</td>	<td>Y</td> <td></td><td></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>gomf_term</td>	<td>Y</td> <td></td><td></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>kegg_disease_cat</td>	<td>Y</td><td></td><td></td> </tr>
                            <tr><td>Assay Definition, Project</td>	<td>kegg_disease_names</td>	<td>Y</td><td></td><td></td> </tr>
                            <tr><td>Compound</td>	<td>anno_key</td>	<td>Y</td><td></td><td></td>   </tr>
                            <tr><td>Compound</td>	<td>anno_val</td>	<td>Y</td><td></td><td></td>  </tr>
                            <tr><td>Compound</td>	<td>cid</td>	<td>Y</td><td></td><td></td>     </tr>
                            <tr><td>Compound</td>	<td>CID</td>		<td>Search for Compounds by one or more PubChem CIDs</td> <td></td><td></td>   </tr>
                            <tr><td>Compound</td>	<td>COLLECTION</td>	<td>Y</td>  <td></td><td></td>    </tr>
                            <tr><td>Compound</td>	<td>compound_class</td>	<td>Y</td>  <td></td><td></td> </tr>
                            <tr><td>Compound</td>	<td>exact</td>		<td>Search for Compounds with the exact given structure specified using SMILES</td>  <td></td><td></td>   </tr>
                            <tr><td>Compound</td>	<td>iupacName</td>	<td>Y</td> <td></td><td></td> </tr>
                            <tr><td>Compound</td>	<td>mwt</td>   <td></td><td></td><td></td>            </tr>
                            <tr><td>Compound</td>	<td>probeId</td>	<td>Y</td> <td></td><td></td>      </tr>
                            <tr><td>Compound</td>	<td>similarity</td>		<td>Search for Compound similar to the given structure specified by SMILES defaulting to a threshold of 90% similarity</td><td></td><td></td>  </tr>
                            <tr><td>Compound</td>	<td>smiles</td>	<td>string</td>  <td></td><td></td>           </tr>
                            <tr><td>Compound</td>	<td>substructure</td>		<td>Search for Compounds that contain the given substructure specified by SMILES</td>  <td></td><td></td>  </tr>
                            <tr><td>Compound</td>	<td>superstructure</td>		<td>Search for Compounds that the given structure specified by SMILES contains	</td> <td></td><td></td> </tr>
                            <tr><td>Compound</td>	<td>target_accession</td>	<td>Y</td> <td></td><td></td>         </tr>
                            <tr><td>Compound</td>	<td>target_description</td>	<td>Y</td> <td></td><td></td>     </tr>
                            <tr><td>Compound</td>	<td>target_gene_id</td>	<td>Y	</td>  <td></td><td></td>     </tr>
                            <tr><td>Compound</td>	<td>threshold</td>		<td>Sets the similarity threshold for a Compound similarity search.  Must be combined with a similarity search.	</td>   <td></td><td></td>  </tr>
                            <tr><td>Compound</td>	<td>tpsa</td>	<td>tfloat</td><td></td><td></td>  </tr>
                            <tr><td>Compound</td>	<td>xlogp</td>	<td>tfloat</td> <td></td><td></td> </tr>
                            <tr><td>Project</td>	<td>num_expt</td>	<td>tint</td> <td></td><td></td>   </tr>
                            <tr><td>Project</td>	<td>PID</td>		<td>Search for Projects by one or more PIDs</td> <td></td><td></td>   </tr>
                            <tr><td>Project</td>	<td>projectId</td><td></td><td></td><td></td>        </tr>
                        </tbody>
                    </table>


                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>



</body>
</html>