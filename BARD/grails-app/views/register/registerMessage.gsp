<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name='layout' content='basic'/>
    <title>Registration Message</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="span2"></div>

            <div class="span8">
                <g:if test="${successMessage}">
                    <div class="alert alert-success">
                        ${successMessage}
                    </div>
                </g:if>
                <g:if test="${errorMessage}">
                    <div class="alert alert-error">
                        ${errorMessage}
                    </div>
                </g:if>
            </div>

            <div class="span2"></div>
        </div>
    </div>
</div>
</body>
</html>
