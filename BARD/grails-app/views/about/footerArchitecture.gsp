<%@ page import="bard.db.enums.ExperimentStatus; bard.db.enums.ContextType; bard.db.registration.DocumentKind; bard.db.model.AbstractContextOwner; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix"/>
    <meta name="layout" content="howto"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>BARD Architecture</title>
</head>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>Architecture &amp; Design</h2></article>
            <aside class="span2"></aside>
        </div>

    </div>
</div>

<article class="hero-block">
    <div class="container-fluid">
        <div class="lowkey-hero-area">
            <div class="row-fluid">
                <aside class="span2"></aside>
                <article class="span8">

                    <h3>
                        Architecture
                    </h3>

                    <p>
                        BARD has been developed to meet the differing needs of both data generators and data consumers.  It uses a component-based architecture with components connected by RESTful web services.
                    </p>

                    <p>
                    <dl class='unindentedDefinition'>
                        <dt>Data dictionary component</dt>
                        <dd>Used by dictionary curators to manage BARD’s hierarchical dictionary of terms</dd>
                        <br/>
                        <dt>Catalog of assay protocols component</dt>
                        <dd>Used by data generators to register assays and upload result data</dd>
                        <br/>
                        <dt>Warehouse component</dt>
                        <dd>Provides persistent storage of result data in a form that is fast and simple to query via a REST API.  Relies on the controlled terms from the dictionary for effective and accurate searching.  Links to data from GO and other sources.  Used by query tool components and by informatics data consumers.  The API can be extended using plug-ins contributed by the community.</dd>
                        <br/>
                        <dt>Query tool components</dt>
                        <dd>Both the 'web query' and 'desktop client' provide methods for novice and experienced users to browse and find the information they need.</dd>
                    </dl>

                </p>

                    <IMG style="margin: 25px auto 25px;" SRC="${resource(dir: 'images/bardHomepage', file: 'BARD_architecture.png')}" ALIGN="top">

                    <h3>
                        Technology Stack
                    </h3>

                    <p>
                        BARD has been developed to be as open source as possible, using commercial components only in limited cases where an open source solution did not meet our needs.  After the public launch, the BARD source code will be made available to the community for extension and re-use.
                    </p>

                    <IMG SRC="${resource(dir: 'images/bardHomepage', file: 'BARD_technology.png')}" ALIGN="bottom">


                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>



</body>
</html>
