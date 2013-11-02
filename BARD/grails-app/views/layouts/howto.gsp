<%@ page import="bardqueryapi.IDSearchType" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>BARD: <g:layoutTitle default="BioAssay Research Database"/></title>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link href='http://fonts.googleapis.com/css?family=Lato:400,400italic,700,700italic,900,900italic,300,300italic'
          rel='stylesheet' type='text/css'>
    <r:require modules="bardHomepage,idSearch,jquerynotifier,downtime,autocomplete"/>
    <!--[if lt IE 9]><link rel="stylesheet" href="../css/bardHomepage/ieBardHomepage.css" media="screen" /><![endif]-->
    <!--[if IE]><script src="../js/bardHomepage/ie.js"></script><![endif]-->

    <g:layoutHead/>

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

<div id="wrapper">
    %{--The control area at the top of the page is all contained within this header--}%
    <header class="navbar navbar-static-top" id="header">
        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span12" id="downtimeMessage"></div>
            </div>

            <div class="row-fluid">
                <div class="span6">
                    <strong class="logo"><a href="#">BARD BioAssay Research Database</a></strong>
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
                                <li class="dropdown">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">How To …</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="#">Search</a></li>
                                        <li><a href="#">Work with results</a></li>
                                        <li><a href="#">Submit data</a></li>
                                        <li><a href="#">Use securely</a></li>
                                        <li><a href="#">Create and use plug-ins</a></li>
                                    </ul>
                                </li>
                                <li class="dropdown">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Support</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="#">Community</a></li>
                                        <li><a href="#">Report a bug</a></li>
                                        <li><a href="#">Contact us</a></li>
                                    </ul>
                                </li>
                                <sec:ifLoggedIn>
                                    <li><a href="/BARD/bardWebInterface/navigationPage">My BARD</a></li>
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
</div>
<r:layoutResources/>
</body>
</html>
