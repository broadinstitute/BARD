//
// Inserted the content of http://java.sun.com/js/deployJava.js
//

var deployJava={debug:null,firefoxJavaVersion:null,myInterval:null,preInstallJREList:null,returnPage:null,brand:null,locale:null,installType:null,EAInstallEnabled:false,EarlyAccessURL:null,getJavaURL:'http://java.sun.com/webapps/getjava/BrowserRedirect?host=java.com',appleRedirectPage:'http://www.apple.com/support/downloads/',oldMimeType:'application/npruntime-scriptable-plugin;DeploymentToolkit',mimeType:'application/java-deployment-toolkit',launchButtonPNG:'http://java.sun.com/products/jfc/tsc/articles/swing2d/webstart.png',browserName:null,browserName2:null,getJREs:function(){var list=new Array();if(deployJava.isPluginInstalled()){var plugin=deployJava.getPlugin();var VMs=plugin.jvms;for(var i=0;i<VMs.getLength();i++){list[i]=VMs.get(i).version;}}else{var browser=deployJava.getBrowser();if(browser=='MSIE'){if(deployJava.testUsingActiveX('1.7.0')){list[0]='1.7.0';}else if(deployJava.testUsingActiveX('1.6.0')){list[0]='1.6.0';}else if(deployJava.testUsingActiveX('1.5.0')){list[0]='1.5.0';}else if(deployJava.testUsingActiveX('1.4.2')){list[0]='1.4.2';}else if(deployJava.testForMSVM()){list[0]='1.1';}}else if(browser=='Netscape Family'){deployJava.getJPIVersionUsingMimeType();if(deployJava.firefoxJavaVersion!=null){list[0]=deployJava.firefoxJavaVersion;}else if(deployJava.testUsingMimeTypes('1.7')){list[0]='1.7.0';}else if(deployJava.testUsingMimeTypes('1.6')){list[0]='1.6.0';}else if(deployJava.testUsingMimeTypes('1.5')){list[0]='1.5.0';}else if(deployJava.testUsingMimeTypes('1.4.2')){list[0]='1.4.2';}else if(deployJava.browserName2=='Safari'){if(deployJava.testUsingPluginsArray('1.7.0')){list[0]='1.7.0';}else if(deployJava.testUsingPluginsArray('1.6')){list[0]='1.6.0';}else if(deployJava.testUsingPluginsArray('1.5')){list[0]='1.5.0';}else if(deployJava.testUsingPluginsArray('1.4.2')){list[0]='1.4.2';}}}}
if(deployJava.debug){for(var i=0;i<list.length;++i){alert('We claim to have detected Java SE '+list[i]);}}
return list;},installJRE:function(requestVersion){var ret=false;if(deployJava.isPluginInstalled()){if(deployJava.getPlugin().installJRE(requestVersion)){deployJava.refresh();if(deployJava.returnPage!=null){document.location=deployJava.returnPage;}
return true;}else{return false;}}else{return deployJava.installLatestJRE();}},installLatestJRE:function(){if(deployJava.isPluginInstalled()){if(deployJava.getPlugin().installLatestJRE()){deployJava.refresh();if(deployJava.returnPage!=null){document.location=deployJava.returnPage;}
return true;}else{return false;}}else{var browser=deployJava.getBrowser();var platform=navigator.platform.toLowerCase();if((deployJava.EAInstallEnabled=='true')&&(platform.indexOf('win')!=-1)&&(deployJava.EarlyAccessURL!=null)){deployJava.preInstallJREList=deployJava.getJREs();if(deployJava.returnPage!=null){deployJava.myInterval=setInterval("deployJava.poll()",3000);}
location.href=deployJava.EarlyAccessURL;return false;}else{if(browser=='MSIE'){return deployJava.IEInstall();}else if((browser=='Netscape Family')&&(platform.indexOf('win32')!=-1)){return deployJava.FFInstall();}else{location.href=deployJava.getJavaURL+
((deployJava.returnPage!=null)?('&returnPage='+deployJava.returnPage):'')+
((deployJava.locale!=null)?('&locale='+deployJava.locale):'')+
((deployJava.brand!=null)?('&brand='+deployJava.brand):'');}
return false;}}},runApplet:function(attributes,parameters,minimumVersion){if(minimumVersion=='undefined'||minimumVersion==null){minimumVersion='1.1';}
var regex="^(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:_(\\d+))?)?)?$";var matchData=minimumVersion.match(regex);if(deployJava.returnPage==null){deployJava.returnPage=document.location;}
if(matchData!=null){var browser=deployJava.getBrowser();if((browser!='?')&&('Safari'!=deployJava.browserName2)){if(deployJava.versionCheck(minimumVersion+'+')){deployJava.writeAppletTag(attributes,parameters);}else if(deployJava.installJRE(minimumVersion+'+')){deployJava.refresh();location.href=document.location;deployJava.writeAppletTag(attributes,parameters);}}else{deployJava.writeAppletTag(attributes,parameters);}}else{if(deployJava.debug){alert('Invalid minimumVersion argument to runApplet():'+
minimumVersion);}}},writeAppletTag:function(attributes,parameters){var s='<'+'applet ';var codeAttribute=false;for(var attribute in attributes){s+=(' '+attribute+'="'+attributes[attribute]+'"');if(attribute=='code'){codeAttribute=true;}}
if(!codeAttribute){s+=(' code="dummy"');}
s+='>';document.write(s);if(parameters!='undefined'&&parameters!=null){var codebaseParam=false;for(var parameter in parameters){if(parameter=='codebase_lookup'){codebaseParam=true;}
s='<param name="'+parameter+'" value="'+
parameters[parameter]+'">';document.write(s);}
if(!codebaseParam){document.write('<param name="codebase_lookup" value="false">');}}
document.write('<'+'/'+'applet'+'>');},versionCheck:function(versionPattern)
{var index=0;var regex="^(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:_(\\d+))?)?)?(\\*|\\+)?$";var matchData=versionPattern.match(regex);if(matchData!=null){var familyMatch=true;var patternArray=new Array();for(var i=1;i<matchData.length;++i){if((typeof matchData[i]=='string')&&(matchData[i]!='')){patternArray[index]=matchData[i];index++;}}
if(patternArray[patternArray.length-1]=='+'){familyMatch=false;patternArray.length--;}else{if(patternArray[patternArray.length-1]=='*'){patternArray.length--;}}
var list=deployJava.getJREs();for(var i=0;i<list.length;++i){if(deployJava.compareVersionToPattern(list[i],patternArray,familyMatch)){return true;}}
return false;}else{alert('Invalid versionPattern passed to versionCheck: '+
versionPattern);return false;}},isWebStartInstalled:function(minimumVersion){var browser=deployJava.getBrowser();if((browser=='?')||('Safari'==deployJava.browserName2)){return true;}
if(minimumVersion=='undefined'||minimumVersion==null){minimumVersion='1.4.2';}
var retval=false;var regex="^(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:_(\\d+))?)?)?$";var matchData=minimumVersion.match(regex);if(matchData!=null){retval=deployJava.versionCheck(minimumVersion+'+');}else{if(deployJava.debug){alert('Invalid minimumVersion argument to isWebStartInstalled(): '+minimumVersion);}
retval=deployJava.versionCheck('1.4.2+');}
return retval;},getJPIVersionUsingMimeType:function(){for(var i=0;i<navigator.mimeTypes.length;++i){var s=navigator.mimeTypes[i].type;var m=s.match(/^application\/x-java-applet;jpi-version=(.*)$/);if(m!=null){deployJava.firefoxJavaVersion=m[1];break;}}},launchWebStartApplication:function(jnlp){return false;},createWebStartLaunchButtonEx:function(jnlp,minimumVersion){if(deployJava.returnPage==null){deployJava.returnPage=jnlp;}
var url='javascript:deployJava.launchWebStartApplication(\''+jnlp+'\');';document.write('<'+'a href="'+url+'" onMouseOver="window.status=\'\'; '+'return true;"><'+'img '+'src="'+deployJava.launchButtonPNG+'" '+'border="0" /><'+'/'+'a'+'>');},createWebStartLaunchButton:function(jnlp,minimumVersion){if(deployJava.returnPage==null){deployJava.returnPage=jnlp;}
var url='javascript:'+'if (!deployJava.isWebStartInstalled(&quot;'+
minimumVersion+'&quot;)) {'+'if (deployJava.installLatestJRE()) {'+'if (deployJava.launch(&quot;'+jnlp+'&quot;)) {}'+'}'+'} else {'+'if (deployJava.launch(&quot;'+jnlp+'&quot;)) {}'+'}';document.write('<'+'a href="'+url+'" onMouseOver="window.status=\'\'; '+'return true;"><'+'img '+'src="'+deployJava.launchButtonPNG+'" '+'border="0" /><'+'/'+'a'+'>');},launch:function(jnlp){document.location=jnlp;return true;},isPluginInstalled:function(){var plugin=deployJava.getPlugin();if(plugin&&plugin.jvms){return true;}else{return false;}},isAutoUpdateEnabled:function(){if(deployJava.isPluginInstalled()){return deployJava.getPlugin().isAutoUpdateEnabled();}
return false;},setAutoUpdateEnabled:function(){if(deployJava.isPluginInstalled()){return deployJava.getPlugin().setAutoUpdateEnabled();}
return false;},setInstallerType:function(type){deployJava.installType=type;if(deployJava.isPluginInstalled()){return deployJava.getPlugin().setInstallerType(type);}
return false;},setAdditionalPackages:function(packageList){if(deployJava.isPluginInstalled()){return deployJava.getPlugin().setAdditionalPackages(packageList);}
return false;},setEarlyAccess:function(enabled){deployJava.EAInstallEnabled=enabled;},isPlugin2:function(){if(deployJava.isPluginInstalled()){if(deployJava.versionCheck('1.6.0_10+')){try{return deployJava.getPlugin().isPlugin2();}catch(err){}}}
return false;},allowPlugin:function(){deployJava.getBrowser();var ret=('Chrome'!=deployJava.browserName2&&'Safari'!=deployJava.browserName2&&'Opera'!=deployJava.browserName2);return ret;},getPlugin:function(){deployJava.refresh();var ret=null;if(deployJava.allowPlugin()){ret=document.getElementById('deployJavaPlugin');}
return ret;},compareVersionToPattern:function(version,patternArray,familyMatch){var regex="^(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:_(\\d+))?)?)?$";var matchData=version.match(regex);if(matchData!=null){var index=0;var result=new Array();for(var i=1;i<matchData.length;++i){if((typeof matchData[i]=='string')&&(matchData[i]!=''))
{result[index]=matchData[i];index++;}}
var l=Math.min(result.length,patternArray.length);if(familyMatch){for(var i=0;i<l;++i){if(result[i]!=patternArray[i])return false;}
return true;}else{for(var i=0;i<l;++i){if(result[i]<patternArray[i]){return false;}else if(result[i]>patternArray[i]){return true;}}
return true;}}else{return false;}},getBrowser:function(){if(deployJava.browserName==null){var browser=navigator.userAgent.toLowerCase();if(deployJava.debug){alert('userAgent -> '+browser);}
if(browser.indexOf('msie')!=-1){deployJava.browserName='MSIE';deployJava.browserName2='MSIE';}else if(browser.indexOf('firefox')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='Firefox';}else if(browser.indexOf('chrome')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='Chrome';}else if(browser.indexOf('safari')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='Safari';}else if(browser.indexOf('mozilla')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='Other';}else if(browser.indexOf('opera')!=-1){deployJava.browserName='Netscape Family';deployJava.browserName2='Opera';}else{deployJava.browserName='?';deployJava.browserName2='unknown';}
if(deployJava.debug){alert('Detected browser name:'+deployJava.browserName+', '+deployJava.browserName2);}}
return deployJava.browserName;},testUsingActiveX:function(version){var objectName='JavaWebStart.isInstalled.'+version+'.0';if(!ActiveXObject){if(deployJava.debug){alert('Browser claims to be IE, but no ActiveXObject object?');}
return false;}
try{return(new ActiveXObject(objectName)!=null);}catch(exception){return false;}},testForMSVM:function(){var clsid='{08B0E5C0-4FCB-11CF-AAA5-00401C608500}';if(typeof oClientCaps!='undefined'){var v=oClientCaps.getComponentVersion(clsid,"ComponentID");if((v=='')||(v=='5,0,5000,0')){return false;}else{return true;}}else{return false;}},testUsingMimeTypes:function(version){if(!navigator.mimeTypes){if(deployJava.debug){alert('Browser claims to be Netscape family, but no mimeTypes[] array?');}
return false;}
for(var i=0;i<navigator.mimeTypes.length;++i){s=navigator.mimeTypes[i].type;var m=s.match(/^application\/x-java-applet\x3Bversion=(1\.8|1\.7|1\.6|1\.5|1\.4\.2)$/);if(m!=null){if(deployJava.compareVersions(m[1],version)){return true;}}}
return false;},testUsingPluginsArray:function(version){if((!navigator.plugins)||(!navigator.plugins.length)){return false;}
var platform=navigator.platform.toLowerCase();for(var i=0;i<navigator.plugins.length;++i){s=navigator.plugins[i].description;if(s.search(/^Java Switchable Plug-in (Cocoa)/)!=-1){if(deployJava.compareVersions("1.5.0",version)){return true;}}else if(s.search(/^Java/)!=-1){if(platform.indexOf('win')!=-1){if(deployJava.compareVersions("1.5.0",version)||deployJava.compareVersions("1.6.0",version)){return true;}}}}
if(deployJava.compareVersions("1.5.0",version)){return true;}
return false;},IEInstall:function(){location.href=deployJava.getJavaURL+
((deployJava.returnPage!=null)?('&returnPage='+deployJava.returnPage):'')+
((deployJava.locale!=null)?('&locale='+deployJava.locale):'')+
((deployJava.brand!=null)?('&brand='+deployJava.brand):'')+
((deployJava.installType!=null)?('&type='+deployJava.installType):'');return false;},done:function(name,result){},FFInstall:function(){location.href=deployJava.getJavaURL+
((deployJava.returnPage!=null)?('&returnPage='+deployJava.returnPage):'')+
((deployJava.locale!=null)?('&locale='+deployJava.locale):'')+
((deployJava.brand!=null)?('&brand='+deployJava.brand):'')+
((deployJava.installType!=null)?('&type='+deployJava.installType):'');return false;},compareVersions:function(installed,required){var a=installed.split('.');var b=required.split('.');for(var i=0;i<a.length;++i){a[i]=Number(a[i]);}
for(var i=0;i<b.length;++i){b[i]=Number(b[i]);}
if(a.length==2){a[2]=0;}
if(a[0]>b[0])return true;if(a[0]<b[0])return false;if(a[1]>b[1])return true;if(a[1]<b[1])return false;if(a[2]>b[2])return true;if(a[2]<b[2])return false;return true;},enableAlerts:function(){deployJava.browserName=null;deployJava.debug=true;},poll:function(){deployJava.refresh();var postInstallJREList=deployJava.getJREs();if((deployJava.preInstallJREList.length==0)&&(postInstallJREList.length!=0)){clearInterval(deployJava.myInterval);if(deployJava.returnPage!=null){location.href=deployJava.returnPage;};}
if((deployJava.preInstallJREList.length!=0)&&(postInstallJREList.length!=0)&&(deployJava.preInstallJREList[0]!=postInstallJREList[0])){clearInterval(deployJava.myInterval);if(deployJava.returnPage!=null){location.href=deployJava.returnPage;}}},writePluginTag:function(){var browser=deployJava.getBrowser();if(browser=='MSIE'){document.write('<'+'object classid="clsid:CAFEEFAC-DEC7-0000-0000-ABCDEFFEDCBA" '+'id="deployJavaPlugin" width="0" height="0">'+'<'+'/'+'object'+'>');}else if(browser=='Netscape Family'&&deployJava.allowPlugin()){deployJava.writeEmbedTag();}},refresh:function(){navigator.plugins.refresh(false);var browser=deployJava.getBrowser();if(browser=='Netscape Family'&&deployJava.allowPlugin()){var plugin=document.getElementById('deployJavaPlugin');if(plugin==null){deployJava.writeEmbedTag();}}},writeEmbedTag:function(){var written=false;if(navigator.mimeTypes!=null){for(var i=0;i<navigator.mimeTypes.length;i++){if(navigator.mimeTypes[i].type==deployJava.mimeType){if(navigator.mimeTypes[i].enabledPlugin){document.write('<'+'embed id="deployJavaPlugin" type="'+
deployJava.mimeType+'" hidden="true" />');written=true;}}}
if(!written)for(var i=0;i<navigator.mimeTypes.length;i++){if(navigator.mimeTypes[i].type==deployJava.oldMimeType){if(navigator.mimeTypes[i].enabledPlugin){document.write('<'+'embed id="deployJavaPlugin" type="'+
deployJava.oldMimeType+'" hidden="true" />');}}}}},do_initialize:function(){deployJava.writePluginTag();if(deployJava.locale==null){var loc=null;if(loc==null)try{loc=navigator.userLanguage;}catch(err){}
if(loc==null)try{loc=navigator.systemLanguage;}catch(err){}
if(loc==null)try{loc=navigator.language;}catch(err){}
if(loc!=null){loc.replace("-","_")
deployJava.locale=loc;}}}};deployJava.do_initialize();

//
// -------------------------------------------------------------------------------
//

//////////////////////////////////////////////////////////////////////////
//// Marvin utility functions
//// Copyright (c) 1998-2012 ChemAxon Ltd., Peter Csizmadia,
////                          Ferenc Csizmadia, Tamas Vertse, Gabor Bartha
//////////////////////////////////////////////////////////////////////////

var modernjava = "1.6.0_10";
var minmarvinjava = "1.6"

// Check whether supported Java version is installed
// @minjver - minimum Java requirement
// @return true - if required Java is available, else false
function isJavaInstalled(minjver) {
	var jplugins = deployJava.getJREs(); 
	if(jplugins == "") { // no java plugins
		return false;
	}
	var status = false;
	// check only the last plugin
	for(count=0; count < jplugins.length; count++) {
		status = (jplugins[count] >= minjver);
	}
	return status;
}

// Check whether the current browser is Firefox 3.6.x
function isFirefox3_6() {
	var s = navigator.userAgent;
	if(s.lastIndexOf("Firefox/3.6") > -1) {
		return true;
	}
	return false;
}


// Check whether current browser supports installed Java.
// return 0 - Proper Java Plugin is installed, 1 - Firefox 3.6 without modern java, 2 - No Java Plugin.
function checkJava() {
	var errmsg = 0;
	if(isFirefox3_6()) { // current browser is Firefox 3.6.x
		if(!isJavaInstalled(modernjava)) { // not modern java
			errmsg = 1;
		}
	} else {
		if(deployJava.getJREs() == "") { // no Java Plugin
			errmsg = 2;
		}
	}
	if(!isJavaInstalled(minmarvinjava)) { // Marvin's Java requirement
		errmsg = 3;
	}
	return errmsg;
}

// Provides error message for the given error code
// see error codes in checkJava function
function getJavaErrorMessage(errorcode) {
	var msg = "";
	if(errorcode == 1) {
		msg = "The applet cannot run, because Firefox 3.6 is only supported with \n"+
"next generation Java plug-in versions JRE 1.6.0_10 or higher.";
	} else if(errorcode == 2) {
		msg = "The applet cannot run because your java plug-in is not available.";
	} else if(errorcode == 3) {
		msg = "The version number of the Java plugin is lower than "+minmarvinjava+".\n"+
			"Current Marvin requires at least version "+minmarvinjava+".";
	}
	return msg;
}

// ----------------------------------------------------------------------------------------

//
// "Public" parameters that can be specified before msketch_begin/mview_begin.
//

// The MAYSCRIPT attribute for MarvinView.
var mview_mayscript = false;

// The MAYSCRIPT attribute for MarvinSketch.
var msketch_mayscript = false;

// The MAYSCRIPT attribute for MarvinSketch.
var mspace_mayscript = false;

// Applet names, unspecified by default
var msketch_name = "";
var mview_name = "";
var mspace_name = "";

var msketch_legacy=!isLeopardSafari();
var mview_legacy=!isLeopardSafari();
var mspace_legacy=!isLeopardSafari();

// Applet can use these additional jar files. If more then one additional
// file are used, files has to be separated by colon. 
// e.g to generate svg from the applet, the batik-core.jar has to be used.
var msketch_usedJars = "";

// Use "builtin" for the browser's default JVM, "plugin" for the Java Plugin.
var marvin_jvm = "";

// GUI used: "awt" or "swing"
var marvin_gui = "";

//
// Internal functions
//

var marvin_jvm0 = "";
var marvin_gui0 = "";
var applet_type; // depends on marvin_jvm, 0=<applet>, 1=<embed>, 2=<object>

// displays an image on the applet's canvas while applet is loading
var loading_image = "img/loading.gif";

// Set marvin_jvm if the URL of the HTML page has a jvm parameter.
if(location.search.lastIndexOf("jvm=plugin") >= 0) {
	marvin_jvm0 = "plugin";
	if(browser_mozilla_version()==5) {//builtin if Netscape 6-
	    marvin_jvm0 = "builtin";
	}
}
if(location.search.lastIndexOf("jvm=builtin") >= 0) {
	marvin_jvm0 = "builtin";
}

// Set marvin_gui if the location string contains the gui parameter.
if(location.search.lastIndexOf("gui=swing") >= 0) {
	marvin_gui0 = "swing";
}
if(location.search.lastIndexOf("gui=awt") >= 0) {
	marvin_gui0 = "awt";
}

var _appletstrbuf = "";

function browser_parse_version0(name) {
	var brz = navigator.userAgent;
	var i = brz.lastIndexOf(name);
	if(i >= 0) {
		var s = brz.substring(i + name.length);
		var j = s.indexOf(".");
		if(j < 0) {
			j = s.indexOf(" ");
		}
		return s.substring(0, j);
	}
	return 0;
}

function browser_parse_version(name) {
	var v = browser_parse_version0(name + "/");
	if(!v) {
		v = browser_parse_version0(name + " ");
	}
	return v;
}

// Returns mozilla version for mozilla and compatible browsers.
function browser_mozilla_version() {
	var s = navigator.userAgent;

	// indexOf is buggy in Netscape 3
	if(s.lastIndexOf("Mozilla/3.") == 0) {
		return 3;
	} else if(s.lastIndexOf("Mozilla/") == 0) {
		return s.substring(8, s.indexOf("."));
	} else {
		return 0;
	}
}

// Returns browser version in Opera, 0 in other browsers.
function browser_Opera() {
	return browser_parse_version("Opera");
}

// Returns mozilla version in Netscape, 0 in other browsers.
function browser_NetscapeMozilla() {
	var brz = navigator.userAgent;
	var compat = brz.toLowerCase().lastIndexOf("compatible") >= 0;
	var opera = browser_Opera();
	if(brz.lastIndexOf("Mozilla/") == 0 && !compat && !opera) {
		return browser_mozilla_version();
	} else {
		return 0;
	}
}

// Returns browser version in MSIE, 0 in other browsers.
function browser_MSIE() {
	var msie = navigator.appName.lastIndexOf("Microsoft Internet Explorer") == 0;
	var opera = browser_Opera();
	if(msie && !opera) {
		return browser_parse_version("MSIE");
	}
	return 0;
}

function browser_Chrome(){
	return navigator.userAgent.indexOf("Chrome")>=0;
}

//Returns the OS version (9 or 10) if it is mac, 0 if isn't.
function macOsVer() {
	var v = navigator.appVersion;
	var vv = navigator.userAgent.toLowerCase();
	var mac = 0;
	if(v.indexOf("Mac") > 0) {
		mac = 9;
		if(vv.indexOf("os x") > 0) {
			mac = 10;
		}
	}
	return mac;
}

function isLeopardSafari() {
    var agent = navigator.userAgent;
    var isLeopard = agent.lastIndexOf("Intel Mac OS X 10_5") > 0 || agent.lastIndexOf("Intel Mac OS X 10.5") > 0 || agent.lastIndexOf("Intel Mac OS X 10_6") > 0 || agent.lastIndexOf("Intel Mac OS X 10.6") > 0;
    if(isLeopard) {
        return agent.lastIndexOf("Safari/") > 0;
    }
    return false;
}

function marvin_default_jvm()
{
    var osver = macOsVer();
    var mozver = browser_NetscapeMozilla();
    // Mac always use built-in Java
    // Netscape 4 prefers built-in Java
    if(osver >= 9 || mozver == 4) {
        return "builtin";
    } else {
        return "plugin";
    }
}

// Determines default GUI (Swing or AWT) from JVM and browser type.
function marvin_default_gui(jvm)
{
    var osver = macOsVer();
    var mozver = browser_NetscapeMozilla();
    // Only OS 9 and Netscape 4 uses AWT
    if(osver == 9 || mozver == 4) {
	return "awt"
    } else {
	return "swing";
    }
}

var mayscrDefined = false;

// "" string if no problem, error message if applet initalization has been failed
var java_error_msg = getJavaErrorMessage(checkJava());

function applet_begin(jvm, codebase, archive, code, width, height, name, mayscr)
{
	if(java_error_msg != "") {
            _appletstrbuf =  "<p><strong>"+java_error_msg+"</strong></p>";
            return _appletstrbuf;
        }
	var netscape = browser_NetscapeMozilla();
	var msie = browser_MSIE();
	var opera = browser_Opera();
	var chrome = browser_Chrome();
	applet_type = 0; // <applet>
	if(jvm == "plugin") {
		if((netscape || opera) && ! chrome) {
			applet_type = 1; // <embed> in Netscape, Opera and Mozilla
		} else if(msie) {
			applet_type = 2; // <object> in Microsoft
		}
	}
	var s;
	if(applet_type == 1) {
		s = '<embed TYPE="application/x-java-applet;version=1.6"\n';
		s += ' PLUGINSPAGE="https://java.sun.com/javase/downloads/index.jsp"\n';
	} else if(applet_type == 2) {
		s = '<object CLASSID="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"\n'; // highest installed version of Java Plug-in.
		s += ' CODEBASE="https://java.sun.com/update/1.6.0/jinstall-6u25-windows-i586.cab#Version=1,6,0,0"\n';
	} else {
		s = '<applet';
	}
	if(mayscr) {
		if(applet_type == 0) { // <applet>
			s += ' MAYSCRIPT';
		} else if(applet_type == 1) { // <embed>
			s += ' MAYSCRIPT=true';
		}
                mayscrDefined = true;
	}
	s += ' WIDTH='+width+' HEIGHT='+height;
	if(name) {
		s += ' ID="'+name+'" NAME="'+name+'"'; // define name attribute to refer with document.MSketch, in other cases refer by id: document.getElementById("MSketch")
	}
	s += '\n';
	if(msketch_usedJars != "") { 	
		archive += "," + msketch_usedJars;
	}
	if(applet_type != 2) { // <applet> and <embed>
		s += ' CODEBASE="'+codebase+'" ARCHIVE="'+archive+'" CODE="'+code+'"';
	}
	if(applet_type != 1) { // <applet> and <object>
		s += '>\n';
        }
	if(applet_type == 2) { // <object>
		s += '<param NAME="codebase" VALUE="'+codebase+'">\n';
		s += '<param NAME="archive" VALUE="'+archive+'">\n';
		s += '<param NAME="code" VALUE="'+code+'">\n';
		s += '<param NAME="scriptable" VALUE="true">\n';
		if(mayscr) {
			s += '<param NAME="mayscript" VALUE="true">\n';
                        mayscrDefined = true;
		}
	}
	_appletstrbuf = s;
	return s;
}

var skinDefined = false;
var isSetLegacy = false;
function applet_param(name, value)
{
	if(java_error_msg != "") {
		return;
	}
	var s;
        if(name == "skin") {
            // do not overwrite skin later
            skinDefined = true;
        }
        if(name != "legacy_lifecycle") {
            value="@javascript-URI-encoded@"+encodeURIComponent(value);
        } else {
            isSetLegacy=true;
        }
	if(applet_type == 1) { // <embed>
		s = ' '+name+'="'+value+'"\n';
	} else { // <applet> and <object>
		s = '<param NAME="'+name+'" VALUE="'+value+'">\n';
	} 
	_appletstrbuf += s;
	return s;
}

function applet_end(type)
{
	if(java_error_msg != "") {
		s0 = _appletstrbuf;
		return s0;
	}
	var s;
	var msg = "<center><b>YOU CANNOT SEE A JAVA APPLET HERE</b></center>\n";
        var legacy = !isLeopardSafari();
        if(type == 'msketch') {
            legacy = msketch_legacy;
        } else if(type == 'mview') {
            legacy = mview_legacy;
        } else {
            legacy = mspace_legacy;
        }

	if(applet_type == 1) { // <embed>
            if(!isSetLegacy && legacy) {
                s = ' legacy_lifecycle="true"\n';
            } else {
                s = '';                
            }
                s += ' java_arguments="-Djnlp.packEnabled=true"\n';
		s += ' codebase_lookup="false"\n';
		s += '>\n<noembed>\n';
		s += msg;
		s += '</noembed>\n';
	} else if(applet_type == 2) { // <object>
                if(!isSetLegacy && legacy) {
                    s = '<param name="legacy_lifecycle" value="true"/>\n';
                } else {
                    s = '';
                }
                s += '<param name="java_arguments" value="-Djnlp.packEnabled=true"/>\n';
		s += '<param name="codebase_lookup" value="false"/>\n';
		s += msg;
		s += '</object>\n';
	} else { // <applet>
                if(!isSetLegacy && legacy) {
                    s = '<param name="legacy_lifecycle" value="true"/>\n';
                } else {
                    s = '';
                }
                s += '<param name="java_arguments" value="-Djnlp.packEnabled=true"/>\n';
                if(mayscrDefined && !skinDefined && isLeopardSafari()) {
                    s += '<param name="skin" value="javax.swing.plaf.metal.MetalLookAndFeel"/>\n'+msg;
                } else {
		    s += msg;
                }        
		s += '<param name="codebase_lookup" value="false"/>\n';        
		s += '</applet>\n';
	}
	_appletstrbuf += s;
        s = _appletstrbuf;
        _appletstrbuf = "";
	return s;
}


//
// "Public" functions
//


// Determine the JVM.
function marvin_get_jvm() {
	var jvm = marvin_jvm0;
	if(!jvm) {
		jvm = (marvin_jvm != "")? marvin_jvm : marvin_default_jvm();
	}
	jvm = jvm.toLowerCase();
	return jvm;
}

// Determine GUI type ("awt" or "swing").
function marvin_get_gui() {
	var gui = marvin_gui0;
	if(!gui) {
		gui = (marvin_gui != "")? marvin_gui : marvin_default_gui(marvin_get_jvm());
	}
	return gui;
}

// If msketch is able to generate image returns 1 else 0. It is depends on 
// the browser.
function msketch_detect() 
{
	var netscape = browser_NetscapeMozilla();
	var msie = browser_MSIE();
	if(msie > 0) {
	    marvin_jvm = "plugin";
	} else if(netscape > 0) {
	    if(netscape > 4) {
	        marvin_jvm = "builtin";
	    } else {
	    	alert("Image generation can be run only in SWING mode.\n"+
			"Your browser does not support SWING.");
		return 0;
	    }
	}
	return 1;
}

function msketch_begin(codebase, width, height){
	msketch_begin(codebase, width, height, isLeopardSafari());
}

function msketch_begin(codebase, width, height, oldbehaviour)
{
        if(oldbehaviour == undefined) {
            oldbehaviour = isLeopardSafari();
        }
	var archive, code;
	var jvm = marvin_get_jvm();
	var gui = marvin_get_gui();
	if (oldbehaviour){
		code = "chemaxon/marvin/applet/JMSketch";
	} else {
		code = "chemaxon/marvin/applet/JMSketchLaunch";
	}
	if(gui.toLowerCase() == "swing") {
		if (oldbehaviour){
			archive = "jmarvin.jar";
		} else {
			archive = "appletlaunch.jar"
		}
	} else {
		return;
//		archive = "marvin.jar";
//		code = "MSketch";
	}
	applet_begin(jvm, codebase, archive, code, width, height, msketch_name,
		     msketch_mayscript);
}


function msketch_param(name, value)
{
	return applet_param(name, value);
}

function msketch_end()
{
	s0 = msketch_end_to_string();
	document.write(s0);
}

function msketch_end_to_string() {
	s0 = applet_end("msketch");
	msketch_name = "";
	return s0;
}

function mview_begin(codebase, width, height){
	mview_begin(codebase, width, height, isLeopardSafari());
}

function mview_begin(codebase, width, height, oldbehaviour)
{
        if(oldbehaviour == undefined) {
            oldbehaviour = isLeopardSafari();
        }
	var archive, code;
	var jvm = marvin_get_jvm();
	var gui = marvin_get_gui();
	if(gui.toLowerCase() == "swing") {
		if (oldbehaviour){
	    	archive = "jmarvin.jar";
			code = "chemaxon/marvin/applet/JMView";
		} else {
			archive = "appletlaunch.jar";
			code = "chemaxon/marvin/applet/JMViewLaunch";
		}
	} else {
		return;
//		archive = "marvin.jar";
//		code = "MView";
	}
	applet_begin(jvm, codebase, archive, code, width, height, mview_name,
		     mview_mayscript);
}

function mview_param(name, value)
{
	return applet_param(name, value);
}

function mview_end_to_string()
{
	s0 = applet_end("mview");
	mview_name = "";
	return s0;
}

function mview_end() {
	s0 = mview_end_to_string();
	document.write(s0);
}

function mspace_begin(name,codebase, width, height)
{
    java_error_msg = getJavaErrorMessage(checkJava());
    if(java_error_msg != "") {
	_appletstrbuf = "<p><strong>"+java_error_msg+"</strong></p>";
	return _appletsrtbuf;
    }
    var jvm = marvin_get_jvm();
    var archive = "mspace.jar,jmarvin.jar, jextexp.jar,dist/lib/applet-launcher.jar,dist/lib/nativewindow.all.jar,dist/lib/jogl.all.jar,dist/lib/gluegen-rt.jar";
    var code = "chemaxon/marvin/applet/MSpaceApplet";
    var netscape = browser_NetscapeMozilla();
    var mayscr = mspace_mayscript;
    var msie = browser_MSIE();
    var opera = browser_Opera();
    applet_type = 0; // <applet>
    if(jvm == "plugin") {
	if(netscape || opera) {
			applet_type = 1; // <embed> in Netscape and Opera
	} else if(msie) {
			applet_type = 2; // <object> in Microsoft
	}
    }
    var s;
    if(applet_type == 1) {
	s = '<embed TYPE="application/x-java-applet;version=1.6"\n';
	s += ' PLUGINSPAGE="https://java.sun.com/javase/downloads/index.jsp"\n';
    } else if(applet_type == 2) {
	s = '<object CLASSID="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"\n'; // highest installed version of Java Plug-in.
	s += ' CODEBASE="https://java.sun.com/update/1.6.0/jinstall-6u25-windows-i586.cab#Version=1,6,0,0"\n';
    } else {
	s = '<applet';
    }
    if(mayscr) {
	if(applet_type == 0) { // <applet>
	    s += ' MAYSCRIPT';
	} else if(applet_type == 1) { // <embed>
	    s += ' MAYSCRIPT=true';
	}
    }
    s += ' WIDTH='+width+' HEIGHT='+height;
    if(name) {
	s += ' ID="'+name+'" NAME="'+name+'"'; // define name attribute to refer with document.MSketch, in other cases refer by id: document.getElementById("MSketch")
    }
    if(applet_type != 2) { // <applet> and <embed>
	s += ' CODEBASE="'+codebase+'" ARCHIVE="'+archive+'" CODE="'+code+'"';
    }
    if(applet_type != 1) { // <applet> and <object>
	s += '>\n';
//        s += '<param NAME="image" VALUE="'+loading_image+'">\n';
    } else {
//        s += ' IMAGE="'+loading_image+'"\n';
    }
    if(applet_type == 2) { // <object>
	s += '<param NAME="codebase" VALUE="'+codebase+'">\n';
	s += '<param NAME="archive" VALUE="'+archive+'">\n';
	s += '<param NAME="code" VALUE="'+code+'">\n';
	s += '<param NAME="scriptable" VALUE="true">\n';
        if(mayscr) {
	    s += '<param NAME="mayscript" VALUE="true">\n';
	}
    }
	_appletstrbuf = s + "\n";
	return s;
}

function mspace_param(name, value) {
    if(java_error_msg != "") {
	return;
    }
    var s;
    if(applet_type == 1) { // <embed>
		s = name+'="'+value+'"\n';
    } else { // <applet> and <object>
	s = '<param NAME="'+name+'" VALUE="'+value+'">\n';
    }
	_appletstrbuf += s;
    return s;
}

function mspace_end_to_string() {
    s0 = applet_end("mspace");
    mspace_name = "";
	return s0;
}

function mspace_end() {
	s0 = mspace_end_to_string();
	document.write(s0);
}

function links_set_search(s) {
	for(i = 0; i < document.links.length; ++i) {
		var p = document.links[i].pathname;
		if(p.lastIndexOf(".html") > 0 || p.lastIndexOf(".jsp") > 0) {
			var href = document.links[i].href;
			var k = href.indexOf('?');
			if(k > 0) {
				href = href.substring(0, k);
			}
			document.links[i].href = href + s;
		}
	}
}

function unix2local(s) {
	var strvalue = "" + s;
	var v = navigator.appVersion;
	if(v.indexOf("Win") > 0) {
		strvalue = strvalue.split("\r\n").join("\n"); // To avoid "\r\r\n"
		return strvalue.split("\n").join("\r\n");
	} else { // Unix
		return strvalue;
	}
}

function local2unix(s) {
	var strvalue = "" + s;
	var v = navigator.appVersion;
	if(v.indexOf("Win") > 0) {
		return strvalue.split("\r").join("");
	} else { // Unix
		return strvalue;
	}
}

