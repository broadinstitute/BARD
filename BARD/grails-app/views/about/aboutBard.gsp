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
<g:render template="howToHeader" model="[title:'About BARD']"/>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>About BARD</h2></article>
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
                    <h2>
                        Discover a New Tool for Discovery.
                    </h2>

                    <p>
                        Introducing BARD, the powerful new bioassay database from the NIH Molecular Libraries Program.
                        BARD empowers researchers with the well-organized and fully annotated data, innovative tools and
                        (in the coming months) secure privacy required to accelerate drug discovery. Now with unprecedented
                        efficiency, scientists can develop and test hypotheses on the influence of different chemical probes
                        on biological functions.
                    </p>

                    <p>
                        Starting with public data sets, BARD applies a standardized language and consistent assay/project/experiment
                        organization, as well as detailed annotations. This intensive hands-on work is performed by a highly
                        skilled team, who whenever possible involve the researchers who generated the data.
                    </p>

                    <p>
                        Accessed through this website or deployed as a complete closed system behind an organization’s
                        firewall (available soon), the goal is to build BARD into a single resource that biologists and
                        chemists can trust for reliably comprehensive query results.
                    </p>

                    <p>
                        BARD’s constantly growing database includes 35M+ compounds and thousands of assays and
                        experiments<a href="#footnote1">*</a>. Researchers can perform multiple types of sophisticated
                        queries, filter and save the results, analyze and visualize the data, then easily export findings
                        to common office software for the generation of reports. BARD also enables the secure and structured
                        capture of your organization’s valuable proprietary scientific data, and simplifies the sharing
                        of that data between collaborators or with the entire research community.
                    </p>

                    <div id="footnote1">
                    <p>
                        * To view the latest numbers of projects, experiments and assay definitions in bard,
                        <a href="${createLink(controller: "bardWebInterface", action: "index", fragment: "bardIsGrowing")}">
                        check out the "BARD is growing" section of the home page</a>
                    </p>
                    </div>
                </article>

                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>


</body>
</html>
