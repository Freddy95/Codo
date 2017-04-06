$( init );

// Strings for grabbing values from Thymeleaf Template.
var test_case = "";
var expected_output = "";
var next_task = "";
var task_id = "";
var lesson_id = "";
var completed = "";

function init() {
  // Get the div for the page, we'll use it to get the data attribute
  // Use the dollar sign to signify that page is a jquery dom element
  var $page = $("#page");

  // Get the data attributes for the data needed in this javascript file.
  test_case = $page.data("test-case");
  expected_output = $page.data("ex-output");
  next_task = $page.data('next-task');
  task_id = $page.data('task-id');
  lesson_id = $page.data('lesson-id');
  completed = $page.data('completed');

  /* Expected output has newlines, we'll turn them in to <br> so it works
   * with html
   */
  expected_output = String(expected_output).replace("\n","<br>") + "<br>";

  /* Makes child elements of editor, toolbox, and holds-one draggable
   * between all elements of those types.
   */  
  $('#editor, #toolbox, .holds-one').sortable({
    connectWith: ".code-placement",
    receive: function(event, ui) {
      // Restrict .holds-one to hold one element at a time.
      if ($(this).hasClass('holds-one') && $(this).children().length > 1) {
          $(ui.sender).sortable('cancel');
      }
    }
  }).disableSelection();
}

function run() {
  // Empty the output when running.
  $('#output').empty();

  // Redirect console.log and window.one-error to output.
  // var former = window.console.log;
  window.console.log = function(msg) {
    $('#output').append(document.createTextNode(msg)).append($('<br />'));
  }

  window.onerror = function(messageOrEvent, source, lineno, colno, error) {
    $('#output').text(messageOrEvent);
  }

  codeArray = [];

  // Add code to the. stored array.
  $.each($('#editor').children(), function(index, value) {
    codeArray.push($(value).text());
  });

  fullCode = codeArray.join('');
  debugger;

  try {
    // Try running the full program.
    eval(fullCode);
  } catch (e) {
    // If it doesn't work, evaluate line by line.
    for (i = 0; i < codeArray.length; i++) {
      try {
        eval(codeArray[i]);
      }
      // Flash on a block that errors.
      catch(e) {
        value = $('#editor').children().eq(i);
        $(value).addClass('flash');
        setTimeout( function(){
          $(value).removeClass('flash');
        }, 1000);
        throw(e);
      }
    }
  }

  /* Adds a next arrow if it doesn't exist already and if the solution is correct.
   */
  if ($('#output-div>.card-title-block').children().length === 1 &&
      $('#output').html() === expected_output) {
    completed = true;
    // Adding next arrow to next task.
    if (next_task > 0) {
      $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" href="/lesson/' + lesson_id + '/task/' + next_task + '" onClick="save()"></a>'));
    }
    // If last lesson, just redirect to user page.
    else {
      $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" href="/user" onClick="save()"></a>'));
    }
  }
}

function save() {
  var data = {};

  var editor = [];
  var toolbox = [];

  $.each($('#editor').children(), function(index, value) {
    var block = {};
    block.value = $(value).text();
    block.block_id = parseInt($(value).attr('id'));
    //TODO: change these values later when we need to
    block.can_edit = false;
    editor.push(block);
  });

  $.each($('#toolbox').children(), function(index, value) {
    var block = {};
    block.value = $(value).text();
    block.block_id = parseInt($(value).attr('id'));
    //TODO: change these values later when we need to
    block.can_edit = false;
    toolbox.push(block);
  });

  // data.task_id = task_id;
  data.editor = editor;
  data.toolbox = toolbox;
  data.completed = completed;


  $.ajax({
    headers: { 
        'Accept': 'application/json',
        'Content-Type': 'application/json' 
    },
    'type': 'POST',
    'url': '/savelesson/' + lesson_id + '/task/' + task_id,
    'data': JSON.stringify(data),
    'dataType': 'json'
  }).done(function() {
    return true;
  }).fail(function() {
    return false;
  });
}
