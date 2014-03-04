<!DOCTYPE html>
<html>
<g:render template="howToHeader" model="[title:'BARD How to use securely']"/>

<body>
<div class="search-panel">
    <div class="container-fluid">
        <div class="row-fluid">
            <aside class="span2"></aside>
            <article class="span8 head-holder"><h2>How to Use BARD Securely</h2></article>
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


                    <p>
                        For the most part you can perform searches and view data anonymously on BARD. However, if you wish to create, edit or submit data (assays, projects, panels, experiments) or upload experiment results, you will need to sign up for a BARD account.
                        </p>


                    <h3>
                        Sign Up for a BARD Account
                        </h3>

                    <p>
                        To sign up for a BARD account, choose the <g:link controller="bardLogin" action="auth">Sign In</g:link> link and you will be redirected to Mozilla Persona to create an account.
                        With Mozilla Persona, you can just click the “Sign in with email” button then enter any valid email address.
                        The first time you perform this action, BARD will automatically create an account for you, using your email address as your username.
                        If the system cannot automatically verify your email address, it will send a verification email.
                        If you choose to use a gmail address, you will be able to use your gmail login credentials to connect to BARD.
                        </p>

                    <aside>
                        <p>
                            Mozilla Persona is a decentralized authentication system based on the open BrowserID protocol. To learn more about this open source solution, please visit
                            <a href="http://www.mozilla.org/en-US/persona/" target="_blank">http://www.mozilla.org/en-US/persona/</a>
                        </p>
                    </aside>


                    <h3>
                        Create a Submission
                        </h3>

                    <p>
                        You will have to belong to a recognized team in order to initiate a data submission.
                        To join a team, please email the <a href="${grailsApplication.config.bard.users.mailing.list}" target="forum">bard-users</a> forum.
                       (Note: you can belong to more than one team.)
                    </p>

                    <p>
                       Once added to the team, re-login and navigate to the <g:link controller="bardWebInterface" action="navigationPage">My BARD</g:link> page to begin creating your submission.
                       </p>

                    <p>Newly created submissions will have a "Draft" status and are only viewable by other members of the team that owns them.  Draft
                    submissions will not show up in search results, so you will need to use the <g:link controller="bardWebInterface" action="navigationPage">My BARD</g:link>
                    page to find them.
                    </p>

                    <p>
                        To make a submission publicly viewable and searchable, you will need to mark it as "Approved" by setting the status in the Overview section
                    of the submission.
                    </p>

                    <p>Note: If you haven't done so already, please review the <g:link controller="about" action="bardOrganizingPrinciples">organizing principles</g:link> of BARD before creating a submission.</p>

                    <h3>
                        Edit a Submission
                        </h3>

                    <p>
                        Every member of your team can edit the submission in BARD. The name of the last person to edit your submission will appear on the view page of the submission as the “Modified By” person. The date will also be displayed.
                        </p>

                    <h3>
                        Upload Experiment Results
                        </h3>


                    <p>Your BARD account and team membership also enables you to upload results to any experiments that belong to any of your teams.
                    </p>

                    <p>To upload results for an experiment, you must first define the types of results that were captured (in section "4. Results" of the Experiment page),
                    then download a template (in section "1. Overview"), fill in the template results, and upload the template using the "Upload Results" button.</p>

                    <p>Experimental results will be not searchable until the experiment is marked "Approved."  You can choose to preview them by clicking the "Preview Results"
                    link in section "4. Results".</p>

                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>



</body>
</html>