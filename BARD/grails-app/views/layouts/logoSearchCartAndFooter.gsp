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
                url:"/BARD/ErrorHandling/handleJsErrors",
                async:true
            });
            return true;
        }
    </script>
    <r:layoutResources/>
    <ga:trackPageview />
    <style>
.logo {
    float: left;
    margin-top: 10px;
    margin-bottom: 20px;
    width: 280px;
    height: 85px;
    text-indent: -9999px;
    overflow: hidden;
    background: url('../images/bard_logo_small.png') no-repeat;
}

.logo a {
    display: block;
    height: 100%;
}

.social-networks {
    float: right;
    margin: -6px -12px 0 0;
    list-style: none;
}

.social-networks li {
    float: left;
    margin: 0 0 0 4px;
}

.social-networks a {
    display: block;
    width: 16px;
    height: 16px;
    text-indent: -9999px;
    overflow: hidden;
    background: url('../images/bardHomepage/sprite.png') no-repeat;
}

.social-networks .google {
    background-position: -18px 0;
}

.nav-panel {
    position: absolute;
    right: 0;
    top: 0;
}

.nav-panel .nav {
    float: right;
    display: inline-block;
    vertical-align: bottom;
    font-size: 13px;
    line-height: 16px;
}

.nav-panel .nav > li {
    margin: 0 0 0 5px;
}

.nav-panel .nav > li > a {
    position: relative;
    padding: 14px 15px;
    letter-spacing: 1px;
    text-transform: uppercase;
    color: #100d0d;
}

.nav-panel .nav > li:hover > a,
.nav-panel .nav > .active > a,
.nav-panel .nav > .active:hover > a,
.nav-panel .nav > .active > a:focus,
.nav-panel .nav li.dropdown.open > .dropdown-toggle,
.nav-panel .nav li.dropdown.active > .dropdown-toggle,
.nav-panel .nav li.dropdown.open.active > .dropdown-toggle {
    color: #38d9c1;
    -webkit-box-shadow: 10px 10px 5px -8px rgba(6, 42, 59, 0.1) inset, -10px 0 5px -8px rgba(6, 42, 59, 0.1) inset, 4px 1px 2px -4px rgba(0, 0, 0, 0.2), -4px 1px 2px -4px rgba(0, 0, 0, 0.2);
    box-shadow: 10px 10px 5px -8px rgba(6, 42, 59, 0.1) inset, -10px 0 5px -8px rgba(6, 42, 59, 0.1) inset, 4px 1px 2px -4px rgba(0, 0, 0, 0.2), -4px 1px 2px -4px rgba(0, 0, 0, 0.2);
    background: #fff;
}

.nav-panel .nav > li > .dropdown-toggle:after {
    content: '';
    display: inline-block;
    vertical-align: middle;
    width: 8px;
    height: 4px;
    margin: -1px 0 0 10px;
    background: url('../images/bardHomepage/sprite.png') no-repeat -46px -20px;
}

.nav-panel .nav > li:hover > .dropdown-toggle:after,
.nav-panel .nav li.dropdown.open > .dropdown-toggle:after,
.nav-panel .nav li.dropdown.active > .dropdown-toggle:after {
    content: ' ';
    background-position: -56px -20px;
}

.nav-panel .nav > li.open > .dropdown-toggle {
    position: relative;
    z-index: 1001;
    margin-bottom: -8px;
    padding-bottom: 22px;
}

.nav-panel .nav > li.open > .dropdown-toggle:before {
    content: '';
    position: absolute;
    right: -7px;
    bottom: -3px;
    width: 18px;
    height: 18px;
    background: url('../images/bardHomepage/sprite.png') no-repeat -320px 0;
}

.nav-panel .nav > li > .dropdown-menu {
    min-width: 197px;
    border: 0;
    -moz-border-radius: 0;
    -webkit-border-radius: 0;
    border-radius: 0;
    margin: 0;
    padding: 17px 0 24px;
    font-size: 13px;
    line-height: 16px;
    -webkit-box-shadow: 0 0 6px rgba(6, 42, 59, 0.2) inset, 0 4px 2px rgba(0, 0, 0, 0.2);
    box-shadow: 0 0 6px rgba(6, 42, 59, 0.2) inset, 0 4px 2px rgba(0, 0, 0, 0.2);
}

.nav-panel .nav > li > .dropdown-menu:before,
.nav-panel .nav > li > .dropdown-menu:after {
    display: none;
}
    .page-header {
        margin: 0 0 25px;
    }

    .page-header h1 small {
        margin: 0 0 0 74px;
    }
.qcart {
    display: inline;
}
    #footer{
    padding:0 0 3px;
    background:#2d2f32;
    }
    .footer-columns{
    padding:26px 0 33px;
    color:#fff;
    font-size:14px;
    line-height:18px;
    }
    .footer-columns a{color:#fff;}
    .footer-columns a:hover{color:#38d9c1;}
    .footer-columns h3{
    margin:0 0 13px;
    font-weight:normal;
    font-size:18px;
    line-height:22px;
    color:#0093d0;
    }
    .footer-columns ul{
    margin:0 0 20px;
    list-style:none;
    font-size:12px;
    line-height:16px;
    }
    .footer-columns ul li{padding:0 0 8px;}
    .footer-columns .by{
    padding:13px 0 0;
    text-align:right;
    line-height:22px;
    }
    .footer-info{
    padding:29px 0 33px;
    font-size:14px;
    line-height:20px;
    text-align:center;
    color:#fff;
    background:#21495c;
    }
    .footer-info ul{
    margin:0;
    list-style:none;
    }
    .footer-info ul li{display:inline;}
    .footer-info ul li:before{
    content:'·';
    margin:0 3px 0 0;
    }
    .footer-info ul li:first-child:before{display:none;}
    .footer-info a{color:#fff;}
    .footer-info a:hover{color:#38d9c1;}
     media queries


@media only screen and (max-width: 767px) {
        body {
            padding: 0;
        }
        .qcart {
            display: inline;
        }


        .container-fluid {
            padding-left: 32px;
            padding-right: 32px;
        }

        #header {
            margin: 0;
        }

        #header .social-networks {
            display: none;
        }

        .logo {
            margin: 0 0 0 -14px;
        }

        .nav-panel {
            margin: 0 -30px 0 -25px;
        }

        .login-nav {
            float: none;
            position: absolute;
            top: 0;
            right: 0;
            margin: 0;
        }

        .articles-gallery {
            display: none;
        }

        .footer-columns [class*="span"] {
            float: left;
            width: 33.3%;
        }

        .footer-columns .by {
            float: none;
            clear: both;
            width: auto;
            text-align: left;
            overflow: hidden;
        }

        .footer-columns .logo-by {
            float: right;
            margin: 0 0 0 20px;
        }

        .footer-columns p {
            overflow: hidden;
        }
    }

    @media only screen and (max-width: 700px) {
        #header .container-fluid {
            padding-top: 13px;
        }
        .qcart {
            display: inline;
        }


        .logo {
            margin: 0 0 3px -14px;
        }

        .nav-panel .nav > li > a {
            padding-top: 14px;
            padding-bottom: 11px;
            letter-spacing: normal;
            text-shadow: none;
        }

        .nav-panel .nav > li.open > .dropdown-toggle {
            margin-bottom: -8px;
            padding-bottom: 19px;
        }

        .nav-panel .nav > li > .dropdown-toggle:after {
            margin-left: 7px;
        }

        .login-nav {
            padding: 14px 21px 16px 17px;
        }
        .tabs-section {
            display: none;
        }

        .footer-info {
            padding: 18px 0 26px;
        }
    }

    @media only screen and (max-width: 479px) {
        .container-fluid {
            padding-left: 10px;
            padding-right: 10px;
        }
        .qcart {
            display: none;
        }
        .logo {
            margin: 0 0 3px;
            width: 104px;
            height: 31px;
            background-size: 100% 100%;
        }

        .nav-panel {
            margin: 0 -10px;
        }

        .nav-panel .nav {
            font-size: 10px;
            line-height: 14px;
        }

        .nav-panel .nav > li > a {
            padding: 5px;
        }

        .nav-panel .nav > li.open > .dropdown-toggle {
            margin-bottom: -8px;
            padding-bottom: 13px;
        }

        .nav-panel .nav > li > .dropdown-menu {
            padding: 10px 0;
            min-width: 150px;
            width: 150px;
        }

        .dropdown-menu > li {
            padding: 5px 10px;
        }

        .dropdown-menu > li > a {
            white-space: normal;
        }

        .login-nav {
            padding: 10px;
            font-size: 11px;
            line-height: 14px;
        }
        .footer-columns [class*="span"] {
            float: none;
            width: auto;
        }
    }
    </style>
</head>

<body>
<div class="container-fluid">

    %{--<div class="row-fluid header">--}%
    <header  class="navbar navbar-static-top" id="header">
    <div class="container-fluid">
        %{--<div class="span3">--}%
            %{--<a href="${createLink(controller: 'BardWebInterface', action: 'index')}">--}%
                %{--<img src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database"/>--}%
            %{--</a>--}%
        %{--</div>--}%

        <strong class="logo"><a href="#">BARD BioAssay Research Database</a></strong>

        <nav class="nav-panel">
        %{--<div class="span3">--}%
            <div class="center-aligned">
                <g:render template="/layouts/templates/loginStrip"/>
            </div>
            <div class="qcart">
            <g:render template="/layouts/templates/queryCart"/>
            </div>
        %{--</div>--}%
        </nav>
    </div>
    </header>
    %{--</div>--}%
    <g:render template="/layouts/templates/searchBox"/>


    <g:if test="${flash.message}">
        <div class="alert">
            <button class="close" data-dismiss="alert">×</button>
            ${flash.message}
        </div>
    </g:if>


    %{--<div class="span6">--}%
        %{--<g:render template="/layouts/templates/searchBox"/>--}%
    %{--</div>--}%



    <g:layoutBody/>

    <g:render template="/layouts/templates/footer"/>

</div>

<r:require modules="core,bootstrap,cart"/>
<r:layoutResources/>

</body>
</html>