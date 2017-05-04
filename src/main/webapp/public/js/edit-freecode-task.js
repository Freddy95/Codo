/**
 * Created by Matt on 4/27/17.
 * Javascript to handle the ajax for creating a user-defined freecode lesson
 */

//declare this handler up here so we can assign it later
var save = null;
$(function(){
    //set up the ace editor
    var editor = ace.edit("freecodeeditor");
    editor.setTheme("ace/theme/chrome");
    //change the font to match the rest of the app
    editor.setOptions({
        fontFamily: "Source Code Pro",
        fontSize: "12pt"
    });
    editor.getSession().setMode("ace/mode/javascript");

  // get the buttons from the dom
  var $instructionsText = $("#instructions"),
    $hintText = $("#hint"),
    $outputText = $("#output"),
    $testCase = $("#test-case"),
    $expectedOutput = $("#ex-output"),
      $taskTitle = $("#task-title"),
      $pageInfo = $("#page-info"),
      taskId = $pageInfo.data("task-id"),
      lessonId=$pageInfo.data("lesson-id"),
      type=$pageInfo.data("type");

  $("#runbutton").click(function () {
     // get the expected output,test case input, and next task/completed status
        var $output=$("#output")
            $page = $("#page-info");

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
        //iterate over each test case
        var is_correct=true;
        for(var i = 0; i<test_case.length; i++) {
            var code = editor.getValue(),
                test = test_case[i],
                expect = expected_output[i];
            expect += "<br>"; // add a line break to the end of the output
            code = clean(code);
            //append the variables to the code and run it
            code = test + code;
            //run the code
            eval(code);
            //check if it's the right values
            var results = $output.html(),
                expected = (expect == clean_output(results));

            if(!expected){
                is_correct=false;
                break;
            }
        }
        //check that it matches the expected output
        if(is_correct && (completed!=true)){
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

  //build the json that we'll send to the backend
  save = function(){
    // get the url we'll send the ajax call to
    // as well as a fake uid for the lesson json
    var baseurl= "http://localhost:8080/createlesson/"+lessonId+"/createtask/"+taskId;

    //make a data object to send to the backend
    var data={};
    //get the instructions and assign them to the data we'll return
    data.instructions = $instructionsText.val();
    data.hint = $hintText.val();
    data.completed = false;
    data.toolbox=null;
    data.editor=null;
    data.title=$("#task-title").text();
    data.task_id=taskId;
    data.type=type;
    
    var test_case = [];
    var ex_output = [];
    $testCase.find('.row-item').each(function() {
      test_case.push($(this).val());
    });
    $expectedOutput.find('.row-item').each(function() {
      ex_output.push($(this).val());
    });

    data.test_case = test_case;
    data.expected_output = ex_output;

    //get the freecode portion of the data
    data.freecode = editor.getValue();

    //do the ajax call
    //TODO: make sure the url is correct
    $.ajax({
      method:'POST',
      url: baseurl,
      dataType:'json',
      contentType: "application/json; charset=utf-8",
      data:JSON.stringify(data)
    });

  };

  deleteTask = function() {
  $.ajax({
    method:'POST',
    url: '/createlesson/' + lessonId + '/createtask/' + taskId + '/delete',
    success: function(data, status, xhttp) {
      window.location.replace('../../../createlesson/' + lessonId);
    }
  });
}

});
// Adds an input and output.
function addOutput() {
  if ($('#ex-output').children().length === 1) {
      $('#ex-output').find('.fa-minus').removeClass('fa-disabled');
      $('#test-case').find('.fa-minus').removeClass('fa-disabled');
  }
  var newInputRow = $('<div class="input-group"/>');
  var newOutputRow = newInputRow.clone();

  newInputRow.appendTo('#test-case');
  newOutputRow.appendTo('#ex-output');

  newInputRow.append('<input type="text" class="form-control row-item" placeholder="Input">' +
            '<div class="input-group-addon fa fa-minus" onClick="minusOutput(this)"></div>');
  newOutputRow.append('<input type="text" class="form-control row-item" placeholder="Output">' +
            '<div class="input-group-addon fa fa-minus" onClick="minusOutput(this)"></div>');
}

// Removes an input and output.
function minusOutput(node) {
  if ($('#ex-output').children().length > 1) {
    var index = $(node).parent().index();
    $('#ex-output').children().eq(index).remove();
    $('#test-case').children().eq(index).remove();

    if ($('#ex-output').children().length === 1) {
      $('#ex-output, #test-case').find('.fa-minus').addClass('fa-disabled');
    }
  }
}
// Allows editing of the title.
function editTitle() {
  var task_title = $('#task-title');
  var title_button = $('#title-button');
  var title_icon = $('#title-icon');
  if (task_title.is('input')) {
    task_title.replaceWith($("<span id='task-title' />").text(task_title.val()));
    title_button.attr('title', 'Edit Title');
    title_icon.removeClass('fa-check').addClass('fa-pencil');
  }
  else {
    task_title.replaceWith($("<input id='task-title' />").val(task_title.text()));
    title_button.attr('title', 'Save Title');
    title_icon.removeClass('fa-pencil').addClass('fa-check');
  }
}
