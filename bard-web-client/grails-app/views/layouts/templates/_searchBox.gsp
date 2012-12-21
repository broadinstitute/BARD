%{--//Wrap this in a grails if statement. Only run on production--}%
%{--Handle DEV--}%
%{--<g:if env="oracledev">--}%
    %{--<script type="text/javascript">--}%
        %{--//Google Analytics--}%
        %{--var _gaq = _gaq || [];--}%
        %{--_gaq.push(['_setAccount', 'UA-37197267-1']);--}%
        %{--_gaq.push(['_trackPageview']);--}%

        %{--(function () {--}%
            %{--var ga = document.createElement('script');--}%
            %{--ga.type = 'text/javascript';--}%
            %{--ga.async = true;--}%
            %{--ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';--}%
            %{--var s = document.getElementsByTagName('script')[0];--}%
            %{--s.parentNode.insertBefore(ga, s);--}%
        %{--})();--}%

    %{--</script>--}%
%{--</g:if>--}%
%{--Handle QA--}%
%{--<g:if env="oracleqa">--}%
    %{--<script type="text/javascript">--}%
        %{--//Google Analytics--}%
        %{--var _gaq = _gaq || [];--}%
        %{--_gaq.push(['_setAccount', 'UA-37181930-1']);--}%
        %{--_gaq.push(['_trackPageview']);--}%

        %{--(function () {--}%
            %{--var ga = document.createElement('script');--}%
            %{--ga.type = 'text/javascript';--}%
            %{--ga.async = true;--}%
            %{--ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';--}%
            %{--var s = document.getElementsByTagName('script')[0];--}%
            %{--s.parentNode.insertBefore(ga, s);--}%
        %{--})();--}%

    %{--</script>--}%
%{--</g:if>--}%
<r:require module="autocomplete"/>
<noscript>
    <a href="http://www.enable-javascript.com/" target="_blank">
        <img src="${resource(dir: 'images', file: 'enable_js.png')}"
             alt="Please enable JavaScript to access the full functionality of this site."/>
    </a>
</noscript>
<script type="text/javascript">
    blah;
</script>
<g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm">
    <div class="row-fluid" style="margin-top: 15px;">
        <div class="input-append">
            <g:textField id="searchString" name="searchString" value="${flash.searchString ?: params?.searchString}"
                         class="span10"/>
            <g:submitButton name="search" value="Search" class="btn btn-primary span2" id="searchButton"/>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span10">
            <div class="pull-right"><a data-toggle="modal" href="#modalDiv">
                <img src="${resource(dir: 'images', file: 'structureEditIcon.png')}" alt="Draw or paste a structure"
                     title="Draw or paste a structure"/> Draw or paste a structure</a> or <a
                    data-toggle="modal" href="#idModalDiv">list of IDs for search</a></div>
        </div>
    </div>
</g:form>

<g:render template="/layouts/templates/structureSearchBox"/>
<g:render template="/layouts/templates/IdSearchBox"/>

