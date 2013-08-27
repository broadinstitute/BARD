#!/usr/bin/perl
use CGI qw(param);

$molfile=param('molfile');
$ns2=(substr($ENV{"HTTP_USER_AGENT"},0,11) eq "Mozilla/2.0");

print <<EOF;
Content-type: text/html

<html>
<head>
<title>Open molfile</title>
<link REL="stylesheet" TYPE="text/css" HREF="../../marvinmanuals.css" TITLE="Style">
<script LANGUAGE="JavaScript">
<!--
function init() {
EOF

if($ns2) {
	print "\tvar s = \"\";\n";
} else {
	print "\tvar s = new String(\"\");\n";
}

$i=0;
while (<$molfile>) {
	print "s+=\"";
	s/\r//;
	chomp;
	print;
	print "\\n\";\n";
	++$i;
}

if($i > 0) {
	print <<EOF;
	if(parent != null && parent.msketch != null) {
		var d = parent.msketch.document;
		if(d.MSketch != null) {
			d.MSketch.setMol(s);
		}
	}
EOF
}

print <<EOF;
}
// -->
</script>
</head>
<body BGCOLOR="#ffffff" onLoad="init()">
<form NAME="fileform" ENCTYPE="multipart/form-data" ACTION="sketch-load.cgi" METHOD="post" VALUE="$molfile">
<center>
<table BORDER=0 CELLSPACING=0 CELLPADDING=0>
<tr>
<td>Molfile:</td>
<td>
    <input TYPE="FILE" SIZE="30" NAME="molfile">
    </td>
<td WIDTH=10></td>
<td>
    <input TYPE="SUBMIT" VALUE="Load via web server">
    </font>
    </td>
</tr>
</table>
</center>
</form>
</body>
</html>
EOF
