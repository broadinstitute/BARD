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

<header class="navbar navbar-static-top" id="header">
    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span3">
                <g:render template="/layouts/templates/downtime"/>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span3" style="min-width: 210px; min-height: 70px;">
                <strong class="logo"><a href="#">BARD BioAssay Research Database</a></strong>
            </div>

            <div class="span9">
                <div class="row-fluid">
                    <div class="center-aligned span6">
                        <g:render template="/layouts/templates/socialMedia"/>
                    </div>

                    <div class="right-aligned span6">
                        <g:render template="/layouts/templates/loginStrip"/>
                    </div>
                </div>

                <div class="row-fluid">
                    <div class="span12">
                        <nav class="nav-panel right-aligned">
                            <ul class="nav">
                                <g:render template="/layouts/templates/howtolinks"/>
                                <sec:ifLoggedIn>
                                    <li><g:link controller="bardWebInterface"
                                                action="navigationPage">My BARD</g:link></li>
                                </sec:ifLoggedIn>
                            </ul>
                            <g:if test="${false}">
                                <ul class="login-nav">
                                    <li><a href="#">Sign up</a></li>
                                    <li><a href="#">Sign in</a></li>
                                </ul>
                            </g:if>
                        </nav>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>
