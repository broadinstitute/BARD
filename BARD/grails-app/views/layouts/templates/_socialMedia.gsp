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

<ul class="social-networks">
    <li>
        %{--Facebook widget plugin--}%
        <a href="#"
           onclick="
               window.open(
                       'https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(location.href),
                       'facebook-share-dialog',
                       'width=626,height=436');
               return false;"
           style="background:url('/BARD/images/bardHomepage/facebook-share-icon.gif') no-repeat; width:58px; height:18px;">
        </a>
    </li>
    <li style="width: 80px;">
        %{--Twitter widget plugin--}%
        <script>!function (d, s, id) {
            var js, fjs = d.getElementsByTagName(s)[0], p = /^http:/.test(d.location) ? 'http' : 'https';
            if (!d.getElementById(id)) {
                js = d.createElement(s);
                js.id = id;
                js.src = p + '://platform.twitter.com/widgets.js';
                fjs.parentNode.insertBefore(js, fjs);
            }
        }(document, 'script', 'twitter-wjs');
        </script>

        <a href="https://twitter.com/share" class="twitter-share-button" data-url="https://bard.nih.gov/BARD/"
           data-text="BARD">Tweet</a>
    </li>
    <li>
        %{--LinkedIn widget plugin--}%
        <script src="//platform.linkedin.com/in.js" type="text/javascript">
            lang: en_US
        </script>
        <script type="IN/Share" data-url="https://bard.nih.gov/BARD/" data-counter="right"></script>
    </li>
    <li>
        %{--Google Plus widget plugin--}%

        <!-- Place this tag where you want the +1 button to render. -->
        <div class="g-plusone"></div>

        <!-- Place this tag after the last +1 button tag. -->
        <script type="text/javascript">
            (function () {
                var po = document.createElement('script');
                po.type = 'text/javascript';
                po.async = true;
                po.src = 'https://apis.google.com/js/plusone.js';
                var s = document.getElementsByTagName('script')[0];
                s.parentNode.insertBefore(po, s);
            })();
        </script>
    </li>
</ul>

