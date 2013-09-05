#!/usr/bin/perl
use CGI qw(param);

$mol=param('mol');
$ncbx=param('n');
$cbx=hex2yn(param('cbx'), $ncbx);

sub hex2yn {
	my $x = $_[0];
	my $n = $_[1];
	my $mask=1;
	my $i;
	my $s="";
	for($i=0; $i<$n; ++$i) {
		use integer;
		my $j = length($x)-1-$i/4;
		if(((hex substr($x, $j, 1)) & $mask) != 0) {
			$s.="y";
		} else {
			$s.="n";
		}
		if(($mask <<= 1) == 16) {
			$mask = 1;
		}
	}
	return $s;
}

print <<EOF;
Content-type: text/html

<html>
<head>
<title>Search More</title>
</head>
<body BGCOLOR="#ffffff">
Button of the <b>$mol</b> molecule pressed.
<p>
There are $ncbx checkboxes in the applet, in the following states:<br>
$cbx
<p>
The state of the page with the molecule table applet can be sent to the
CGI script like <a HREF="example-searchmore2.txt">this one</a>.
This information may include
<ul>
<li><em>Checkboxes</em> in the applet - see the example above</li>
<li><em>HTML form</em> values and <em>JavaScript variables</em>
    - you can let the applet know these information by setting the
    button action string with <code>MolView.setActionB</code> from JavaScript.
    </li>
</ul>

</body>
</html>
EOF
