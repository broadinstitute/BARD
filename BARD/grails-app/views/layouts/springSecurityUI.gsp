%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<%@ page import="org.codehaus.groovy.grails.plugins.PluginManagerHolder" %>
<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils" %>
<%@ page import="grails.plugins.springsecurity.SecurityConfigType" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>

<head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <title><g:layoutTitle default='Security Management Console'/></title>

    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>

    <s2ui:resources module='spring-security-ui'/>

    <style>
    .icon_role {
        background-image: url('${fam.icon(name: 'lock')}');
    }

    .icon_users {
        background-image: url('${fam.icon(name: 'group')}');
    }

    .icon_user {
        background-image: url('${fam.icon(name: 'user')}');
    }

    .icon_error {
        background-image: url('${fam.icon(name: 'exclamation')}');
    }

    .icon_info {
        background-image: url('${fam.icon(name: 'information')}');
    }

    .icon, .ui-tabs .ui-tabs-nav li a.icon {
        background-repeat: no-repeat;
        padding-left: 24px;
        background-position: 4px center;
    }
    </style>

    <g:layoutHead/>

</head>

<body>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">

        </div>

        <div class="span10">
            <ul class="jd_menu jd_menu_slate">
                <g:if test="${PluginManagerHolder.pluginManager.hasGrailsPlugin('springSecurityAcl')}">
                    <li><a class="accessible"><g:message code="spring.security.ui.menu.acl"/></a>
                        <ul>
                            <li><g:message code="spring.security.ui.menu.aclClass"/> &raquo;
                                <ul>
                                    <li><g:link controller="aclClass" action='search'><g:message
                                            code="spring.security.ui.search"/></g:link></li>

                                </ul>
                            </li>
                            <li><g:message code="spring.security.ui.menu.aclSid"/> &raquo;
                                <ul>
                                    <li><g:link controller="aclSid" action='search'><g:message
                                            code="spring.security.ui.search"/></g:link></li>
                                    <li><g:link controller="aclSid" action='create'><g:message
                                            code="spring.security.ui.create"/></g:link></li>
                                </ul>
                            </li>
                            <li><g:message code="spring.security.ui.menu.aclObjectIdentity"/> &raquo;
                                <ul>
                                    <li><g:link controller="aclObjectIdentity" action='search'><g:message
                                            code="spring.security.ui.search"/></g:link></li>

                                </ul>
                            </li>
                            <li><g:message code="spring.security.ui.menu.aclEntry"/> &raquo;
                                <ul>
                                    <li><g:link controller="aclEntry" action='search'><g:message
                                            code="spring.security.ui.search"/></g:link></li>

                                </ul>
                            </li>
                        </ul>
                    </li>
                </g:if>
            </ul>

            <div id='s2ui_header_body'>

                <div id='s2ui_header_title'>
                    Spring Security Management Console
                </div>
            </div>

        </div>

        <div id="s2ui_main">
            <div id="s2ui_content">
                <s2ui:layoutResources module='spring-security-ui'/>
                <g:layoutBody/>
            </div>
        </div>
    </div>
</div>


<s2ui:showFlash/>

</body>
</html>
