<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="cartAndFooter"/>
    <script type="text/javascript">

        //report error messages
        window.onerror = bardClientErrorHandler;
        //    Handle javascript errors
        function bardClientErrorHandler(message, url, line) {
            $.ajax({
                cache:false,
                type:"post",
                data:{error:message, url:url, line:line, browser:navigator.userAgent},
                url:"/bardwebclient/ErrorHandling/handleJsErrors",
                async:true
            });
            return true;
        }
    </script>
    <ga:trackPageview />
</head>

<body>
<div class="row-fluid">
    <div class="span6 offset3">
        <a href="${createLink(controller:'BardWebInterface',action:'index')}">
            <img src="${resource(dir: 'images', file: 'bard_logo_lg.jpg')}" alt="BioAssay Research Database" />
        </a>
    </div>
</div>
<div class="row-fluid">
    <div class="span6 offset3">
        <g:render template="/layouts/templates/searchBox"/>
    </div>
</div>
<div class="row-fluid">
    <div class="span6 offset3 well well-small">
        <p id="bardisfree">BARD is a free, public platform funded by the <a href="http://mli.nih.gov">NIH Molecular Libraries Program</a> that enables
        scientists to store and analyze well-annotated small-molecule bioassay results.  You can use
        BARD to search across X projects, Y experiments, Z assay definitions, N compounds, and M results.
        For further analytical power, <a href="http://bard.nih.gov">download our desktop client</a> or access the
            <a href="http://bard.nih.gov/api">REST API</a> directly.  You can
        also <a href="http://bard.nih.gov">contribute</a> your own bioassay data, <a href="http://bard.nih.gov">install</a>
            a local copy of BARD, or extend the system by writing a <a href="http://bard.nih.gov">plug-in</a>.
        </p>
        <p>Example search queries:
            <ul class="examples">
                <li><g:link controller="bardWebInterface" action="search" params='[searchString:"\"dna repair\""]'>"dna repair"</g:link> - search all documents and annotations attached to assay definitions, compounds, and projects for the phrase "dna repair"</li>
                <li><g:link controller="bardWebInterface" action="search" params='[searchString:"gobp_term:\"DNA repair\""]'>gobp_term:"DNA repair"</g:link> - search for items specifically annotated with the GO Biological Process term DNA Repair (or any of its descendant terms)</li>
                <li><g:link controller="bardWebInterface" action="search" params="[searchString:'similarity:O=C(Nc1c(cccc1C)C)CN(CC)CC']">similarity:O=C(Nc1c(cccc1C)C)CN(CC)CC</g:link> - search for compounds similar to lidocaine (90% tanimoto similarity)</li>
            </ul>
        </p>
    </div>
</div>
</body>
</html>