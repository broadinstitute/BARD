<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    %{--<script type="text/javascript" src="${resource(dir: 'js/dojo-min/dojo', file: 'dojo.js')}"></script>--}%
    %{--<script type="text/javascript" src="${resource(dir: 'js/jsDraw', file: 'Scilligence.JSDraw2.js')}"></script>--}%
    %{--<script type="text/javascript" src="${resource(dir: 'js/jsDraw', file: 'license.js')}"></script>--}%
    <script type="text/javascript" src="${request.contextPath}/js/dojo-min/dojo/dojo.js"></script>
    <script type="text/javascript" src="${request.contextPath}/js/jsDraw/Scilligence.JSDraw2.js"></script>
    <script type="text/javascript" src="${request.contextPath}/js/jsDraw/license.js"></script>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="BioAssay Research Database"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

    <g:layoutHead/>
    <r:require modules="core,bootstrap,cart" />
    <r:layoutResources />
</head>
<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3 offset9">
            <g:render template="/layouts/templates/queryCart"/>
            <div class="center-aligned">
               <g:render template="/layouts/templates/loginStrip"/>
            </div>
        </div>
    </div>

    <g:if test="${flash.message}">
        <div class="alert">
            <button class="close" data-dismiss="alert">×</button>
            ${flash.message}
        </div>
    </g:if>

    <g:layoutBody/>

    <g:render template="/layouts/templates/footer"/>

</div>

<r:require modules="core,bootstrap,cart"/>
<r:layoutResources />

</body>
</html>