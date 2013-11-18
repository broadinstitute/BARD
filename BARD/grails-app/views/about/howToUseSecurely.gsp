<%@ page import="bard.db.enums.ExperimentStatus; bard.db.enums.ContextType; bard.db.registration.DocumentKind; bard.db.model.AbstractContextOwner; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix"/>
    <meta name="layout" content="howto"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>BARD How to use securely</title>
</head>

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
                        There are two ways to sign up for a BARD account: You can go to the Sign Up page and complete the simple form, or you can sign up using Mozilla Persona, a decentralized authentication system based on the open BrowserID protocol. With Mozilla Persona, you can just click the “Sign in with email” button then enter any valid email address. The first time you perform this action, BARD will automatically create an account for you, using your email address as your username. If the system cannot automatically verify your email address, it will send a verification email. Note: When signing up for your BARD account, you will need to accept our Terms of Service and Privacy Policy.
                        </p>

                    <aside>
                        <p>
                            Mozilla Persona is a decentralized authentication system based on the open BrowserID protocol. To learn more about this open source solution, please visit
                            http://www.mozilla.org/en-US/persona/
                        </p>
                    </aside>


                    <h3>
                        Create a Submission
                        </h3>

                    <p>
                        You will have to belong to a recognized team in order to initiate a data submission. Visit the Available Teams page then email the administrator of the team you wish to join. (Note: you can belong to more than one team.) Once added to the team, re-login to begin creating your submission.
                        </p>

                    <h3>
                        Edit a Submission
                        </h3>

                    <p>
                        Every member of you team can edit the submission in BARD. The name of last person to edit your submission will appear on the view page of the submission as the “Modified By” person. The date will also be displayed.
                        </p>

                    <h3>
                        Upload Experiment Results
                        </h3>


                    <p><strong>Your BARD account and team membership also enables you to upload results to any experiments that belong to any of your teams.</strong>
                    </p>

                </article>
                <aside class="span2"></aside>
            </div>
        </div>
    </div>
</article>



</body>
</html>