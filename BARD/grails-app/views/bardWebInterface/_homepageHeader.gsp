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
