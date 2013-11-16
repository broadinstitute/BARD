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

<header class="container-fluid" id="bard-header">

    <div class="search-panel">

        <div class="container-fluid">
            <div id="downtimenotify" class="row-fluid span12" style="display:none">
                <div id="basic-template">
                    <a class="ui-notify-cross ui-notify-close" href="#">x</a>

                    <h1 id="downTimeTitle">#{title}</h1>

                    <p>#{text}</p>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span2">
                    <strong class="logo"><a
                            href="${createLink(controller: 'BardWebInterface', action: 'index')}">BARD BioAssay Research Database</a>
                    </strong>
                </div>

                <div class="span8">
                    <div class="search-block">
                        <g:render template="/layouts/templates/searchBlock"/>
                    </div>
                    <div class="share-block">
                        <g:render template="/layouts/templates/socialMedia"/>
                    </div>
                </div>

                <div class="span2">
                    <nav class="nav-panel">
                        <div class="center-aligned">
                            <g:render template="/layouts/templates/loginStrip"/>
                        </div>

                        <div class="visible-desktop">
                            <g:render template="/layouts/templates/queryCart"/>
                        </div>

                        <sec:ifLoggedIn>
                            <g:link controller="bardWebInterface" action="navigationPage" class="my-bard-button btn">My BARD</g:link>
                        </sec:ifLoggedIn>

                        <div class="navbar right-aligned">
                            <ul class="nav">
                                <g:render template="/layouts/templates/howtolinks"/>
                            </ul>
                        </div>

                    </nav>
                </div>

            </div>
        </div>
    </div>

    <g:render template="/layouts/templates/IdSearchBox"></g:render>

    <g:if test="${flash.message}">
        <div class="alert">
            <button class="close" data-dismiss="alert">×</button>
            ${flash.message}
        </div>
    </g:if>

</header>

<div class="container-fluid" id="bard-container">
    <div class="row-fluid">
        <div class="span12">
            <div class="spinner-container">
                <div id="spinner" class="spinner" style="display:none; color: blue;"><g:message code="spinner.alt"
                                                                                                default=""/></div>
            </div>
            <g:layoutBody/>
        </div>
    </div>
</div>

<g:render template="/layouts/templates/footer"/>

<r:layoutResources/>
</body>
</html>
