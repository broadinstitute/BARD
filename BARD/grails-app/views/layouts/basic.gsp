<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>BARD: <g:layoutTitle default="BioAssay Research Database"/></title>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link href='http://fonts.googleapis.com/css?family=Lato:400,400italic,700,700italic,900,900italic,300,300italic'
          rel='stylesheet' type='text/css'>
    <g:layoutHead/>

    <r:require modules="basic,bootstrap,autocomplete,cart,idSearch,jquerynotifier,downtime"/>
    <%@ page import="bardqueryapi.IDSearchType" %>
    <r:layoutResources/>
    <ga:trackPageview/>

</head>

<body>
<noscript>
    <a href="http://www.enable-javascript.com/" target="javascript">
        <img src="${resource(dir: 'images', file: 'enable_js.png')}"
             alt="Please enable JavaScript to access the full functionality of this site."/>
    </a>
</noscript>

<header class="container-fluid">

    <div class="search-panel">

        <div class="container-fluid">
            <div id="downtimenotify" class="row-fluid span12" style="display:none">
                <div id="basic-template">
                    <a class="ui-notify-cross ui-notify-close" href="#">x</a>

                    <h1 id="downTimeTitle">#{title}</h1>

                    <p>#{text}</p>
                </div>
            </div>

            <div class="row-fluid span12">
                <strong class="logo"><a
                        href="${createLink(controller: 'BardWebInterface', action: 'index')}">BARD BioAssay Research Database</a>
                </strong>

                <div class="search-block">
                    <g:render template="/layouts/templates/socialMedia"/>
                    <br/>
                    <g:render template="/layouts/templates/searchBlock"/>
                </div>


                <nav class="nav-panel">
                    <div class="center-aligned">
                        <g:render template="/layouts/templates/loginStrip"/>
                    </div>

                    <div class="visible-desktop">
                        <g:render template="/layouts/templates/queryCart"/>
                    </div>
                    <sec:ifLoggedIn>
                        <a href='/BARD/bardWebInterface/navigationPage'
                           style="background: #0093d0; color: white; margin-top: 8px"
                           class="btn btn-primary">My BARD
                        </a>
                    </sec:ifLoggedIn>
                </nav>

            </div>
        </div>
    </div>


    <div class="modal hide" id="idModalDiv">
        <div class="modal-header">
            <a class="close" data-dismiss="modal">×</a>

            <h3>Enter a Comma separated list of IDs</h3>
        </div>

        <div class="modal-body">
            <textarea class="field span9" id="idSearchString" name="idSearchString" rows="15"></textarea>
        </div>

        <div class="modal-footer">
            <g:form name="idSearchForm" class="form-inline">
                <div>
                    <g:radioGroup name="idSearchType"
                                  values="${IDSearchType.values()}"
                                  value="${IDSearchType.ALL}"
                                  labels="${IDSearchType.values().label}">
                        <label class="radio inline"><%=it.radio%>${it.label}</label>
                    </g:radioGroup>
                </div>

                <br>

                <div>
                    <a href="#" class="btn" data-dismiss="modal" id="closeButton21">Close</a>
                    <a href="#" class="idSearchButton btn btn-primary" data-dismiss="modal">Search</a>
                </div>
            </g:form>
        </div>

    </div>



    <g:if test="${flash.message}">
        <div class="alert">
            <button class="close" data-dismiss="alert">×</button>
            ${flash.message}
        </div>
    </g:if>

</header>


<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="spinner-container">
                <div id="spinner" class="spinner" style="display:none; color: blue;"><g:message code="spinner.alt"
                                                                                                default=""/></div>
            </div>
            <g:layoutBody/>
        </div>
    </div>


    <div class="row-fluid bard-footer">
        <footer id="footer">
            <div class="footer-columns">
                <div class="container-fluid">
                    <div class="row-fluid">

                        <div class="span5 bard-footer-versioninfo muted">
                            <div>
                                <b>Created:</b> ${grailsApplication.metadata['war.created']} <b>branch:</b> ${grailsApplication?.metadata['git.branch.name']} <b>revision:</b> ${grailsApplication?.metadata['git.branch.version']}
                            </div>
                        </div>

                        <div class="span5">
                        </div>


                        <div class="span2 right-aligned">
                            <a href="http://www.chemaxon.com/" target="chemAxon"><img
                                    src="${resource(dir: 'images/bardHomepage', file: 'logo-by.png')}"
                                    alt="Powered by ChemAxon"/></a>
                        </div>
                    </div>
                </div>
            </div>

            %{--The bottom line of the whole page--}%
            <div class="footer-info">
                <div class="container-fluid">
                    <ul>
                        <li><a href="#">National Institutes of Health</a></li>
                        <li><a href="#">U.S. Department of Health and Human Services</a></li>
                        <li><a href="#">USA.gov – Government Made Easy</a></li>
                    </ul>
                </div>
            </div>
        </footer>
    </div>

</div>
<r:layoutResources/>
</body>
</html>
