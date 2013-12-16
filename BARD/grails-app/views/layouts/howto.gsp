<%@ page import="bardqueryapi.IDSearchType" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>BARD: <g:layoutTitle default="BioAssay Research Database"/></title>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link href='https://fonts.googleapis.com/css?family=Lato:400,400italic,700,700italic,900,900italic,300,300italic'
          rel='stylesheet' type='text/css'>
    <r:require modules="bardHomepage,idSearch,jquerynotifier,downtime,autocomplete"/>
    <!--[if lt IE 9]><link rel="stylesheet" href="${resource(dir:'css/bardHomepage', file: 'ieBardHomepage.css')}" media="screen" /><![endif]-->
    <!--[if IE]><script src="${resource(dir: 'js/bardHomepage', file: 'ie.js')}" /></script><![endif]-->

    <g:layoutHead/>

    <r:layoutResources/>

    <style type="text/css">
    @media (min-width: 768px) {
        /* start of modification for 5 columns.  Must follow after bootstrap definitions */
        .fivecolumns .span2 {
            width: 18.2%;
            *width: 18.2%;
        }
    }

    @media (min-width: 1200px) {
        .fivecolumns .span2 {
            width: 17.9%;
            *width: 17.8%;
        }
    }

    @media (min-width: 768px) and (max-width: 979px) {
        .fivecolumns .span2 {
            width: 17.7%;
            *width: 17.7%;
        }
    }
    </style>

    <ga:trackPageview/>

</head>

<body>
<noscript>
    <a href="http://www.enable-javascript.com/" target="javascript">
        <img src="${resource(dir: 'images', file: 'enable_js.png')}"
             alt="Please enable JavaScript to access the full functionality of this site."/>
    </a>
</noscript>

<div id="wrapper">
    %{--The control area at the top of the page is all contained within this header--}%
    <header class="navbar navbar-static-top" id="header">
        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span3">
                    <g:render template="/layouts/templates/downtime"/>
                </div>

                <div class="row-fluid">
                    <div class="span6">
                        <strong class="logo"><a href="${request.contextPath}">BARD BioAssay Research Database</a></strong>
                    </div>

                    <div class="span6">
                        <div class="row-fluid">
                            <div class="center-aligned span6">
                                <g:render template="/layouts/templates/socialMedia"/>
                            </div>

                            <div class="right-aligned span6">
                                <g:render template="/layouts/templates/loginStrip"/>
                            </div>
                        </div>

                        <div class="row-fluid">
                            <nav class="nav-panel span12 right-aligned">
                                <ul class="nav">
                                    <g:render template="/layouts/templates/howtolinks"/>
                                    <sec:ifLoggedIn>
                                        <li><a href="${request.contextPath}/bardWebInterface/navigationPage">My BARD</a></li>
                                    </sec:ifLoggedIn>
                                </ul>
                                <g:if test="${false}">
                                    <ul class="login-nav">
                                        <li><a href="#">Sign up</a></li>
                                        <li><a href="#">Sign in</a></li>
                                    </ul>
                                </g:if>
                            </nav>
                        </div>

                    </div>
                </div>
            </div>
    </header>


    %{--Enhanced data and advanced tools block--}%
    %{--<article class="hero-block">--}%
    %{--<div class="container-fluid">--}%
    %{--<div class="hero-area">--}%

    %{--<div class="row-fluid">--}%
    %{--<article class="span8">--}%
    %{--<h1>Enhanced data and advanced tools to accelerate drug discovery.</h1>--}%

    %{--<p>Introducing BARD, the powerful new bioassay database from the NIH Molecular Libraries Program. Now with unprecedented efficiency, scientists can develop and test hypotheses on the influence of different chemical probes on biological functions.</p>--}%
    %{--<a href="#" class="btn btn-primary">LEARN MORE</a>--}%
    %{--</article>--}%
    %{--<aside class="span4"></aside>--}%
    %{--</div>--}%
    %{--</div>--}%
    %{--</div>--}%
    %{--</article>--}%

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
    </div>

    <g:render template="/layouts/templates/footer"/>

</div>
<r:layoutResources/>
</body>
</html>
