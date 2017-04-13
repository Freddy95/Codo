/**
 * Created by Matt on 4/11/17.
 * This handles the frontend code for checking and sending freecode task information
 */

//shorthand for document.ready
$(function(){
    var editor = ace.edit("freecodeeditor");
    editor.setTheme("ace/theme/monokai");
    editor.getSession().setMode("ace/mode/javascript");

});
