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

