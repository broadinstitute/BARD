#!/usr/bin/perl
use CGI qw(param);

$mol=param('mol');

print <<EOF;
Content-type: text/html

<html>
<head>
<title>Search More</title>
</head>
<body BGCOLOR="#ffffff">
Button of the <b>$mol</b> molecule pressed.
<p>
This page was created by a <a HREF="example-searchmore1.txt">CGI script</a>.
</body>
</html>
EOF
