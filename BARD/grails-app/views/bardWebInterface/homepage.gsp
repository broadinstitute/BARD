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

    <r:script disposition='head'>
        window.bardAppContext = "${request.contextPath}";
    </r:script>
    <r:require modules="bardHomepage,idSearch,jquerynotifier,downtime,autocomplete,compoundOptions"/>
    <!--[if lt IE 9]><link rel="stylesheet" href="${resource(dir: 'css/bardHomepage', file: 'ieBardHomepage.css')}" media="screen" /><![endif]-->
    <!--[if IE]><script src="${resource(dir: 'js/bardHomepage', file: 'ie.js')}" /></script><![endif]-->

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
    <g:render template="homepageHeader"/>

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
                            <g:each in="${bardNewsInstances}" status="i" var="newsItem">
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
                            <r:img dir="images/bardHomepage" file="img-01.png" alt="Search and analyze your own way."/>
                        </div>

                        <div class="span6 pull-right article">
                            <div class="article-holder">
                                <h1>Search and analyze your own way.</h1>

                                <p>Keeping the promise of the MLP, BARD gives you nimble access to most all the program’s data through an array of query, analysis and visualization tools.</p>
                                <g:link controller="about" action="howToVisualizeAndCaptureData"
                                        class="btn btn-primary">HOW TO VISUALIZE DATA</g:link>
                            </div>
                        </div>
                    </div>
                </div>
            </article>
            <article class="item">
                <div class="container-fluid">
                    <div class="row-fluid">
                        <div class="span6 img-box">
                            <r:img dir="images/bardHomepage" file="img-02.png" alt="The power of a common language."/>
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
                            <r:img dir="images/bardHomepage" file="img-03.png"
                                   alt="Public bioassay data – organized, standardized and put into context."/>
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

    %{--Populated by an ajax call--}%
    <div id="bardIsGrowing" href="${createLink(controller: "bardWebInterface", action: 'bardIsGrowing')}">

    </div>

    <g:render template="/layouts/templates/footer"/>

</div>
<script> var $ = jQuery.noConflict();
$(document).ready(function () {
    $('#articles-gallery').carousel({ interval: 10000, cycle: true });
});

</script>
<r:layoutResources/>
</body>
</html>
