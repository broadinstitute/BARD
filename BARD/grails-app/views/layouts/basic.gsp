<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <g:layoutHead/>

    <title>BARD: Catalog of Assay Protocols</title>
    <r:external uri="/css/layout.css"/>
    <r:external uri="/css/table.css"/>
    <r:external uri="/css/bardHomepage/BardHeaderFooter.css"/>
    <r:require modules="autocomplete"/>
    <r:require module="cart"/>
    <r:require module="idSearch"/>
    <%@page defaultCodec="none"%>
    <%@ page import="bardqueryapi.IDSearchType" %>
    <r:layoutResources/>

%{--<style>--}%
%{--.search-panel{--}%
    %{--position:relative;--}%
    %{--z-index:1;--}%
    %{--padding:23px 0 20px;--}%
    %{--text-align:center;--}%
    %{--color:#fff;--}%
    %{--background:#062a3b url('../images/bardHomepage/bg-search-panel.png') no-repeat;--}%
    %{--background-size:586px 100%;--}%
%{--}--}%
%{--.search-panel a:focus{--}%
    %{--text-decoration:none;--}%
    %{--color:#0093d0;--}%
%{--}--}%
%{--.search-panel a:hover{--}%
    %{--text-decoration:none;--}%
    %{--color:#38d9c1;--}%
%{--}--}%
%{--.search-panel a:active{background-color:transparent;}--}%
%{--input,--}%
%{--textarea,--}%
%{--select{--}%
    %{--font:100% "Lato", Arial, Helvetica, sans-serif;--}%
    %{--vertical-align:middle;--}%
    %{--color:#000;--}%
%{--}--}%
%{--.search-panel a:active{background-color:transparent;}--}%
%{--.search-panel input,--}%
%{--textarea,--}%
%{--select{--}%
    %{--font:100% "Lato", Arial, Helvetica, sans-serif;--}%
    %{--vertical-align:middle;--}%
    %{--color:#000;--}%
%{--}--}%
%{--.search-panel input::-webkit-input-placeholder{color:#000;}--}%
%{--.search-panel input:-moz-placeholder{color:#000;}--}%
%{--input::-moz-placeholder{--}%
    %{--color:#000;--}%
    %{--opacity:1;--}%
%{--}--}%
%{--input:-ms-input-placeholder{color:#000;}--}%
%{--.search-panel .head-holder{--}%
    %{--display:table;--}%
    %{--text-align:left;--}%
    %{--margin:0 auto 35px;--}%
%{--}--}%
%{--.search-panel .head-holder h2{--}%
    %{--display:table-cell;--}%
    %{--vertical-align:top;--}%
    %{--white-space:nowrap;--}%
    %{--margin:0;--}%
    %{--padding:0 22px 0 0;--}%
    %{--font:300 32px/36px "Lato", Arial, Helvetica, sans-serif;--}%
    %{--letter-spacing:5px;--}%
    %{--color:#fff;--}%
    %{--text-transform:uppercase;--}%
%{--}--}%
%{--.search-panel .head-holder p{--}%
    %{--display:table-cell;--}%
    %{--vertical-align:bottom;--}%
    %{--margin:0;--}%
    %{--padding:3px 0 0;--}%
    %{--font-size:16px;--}%
    %{--line-height:24px;--}%
%{--}--}%
%{--.search-block{--}%
    %{--max-width:660px;--}%
    %{--margin:0 auto;--}%
    %{--padding:0 0 11px;--}%
%{--}--}%
%{--.search-form{--}%
    %{--margin:0 0 26px;--}%
    %{--padding:8px 9px 10px;--}%
    %{--border:1px solid rgba(244, 244, 244, 0.2);--}%
    %{--background:rgba(228, 228, 228, 0.2);--}%
%{--}--}%
%{--.search-field{--}%
    %{--display:table;--}%
    %{--width:100%;--}%
%{--}--}%
%{--.search-field .text-field,--}%
%{--.search-field .btn-field{--}%
    %{--display:table-cell;--}%
    %{--vertical-align:top;--}%
%{--}--}%
%{--.search-form input[type="text"],--}%
%{--.search-form input[type="search"]{--}%
    %{---moz-box-sizing:border-box;--}%
    %{---webkit-box-sizing:border-box;--}%
    %{--box-sizing:border-box;--}%
    %{--width:100%;--}%
    %{--height:39px;--}%
    %{--margin:0;--}%
    %{--padding:9px 11px 7px;--}%
    %{--border:3px solid #fff;--}%
    %{---moz-border-radius:0;--}%
    %{---webkit-border-radius:0;--}%
    %{--border-radius:0;--}%
    %{---moz-box-shadow:none;--}%
    %{---webkit-box-shadow:none;--}%
    %{--box-shadow:none;--}%
    %{--font-size:14px;--}%
    %{--line-height:17px;--}%
    %{--background:#e4e4e4;--}%
%{--}--}%
%{--.search-field .btn-field{--}%
    %{--width:1px;--}%
    %{--white-space:nowrap;--}%
    %{--padding:0 0 0 2px;--}%
%{--}--}%
%{--.search-form .btn{line-height:39px;}--}%
%{--.search-block .links-holder{--}%
    %{--padding:0 21px 0 10px;--}%
    %{--font-weight:bold;--}%
    %{--font-size:15px;--}%
    %{--line-height:20px;--}%
    %{--letter-spacing:1px;--}%
    %{--overflow:hidden;--}%
%{--}--}%
%{--.search-block .links-holder a{--}%
    %{--position:relative;--}%
    %{--float:left;--}%
    %{--padding:0 15px 0 0;--}%
%{--}--}%
%{--.search-block .links-holder a:first-child{float:right;}--}%
%{--.search-block .links-holder a:after{--}%
    %{--content:'';--}%
    %{--position:absolute;--}%
    %{--top:50%;--}%
    %{--right:0;--}%
    %{--width:6px;--}%
    %{--height:8px;--}%
    %{--margin:-2px 0 0;--}%
    %{--background:url('../../images/bardHomepage/sprite.png') no-repeat -38px 0;--}%
%{--}--}%
%{--.search-block .links-holder a:hover:after{--}%
    %{--content:' ';--}%
    %{--background-position:-38px -12px;--}%
%{--}--}%
%{--.search-block .links-holder .download-link{padding-left:39px;}--}%
%{--.search-block .links-holder .download-link:before{--}%
    %{--content:'';--}%
    %{--position:absolute;--}%
    %{--top:50%;--}%
    %{--left:0;--}%
    %{--width:25px;--}%
    %{--height:18px;--}%
    %{--margin:-9px 0 0;--}%
    %{--background:url('../../images/bardHomepage/sprite.png') no-repeat -46px 0;--}%
%{--}--}%
%{--.search-block .links-holder .download-link:hover:before{--}%
    %{--content:' ';--}%
    %{--background-position:-74px 0;--}%
%{--}--}%
%{--.search-panel{padding:26px 0 20px;}--}%
%{--.search-panel .head-holder{margin:0 0 17px;}--}%
%{--.search-panel .head-holder h2{--}%
    %{--padding:0 41px 0 0;--}%
    %{--font-size:28px;--}%
    %{--line-height:34px;--}%
%{--}--}%
%{--.search-panel .head-holder p{padding:3px 30px 0 0;}--}%
%{--.search-panel {--}%
    %{--margin:0;--}%
    %{--font:16px/24px "Lato", Arial, Helvetica, sans-serif;--}%
    %{--color:#2d2f32;--}%
    %{--background: rgb(111,179,206);--}%
    %{--background: url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/Pgo8c3ZnIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgdmlld0JveD0iMCAwIDEgMSIgcHJlc2VydmVBc3BlY3RSYXRpbz0ibm9uZSI+CiAgPGxpbmVhckdyYWRpZW50IGlkPSJncmFkLXVjZ2ctZ2VuZXJhdGVkIiBncmFkaWVudFVuaXRzPSJ1c2VyU3BhY2VPblVzZSIgeDE9IjAlIiB5MT0iMCUiIHgyPSIwJSIgeTI9IjEwMCUiPgogICAgPHN0b3Agb2Zmc2V0PSIwJSIgc3RvcC1jb2xvcj0iIzZmYjNjZSIgc3RvcC1vcGFjaXR5PSIxIi8+CiAgICA8c3RvcCBvZmZzZXQ9IjY0JSIgc3RvcC1jb2xvcj0iI2VkZjRmOSIgc3RvcC1vcGFjaXR5PSIxIi8+CiAgICA8c3RvcCBvZmZzZXQ9IjEwMCUiIHN0b3AtY29sb3I9IiNmY2ZjZmMiIHN0b3Atb3BhY2l0eT0iMSIvPgogIDwvbGluZWFyR3JhZGllbnQ+CiAgPHJlY3QgeD0iMCIgeT0iMCIgd2lkdGg9IjEiIGhlaWdodD0iMSIgZmlsbD0idXJsKCNncmFkLXVjZ2ctZ2VuZXJhdGVkKSIgLz4KPC9zdmc+);--}%
    %{--background: -moz-linear-gradient(top,  rgba(111,179,206,1) 0%, rgba(237,244,249,1) 64%, rgba(252,252,252,1) 100%);--}%
    %{--background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,rgba(111,179,206,1)), color-stop(64%,rgba(237,244,249,1)), color-stop(100%,rgba(252,252,252,1)));--}%
    %{--background: -webkit-linear-gradient(top,  rgba(111,179,206,1) 0%,rgba(237,244,249,1) 64%,rgba(252,252,252,1) 100%);--}%
    %{--background: -o-linear-gradient(top,  rgba(111,179,206,1) 0%,rgba(237,244,249,1) 64%,rgba(252,252,252,1) 100%);--}%
    %{--background: -ms-linear-gradient(top,  rgba(111,179,206,1) 0%,rgba(237,244,249,1) 64%,rgba(252,252,252,1) 100%);--}%
    %{--background: linear-gradient(to bottom,  rgba(111,179,206,1) 0%,rgba(237,244,249,1) 64%,rgba(252,252,252,1) 100%);--}%
    %{--filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#6fb3ce', endColorstr='#fcfcfc',GradientType=0 );--}%

    %{---webkit-text-size-adjust:100%;--}%
    %{---ms-text-size-adjust:none;--}%
%{--}--}%
%{--.search-panel{padding:32px 0 1px;}--}%
%{--.search-panel .head-holder{--}%
    %{--display:block;--}%
    %{--text-align:center;--}%
%{--}--}%
%{--.search-panel .head-holder h2{--}%
    %{--display:block;--}%
    %{--margin:0 0 26px;--}%
    %{--padding:0;--}%
%{--}--}%
%{--.search-panel .head-holder p{--}%
    %{--display:block;--}%
    %{--padding:0;--}%
    %{--margin:0 0 27px;--}%
%{--}--}%
%{--.search-form input[type="text"],--}%
%{--.search-form input[type="search"]{--}%
    %{--padding:9px 11px;--}%
    %{--font-size:12px;--}%
    %{--line-height:15px;--}%
%{--}--}%
%{--.search-block .links-holder{text-align:center;}--}%
%{--.search-block .links-holder a{--}%
    %{--float:none !important;--}%
    %{--display:inline-block;--}%
    %{--vertical-align:top;--}%
    %{--margin:0 0 17px;--}%
%{--}--}%
%{--.search-form{margin:0 0 19px;}--}%
%{--.search-form .btn{--}%
    %{--padding-left:15px;--}%
    %{--padding-right:15px;--}%
%{--}--}%

%{--.logo {--}%
    %{--float: left;--}%
    %{--margin-top: 10px;--}%
    %{--margin-bottom: 20px;--}%
    %{--width: 280px;--}%
    %{--height: 85px;--}%
    %{--text-indent: -9999px;--}%
    %{--overflow: hidden;--}%
    %{--background: url('../images/bard_logo_small.png') no-repeat;--}%
%{--}--}%

%{--.logo a {--}%
    %{--display: block;--}%
    %{--height: 100%;--}%
%{--}--}%

%{--.social-networks {--}%
    %{--float: right;--}%
    %{--margin: -6px -12px 0 0;--}%
    %{--list-style: none;--}%
%{--}--}%

%{--.social-networks li {--}%
    %{--float: left;--}%
    %{--margin: 0 0 0 4px;--}%
%{--}--}%

%{--.social-networks a {--}%
    %{--display: block;--}%
    %{--width: 16px;--}%
    %{--height: 16px;--}%
    %{--text-indent: -9999px;--}%
    %{--overflow: hidden;--}%
    %{--background: url('../images/bardHomepage/sprite.png') no-repeat;--}%
%{--}--}%

%{--.social-networks .google {--}%
    %{--background-position: -18px 0;--}%
%{--}--}%

%{--.nav-panel {--}%
    %{--position: absolute;--}%
    %{--right: 0;--}%
    %{--top: 0;--}%
%{--}--}%

%{--.nav-panel .nav {--}%
    %{--float: right;--}%
    %{--display: inline-block;--}%
    %{--vertical-align: bottom;--}%
    %{--font-size: 13px;--}%
    %{--line-height: 16px;--}%
%{--}--}%

%{--.nav-panel .nav > li {--}%
    %{--margin: 0 0 0 5px;--}%
%{--}--}%

%{--.nav-panel .nav > li > a {--}%
    %{--position: relative;--}%
    %{--padding: 14px 15px;--}%
    %{--letter-spacing: 1px;--}%
    %{--text-transform: uppercase;--}%
    %{--color: #100d0d;--}%
%{--}--}%

%{--.nav-panel .nav > li:hover > a,--}%
%{--.nav-panel .nav > .active > a,--}%
%{--.nav-panel .nav > .active:hover > a,--}%
%{--.nav-panel .nav > .active > a:focus,--}%
%{--.nav-panel .nav li.dropdown.open > .dropdown-toggle,--}%
%{--.nav-panel .nav li.dropdown.active > .dropdown-toggle,--}%
%{--.nav-panel .nav li.dropdown.open.active > .dropdown-toggle {--}%
    %{--color: #38d9c1;--}%
    %{---webkit-box-shadow: 10px 10px 5px -8px rgba(6, 42, 59, 0.1) inset, -10px 0 5px -8px rgba(6, 42, 59, 0.1) inset, 4px 1px 2px -4px rgba(0, 0, 0, 0.2), -4px 1px 2px -4px rgba(0, 0, 0, 0.2);--}%
    %{--box-shadow: 10px 10px 5px -8px rgba(6, 42, 59, 0.1) inset, -10px 0 5px -8px rgba(6, 42, 59, 0.1) inset, 4px 1px 2px -4px rgba(0, 0, 0, 0.2), -4px 1px 2px -4px rgba(0, 0, 0, 0.2);--}%
    %{--background: #fff;--}%
%{--}--}%

%{--.nav-panel .nav > li > .dropdown-toggle:after {--}%
    %{--content: '';--}%
    %{--display: inline-block;--}%
    %{--vertical-align: middle;--}%
    %{--width: 8px;--}%
    %{--height: 4px;--}%
    %{--margin: -1px 0 0 10px;--}%
    %{--background: url('../images/bardHomepage/sprite.png') no-repeat -46px -20px;--}%
%{--}--}%

%{--.nav-panel .nav > li:hover > .dropdown-toggle:after,--}%
%{--.nav-panel .nav li.dropdown.open > .dropdown-toggle:after,--}%
%{--.nav-panel .nav li.dropdown.active > .dropdown-toggle:after {--}%
    %{--content: ' ';--}%
    %{--background-position: -56px -20px;--}%
%{--}--}%

%{--.nav-panel .nav > li.open > .dropdown-toggle {--}%
    %{--position: relative;--}%
    %{--z-index: 1001;--}%
    %{--margin-bottom: -8px;--}%
    %{--padding-bottom: 22px;--}%
%{--}--}%

%{--.nav-panel .nav > li.open > .dropdown-toggle:before {--}%
    %{--content: '';--}%
    %{--position: absolute;--}%
    %{--right: -7px;--}%
    %{--bottom: -3px;--}%
    %{--width: 18px;--}%
    %{--height: 18px;--}%
    %{--background: url('../images/bardHomepage/sprite.png') no-repeat -320px 0;--}%
%{--}--}%

%{--.nav-panel .nav > li > .dropdown-menu {--}%
    %{--min-width: 197px;--}%
    %{--border: 0;--}%
    %{---moz-border-radius: 0;--}%
    %{---webkit-border-radius: 0;--}%
    %{--border-radius: 0;--}%
    %{--margin: 0;--}%
    %{--padding: 17px 0 24px;--}%
    %{--font-size: 13px;--}%
    %{--line-height: 16px;--}%
    %{---webkit-box-shadow: 0 0 6px rgba(6, 42, 59, 0.2) inset, 0 4px 2px rgba(0, 0, 0, 0.2);--}%
    %{--box-shadow: 0 0 6px rgba(6, 42, 59, 0.2) inset, 0 4px 2px rgba(0, 0, 0, 0.2);--}%
%{--}--}%

%{--.nav-panel .nav > li > .dropdown-menu:before,--}%
%{--.nav-panel .nav > li > .dropdown-menu:after {--}%
    %{--display: none;--}%
%{--}--}%
%{--.page-header {--}%
    %{--margin: 0 0 25px;--}%
%{--}--}%

%{--.page-header h1 small {--}%
    %{--margin: 0 0 0 74px;--}%
%{--}--}%
%{--.qcart {--}%
    %{--display: inline;--}%
%{--}--}%
%{--#footer{--}%
    %{--padding:0 0 3px;--}%
    %{--background:#2d2f32;--}%
%{--}--}%
%{--.footer-columns{--}%
    %{--padding:26px 0 33px;--}%
    %{--color:#fff;--}%
    %{--font-size:14px;--}%
    %{--line-height:18px;--}%
%{--}--}%
%{--.footer-columns a{color:#fff;}--}%
%{--.footer-columns a:hover{color:#38d9c1;}--}%
%{--.footer-columns h3{--}%
    %{--margin:0 0 13px;--}%
    %{--font-weight:normal;--}%
    %{--font-size:18px;--}%
    %{--line-height:22px;--}%
    %{--color:#0093d0;--}%
%{--}--}%
%{--.footer-columns ul{--}%
    %{--margin:0 0 20px;--}%
    %{--list-style:none;--}%
    %{--font-size:12px;--}%
    %{--line-height:16px;--}%
%{--}--}%
%{--.footer-columns ul li{padding:0 0 8px;}--}%
%{--.footer-columns .by{--}%
    %{--padding:13px 0 0;--}%
    %{--text-align:right;--}%
    %{--line-height:22px;--}%
%{--}--}%
%{--.footer-info{--}%
    %{--padding:29px 0 33px;--}%
    %{--font-size:14px;--}%
    %{--line-height:20px;--}%
    %{--text-align:center;--}%
    %{--color:#fff;--}%
    %{--background:#21495c;--}%
%{--}--}%
%{--.footer-info ul{--}%
    %{--margin:0;--}%
    %{--list-style:none;--}%
%{--}--}%
%{--.footer-info ul li{display:inline;}--}%
%{--.footer-info ul li:before{--}%
    %{--content:'·';--}%
    %{--margin:0 3px 0 0;--}%
%{--}--}%
%{--.footer-info ul li:first-child:before{display:none;}--}%
%{--.footer-info a{color:#fff;}--}%
%{--.footer-info a:hover{color:#38d9c1;}--}%

%{--@media only screen and (max-width: 767px) {--}%
    %{--body {--}%
        %{--padding: 0;--}%
    %{--}--}%
    %{--.qcart {--}%
        %{--display: inline;--}%
    %{--}--}%


    %{--.container-fluid {--}%
        %{--padding-left: 32px;--}%
        %{--padding-right: 32px;--}%
    %{--}--}%

    %{--#header {--}%
        %{--margin: 0;--}%
    %{--}--}%

    %{--#header .social-networks {--}%
        %{--display: none;--}%
    %{--}--}%

    %{--.logo {--}%
        %{--margin: 0 0 0 -14px;--}%
    %{--}--}%

    %{--.nav-panel {--}%
        %{--margin: 0 -30px 0 -25px;--}%
    %{--}--}%

    %{--.login-nav {--}%
        %{--float: none;--}%
        %{--position: absolute;--}%
        %{--top: 0;--}%
        %{--right: 0;--}%
        %{--margin: 0;--}%
    %{--}--}%

    %{--.articles-gallery {--}%
        %{--display: none;--}%
    %{--}--}%

    %{--.footer-columns [class*="span"] {--}%
        %{--float: left;--}%
        %{--width: 33.3%;--}%
    %{--}--}%

    %{--.footer-columns .by {--}%
        %{--float: none;--}%
        %{--clear: both;--}%
        %{--width: auto;--}%
        %{--text-align: left;--}%
        %{--overflow: hidden;--}%
    %{--}--}%

    %{--.footer-columns .logo-by {--}%
        %{--float: right;--}%
        %{--margin: 0 0 0 20px;--}%
    %{--}--}%

    %{--.footer-columns p {--}%
        %{--overflow: hidden;--}%
    %{--}--}%
%{--}--}%

%{--@media only screen and (max-width: 700px) {--}%
    %{--#header .container-fluid {--}%
        %{--padding-top: 13px;--}%
    %{--}--}%
    %{--.qcart {--}%
        %{--display: inline;--}%
    %{--}--}%


    %{--.logo {--}%
        %{--margin: 0 0 3px -14px;--}%
    %{--}--}%

    %{--.nav-panel .nav > li > a {--}%
        %{--padding-top: 14px;--}%
        %{--padding-bottom: 11px;--}%
        %{--letter-spacing: normal;--}%
        %{--text-shadow: none;--}%
    %{--}--}%

    %{--.nav-panel .nav > li.open > .dropdown-toggle {--}%
        %{--margin-bottom: -8px;--}%
        %{--padding-bottom: 19px;--}%
    %{--}--}%

    %{--.nav-panel .nav > li > .dropdown-toggle:after {--}%
        %{--margin-left: 7px;--}%
    %{--}--}%

    %{--.login-nav {--}%
        %{--padding: 14px 21px 16px 17px;--}%
    %{--}--}%
    %{--.tabs-section {--}%
        %{--display: none;--}%
    %{--}--}%

    %{--.footer-info {--}%
        %{--padding: 18px 0 26px;--}%
    %{--}--}%
%{--}--}%

%{--@media only screen and (max-width: 479px) {--}%
    %{--.container-fluid {--}%
        %{--padding-left: 10px;--}%
        %{--padding-right: 10px;--}%
    %{--}--}%
    %{--.qcart {--}%
        %{--display: none;--}%
    %{--}--}%
    %{--.logo {--}%
        %{--margin: 0 0 3px;--}%
        %{--width: 104px;--}%
        %{--height: 31px;--}%
        %{--background-size: 100% 100%;--}%
    %{--}--}%

    %{--.nav-panel {--}%
        %{--margin: 0 -10px;--}%
    %{--}--}%

    %{--.nav-panel .nav {--}%
        %{--font-size: 10px;--}%
        %{--line-height: 14px;--}%
    %{--}--}%

    %{--.nav-panel .nav > li > a {--}%
        %{--padding: 5px;--}%
    %{--}--}%

    %{--.nav-panel .nav > li.open > .dropdown-toggle {--}%
        %{--margin-bottom: -8px;--}%
        %{--padding-bottom: 13px;--}%
    %{--}--}%

    %{--.nav-panel .nav > li > .dropdown-menu {--}%
        %{--padding: 10px 0;--}%
        %{--min-width: 150px;--}%
        %{--width: 150px;--}%
    %{--}--}%

    %{--.dropdown-menu > li {--}%
        %{--padding: 5px 10px;--}%
    %{--}--}%

    %{--.dropdown-menu > li > a {--}%
        %{--white-space: normal;--}%
    %{--}--}%

    %{--.login-nav {--}%
        %{--padding: 10px;--}%
        %{--font-size: 11px;--}%
        %{--line-height: 14px;--}%
    %{--}--}%
    %{--.footer-columns [class*="span"] {--}%
        %{--float: none;--}%
        %{--width: auto;--}%
    %{--}--}%
%{--}--}%


%{--</style>--}%
</head>

<body>

<script src="../js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.accentFolding.js"></script>
<script src="../js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.html.js"></script>

<noscript>
    <a href="http://www.enable-javascript.com/" target="javascript">
        <img src="${resource(dir: 'images', file: 'enable_js.png')}"
             alt="Please enable JavaScript to access the full functionality of this site."/>
    </a>
</noscript>

<div class="container-fluid">

    <header  class="navbar navbar-static-top" id="header">
        <div class="container-fluid">
            %{--switch eventally to <img src="${resource(dir: 'images', file: 'bard_logo_small.png')}" alt="BioAssay Research Database"/>--}%
            <strong class="logo"><a href="${createLink(controller: 'BardWebInterface', action: 'index')}">BARD BioAssay Research Database</a></strong>

            <nav class="nav-panel">
                <div class="center-aligned">
                    <div id="login-form">
                        <sec:ifLoggedIn>

                            <g:form name="logoutForm" controller="bardLogout">
                                Logged in as: <span
                                    style="font-weight: bold;"><sec:username/></span>&nbsp;&nbsp;
                                <button type="submit" class="btn btn-mini" id="logoutButton">Logout</button>
                            </g:form>
                        </sec:ifLoggedIn>
                        <sec:ifNotLoggedIn>
                            <g:form name="loginForm" controller="bardLogin">
                                Not logged in&nbsp;&nbsp;
                                <button type="submit" class="btn btn-mini">Login</button>
                            </g:form>
                            OR
                            <a class="btn btn-mini" id='signin'>Sign in with your Email</a>
                        </sec:ifNotLoggedIn>
                    </div>

                </div>
                <div class="qcart">


                    <div class="well well-small">
                        <g:if test="${flash.searchString}">
                            <g:include controller="queryCart" action="refreshSummaryView" params="[searchString: flash.searchString]"/>
                        </g:if>
                        <g:elseif test="${params?.searchString}">
                            <g:include controller="queryCart" action="refreshSummaryView" params="[searchString: params.searchString]"/>
                        </g:elseif>
                        <g:else>
                            <g:include controller="queryCart" action="refreshSummaryView"/>
                        </g:else>
                    </div>

                    <div class="panel" style="z-index: 10">
                        <a class="trigger" href="#">Click to hide query cart</a>
                        <g:if test="${flash.searchString}">
                            <g:include controller="queryCart" action="refreshDetailsView" params="[searchString: flash.searchString]"/>
                        </g:if>
                        <g:elseif test="${params?.searchString}">
                            <g:include controller="queryCart" action="refreshDetailsView" params="[searchString: params.searchString]"/>
                        </g:elseif>
                        <g:else>
                            <g:include controller="queryCart" action="refreshDetailsView"/>
                        </g:else>
                    </div>

                </div>
            </nav>
        </div>
    </header>


    <div class="search-panel">
        <div class="container-fluid">
            <div class="search-block">
            %{--<g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm" class="form-inline">--}%
                <g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm" class="search-form">
                    <div class="row-fluid" style="margin-top: 15px;">
                        <div class="search-field input-append">
                            <div class="text-field">
                                <g:if test="${flash?.searchString}">
                                    <g:textField id="searchString" name="searchString" value="${flash.searchString}"/>
                                </g:if>
                                <g:elseif test="${params?.searchString}">
                                    <g:textField id="searchString" name="searchString" value="${params?.searchString}"/>
                                </g:elseif>
                                <g:else>
                                    <g:textField id="searchString" name="searchString" value=""/>
                                </g:else>
                            </div>

                            <div class="btn-field">
                                <button type="submit" name="search" class="btn btn-primary"
                                        id="searchButton">Search</button>
                            </div>
                        </div>
                    </div>

                    <div class="row-fluid">
                        <div class="span10">
                            %{--<r:script> $(document).on('click', '#structureSearchLink', showJSDrawEditor)</r:script>--}%
                            <div class="pull-right"><g:link controller="bardWebInterface" action="jsDrawEditor">
                                <img src="${resource(dir: 'images', file: 'structureEditIcon.png')}"
                                     alt="Draw or paste a structure"
                                     title="Draw or paste a structure"/> Draw or paste a structure</g:link> or <a
                                    data-toggle="modal" href="#idModalDiv">list of IDs for search</a></div>
                        </div>
                    </div>
                </g:form>
            </div>
        </div>
    </div>



    <div class="modal hide" id="idModalDiv">
        <div class="modal-header">
            <a class="close" data-dismiss="modal">×</a>

            <h3>Enter a Comma separated list of IDs</h3>
        </div>

        <div class="modal-body">
            <textarea class="field span12" id="idSearchString" name="idSearchString" rows="15"></textarea>
        </div>

        <div class="modal-footer">
            <g:form name="idSearchForm" class="form-inline">
                <div>
                    <g:radioGroup name="idSearchType"
                                  values="${IDSearchType.values()}"
                                  value="${IDSearchType.ALL}"
                                  labels="${IDSearchType.values().label}">
                        <label class="radio inline">
                            ${it.radio} ${it.label}
                        </label>
                    </g:radioGroup>
                </div>

                <br>

                <div>
                    <a href="#" class="btn" data-dismiss="modal" id="closeButton2">Close</a>
                    <a href="#" id="idSearchButton" class="btn btn-primary" data-dismiss="modal">Search</a>
                </div>
            </g:form>
        </div>

    </div>
    <div class="modal hide" id="idModalDiv">
        <div class="modal-header">
            <a class="close" data-dismiss="modal">×</a>

            <h3>Enter a Comma separated list of IDs</h3>
        </div>

        <div class="modal-body">
            <textarea class="field span12" id="idSearchString" name="idSearchString" rows="15"></textarea>
        </div>

        <div class="modal-footer">
            <g:form name="idSearchForm" class="form-inline">
                <div>
                    <g:radioGroup name="idSearchType"
                                  values="${IDSearchType.values()}"
                                  value="${IDSearchType.ALL}"
                                  labels="${IDSearchType.values().label}">
                        <label class="radio inline">
                            ${it.radio} ${it.label}
                        </label>
                    </g:radioGroup>
                </div>

                <br>

                <div>
                    <a href="#" class="btn" data-dismiss="modal" id="closeButton2">Close</a>
                    <a href="#" id="idSearchButton" class="btn btn-primary" data-dismiss="modal">Search</a>
                </div>
            </g:form>
        </div>

    </div>



    <g:if test="${flash.message}">
        <div class="alert">
            <button class="close" data-dismiss="alert">×</button>
            ${flash.message}
        </div>
    </g:if>


 </div>



<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="spinner-container">
                <div id="spinner" class="spinner" style="display:none; color: blue;"><g:message code="spinner.alt"
                                                                                                default="Loading&hellip;"/></div>
            </div>
            <g:layoutBody/>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span12 cap-footer">
            <b>Version:</b> ${grailsApplication?.metadata['app.version']} <b>branch:</b> ${grailsApplication?.metadata['git.branch.name']} <b>revision:</b> ${grailsApplication?.metadata['git.branch.version']}
        </div>
    </div>
</div>

<r:layoutResources/>
</body>
</html>

%{--<!DOCTYPE html>--}%
%{--<html>--}%
%{--<head>--}%
    %{--<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">--}%
    %{--<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">--}%
    %{--<g:layoutHead/>--}%
    %{--<r:layoutResources/>--}%
    %{--<title>BARD: Catalog of Assay Protocols</title>--}%
    %{--<r:external uri="/css/layout.css"/>--}%
    %{--<r:external uri="/css/table.css"/>--}%
%{--</head>--}%

%{--<body>--}%
%{--<div class="navbar navbar-inverse navbar-static-top">--}%
    %{--<div class="navbar-inner">--}%
        %{--<div class="container-fluid">--}%
            %{--<div class="row-fluid">--}%
                %{--<div class="span12">--}%
                    %{--<a class="brand" href="/BARD">--}%
                        %{--<img width="140" height="43" src="${resource(dir: 'images', file: 'bard_logo_small.png')}"--}%
                             %{--alt="BioAssay Research Database"/>--}%
                    %{--</a>--}%
                    %{--<ul class="nav">--}%
                        %{--<li><a href="/BARD">CAP</a></li>--}%

                        %{--<sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">--}%
                            %{--<li class="dropdown">--}%
                                %{--<a href="#" class="dropdown-toggle" data-toggle="dropdown">--}%
                                    %{--Admin--}%
                                    %{--<b class="caret"></b>--}%
                                %{--</a>--}%

                                %{--<ul class="dropdown-menu">--}%
                                    %{--<li class="controller"><g:link--}%
                                            %{--controller="aclClass">ACL Management</g:link></li>--}%
                                    %{--<li class="controller"><g:link--}%
                                            %{--controller="register">Register External BARD User</g:link></li>--}%
                                    %{--<li class="controller"><g:link controller="register"--}%
                                                                   %{--action="listUsersAndGroups">List External BARD Users</g:link></li>--}%
                                    %{--<li class="controller"><g:link controller="person"--}%
                                                                   %{--action="list">List Person Table</g:link></li>--}%
                                    %{--<li class="controller"><g:link controller="moveExperiments"--}%
                                                                   %{--action="show">Move Experiments</g:link></li>--}%
                                    %{--<li class="controller"><g:link controller="mergeAssayDefinition"--}%
                                                                   %{--action="show">Merge Assays</g:link></li>--}%
                                    %{--<li class="controller"><g:link controller="assayDefinition"--}%
                                                                   %{--action="assayComparisonReport">Compare Assays</g:link></li>--}%
                                    %{--<li class="controller"><g:link controller="splitAssayDefinition"--}%
                                                                   %{--action="show">Split Assays</g:link></li>--}%
                                %{--</ul>--}%

                            %{--</li>--}%
                        %{--</sec:ifAnyGranted>--}%

                        %{--<li class="dropdown">--}%
                            %{--<a href="#" class="dropdown-toggle" data-toggle="dropdown">--}%
                                %{--Assay Definitions--}%
                                %{--<b class="caret"></b>--}%
                            %{--</a>--}%
                            %{--<ul class="dropdown-menu">--}%
                                %{--<li class="controller"><g:link controller="assayDefinition"--}%
                                                               %{--action="groupAssays">My Assay Definitions</g:link></li>--}%
                                %{--<li class="controller"><g:link controller="assayDefinition"--}%
                                                               %{--action="findById">Search by Assay Definition ID</g:link></li>--}%
                                %{--<li class="controller"><g:link controller="assayDefinition"--}%
                                                               %{--action="findByName">Search by Assay Definition Name</g:link></li>--}%
                                %{--<li class="controller"><g:link controller="assayDefinition"--}%
                                                               %{--action="create">Create Assay Definition</g:link></li>--}%
                                %{--<li class="controller"><g:link controller="assayDefinition"--}%
                                                               %{--action="assayComparisonReport">Compare Assay Definitions</g:link></li>--}%
                            %{--</ul>--}%
                        %{--</li>--}%
                        %{--<li class="dropdown">--}%
                            %{--<a href="#" class="dropdown-toggle" data-toggle="dropdown">--}%
                                %{--Projects--}%
                                %{--<b class="caret"></b>--}%
                            %{--</a>--}%
                            %{--<ul class="dropdown-menu">--}%
                                %{--<li class="controller"><g:link controller="project"--}%
                                                               %{--action="groupProjects">My Projects</g:link></li>--}%
                                %{--<li class="controller"><g:link controller="project"--}%
                                                               %{--action="findById">Search by Project ID</g:link></li>--}%
                                %{--<li class="controller"><g:link controller="project"--}%
                                                               %{--action="findByName">Search by Project Name</g:link></li>--}%
                                %{--<li class="controller"><g:link controller="project"--}%
                                                               %{--action="create">Create a New Project</g:link></li>--}%
                            %{--</ul>--}%
                        %{--</li>--}%
                        %{--<li class="dropdown">--}%
                            %{--<a href="#" class="dropdown-toggle" data-toggle="dropdown">--}%
                                %{--Panels--}%
                                %{--<b class="caret"></b>--}%
                            %{--</a>--}%
                            %{--<ul class="dropdown-menu">--}%
                                %{--<li class="controller"><g:link controller="panel"--}%
                                                               %{--action="myPanels">My Panels</g:link></li>--}%

                                %{--<li class="controller"><g:link controller="panel"--}%
                                                               %{--action="findById">Search by Panel ID</g:link></li>--}%
                                %{--<li class="controller"><g:link controller="panel"--}%
                                                               %{--action="findByName">Search by Panel Name</g:link></li>--}%
                                %{--<li class="controller"><g:link controller="panel"--}%
                                                               %{--action="create">Create New Panel</g:link></li>--}%
                            %{--</ul>--}%
                        %{--</li>--}%
                        %{--<li>--}%
                            %{--<g:link url="${grailsApplication.config.bard.home.page}">Bard Web Client</g:link>--}%
                        %{--</li>--}%
                    %{--</ul>--}%
                    %{--<sec:ifLoggedIn>--}%
                        %{--<g:form class="navbar-form pull-right" name="logoutForm" controller="logout">--}%
                            %{--<span--}%
                                    %{--style="color: white; font-weight: bold;">Logged in as: <sec:username/></span>&nbsp;&nbsp;--}%
                            %{--<button type="submit" class="btn" id="logoutButton">Logout</button>--}%
                        %{--</g:form>--}%
                    %{--</sec:ifLoggedIn>--}%
                    %{--<sec:ifNotLoggedIn>--}%
                        %{--<g:form class="navbar-form pull-right" name="loginForm" controller="login">--}%
                            %{--Not logged in&nbsp;&nbsp;--}%
                            %{--<button type="submit" class="btn">Login</button>--}%
                        %{--</g:form> OR--}%
                        %{--<a class="btn btn-large" id='signin'>Sign in with your Email</a>--}%
                    %{--</sec:ifNotLoggedIn>--}%
                %{--</div>--}%
            %{--</div>--}%
        %{--</div>--}%
    %{--</div>--}%
%{--</div>--}%

%{--<div class="container-fluid">--}%
    %{--<div class="row-fluid">--}%
        %{--<div class="span12">--}%
            %{--<div class="spinner-container">--}%
                %{--<div id="spinner" class="spinner" style="display:none; color: blue;"><g:message code="spinner.alt"--}%
                                                                                                %{--default="Loading&hellip;"/></div>--}%
            %{--</div>--}%
            %{--<g:layoutBody/>--}%
        %{--</div>--}%
    %{--</div>--}%

    %{--<div class="row-fluid">--}%
        %{--<div class="span12 cap-footer">--}%
            %{--<b>Version:</b> ${grailsApplication?.metadata['app.version']} <b>branch:</b> ${grailsApplication?.metadata['git.branch.name']} <b>revision:</b> ${grailsApplication?.metadata['git.branch.version']}--}%
        %{--</div>--}%
    %{--</div>--}%
%{--</div>--}%

 %{--<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>--}%

%{--<r:layoutResources/>--}%
%{--</body>--}%
%{--</html>--}%