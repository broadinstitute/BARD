<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<g:render template="/layouts/templates/handleOldBrowsers" />
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="BioAssay Research Database"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

    <g:layoutHead/>
    <r:require modules="core,bootstrap,cart,bardHeaderFooter"/>
    <script type="text/javascript">

        //report error messages
        window.onerror = bardClientErrorHandler;
        //    Handle javascript errors
        function bardClientErrorHandler(message, url, line) {
            $.ajax({
                cache:false,
                type:"post",
                data:{error:message, url:url, line:line, browser:navigator.userAgent},
                url:"/BARD/ErrorHandling/handleJsErrors",
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


    <g:render template="/layouts/templates/searchBox"/>


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