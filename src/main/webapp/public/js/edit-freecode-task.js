/**
 * Created by Matt on 4/27/17.
 * Javascript to handle the ajax for creating a user-defined freecode lesson
 */
$(function(){
    //set up the ace editor
    editor = ace.edit("freecodeeditor");
    editor.setTheme("ace/theme/chrome");
    //change the font to match the rest of the app
    editor.setOptions({
        fontFamily: "Source Code Pro",
        fontSize: "12pt"
    });
    editor.getSession().setMode("ace/mode/javascript");


});