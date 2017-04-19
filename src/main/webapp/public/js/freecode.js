/**
 * Created by Matt on 4/11/17.
 * This handles the frontend code for checking and sending freecode task information
 */

var editor;
//shorthand for document.ready
$(function () {
    //set up the ace editor
    editor = ace.edit("freecodeeditor");
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
            next_task=$page.data("next-task"),
            $output=$("#output");

        expected_output = String(expected_output).replace("\n","<br/>") + "<br/>";

        // Empty the output when running.
        $output.empty();

        // Redirect console.log and window.one-error to output.
        // var former = window.console.log;
        window.console.log = function (msg) {
            $output.append(document.createTextNode(msg)).append($('<br />'));
        };

        window.onerror = function (messageOrEvent, source, lineno, colno, error) {
            $output.text(messageOrEvent);
        };
        //pull out any xhr requests
        //TODO: how do we add test-case variables to input?
        //TODO: maybe append them as var declarations in the beginning of the cleaned code string?
        var code = editor.getValue();
        code = clean(code);
        //run the code
        eval(code);
        //check if it's the right values
        var results=$output.html(),
            expected=(results==clean_output(expected_output));
        //check that it matches the expected output
        if(expected){
            completed = true;
            // Adding next arrow to next task.
            if (next_task > 0) {
              $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" href="/lesson/' + lesson_id + '/task/' + next_task + '" onClick="save()"></a>'));
            }
            // If last lesson, just redirect to user page.
            else {
              $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" href="/user" onClick="save()"></a>'));
            }
            $page.data("completed",true);
            //save the result
            save(editor.getValue(),true);
        }
        });

});

function save(){
    var $pg=$("#page"),
        task_id=$pg.data("task-id"),
        lesson_id=$pg.data("lesson-id");

    var data = {
        freecode: editor.getValue(),
        completed:$pg.data("completed")
    };
    $.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        method:'POST',
        url:'/savelesson/'+lesson_id+'/freecodetask/'+task_id,
        data:JSON.stringify(data),
        dataType:"json"
    });
}

//cleans up the the code using regex
function clean(code) {
    //code : the string of js code we need to clean up and ensure is not malicious
    var regex = /(XMLHttpRequest)|(xhr\.\w*)|(\$\.\w*)|(document\.getElementBy\w*\(\w*\))|(document\.getElementsBy\w*\(\w*\))/g;
    //replace the offending functions and return the sanitized code
    return code.replace(regex, " ");

}

//clean the br tags
function clean_output(out) {
    //NOTE: make sure to use the g flag so it finds all (global) instances
    var brregex = /<br\/>/g;
    var ret =  out.replace(brregex,"<br>");
    return ret;
}
