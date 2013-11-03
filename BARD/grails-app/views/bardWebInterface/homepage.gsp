<%@ page import="bardqueryapi.IDSearchType" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>BioAssay Research Database</title>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link href='http://fonts.googleapis.com/css?family=Lato:400,400italic,700,700italic,900,900italic,300,300italic'
          rel='stylesheet' type='text/css'>
    <r:require modules="bardHomepage,idSearch,jquerynotifier,downtime,autocomplete"/>
    <!--[if lt IE 9]><link rel="stylesheet" href="../css/bardHomepage/ieBardHomepage.css" media="screen" /><![endif]-->
    <!--[if IE]><script src="../js/bardHomepage/ie.js"></script><![endif]-->

    %{--Make sure that people have their JavaScript turned on--}%
    <noscript>
        <a href="http://www.enable-javascript.com/" target="javascript">
            <img src="${resource(dir: 'images', file: 'enable_js.png')}"
                 alt="Please enable JavaScript to access the full functionality of this site."/>
        </a>
    </noscript>
    <r:layoutResources/>

    <style type="text/css">
        /* start of modification for 5 columns */
    @media (min-width: 768px) {
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

        /* end of modification for 5 columns */
    </style>
</head>

<body>

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
                    <div class="span12">
                        <nav class="nav-panel right-aligned">
                            <ul class="nav">
                                <li class="dropdown">
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">How To …</a>
                                    <ul class="dropdown-menu">
                                        <li><a href="../about/howToSearch">Search</a></li>
                                        <li><a href="../about/howToFilterResults">Filter search results</a></li>
                                        <li><a href="../about/howToReadResults">Interpret search results</a></li>
                                        <li><a href="../about/howToUseSecurely">Use securely</a></li>
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
    </div>
</header>

%{--Enhanced data and advanced tools block--}%
<article class="hero-block">
    <div class="container-fluid">
        <div class="hero-area">

            <div class="row-fluid">
                <article class="span8">
                    <h1>Enhanced data and advanced tools to accelerate drug discovery.</h1>

                    <p>Introducing BARD, the powerful new bioassay database from the NIH Molecular Libraries Program. Now with unprecedented efficiency, scientists can develop and test hypotheses on the influence of different chemical probes on biological functions.</p>
                    <a href="#" class="btn btn-primary">LEARN MORE</a>
                </article>
                <aside class="span4"></aside>
            </div>
        </div>
    </div>
</article>


%{--Block to hold the main search textblock--}%
<div class="search-panel">
    <div class="container-fluid">

        <div class="head-holder">
            <h2>SEARCH BARD</h2>

            <p>Search assay, project and experiment data or <a
                    href="#">learn about BARD’s innovative search features.</a></p>
        </div>

        <div class="search-block">

            <g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm"
                    class="search-form">
                <fieldset>
                    <div class="search-field input-append">
                        <g:link controller="bardWebInterface" action="jsDrawEditor">
                        %{--<img src="${resource(dir: 'images', file: 'structureEditIcon.png')}"--}%
                            <img
                            %{--src="../images/bardHomepage/struct_icon.png" --}%
                                    src="${resource(dir: 'images/bardHomepage', file: 'struct_icon.png')}"

                                    alt="Draw or paste a structure"
                                    title="Draw or paste a structure" style="width:74px;float: left;"/>
                        </g:link>
                        <img src="${resource(dir: 'images/bardHomepage', file: 'ids_icon.png')}"
                        %{--src="../images/bardHomepage/ids_icon.png"--}%
                             alt="List of IDs for search"
                             title="List of IDs for search" data-toggle="modal" href="#idModalDiv"
                             style="width:44px;float: left;"/>

                        <div class="text-field">
                            <g:textField id="searchString" name="searchString"
                                         placeholder="Search by Chemistry, Biology, Structure and More"
                                         value="${flash.searchString}"/>
                        </div>

                        <div class="btn-field">
                            <button name="search" class="btn btn-primary" id="searchButton"
                                    type="submit">Search <span
                                    class="hidden-phone">BARD</span>
                            </button>
                        </div>
                    </div>
                </fieldset>
            </g:form>



            <div class="links-holder">
                %{--<a href="#">Advanced Search</a>                    We can put this link back when we have an advanced search to present--}%
                <a href="${createLink(controller: 'queryCart', action: 'toDesktopClient')}" target="_blank"
                   class="download-link hidden-phone">Download the BARD Desktop Client</a>
            </div>

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
%{--carousel news panel. This will need dynamic content--}%
<g:if test="${true}">
    <section class="news-panel">
        <div class="container-fluid">
            <div class="news-row">
                <h1>BARD NEWS</h1>

                <div class="news-holder">
                    <div class="news-gallery slide" id="news-gallery" data-interval="false">
                        <a href="#news-gallery" class="btn-prev" data-slide="prev">Previous</a>
                        <a href="#news-gallery" class="btn-next" data-slide="next">Next</a>

                        <div class="carousel-inner">
                            <div class="item active">
                                <strong class="ttl">Presentation<time
                                        datetime="2013-08-16T08:20">SEP 20 2013 @ 1:00 pm</time>
                                </strong>

                                <p><a href="#">Steve Brudz reviews BARD in keynote at CBBO</a></p>
                            </div>

                            <div class="item">
                                <strong class="ttl">Release<time
                                        datetime="2013-08-17T08:20">NOV 20 2013 @ 3:00 pm</time>
                                </strong>

                                <p><a href="#">Projected public release of BARD</a></p>
                            </div>

                            <div class="item">
                                <strong class="ttl">Demonstration<time
                                        datetime="2013-08-18T08:20">NOV 5 2013 @ 2:00 pm</time>
                                </strong>

                                <p><a href="#">Regularly scheduled BARD iteration demo.</a></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</g:if>

%{--This is the main carousel, with three big pictures and associated buttons--}%
<div class="articles-gallery slide" id="articles-gallery" data-interval="false">
    <a href="#articles-gallery" class="btn-prev" data-slide="prev">Previous</a>
    <a href="#articles-gallery" class="btn-next" data-slide="next">Next</a>

    <div class="carousel-inner">
        <article class="item active">
            <div class="container-fluid">
                <div class="row-fluid">
                    <div class="span6 img-box">
                        <img src="../images/bardHomepage/img-01.png" alt="image description">
                    </div>

                    <div class="span6 pull-right article">
                        <div class="article-holder">
                            <h1>Search and analyze your own way.</h1>

                            <p>Keeping the promise of the MLP, BARD gives you nimble access to most all the program’s data through an array of query, analysis and visualization tools.</p>
                            <a href="#" class="btn btn-primary">HOW TO WORK WITH RESULTS</a>
                        </div>
                    </div>
                </div>
            </div>
        </article>
        <article class="item">
            <div class="container-fluid">
                <div class="row-fluid">
                    <div class="span6 img-box">
                        <img src="../images/bardHomepage/img-02.png" alt="image description">
                    </div>

                    <div class="span6 pull-right article">
                        <div class="article-holder">
                            <h1>The power of a common language.</h1>

                            <p>A focused but flexible ontology designed to capture fully annotated preclinical data from multiple scientific disciplines for comprehensive search results.</p>
                            <a href="#" class="btn btn-primary">HOW TO SEARCH BARD</a>
                        </div>
                    </div>
                </div>
            </div>
        </article>
        <article class="item">
            <div class="container-fluid">
                <div class="row-fluid">
                    <div class="span6 img-box">
                        <img src="../images/bardHomepage/img-03.png" alt="image description">
                    </div>

                    <div class="span6 pull-right article">
                        <div class="article-holder">
                            <h1>Public bioassay data – organized, standardized and put into context.</h1>

                            <p>Public data sets cleaned up, organized and enhanced with assay, experiment and project contextual information for reliable and productive searches.</p>
                            <a href="#" class="btn btn-primary">Learn More About BARD</a>
                        </div>
                    </div>
                </div>
            </div>
        </article>
    </div>
</div>

<g:if test="${true}">
%{--The BARD is  growing line sits on its own above the blocks--}%
    <section
            class="tabs-section">%{--This section tag binds 'Bard is growing', the blocks, and the tab information together  --}%
        <div class="container-fluid">
            <div class="page-header">
                <h1>BARD Is Growing <small>Statistics &amp; Recent Submissions</small></h1>
            </div>
        </div>

        %{--Here we have a set of clickable boxes, each one leading to a carousel of information. These are implemented simply as tabs,--}%
        %{--all of which are defined in the next section. This information should probably come back dynamically through ajax ( at least --}%
        %{--once we have information worth providing--}%
        <div class="tabs-list-holder">
            <ul class="tabs-list">
                <li>
                    <g:projectCount/>
                </li>
                <li class="active">
                    <g:assayCount/>
                </li>
                <li>
                    <g:experimentCount/>
                </li>
                <li>
                    <g:substanceCount/>
                </li>
                <li>
                    <g:probeCount/>
                </li>
            </ul>
        </div>


        <div class="container-fluid">
            <div class="tab-content">
                %{--Contents of the "Projects" tab (of our row of five content boxes) --}%
                <g:render template="recentlyAddedProjects" model="['recentlyAddedProjects': recentlyAddedProjects]"/>

                %{--Contents of the "Definitions" tab (of our row of five content boxes) --}%
                <g:render template="recentlyAddedAssays" model="['recentlyAddedAssays': recentlyAddedAssays]"/>


                %{--Contents of the "Experiments" tab (of our row of five content boxes) --}%
                <g:render template="recentlyAddedExperiments"
                          model="['recentlyAddedExperiments': recentlyAddedExperiments]"/>
                %{--Contents of the "Compounds" tab (of our row of five content boxes) --}%
                <g:render template="recentlyAddedSubstances"
                          model="['recentlyAddedSubstances': recentlyAddedSubstances]"/>

                %{--Contents of the "Probes" tab (of our row of five content boxes) --}%
                <g:render template="recentlyAddedProbes" model="['recentlyAddedProbes': recentlyAddedProbes]"/>
            </div>
        </div>
    </section>
</g:if>


%{--Now we have a footer section containing a bunch of links--}%
<footer id="footer">
    <div class="footer-columns">
        <div class="container-fluid">
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
                        <li><a href="#">Forums</a></li>
                        <li><a href="#">Submit a Bug Report</a></li>
                        <li><a href="#">Ask a Question</a></li>
                    </ul>
                </div>

                <div class="span2">
                    <h3>Technology</h3>
                    <ul>
                        <li><a href="../about/bardArchitecture">Architecture &amp; Design</a></li>
                        <li><a href="https://github.com/ncatsdpiprobedev/bard/wiki">REST API</a></li>
                        <li><a href="#" style="text-decoration: line-through;">Source code on GitHub<img
                                src="${resource(dir: 'images/bardHomepage', file: 'comingSoon2.png')}"
                                alt="coming soon"></a></li>
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
                    <strong class="logo-by"><a href="#" title="Powered by ChemAxon">Powered by ChemAxon</a></strong>

                    <p>&copy; 2013 BARD</p>
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
<r:layoutResources/>
</body>
</html>
