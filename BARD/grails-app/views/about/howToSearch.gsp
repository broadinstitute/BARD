<!DOCTYPE html>
<html>
<g:render template="howToHeader" model="[title:'BARD How to search']"/>

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
                    </p>You’ll notice that when you select one of the auto-suggest matches to perform a constrained search, BARD changes the text in the search box. So for our above example, the search box would read: gobp_term:“ras protein signal transduction". You can use this formula – category:“search term” – to perform constrained searches by typing them directly into the search box. There are many more <a href="#advanced_search_formulas">advanced search formulas</a>.

                    <h3>
                    Search by One or More ID Numbers
                    </h3>

                <p>
                    Every assay definition, project, and compound has a searchable ID number in BARD. For example, the assay definition named “Additional SAR compounds tested via Multiplex dose response to identify specific small-molecule inhibitors of Ras and Ras-related GTPases specifically Rac1 wildtype” has been assigned the ADID number 4159. So if you wish to revisit this specific entry, instead of typing its long name, you can just type “4159”. Partial ID searches are not possible, thus typing in “41” or “415” would not  find the above assay definition.
                    </p>

                    <p>
                        You can also search multiple ID numbers of the same type of data (assay definition, project or compound) simply by separating them with commas. For a more convenient way to search multiple IDs, click “IDs” near the search box to open a larger search box.  In this box, you can paste a carriage return-separated list of ID numbers copied from a spreadsheet.
                    </p>

                    <aside class="calloutbox">
                        <h3>
                         About ID Numbers
                        </h3>

                        <p>
                            Most of the ID numbers you’ll see are assigned by BARD based upon the entry’s data type, the exception being the CID numbers, which BARD shares with PubChem.
                            </p>


                        <dl class='unindentedDefinition'>
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

                    <h3 id="advanced_search_formulas">
                        Advanced Search Formulas
                    </h3>

                    <p>
                        As you can see below, there are a wide variety of search formulas available. Advanced users can use these to quickly target specific categories within a particular type of data.
                    </p>

                    <table class='table table-striped table-bordered'>
                        <thead>
                           <th>Data Type</th>
                           <th>Category</th>
                           <th>Use Quotes for Term?</th>
                           <th>Description</th>
                           <th>Example Formula</th>
                        </thead>
                        <tbody>
                            <tr><td>All</td>	<td>name</td>	<td>Y</td>	<td>Search for assay definitions, compounds and projects with the given string in their name</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"name:\"ras\""]'>name:"ras"</g:link></td></tr>
                            <tr><td>Assay Definition</td>	<td>accession_gene</td>	<td>Y</td> <td>Search for assay definitions with gene target ID numbers matching the given string</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"accession_gene:\"12381848\""]'>accession_gene:"12381848"</g:link></td></tr>
                            <tr><td>Assay Definition</td>	<td>accession_process</td>	<td>Y</td> <td>Search for assay definitions with process target ID numbers matching the given string</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"accession_process:\"GO:0010468\""]'>accession_process:"GO:0010468"</g:link></td></tr>
                            <tr><td>Assay Definition</td>	<td>accession_protein</td>	<td>Y</td><td>Search for assay definitions with protein target ID numbers matching the given string</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"accession_protein:\"P17861\""]'>accession_protein:"P17861"</g:link></td></tr>
                            <tr><td>Assay Definition</td>	<td>ADID</td>	<td>N</td>	<td>Search for assay definitions by one or more ADIDs</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"ADID:1121,1128"]'>ADID:1121,1128</g:link></td></tr>
                            <tr><td>Assay Definition</td>	<td>assay_format</td>	<td>Y</td>	<td>Search for assay definitions with the given assay format</td>
                                %{--TODO This search does not work. investigate with NCGC--}%
                                <td>%{--<g:link controller="bardWebInterface" action="search" params='[searchString:"assay_format:\"biochemical format\""]'>assay_format:"biochemical format"</g:link>--}%</td> </tr>
                            <tr><td>Assay Definition</td>	<td>assay_type</td>	<td>Y</td>	<td>Search for assay definitions with the given assay type</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"assay_type:\"direct enzyme activity assay\""]'>assay_type:"direct enzyme activity assay"</g:link></td> </tr>
                            <tr><td>Assay Definition</td>	<td>comment</td>	<td>Y</td>	<td>Search for assay definitions containing the given string in their comment field</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"comment:\"genedata\""]'>comment:"genedata"</g:link></td></tr>
                            <tr><td>Assay Definition</td>	<td>detection_method_type</td>	<td>Y</td>	<td>Search for assay definitions with the given detection method type</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"detection_method_type:\"absorbance\""]'>detection_method_type:"absorbance"</g:link></td></tr>
                            <tr><td>Assay Definition</td>	<td>protocol</td>	<td>Y</td>	<td>Search for assay definitions containing the given string in their protocol</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"prototocol:\"pyoverdine\""]'>prototocol:"pyoverdine"</g:link></td> </tr>
                            <tr><td>Assay Definition</td>	<td>target_name_gene</td>	<td>Y</td><td>Search for assay definitions with gene target names matching the given string</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"target_gene_process:\"http://www.ncbi.nlm.nih.gov/gquery/?term=12381848\""]'>target_gene_process:"http://www.ncbi.nlm.nih.gov/gquery/?term=12381848"</g:link></td> </tr>
                            <tr><td>Assay Definition</td>	<td>target_name_process</td>	<td>Y</td><td>Search for assay definitions with process target names matching the given string</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"target_name_process:\"regulation of gene expression\""]'>target_name_process:"regulation of gene expression"</g:link></td></tr>
                            <tr><td>Assay Definition</td>	<td>target_name_protein</td>	<td>Y</td><td>Search for assay definitions with protein target names matching the given string</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"target_name_protein:\"X-box-binding protein 1\""]'>target_name_protein:"X-box-binding protein 1"</g:link></td></tr>
                            <tr><td>Assay Definition, Compound</td>	<td>target_name</td>	<td>Y</td><td>Search for assay definitions with target names matching the given string</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"target_name:\"(GO:0008219) cell death\""]'>target_name:"(GO:0008219) cell death"</g:link></td> </tr>
                            <tr><td>Assay Definition, Project</td>	<td>accession</td>	<td>Y</td> <td>Search for assay definitions with gene target accession numbers that match the given string</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"accession:\"12381848\""]'>accession:"12381848"</g:link></td>  </tr>
                            <tr><td>Assay Definition, Project</td>	<td>biology</td>	<td>Y</td><td>Search for assay definitions with a target biology matching the given string</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"biology:\"gene\""]'>biology:"gene"</g:link></td></tr>
                            %{--Question for NCGC: how does the previous call differ from the subsequent one?--}%
                            <tr><td>Assay Definition, Project</td>	<td>biology_dict_label</td>	<td>Y</td> <td>Search for assay definitions with a target biology matching the given string</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"biology_dict_label:\"process\""]'>biology_dict_label:"process"</g:link></td> </tr>
                            <tr><td>Assay Definition, Project</td>	<td>description</td>	<td>Y</td> <td>Search for assay definitions and projects containing the given string in their description</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"description:\"affiliation\""]'>description:"affiliation"</g:link></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>gobp_id</td>	<td>Y</td> <td>Search for assay definitions and projects annotated as being related to the given GO Biological Process ID</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"gobp_id:\"GO:0010468\""]'>gobp_id:"GO:0010468"</g:link></td> </tr>
                            <tr><td>Assay Definition, Project</td>	<td>gobp_term</td>	<td>Y</td> <td>Search using a GO Biological Process term</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"gobp_term:\"accession\""]'>gobp_term:"accession"</g:link></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>gocc_id</td>	<td>Y</td> <td>Search using a GO Cellular Component ID</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"gocc_id:\"movement\""]'>gocc_id:"movement"</g:link></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>gocc_term</td>	<td>Y</td> <td>Search using a GO Cellular Component Term</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"gocc_term:\"nucleus\""]'>gocc_term:"nucleus"</g:link></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>gomf_id</td>	<td>Y</td> <td>Search using a GO Molecular Function ID</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"gomf_id:\"endoplasmic reticulum\""]'>gomf_id:"endoplasmic reticulum"</g:link></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>gomf_term</td>	<td>Y</td> <td>Search using a GO Molecular Function Term</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"gomf_term:\"binding\""]'>gomf_term:"binding"</g:link></td></tr>
                            <tr><td>Assay Definition, Project</td>	<td>kegg_disease_cat</td>	<td>Y</td><td>Search by KEGG disease category</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"kegg_disease_cat:\"infectious disease\""]'>kegg_disease_cat:"infectious disease"</g:link></td> </tr>
                            <tr><td>Assay Definition, Project</td>	<td>kegg_disease_names</td>	<td>Y</td><td>Search by KEGG disease name</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"kegg_disease_names:\"crohns disease\""]'>kegg_disease_names:"crohns disease"</g:link></td> </tr>
                            <tr><td>Compound</td>	<td>CID</td>	<td>N</td>	<td>Search for Compounds by one or more PubChem CIDs</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"cid:11972586,16759566"]'>cid:11972586,16759566</g:link></td>   </tr>
                            <tr><td>Compound</td>	<td>collection</td>	<td>Y</td>  <td>%{--TODO: Add description--}%</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"collection:\"mlpcn\""]'>collection:"mlpcn"</g:link></td></tr>
                            <tr><td>Compound</td>	<td>compound_class</td>	<td>Y</td>  <td>Search for compounds of a particular class.  Options are: drug, probe</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"compound_class:\"drug\""]'>compound_class:"drug"</g:link></td> </tr>
                            <tr><td>Compound</td>	<td>exact</td>		<td>N</td><td>Search for compounds with the exact given structure specified using SMILES</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"exact:OC(C1CCNCC1)(C2=CC=CC=C2)C3=CC=CC=C3"]'>exact:OC(C1CCNCC1)(C2=CC=CC=C2)C3=CC=CC=C3</g:link></td>   </tr>
                            <tr><td>Compound</td>	<td>iupacName</td>	<td>Y</td> <td>Search for compounds having the given IUPAC name</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"iupacName:\"[4-[bis(4-chlorophenyl)methyl]piperazin-1-yl]-(5-methyl-4-nitro-1,2-oxazol-3-yl)methanone\""]'>iupacName:"[4-[bis(4-chlorophenyl)methyl]piperazin-1-yl]-(5-methyl-4-nitro-1,2-oxazol-3-yl)methanone"</g:link></td> </tr>
                            <tr><td>Compound</td>	<td>similarity</td>		<td>N</td><td>Search for Compound similar to the given structure specified by SMILES defaulting to a threshold of 90% similarity</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"similarity:N.N.Cl[Pt]Cl"]'>similarity:N.N.Cl[Pt]Cl</g:link></td></tr>
                            <tr><td>Compound</td>	<td>substructure</td>	<td>N</td>	<td>Search for Compounds that contain the given substructure specified by SMILES</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"substructure:Cl.OC(CCCN1CCCCC1)(C2=CC=CC=C2)C3=CC=CC=C3"]'>substructure:Cl.OC(CCCN1CCCCC1)(C2=CC=CC=C2)C3=CC=CC=C3</g:link></td></tr>
                            <tr><td>Compound</td>	<td>superstructure</td>	<td>N</td>	<td>Search for Compounds that the given structure specified by SMILES contains	</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"superstructure:Cl.OC(CCCN1CCCCC1)"]'>superstructure:Cl.OC(CCCN1CCCCC1)</g:link></td></tr>
                            <tr><td>Compound</td>	<td>target_accession</td>	<td>Y</td> <td>get search using target a session numbers</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"target_accession:\"12381848\""]'>target_accession:"12381848"</g:link></td></tr>
                            <tr><td>Compound</td>	<td>target_description</td>	<td>Y</td> <td>Search using description of target</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"target_description:\"Acetylcholinesterase\""]'>target_description:"Acetylcholinesterase"</g:link></td></tr>
                            <tr><td>Compound</td>	<td>target_gene_id</td>	<td>Y</td>  <td>Search using gene target ID numbers</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"target_gene_id:\"12381848\""]'>target_gene_id:"12381848"</g:link></td></tr>
                            <tr><td>Compound</td>	<td>threshold</td>	<td>N</td>	<td>Sets the percent similarity threshold for a Compound similarity search.  Must be combined with a similarity search.	</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"similarity: NC(=O)NS(=O)(=O)C1=CC=C(N)C=C1  threshold:90"]'>similarity: NC(=O)NS(=O)(=O)C1=CC=C(N)C=C1  threshold:90</g:link></td></tr>
                            <tr><td>Compound, Project</td>	<td>probeId</td>	<td>Y</td> <td>Search for compounds with the given ML probe id</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"probeid:\"ML329\""]'>probeid:"ML329"</g:link></td>      </tr>
                            <tr><td>Project</td>	<td>PID</td>	<td>N</td>	<td>Search for Projects by one or more PIDs</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"pid:1244"]'>pid:1244</g:link></td>   </tr>
                            <tr><td>Project</td>	<td>projectId</td><td>Y</td><td>Find a project by the project identifier assigned by the warehouse</td>
                                <td><g:link controller="bardWebInterface" action="search" params='[searchString:"projectid:\"17\""]'>projectid:"17"</g:link></td></tr>
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