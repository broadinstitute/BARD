<%@ page import="bard.db.people.Role" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Register a New External BARD User in Crowd</title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">
    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span12">
                <div class="span2"></div>
                <div class="span8">
                    <h3>Register a New External BARD User in Crowd</h3>
                    <g:form action='register' name='registerForm'>
                        <g:render template="register"/>
                        <g:render template="primaryGroup" model="[]"/>
                    </g:form>
                </div>

                <div class="span2"></div>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function () {
            $('#username').focus();
        });
    </script>
</sec:ifAnyGranted>
</body>
</html>
