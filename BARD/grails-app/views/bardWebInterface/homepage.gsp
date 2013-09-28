<!DOCTYPE html>
<html lang="en">
<head>
    <title>BioAssay Research Database</title>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BARD</title>
    <link href='http://fonts.googleapis.com/css?family=Lato:400,400italic,700,700italic,900,900italic,300,300italic'
          rel='stylesheet' type='text/css'>
    <r:require modules="bardHomepage,downtime,autocomplete"/>
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
    %{--We have to run a small function in order to kickstart the auto complete--}%
    %{--<script>--}%
        %{--$(document).ready(function () {--}%

            %{--//set up auto complete--}%
            %{--var autoOpts = {--}%
                %{--source: "/BARD/bardWebInterface/autoCompleteAssayNames",--}%
                %{--minLength: 2,--}%
                %{--html: true,--}%
                %{--delay: 1000--}%
            %{--};--}%

            %{--$("#searchString").autocomplete(autoOpts);--}%
            %{--$("#searchString").bind("autocompleteselect", function (event, ui) {--}%
                %{--$("#searchString").val(ui.item.value)--}%
                %{--$("#searchButton").click();--}%
            %{--});--}%
            %{--// make sure to close the autocomplete box when the search button or ENTER are clicked--}%
            %{--$("#searchButton").click(function () {--}%
                %{--$("#searchString").autocomplete("close");--}%
            %{--});--}%
            %{--$('#searchButton').keypress(function (eventData) {--}%
                %{--if (eventData.which == 13) {--}%
                    %{--$("#searchString").autocomplete("close");--}%
                %{--}--}%
            %{--});--}%

        %{--});--}%
    %{--</script>--}%

</head>

<body>

<div id="wrapper">

%{--The control area at the top of the page is all contained within this header--}%
<header class="navbar navbar-static-top" id="header">
    <div class="container-fluid">
        <div class="row-fluid span12" id="downtimeMessage">

        </div>
        <strong class="logo"><a href="#">BARD BioAssay Research Database</a></strong>
        <ul class="social-networks">
            <li>
                %{--Facebook widget plugin--}%
                <a href="#"
                   onclick="
                       window.open(
                               'https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(location.href),
                               'facebook-share-dialog',
                               'width=626,height=436');
                       return false;"
                   style="background:url('../images/bardHomepage/facebook-share-icon.gif') no-repeat; width:58px; height:18px;">
                </a>
            </li>
            <li style="width: 80px;">
                %{--Twitter widget plugin--}%
                <script>!function (d, s, id) {
                    var js, fjs = d.getElementsByTagName(s)[0], p = /^http:/.test(d.location) ? 'http' : 'https';
                    if (!d.getElementById(id)) {
                        js = d.createElement(s);
                        js.id = id;
                        js.src = p + '://platform.twitter.com/widgets.js';
                        fjs.parentNode.insertBefore(js, fjs);
                    }
                }(document, 'script', 'twitter-wjs');
                </script>

                <a href="https://twitter.com/share" class="twitter-share-button" data-url="https://bard.nih.gov/BARD/"
                   data-text="BARD">Tweet</a>
            </li>
            <li>
                %{--LinkedIn widget plugin--}%
                <script src="//platform.linkedin.com/in.js" type="text/javascript">
                    lang: en_US
                </script>
                <script type="IN/Share" data-url="https://bard.nih.gov/BARD/" data-counter="right"></script>
            </li>
            <li>
                %{--Google Plus widget plugin--}%

                <!-- Place this tag where you want the +1 button to render. -->
                <div class="g-plusone"></div>

                <!-- Place this tag after the last +1 button tag. -->
                <script type="text/javascript">
                    (function () {
                        var po = document.createElement('script');
                        po.type = 'text/javascript';
                        po.async = true;
                        po.src = 'https://apis.google.com/js/plusone.js';
                        var s = document.getElementsByTagName('script')[0];
                        s.parentNode.insertBefore(po, s);
                    })();
                </script>
            </li>
        </ul>
        <nav class="nav-panel">
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
                <li><a href="/BARD/bardWebInterface/navigationPage">Submissions</a></li>
            </ul>
            <g:if test="${false}">
                <ul class="login-nav">
                    <li><a href="#">Sign up</a></li>
                    <li><a href="#">Sign in</a></li>
                </ul>
            </g:if>
        </nav>
    </div>
</header>

%{--Enhanced data and advanced tools block--}%
<article class="hero-block">
    <div class="container-fluid">
        <div class="hero-area">

            <div class="row-fluid">
                <article class="span8">
                    <h1>Enhanced data and advanced tools to accelerate drug discovery.</h1>

                    <p>Introducing BARD, the powerful new bioassay database from the NIH Molecular Libraries Program. Now with unprecedented efficiency, scientists can develop and testhypotheses on the influence of different chemical probes on biological functions.</p>
                    <a href="#" class="btn btn-primary">LEARN MORE</a>
                </article>
                <aside class="span4"></aside>
            </div>
        </div>
    </div>
</article>

<g:if test="${true}">
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
                    <a href="#">Advanced Search</a>
                    <a href="#" class="download-link hidden-phone">Download the BARD Desktop Client</a>
                </div>

            </div>

        </div>
    </div>
</g:if>

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
                                <strong class="ttl">Presentation<time
                                        datetime="2013-08-17T08:20">SEP 08 2013 @ 9:00 am</time>
                                </strong>

                                <p><a href="#">Session at ACS meeting devoted to Bard</a></p>
                            </div>

                            <div class="item">
                                <strong class="ttl">Demonstration<time
                                        datetime="2013-08-18T08:20">SEP 24 2013 @ 2:00 pm</time>
                                </strong>

                                <p><a href="#">Next regularly scheduled BARD iteration demo.</a></p>
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
            <a href="#tab-projects" data-toggle="tab">
                <span><strong class="number">127</strong> Projects</span>
                <i class="arrow"></i>
            </a>
        </li>
        <li class="active">
            <a href="#tab-definitions" data-toggle="tab">
                <span><strong class="number">857</strong> Assay Definitions</span>
                <i class="arrow"></i>
            </a>
        </li>
        <li>
            <a href="#tab-experiments" data-toggle="tab">
                <span><strong class="number">1,204</strong> Experiments</span>
                <i class="arrow"></i>
            </a>
        </li>
        <li>
            <a href="#tab-compounds" data-toggle="tab">
                <span><strong class="number">573,062</strong> Tested Compounds</span>
                <i class="arrow"></i>
            </a>
        </li>
        <li>
            <a href="#tab-probes" data-toggle="tab">
                <span><strong class="number">135</strong> ML Probes</span>
                <i class="arrow"></i>
            </a>
        </li>
    </ul>
</div>



%{--Contents of the "Projects" tab (of our row of five content boxes) --}%
<div class="container-fluid">
<div class="tab-content">
<div class="tab-pane" id="tab-projects" data-interval="false">
    <div class="items-gallery slide" id="items-gallery-1">
        <a href="#items-gallery-1" class="btn-prev" data-slide="prev">Previous</a>
        <a href="#items-gallery-1" class="btn-next" data-slide="next">Previous</a>

        <div class="carousel-inner">
            <div class="item active">
                <div class="row-fluid">
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                </div>
            </div>

            <div class="item">
                <div class="row-fluid">
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                </div>
            </div>
        </div>
    </div>
</div>


%{--Contents of the "Definitions" tab (of our row of five content boxes) --}%
<div class="tab-pane active" id="tab-definitions">
    <div class="items-gallery slide" id="items-gallery-2" data-interval="false">
        <a href="#items-gallery-2" class="btn-prev" data-slide="prev" data-toggle="collapse">Previous</a>
        <a href="#items-gallery-2" class="btn-next" data-slide="next">Previous</a>

        <div class="carousel-inner">
            <div class="item active">
                <div class="row-fluid">
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                </div>
            </div>

            <div class="item">
                <div class="row-fluid">
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                </div>
            </div>
        </div>
    </div>
</div>

%{--Contents of the "Experiments" tab (of our row of five content boxes) --}%
<div class="tab-pane" id="tab-experiments">
    <div class="items-gallery slide" id="items-gallery-3" data-interval="false">
        <a href="#items-gallery-3" class="btn-prev" data-slide="prev">Previous</a>
        <a href="#items-gallery-3" class="btn-next" data-slide="next">Previous</a>

        <div class="carousel-inner">
            <div class="item active">
                <div class="row-fluid">
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                </div>
            </div>

            <div class="item">
                <div class="row-fluid">
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                </div>
            </div>
        </div>
    </div>
</div>

%{--Contents of the "Compounds" tab (of our row of five content boxes) --}%
<div class="tab-pane" id="tab-compounds">
    <div class="items-gallery slide" id="items-gallery-4" data-interval="false">
        <a href="#items-gallery-4" class="btn-prev" data-slide="prev">Previous</a>
        <a href="#items-gallery-4" class="btn-next" data-slide="next">Previous</a>

        <div class="carousel-inner">
            <div class="item active">
                <div class="row-fluid">
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                </div>
            </div>

            <div class="item">
                <div class="row-fluid">
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                </div>
            </div>
        </div>
    </div>
</div>

%{--Contents of the "Probes" tab (of our row of five content boxes) --}%
<div class="tab-pane" id="tab-probes">
    <div class="items-gallery slide" id="items-gallery-5" data-interval="false">
        <a href="#items-gallery-5" class="btn-prev" data-slide="prev">Previous</a>
        <a href="#items-gallery-5" class="btn-next" data-slide="next">Previous</a>

        <div class="carousel-inner">
            <div class="item active">
                <div class="row-fluid">
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">10.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                </div>
            </div>

            <div class="item">
                <div class="row-fluid">
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Universal chemical assay for the detection and determination of siderophores.</a>
                        </h2>

                        <p>Schwyn B, Neilands JB.</p>
                    </article>
                    <article class="span4">
                        <time datetime="2013-10-16">11.16.13</time>

                        <h2><a href="#">Chemical Assay of Drugs and Drug  Metabolites</a></h2>

                        <p>Sanford P. Markey</p>
                    </article>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
</div>
</section>
</g:if>


%{--Now we have a footer section containing a bunch of links--}%
<footer id="footer">
    <div class="footer-columns">
        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span3">
                    <h3>About</h3>
                    <ul>
                        <li><a href="#">Research Data Mgt.</a></li>
                        <li><a href="#">History</a></li>
                        <li><a href="#">Development Team</a></li>
                    </ul>
                </div>

                <div class="span3">
                    <h3>Help</h3>
                    <ul>
                        <li><a href="#">Forums</a></li>
                        <li><a href="#">Submit a Bug Report</a></li>
                        <li><a href="#">Ask a Question</a></li>
                    </ul>
                </div>

                <div class="span3">
                    <h3>Technology</h3>
                    <ul>
                        <li><a href="#">Architecture &amp; Design</a></li>
                        <li><a href="#">REST API</a></li>
                        <li><a href="#">Source code on GitHub</a></li>
                    </ul>
                </div>

                <div class="span3 by">
                    <strong class="logo-by"><a href="#" title="Powered by ChemAxon">Powered by ChemAxon</a></strong>

                    <p>&copy; 2013 BARD Lorem Ipsum Dolor</p>
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
