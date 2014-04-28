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

<!DOCTYPE html>
<html>
<g:render template="howToHeader" model="[title:'BARD Contact us']"/>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>Contact us</h2></article>
            <aside class="span2"></aside>
        </div>

    </div>
</div>


<article class="hero-block">
    <div class="container-fluid">
        <div class="lowkey-hero-area">
            <div class="row-fluid">
                <aside class="span2"></aside>
                <article class="span8">

                    <h3>
                        Contact Us
                        </h3>

                    <p>
                        Not only is BARD for researchers, it is by researchers – your data submissions, your plug-in contributions and your valued suggestions and ideas. We encourage any and all feedback, and here’s where to send it:
                        </p>

                    <br/>
                    <dl class='unindentedDefinition'>
                        <dt>General Feedback</dt>
                        <dd>Tell us about your experience using BARD. Which features are helpful? Which could use improvement? What functionality would you like to see in the future?
                        Please tell us at <g:render template="../layouts/templates/bardusers"/>

                        </dd>

                        <br/>
                        <dt>Report a Bug</dt>
                        <dd>We despise bugs too! If you find one, please be sure to tell us what you see, exactly where you see it, and step-by-step what you are doing when it occurs. Please complete the simple form on our Report a Bug page or email
                        <g:render template="../layouts/templates/bardusers"/>.</dd>

                        <br/>
                        <dt>Support Community</dt>
                        <dd>Whether you’re seeking advice that other users could use themselves, or have an effective tip to share, your small contributions to the BARD Support Community could make a big difference in someone else’s research. Visit the BARD Support Community now.</dd>

                        <br/>
                        <dt>Collaborate with Us</dt>
                        <dd>Do you foresee an opportunity for collaboration between your institution and the BARD team? Do you need to request a local installation of
                        the BARD data warehouse and REST API for plug-in development? Just let us know at <g:render template="../layouts/templates/bardusers"/>.</dd>

                    </dl>

                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>
