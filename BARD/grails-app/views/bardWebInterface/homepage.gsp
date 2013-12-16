<%@ page import="bardqueryapi.IDSearchType" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>BioAssay Research Database</title>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link href='https://fonts.googleapis.com/css?family=Lato:400,400italic,700,700italic,900,900italic,300,300italic'
          rel='stylesheet' type='text/css'>
    <r:require modules="bardHomepage,idSearch,jquerynotifier,downtime,autocomplete,compoundOptions,historyJsHtml5"/>
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
            <div class="span3" style="min-width: 210px; min-height: 70px;">
                <strong class="logo"><a href="#">BARD BioAssay Research Database</a></strong>
            </div>

            <div class="span9">
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
                                <g:render template="/layouts/templates/howtolinks"/>
                                <sec:ifLoggedIn>
                                    <li><g:link controller="bardWebInterface"
                                                action="navigationPage">My BARD</g:link></li>
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
                    <g:link controller="about" action="aboutBard" class="btn btn-primary">LEARN MORE</g:link>
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

            <p>Search assay, project and experiment data or <g:link controller="about"
                                                                    action="howToSearch">learn about BARD’s innovative search features.</g:link></p>
        </div>

        <div class="search-block">
            <div class="row-fluid">
                <g:render template="../layouts/templates/searchBlock"/>
            </div>

            <div class="links-holder">
                %{--<a href="#">Advanced Search</a>                    We can put this link back when we have an advanced search to present--}%
                <g:link controller="queryCart" action="toDesktopClient" target="_blank"
                        class="download-link hidden-phone">Download the BARD Desktop Client</g:link>
            </div>
        </div>

    </div>
</div>

<g:render template="/layouts/templates/IdSearchBox"/>

%{--carousel news panel. Turn this back on by reversing the conditional below
    as soon as we have some dynamic content to display --}%
<section class="news-panel">
    <div class="container-fluid">
        <div class="news-row">
            <h1>BARD NEWS</h1>

            <div class="news-holder">
                <div class="news-gallery slide" id="news-gallery" data-interval="false">
                    <a href="#news-gallery" class="btn-prev" data-slide="prev">Previous</a>
                    <a href="#news-gallery" class="btn-next" data-slide="next">Next</a>

                    <div class="carousel-inner">
                        <g:each in="${bard.db.util.BardNews.listOrderByEntryDateUpdated(order: "desc")}" status="i"
                                var="newsItem">
                            <div class="item ${!i ? 'active' : ''}">
                                <g:bardNewsItem item="${newsItem}"/>
                            </div>
                        </g:each>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

%{--This is the main carousel, with three big pictures and associated buttons--}%
<div class="articles-gallery slide" id="articles-gallery">
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
                            <a href="../about/howToVisualizeAndCaptureData"
                               class="btn btn-primary">HOW TO VISUALIZE DATA</a>
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
                            <g:link controller="about" action="howToSearch"
                                    class="btn btn-primary">HOW TO SEARCH BARD</g:link>
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
                            <g:link controller="about" action="aboutBard"
                                    class="btn btn-primary">Learn More About BARD</g:link>
                        </div>
                    </div>
                </div>
            </div>
        </article>
    </div>
</div>

<div id="bardIsGrowing" href="${createLink(controller:"bardWebInterface", action: 'bardIsGrowing')}">


</div>

<g:render template="/layouts/templates/footer"/>

</div>
<script> var $ = jQuery.noConflict(); $(document).ready(function()
{ $('#articles-gallery').carousel({ interval: 10000, cycle: true }); });

</script>
<r:layoutResources/>
</body>
</html>
