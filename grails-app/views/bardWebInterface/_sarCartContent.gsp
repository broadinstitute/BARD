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
                        ${elem.toString()}
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
                        ${elem.toString()}
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
                            ${elem.toString()}
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
                    <li><a href="#">Molecular Spreadsheet</a></li>
                    <li><a href="#">Advanced Analysis Client</a></li>
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
