<table class="QcartAppearance" id="detailView">
%{-- hold everything that Ajax will refill--}%
    <tbody id="sarCartRefill">
    <tr>
        <td>

         <h5><span class="qcartTitle">Query Cart -</span> <span class="qcartResults">Selected Results</span></h5>
         <h6>${totalItemCount} results selected</h6>
         <h5>COMPOUNDS</h5>

        <table class="QcartSubsection">
        %{-- hold everything related to assays --}%
            <g:each in="${compounds}" var="elem" status="item">
                <tr>
                    <td>
                        <a href="${createLink(controller:'bardWebInterface' , action: 'showCompound', id:elem.compoundId)}">${elem.toString()}</a>
                    </td>
                    <td>
                        <a href="/bardwebquery/queryCart/remove/${elem.id}" class="removeXMark removeItemFromCart">X</a>
                    </td>
                </tr>
            </g:each>

        </table>

        </td>
    </tr>
    <tr>
        <td>

            <h5>ASSAY DEFINITIONS</h5>
            <table>
            <g:each in="${assayDefinitions}" var="elem" status="item">
                <tr>
                    <td>
                        <a href="${createLink(controller:'bardWebInterface' , action: 'showAssay', id:elem.assayId)}">${elem.toString()}</a>
                    </td>
                    <td>
                        <a href="/bardwebquery/queryCart/remove/${elem.id}" class="removeXMark removeItemFromCart">X</a>
                    </td>
                </tr>
             </g:each>
             </table>

       </td>
    </tr>
    <tr>
        <td>


            <h5>PROJECTS</h5>
            <table>
                <g:each in="${projects}" var="elem" status="item">
                    <tr>
                        <td>
                            <a href="${createLink(controller:'bardWebInterface' , action: 'showProject', id:elem.projectId)}">${elem.toString()}</a>
                        </td>
                        <td>
                            <a href="/bardwebquery/queryCart/remove/${elem.id}" class="removeXMark removeItemFromCart">X</a>
                        </td>
                    </tr>
                </g:each>
            </table>

        </td>
    </tr>
    <tr>
        <td>


            <br/>
            <div class="leftofline">
            <button class="visualizebiggerbutton">
            <div class="btn-group" style="vertical-align: bottom;">
                <a class="btn-small dropdown-toggle" data-toggle="dropdown" href="#">
                    <i class="icon-eye-open"></i> Visualize
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                    <li><g:link controller="molSpreadSheet">Molecular Spreadsheet</g:link></li>
                    <li><a href="http://bard.nih.gov/bard/compounds/44552613?view=Record" target="_blank">Desktop Client</a></li>
                </ul>
            </div>
            </button>
            </div>

            <div class="rightofline">

            <button  class="visualizebiggerbutton"><a href="#" class="removeAllFromCart">Clear all</a>
                </button>

             </div>

        </td>
    </tr>
    </tbody>
</table>
