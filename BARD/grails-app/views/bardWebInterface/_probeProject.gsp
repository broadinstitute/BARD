<div class="item ${activeClass}">
    <div class="row-fluid">
        <article class="span12">
            <ul class="thumbnails">
                <g:each var="probe" in="${currentCompoundAdapters}" status="i">
                    <li class="span3">
                        <div class="thumbnail">
                            <g:compoundOptions cid="${probe.id}" sid="${probe.id}" smiles="${probe?.smiles}"
                                               name="${bardqueryapi.JavaScriptUtility.cleanup(probe.name)}"
                                               imageHeight="100" imageWidth="300"/>
                            <div class="caption">
                                <h3>Probe ML#: ${probe.probeId}</h3>
                                <ul>
                                    <li><a href="${probe.url}">Download probe report from Molecular Library BookShelf</a>
                                    </li>
                                    <li><g:link controller="bardWebInterface" action="showCompound"
                                                params="[cid: probe.id]">Show Compound Details in BARD</g:link></li>
                                    <li><a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=${probe.id}"
                                           target="_blank">View CID ${probe.id} in PubChem</a></li>
                                    <li><g:link controller="molSpreadSheet" action="showExperimentDetails"
                                                params="[cid: probe.id,transpose: true]"
                                                data-placement="top"
                                                class="projectTooltip"
                                                rel="tooltip"
                                                data-original-title="">Show Experimental Details</g:link></li>
                                </ul>
                            </div>
                        </div>
                    </li>
                </g:each>
                <li class="span3">
                    <br/>
                    <br/>
                    <g:if test="${probeCompoundsSearchString}">
                        <g:form controller="BardWebInterface" action="search" method="POST">
                            <g:hiddenField name="searchString" value="${probeCompoundsSearchString}"/>
                            <g:submitButton name="View All Probe Compounds" class='btn'
                                            value="VIEW ALL PROBE COMPOUNDS"/>
                        </g:form>
                    </g:if>
                    <g:if test="${probeProjectSearchString}">
                        <g:form controller="BardWebInterface" action="search" method="POST">
                            <g:hiddenField name="searchString" value="${probeProjectSearchString}"/>
                            <g:submitButton name="View All Probe Projects" class='btn'
                                            value="VIEW ALL PROBE PROJECTS"/>
                        </g:form>
                    </g:if>
                </li>
            </ul>
        </article>
    </div>
</div>



