/**
 * Created by Matt on 4/11/17.
 * This handles the frontend code for checking and sending freecode task information
 */

//shorthand for document.ready
$(function () {
    //set up the ace editor
    var editor = ace.edit("freecodeeditor");
    editor.setTheme("ace/theme/chrome");
    //change the font to match the rest of the app
    editor.setOptions({
        fontFamily: "Source Code Pro",
        fontSize: "12pt"
    });
    editor.getSession().setMode("ace/mode/javascript");


    //attach event handlers to some of the buttons
    var $runbtn = $("#runbutton");
    $runbtn.click( function () {
        // get the expected output,test case input, and next task/completed status
        var $page=$("#page"),
            test_case=$page.data("test-case"),
            expected_output=$page.data("ex-output"),
            completed=$page.data("completed"),
            next_task=$page.data("next-task");

        // Empty the output when running.
        $('#output').empty();

        // Redirect console.log and window.one-error to output.
        // var former = window.console.log;
        window.console.log = function (msg) {
            $('#output').append(document.createTextNode(msg)).append($('<br />'));
        };

        window.onerror = function (messageOrEvent, source, lineno, colno, error) {
            $('#output').text(messageOrEvent);
        };
        //pull out any xhr requests
        //TODO: how do we add test-case variables to input?
        //TODO: maybe append them as var declarations in the beginning of the cleaned code string?
        var code = editor.getValue();
        code = clean(code);
        // run the cleaned code
        //check if it's the right values
        var results=eval(code),
            expected=(results==expected_output);
        });
});

//cleans up the the code using regex
function clean(code) {
    //code : the string of js code we need to clean up and ensure is not malicious
    var regex = /(XMLHttpRequest)|(xhr\.\w*)|(\$\.\w*)|(document\.getElementBy\w*\(\w*\))|(document\.getElementsBy\w*\(\w*\))/;
    //replace the offending functions and return the sanitized code
    return code.replace(regex, " ");

}
