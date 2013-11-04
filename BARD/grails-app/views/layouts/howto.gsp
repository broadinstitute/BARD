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
                                        <li><a href="../about/howToSearch">Search</a></li>
                                        <li><a href="../about/howToFilterResults">Filter search results</a></li>
                                        <li><a href="../about/howToReadResults">Interpret search results</a></li>
                                        %{--<li><a href="../about/howToUseSecurely">Use securely</a></li>--}%
                                        <li><a href="../about/howToUsePlugins">Create and use plug-ins</a></li>
                                    </ul>
                                </li>
                                <li class="dropdown">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Support</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="../about/howToReportABug">Report a bug</a></li>
                                        <li><a href="../about/howToContactUs">Contact us</a></li>
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
                        %{--<div class="row-fluid">--}%
                        %{--Note:  remove 'fivecolumns' class and go to span3's to move down to four columns--}%
                        <div class="row-fluid fivecolumns">
                            <div class="span2">
                                <h3>About</h3>
                                <ul>
                                    <li><a href="../about/bardHistory">History</a></li>
                                    <li><a href="../about/bardDevelopmentTeam">Development Team</a></li>
                                </ul>
                            </div>

                            <div class="span2">
                                <h3>Help</h3>
                                <ul>
                                    <li><a href="https://groups.google.com/a/broadinstitute.org/forum/#!forum/bard-users">Forums</a></li>
                                    <li><a href="https://groups.google.com/a/broadinstitute.org/forum/#!newtopic/bard-users">Submit a Bug Report</a></li>
                                    <li><a href="https://groups.google.com/a/broadinstitute.org/forum/#!newtopic/bard-users">Ask a Question</a></li>
                                </ul>
                            </div>

                            <div class="span2">
                                <h3>Technology</h3>
                                <ul>
                                    <li><a href="../about/bardArchitecture">Architecture &amp; Design</a></li>
                                    <li><a href="https://github.com/ncatsdpiprobedev/bard/wiki">REST API</a></li>
                                    <li><a href="#" style="text-decoration: line-through;">Source code on GitHub<img src="${resource(dir: 'images/bardHomepage', file: 'comingSoon2.png')}" alt="coming soon"></a></li>
                                </ul>
                            </div>

                            <div class="span2">
                                <h3>RDM</h3>
                                <ul>
                                    <li><a href="../about/bardOrganizingPrinciples">Organizing principles</a></li>
                                    <li><a href="../element/showTopLevelHierarchyHelp">Top-level concepts</a></li>
                                    <li><a href="../dictionaryTerms/dictionaryTerms">Glossary</a></li>
                                </ul>
                            </div>



                            <div class="span2">
                                <strong class="logo-by"><a href="http://www.chemaxon.com/" title="Powered by ChemAxon">Powered by ChemAxon</a></strong>
                                <p><strong class="logo-scilligence"><a href="http://www.scilligence.com/web/" title="Scilligence">Scilligence</a></strong></p>
                                <p>&copy; 2013 BARD</p>
                            </div>
                        </div>
                    </div>
                </div>

                %{--The bottom line of the whole page--}%
                <div class="footer-info">
                    <div class="container-fluid">
                        <ul>
                            <li><a href="http://www.nih.gov/">National Institutes of Health</a></li>
                            <li><a href="http://www.hhs.gov/">U.S. Department of Health and Human Services</a></li>
                            <li><a href="http://www.usa.gov/">USA.gov – Government Made Easy</a></li>
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
