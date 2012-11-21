///////////////////////////////////////////////////////////////////////////////
//// Extra utility for Marvin:
//// Workaround for missing JavaScript to Java 
//// communication for Macintosh
//// browsers where no LiveConnect support.
//// Version 3.3.2, Copyright (C) 2000-2005 
//// ChemAxon Ltd., Tamas Vertse
//////////////////////////////////////////////////////////////////////////


var _result = null;

//Netscape 4 doesn't support iframe, LiveConnect enabled browser does not
//need this workaround.
var _isdiv = (browser_NetscapeMozilla() != 4) && !isLiveConnect();
if(_isdiv) {
document.write('<div id="DivApplet"></div>');
document.write('<iframe name="_respage" src="" width=0 height=0'+
' frameborder=0 scrolling="no"></iframe>');
}
var _path = "."

function setPath(p) {
    _path = p;
}

var _iscompressed = "true";
var _maxlength = "2048";

function setIsCompressed(s) {
    _iscompressed = s;
}

function browser_Safari() {
    var x = navigator.appVersion.toLowerCase().lastIndexOf("safari/");
    if(x > -1) {
	v = navigator.appVersion.substring(x+7);
	p = v.indexOf(".");
	if(p > -1) {
	    v = v.substring(0,p);
	}
	return v;
    }
    return 0;
}

function isLiveConnect() {
    var macv = macOsVer();
    var mozillaver = browser_NetscapeMozilla();
    var safariver = browser_Safari();
    var isjava = navigator.javaEnabled();
    return ((macv == 0) ||
	((macv > 0) && (mozillaver > 0) && (safariver == 0) && isjava) ||
	((macv > 9) && (safariver >= 119)));
}

/**Read the stored result.*/
function getResult() {
    return _result;
}


var _resFragment = null;
/* Append the current fregment to the received ones.
  Called from _result.html */
function writeResult(s) {
    if(_resFragment == null) {
	_resFragment = s;	
    } else {
	_resFragment = _resFragment + s;
    }
}

var _postJsMethod = null;

/* Close 'result stream' and evaluate 'postJsMethod'.
    Called after 'writeResult()' from _result.html. */
function closeResult() {
    if(_resFragment != null) {
	_result = unescape(_resFragment);
	_resFragment = null;
    }
    //eval postJsMethod
    if(_postJsMethod != null) {
	eval(_postJsMethod);
	_postJsMethod = null;
    }
}

/**Delete extra backslashes from the string.*/
function convertJs2Html(str) {
    if(str == null) {
	return null;
    }
    var v = str;
    if(_isdiv) {
	v = v.split("\\\"").join("\"");
	v = v.split("\\\'").join("\'");
	v = v.split("\\n").join("\n");
	v = v.split("\\\\").join("\\");
    }
    v = unix2local(v);
    return v;
}

var _appletsource = "";
var _paramindex = 0;

/* Sets the method of the applet that you want to call.
  @param methodname - the name of the method with the name of the applet.
	E.g.: 'MSketch.setMol'
  @param paramtypes - the formal parameter list of the method
    E.g.: 'java.lang.String'
*/
function setMethod(methodname,paramtypes) {
    if(!_isdiv) {
	return;
    }
	_appletsource = '<APPLET code=Js2JavaApplet NAME=Js2JavaApplet' +
	' CODEBASE="'+_path+'"'+' ARCHIVE="js2java.jar"'+
    ' WIDTH=0 HEIGHT=0 MAYSCRIPT> '+
    '<param name="maxlength" value="'+ _maxlength +'">'+
    '<param name="iscompressed" value="'+ _iscompressed +'">'+
    '<param name="respath" value="'+_path+'/">'+
    '<PARAM NAME=method VALUE="'+methodname+'">'+
    '<PARAM NAME=paramtypes VALUE="'+paramtypes+'">';
    _paramindex = 0;
}

/**Generate the code of the specified applet parameter.*/
function getAppletParam(paramname,paramvalue) {
    var s = "@javascript-escape-encoded@"+escape(paramvalue);
    return '<PARAM NAME='+paramname+' VALUE="'+s+'">\n';
}

/* Sets the next parameter of the applet method. Set first the first parameter
  of the method then the secondn and etc.*/
function addMethodParam(paramvalue) {
    if(!_isdiv) {
	return;
    }
    _appletsource += getAppletParam('arg'+_paramindex,paramvalue);
    _paramindex = _paramindex + 1;
}

/*You can specify the method that runs after the Applet method returned.
E.g.: 'parent.print()'*/
function setPostJsMethod(paramvalue) {
    _postJsMethod = paramvalue;
}

/*Commit the above settings and start Java calling.*/
function runMethod() {
    if(!_isdiv) {
	return;
    }
    _appletsource += '</APPLET>';
    document.getElementById('DivApplet').innerHTML = _appletsource;
}

