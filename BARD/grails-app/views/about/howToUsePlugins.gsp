%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<!DOCTYPE html>
<html>
<g:render template="howToHeader" model="[title:'BARD How to create and use plug-ins']"/>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>How to Create and Use Plug-ins</h2></article>
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
        Flexibility and responsiveness are core tenets of BARD. One way the system delivers this is through an open architecture where users can create shareable plug-ins that meet their needs. That means that computational biologists, informatics experts, developers of advanced computational methods and other adept scientists can contribute code to BARD that benefits themselves and the entire research community.
    </p>


    <h3>
        Existing and Future Plugins
    </h3>

    <p>
        Already there are three plug-ins live on BARD providing enhanced insights into research data. "Badapple" performs scaffold-based promiscuity analysis of compounds, while “SmartCyp” and “WhichCyp” perform metabolism and binding predictions of cytochrome P450. These are only the beginning. Several more plug-ins are in development and planning stages:
    </p>

    <table class='table-striped table-bordered'>
        <thead>
        <th>Name</th>	<th>Lead Author[s]</th>	<th>Institution</th>	<th>Brief Description</th>	<th>Status</th>
        </thead>
        <tbody>
        <tr>
            <td><a href="http://bard.nih.gov/api/latest/plugins/badapple/_manifest">Badapple</a></td>	<td>Jeremy Yang</td><td>UNM</td><td>Evidence-based promiscuity scores</td><td>released Oct 2012</td>
        </tr>
        <tr>
            <td>HScaf</td>	<td>Jeremy Yang</td><td>UNM</td><td>Hierarchical scaffold analysis</td><td>In development</td>
        </tr>
        <tr>
            <td><a href="http://bard.nih.gov/api/latest/plugins/smartcyp/_manifest">SmartCyp</a></td>	<td>Rajarshi Guha</td><td>NCATS</td><td>Prediction of which sites in a molecule are most liable to metabolism by Cytochrome P450</td><td>released March 2013</td>
        </tr>
        <tr>
            <td><a href="http://bard.nih.gov/api/latest/plugins/whichcyp/_manifest">WhichCyp</a></td>	<td>Rajarshi Guha</td><td>NCATS</td><td>Prediction of which Cytochrome P450 isoform(s) is(are) likely to bind a drug-like molecule</td><td>released June 2013</td>
        </tr>
        <tr>
            <td><a href="http://bard.nih.gov/api/latest/plugins/protclass/_manifest">ProtClass</a></td>	<td>Rajarshi Guha</td><td>NCATS</td><td>Protein classifications (Panther) based on Uniprot IDs</td><td>released October 2013</td>
        </tr>

        <tr>
            <td><a href="http://bard.nih.gov/api/latest/plugins/knnbioactivity/_manifest">kNNBioactivity</a></td>	<td>Oleg Ursu</td><td>UNM</td><td>kNN machine learning, bioactivity profile prediction</td><td>released October 2013</td>
        </tr>

        <tr>
            <td><a href="http://bard.nih.gov/api/latest/plugins/qed/_manifest">QED</a></td>	<td>Oleg Ursu</td><td>UNM</td><td>Quantitative Estimate of Drug-likeness (Molecular Property & Filtering Suite)</td><td>released January 2014</td>
        </tr>
        <tr>
            <td>Ro5</td>	<td>Jeremy Yang</td><td>UNM</td><td>Lipinski Rule of 5 (Molecular Property & Filtering Suite)</td><td>In development</td>
        </tr>
        <tr>
            <td>SMARTS</td>	<td>Jeremy Yang</td><td>UNM</td><td>SMARTS structural alerts (Molecular Property & Filtering Suite)</td><td>In development</td>
        </tr>
        <tr>
            <td>Metaprint2D</td>	<td>Lars Carlsson</td><td>AstraZeneca</td><td>Metabolic site prediction based on historic metabolic data</td><td>In development</td>
        </tr>
        <tr>
            <td>ALOGPS</td>	<td>Iurii Sushko, Igor Tetko</td><td>HZM / VCCLAB / eADMET        </td><td>LogP & solubility prediction</td><td>Planned</td>
        </tr>
        <tr>
            <td>TBE (QSAR)</td>	<td>Diane Pozefsky, Alex Tropsha</td><td>UNC</td><td>QSAR modeling</td><td>Planned</td>
        </tr>
        <tr>
            <td>TBE (assay-based similarity)</td>	<td>Vlado Dancik</td><td>Broad</td><td>Compound similarity based on bioactivity</td><td>Planned</td>
        </tr>

        <tr>
            <td>TBE (various predictive models)</td>	<td>Jens Meiler, Edward Lowe</td><td>Vanderbilt</td><td>Bioactivity and potency prediction using BCL-based algorithms</td><td>Planned</td>
        </tr>
        <tr>
            <td>TBE (various predictive models)</td>	<td>Alexey Zakharov, Marc Nicklaus</td><td>NCI</td><td>Bioactivity, toxicity, property prediction</td><td>Planned</td>
        </tr>

        </tbody>
    </table>


    <h3>
        Creating Your Own Plug-ins
        </h3>

    <p>
        Think of a computational method that you’ve developed and published. Maybe you’ve received emails from other computational experts interested in using it, but adoption is limited to those who have the expertise.  Now imagine if the general research community could easily leverage your work on a rich data set without needing expert computational knowledge. With BARD, chances are you could develop a plug-in that accomplishes exactly that. So long as your code implements a small number of methods based on a defined set of signatures then it can be integrated into BARD:
    </p>
    <img  style="width: 100%"
        src="${resource(dir: 'images/bardHomepage', file: 'plugin_architecture.png')}"
        alt="How plug-ins fit into the Bard architecture"
        title="How plug-ins fit into the Bard architecture" />

    <p>
        Note that while a plug-in may be developed on any computer, currently all plug-ins run on BARD’s backend server, operating in a servlet container. Our engineering support team can help you to install a copy of the BARD data warehouse and REST API on your machine for plug-in development purposes. That way your plug-in may include direct references into the BARD data warehouse, and can thus potentially offer performance roughly equivalent to our internally developed software even if extensive database queries are required. We also can support asynchronous links, allowing plug-in developers to perform calculations off-line but then return those results to their plug-in.
        </p>

    <p>
        For more technical details on how to develop plug-ins, please see <a href="https://github.com/ncats/bard/wiki/Plugins">https://github.com/ncats/bard/wiki/Plugins</a>. To initiate a request for a copy of the BARD date warehouse, please email
    <g:render template="../layouts/templates/bardusers"/>.
    </p>


</article>
<aside class="span2"></aside>
</div>
</div>
</div>
</article>



</body>
</html>
