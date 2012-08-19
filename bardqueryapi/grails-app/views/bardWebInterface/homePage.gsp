<%@ page import="bardqueryapi.CartAssay; bardqueryapi.SearchType; bard.db.registration.*; com.metasieve.shoppingcart.*; com.metasieve.shoppingcart.Shoppable.*;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <r:require modules="core"/>
    <r:require modules="bootstrap"/>


    <r:script>
        $(document).ready(function () {
            var autoOpts = {
                source:"/bardqueryapi/bardWebInterface/autoCompleteAssayNames",
                minLength:2
            };
            $("#searchString").autocomplete(autoOpts);

            <%-- An even handler to open (bring-to-front) the MarvinSketch modal window. Opens MarvingSketch in a new gsp: marvinSketch.gsp --%>
            $('#structureModal').click(
                function() {
                    url = '${request.contextPath}/chemAxon/marvinSketch';
                    $("#modalDiv").dialog("open");
                    $("#modalIFrame").attr('src',url);
                    return false;
            });

            <%-- Define the MarvinSketch modal window (JQuery UI Modal) --%>
            $("#modalDiv").dialog({
                modal: true,
                autoOpen: false,
                height: '660',
                width: '600',
                draggable: true,
                resizeable: true,
                title: "",
                beforeClose: function() {
                    <%-- Processing before closing (send-to-back) of the modal window could be done here
                    FALSE will prevent closing of the modal window. --%>
		            return true;
                }
            });
        });
    </r:script>

    <r:script>

        $(document).ready(function(){
            $(".trigger").click(function(){
                $(".panel").toggle("fast");
                $(this).toggleClass("active");
                return false;
            });
        });
     </r:script>

        <title>BARD Home</title>
</head>

<body>
<div>
    <div>
        <h1 style="text-align: center">BARD</h1>

        <h2 style="text-align: center">BioAssay Research Database</h2> <br/><br/>
        <br/>
        <h3 style="text-align: center"><a href="${grailsApplication.config.bard.cap.home}" style="color:blue;text-decoration: underline;">CAP - Catalog of Assay Protocols Home</a></h3>
        <br/>
        <g:if test="${flash.message}">
            <div class="error">${flash.message}</div>
        </g:if>
        <br/><br/>




        <g:form name="aidForm" controller="bardWebInterface" action="search">

            <div class="content ">
                <table class="skinnyTable">
                    <tr>
                        <td>
                            <g:textField id="searchString" name="searchString" value="${params?.searchString}"
                                         size="50"/>
                        </td>
                        <td>
                            <g:submitButton name="search"
                                            value="Search"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: right;">
                            <span id="structureModal" style="color:blue;text-decoration: underline;">
                                Create a structure for a search
                            </span>
                        </td>
                        <td/>
                    </tr>
                </table>
            </div>
        </g:form>
        <br/>
        <br/>
    </div>
</div><!-- End body div -->
<div id="resultTab">
    <ul id="resultTabUL" class="nav nav-tabs">
        <li class="active"><a href="#assays" data-toggle="tab">Assays (${assays?.size()})</a></li>
        <li><a href="#compounds" data-toggle="tab">Compounds (${compounds?.size()})</a></li>
        <li><a href="#experiments" data-toggle="tab">Experiments (${experiments?.size()})</a></li>
        <li><a href="#projects" data-toggle="tab">Projects (${projects?.size()})</a></li>
    </ul>

    <div id="resultTabContent" class="tab-content">
        <div class="tab-pane fade in active" id="assays">
            <g:render template="assays"/>
        </div>

        <div class="tab-pane fade" id="compounds">
            <g:render template="compounds"/>
        </div>

        <div class="tab-pane fade" id="experiments">
            <g:render template="experiments"/>
        </div>

        <div class="tab-pane fade" id="projects">
            <g:render template="projects"/>
        </div>
    </div>
</div>    <!-- End accordion -->

<%-- MarvinSketch's modal window --%>
<div id="modalDiv">
    <iframe name="modalIFrame" id="modalIFrame" width="100%" height="100%" marginWidth="0"
            marginHeight="0" frameBorder="0"
            scrolling="auto">

    </iframe>
</div>

<%-- Is used to capture the SMILES and search-type in the parent form (entered in the modal child window) before submit for searching --%>
<div id="structureSearchDiv">
    <g:form name="structureSearchForm" id="structureSearchForm" controller="bardWebInterface"
            action="structureSearch">
        <g:hiddenField name="smiles" id='hiddenFieldSmiles'/>
        <g:hiddenField name="structureSearchType" id='hiddenFieldStructureSearchType'/>
    </g:form>
</div>

%{--container for the SAR cart goes here--}%
<div id="container">
</div>

%{-- SAR cart itself goes here--}%
<div class="panel">
    <h3>SAR Cart</h3>

    <sc:each>
        <tr>
            <td>
                ${CartAssay.findByShoppingItem(it['item'])}
            </td>
            <td>
                ${it['qty']}
            </td>
            <td>
                <g:remoteLink action="add"
                              controller="sarCart"
                              params="${[id:CartAssay.findByShoppingItem(it['item']).id, class:(CartAssay.findByShoppingItem(it['item'])).class, version:(CartAssay.findByShoppingItem(it['item'])).version]}"
                              update="shoppingCartContent"
                              onComplete="Effect.Pulsate('shoppingCartContent', {pulses: 1, duration: 1.0});">
                    Add
                </g:remoteLink>
            </td>
            <td>
                <g:remoteLink action="remove"
                              controller="sarCart"
                              params="${[id:CartAssay.findByShoppingItem(it['item']).id, class:(CartAssay.findByShoppingItem(it['item'])).class, version:(CartAssay.findByShoppingItem(it['item'])).version]}"
                              update="shoppingCartContent"
                              onComplete="Effect.Pulsate('shoppingCartContent', {pulses: 1, duration: 1.0});">
                    Remove
                </g:remoteLink>
            </td>
            <td>
                <g:remoteLink action="removeAll"
                              controller="sarCart"
                              params="${[id:CartAssay.findByShoppingItem(it['item']).id, class:(CartAssay.findByShoppingItem(it['item'])).class, version:(CartAssay.findByShoppingItem(it['item'])).version]}"
                              update="shoppingCartContent"
                              onComplete="Effect.Pulsate('shoppingCartContent', {pulses: 1, duration: 1.0});">
                    Remove All
                </g:remoteLink>
            </td>
        </tr>
    </sc:each>
    <g:if test="${checkedOutItems}">
        <tr>
            <td><h2>Checked out items</h2></td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
        </tr>
        <g:each in="${checkedOutItems}" var="item">
            <tr>
                <td>
                    ${com.metasieve.shoppingcart.Shoppable.findByShoppingItem(item['item']) ?: com.metasieve.shoppingcart.CartAssay.findByShoppingItem(item['item'])}
                </td>
                <td>
                    ${item['qty']}
                </td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
        </g:each>
    </g:if>

</div>
<a class="trigger" href="#">infos</a>


</body>
</html>