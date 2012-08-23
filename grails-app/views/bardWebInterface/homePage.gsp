<%@ page import="bardqueryapi.SearchType; bard.db.registration.*; com.metasieve.shoppingcart.*; com.metasieve.shoppingcart.Shoppable.*;" %>
<!DOCTYPE html>
<html>
<g:render template="homePageHeader"/>
<body>


<table>
    <tr>
        <td width=80%  class="screenSection">

            <div>
    <div>
        <h1 style="text-align: center">BARD</h1>

        <h2 style="text-align: center">BioAssay Research Database</h2> <br/><br/>
        <br/>

        <h3 style="text-align: center"><a href="${grailsApplication.config.bard.cap.home}"
                                          style="color:blue;text-decoration: underline;">CAP - Catalog of Assay Protocols Home</a>
        </h3>
        <br/>
        <g:if test="${flash.message}">
            <div class="error">${flash.message}</div>
        </g:if>
        <br/><br/>

        <g:form name="aidForm" id="aidForm" controller="bardWebInterface">

            <div class="content ">
                <table class="skinnyTable">
                    <tr>
                        <td>
                            <g:textField id="searchString" name="searchString" value="${params?.searchString}"
                                         size="50"/>
                        </td>
                        <td>
                            <g:submitButton name="search"
                                            value="Search" id="search"/>
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
        <li class="active"><a href="#assays" data-toggle="tab" id="assaysTab">Assays (0)</a></li>
        <li><a href="#compounds" data-toggle="tab" id="compoundsTab">Compounds (0)</a></li>
        <li><a href="#projects" data-toggle="tab" id="projectsTab">Projects (0)</a></li>
    </ul>

    <div id="resultTabContent" class="tab-content">
        <div class="tab-pane fade in active" id="assays">
            <g:render template="assays"/>
        </div>

        <div class="tab-pane fade" id="compounds">
            <g:render template="compounds"/>
        </div>
        <div class="tab-pane fade" id="projects">
            <g:render template="projects"/>
        </div>
    </div>
</div>    <!-- End accordion -->


        </td>

        <td width=2%>
        </td>


        <td width=18%  class="screenSection">
            %{-- SAR cart itself goes here--}%

            <g:render template="sarCartContent"/>

        </td>

    </tr>
</table>



<%-- MarvinSketch's modal window --%>
<div id="modalDiv">
    <iframe name="modalIFrame" id="modalIFrame" width="100%" height="100%" marginWidth="0"
            marginHeight="0" frameBorder="0"
            scrolling="auto">

    </iframe>
</div>

</body>
</html>