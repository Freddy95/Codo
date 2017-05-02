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
      $pageInfo = $("#page-info"),
      title = $pageInfo.data("title"),
      taskId = $pageInfo.data("task-id"),
      lessonId=$pageInfo.data("lesson-id"),
      type=$pageInfo.data("type");

  //build the json that we'll send to the backend
  save = function(){
    // get the url we'll send the ajax call to
    // as well as a fake uid for the lesson json
    // TODO: do we nee dthis? ask henry
      //FIXME: use a query paramater
    var baseurl= "http://localhost:8080/savefreecodetask/",
      id = 0;

    //make a data object to send to the backend
    var data={};
    //get the instructions and assign them to the data we'll return
    data.instructions = $instructionsText.text();
    data.hint = $hintText.text();
    data.completed = false;
    data.toolbox=null;
    data.editor=null;
    data.title=title;
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
     data:JSON.stringify(data),
    });

  };

});
// Adds an input and output.
function addOutput() {
  if ($('#ex-output').children().length === 1) {
      $('#ex-output').find('i').removeClass('fa-disabled');
      $('#test-case').find('i').removeClass('fa-disabled');
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


