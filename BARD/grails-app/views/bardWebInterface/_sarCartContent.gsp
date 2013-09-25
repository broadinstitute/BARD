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
                    <td class="QcartAppearance">
                        <a href="${createLink(controller:'bardWebInterface' , action: 'showCompound', id:elem.externalId)}" style="color: #000000;">${elem.toString()}</a>
                    </td>
                    <td>
                        <button title="Remove ${elem.toString()}" class="removeItemFromCart btn btn-link"
                                data-cart-id="${elem.externalId}" data-cart-type="${elem.queryItemType}">
                            <i class="icon-remove-sign"></i>
                        </button>
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
                    <td class="QcartAppearance">
                        <a href="${createLink(controller:'assayDefinition' , action: 'show', id:elem.internalId)}" style="color: #000000;">${elem.toString()}</a>
                    </td>
                    <td>
                        <button title="Remove ${elem.toString()}" class="removeItemFromCart btn btn-link"
                                data-cart-id="${elem.internalId}" data-cart-type="${elem.queryItemType}">
                            <i class="icon-remove-sign"></i>
                        </button>
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
                        <td class="QcartAppearance">
                            <a href="${createLink(controller:'project' , action: 'show', id:elem.internalId)}" style="color: #000000;">${elem.toString()}</a>
                        </td>
                        <td>
                            <button title="Remove ${elem.toString()}" class="removeItemFromCart btn btn-link"
                                    data-cart-id="${elem.internalId}" data-cart-type="${elem.queryItemType}">
                                <i class="icon-remove-sign"></i>
                            </button>
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
                <div class="btn-group" style="vertical-align: bottom;">
                    <a class="btn dropdown-toggle" data-toggle="dropdown"  role="button" data-target="#"
                       style="background: #0093d0;  color: #ffffff">
                        <i class="icon-eye-open"></i> VISUALIZE
                        <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu pull-left"  role="menu">
                        <li><g:link controller="molSpreadSheet" style="color: #000000;">Molecular Spreadsheet</g:link></li>
                       <li><a  style="color: #000000;" href="${createLink(controller: 'queryCart', action: 'toDesktopClient')}" target="_blank">Desktop Client</a></li>

                        %{--<li><a href="http://bard.nih.gov/bard/compounds/44552613?view=Record" target="_blank">Desktop Client</a></li>--}%
                    </ul>
                </div>
            </div>

            <div class="rightofline">

                <a class="removeAllFromCart btn" role="button" href="#"
                   style="background: #0093d0;  color: #ffffff">
                    CLEAR ALL
                </a>

            </div>

        </td>
    </tr>
    </tbody>
</table>
