<!DOCTYPE html>
<html>
<g:render template="howToHeader" model="[title:'BARD Report a bug']"/>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>Report A Bug</h2></article>
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
                        Report a Bug
                        </h3>

                    <p>
                        Anyone who knows about pushing the limits of technology understands that the occasional bug is to be expected. They also understand how important it is to report that bug to the technology’s creators. The same goes for suggesting enhancements.
                        </p>

                    <p>
                        But before you invest your valuable time, we suggest that you browse our list of <a href="${grailsApplication.config.bard.users.mailing.list}" target="forum">previously reported issues</a> and visit the BARD Support Community to see if there is already an ongoing discussion.
                        </p>

                    <p>
                        If it appears you’re the first to spot the bug, great! Please
                        <a href="${grailsApplication.config.bard.users.mailing.list}" target="forum">tell us</a> what you see, exactly where you see it, and step-by-step what you are doing when it occurs. Simply complete the form or email
                    <g:render template="../layouts/templates/bardusers"/>. We promise a prompt response, as outlined by the Feedback Policy in our Service Level Agreement.
                    </p>

                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>



</body>
</html>