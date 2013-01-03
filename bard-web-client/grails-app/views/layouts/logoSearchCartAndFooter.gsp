<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="BioAssay Research Database"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

    <g:layoutHead/>
    <r:require modules="core,bootstrap,cart"/>
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
    <r:layoutResources/>
    <ga:trackPageview />
</head>

<body>
<div class="container-fluid">

    <div class="row-fluid header">
        <div class="span3">
            <a href="${createLink(controller: 'BardWebInterface', action: 'index')}">
                <img src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database"/>
            </a>
        </div>

        <div class="span6">
            <g:render template="/layouts/templates/searchBox"/>
        </div>

        <div class="span3">
            <div class="center-aligned">
                <g:render template="/layouts/templates/loginStrip"/>
            </div>
            <g:render template="/layouts/templates/queryCart"/>
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
<r:layoutResources/>

</body>
</html>