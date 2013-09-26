%{--<r:require modules="autocomplete, structureSearch"/>--}%
<script src="../js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.accentFolding.js"></script>
<script src="../js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.html.js"></script>
<r:require modules="autocomplete"/>
<noscript>
    <a href="http://www.enable-javascript.com/" target="javascript">
        <img src="${resource(dir: 'images', file: 'enable_js.png')}"
             alt="Please enable JavaScript to access the full functionality of this site."/>
    </a>
</noscript>


<div class="search-panel">
    <div class="container-fluid">

        <strong class="logo"><a href="${createLink(controller: 'BardWebInterface', action: 'index')}">BARD BioAssay Research Database</a></strong>

        <div class="search-block">
            <g:render template="/layouts/templates/searchBlock"/>
        </div>
    </div>
    <nav class="nav-panel">
        <div class="center-aligned">
            <g:render template="/layouts/templates/loginStrip"/>
        </div>
        <div class="visible-desktop">
            <g:render template="/layouts/templates/queryCart"/>
        </div>
    </nav>
</div>
<g:render template="/layouts/templates/IdSearchBox"/>
