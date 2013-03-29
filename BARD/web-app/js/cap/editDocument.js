/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 2/7/13
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */

/*
	Add the list of buttons in the panel. Remove 'image' (adding images) and 'bgcolor' (changing text backgroung color)
*/

bkLib.onDomLoaded(function() {
    new nicEditor({
    	iconsPath : '/BARD/images/nicedit/nicEditorIcons.gif',
    	buttonList : ['bold', 'italic', 'underline', 'left', 'center', 'right', 'justify', 'ol', 'ul', 'subscript', 'superscript', 'strikethrough', 'removeformat', 'indent', 'outdent', 'hr', 'forecolor', 'link', 'unlink', 'fontSize', 'fontFamily', 'fontFormat', 'xhtml']
    }).panelInstance('documentContent');
});