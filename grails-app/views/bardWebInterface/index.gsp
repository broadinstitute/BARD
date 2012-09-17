<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="cartAndFooter"/>
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
</body>
</html>