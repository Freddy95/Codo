$(document).ready(function() {
  init();
});

// Strings for grabbing values from Thymeleaf Template.
var test_case = "";
var expected_output = "";
var next_task = "";
var task_id = "";
var lesson_id = "";
var completed = "";

function resize_content() {
  console.log('Resizing');
  padding = 10;
  border = 1;

  // Calculate the height to resize the row to.
  content_height = 0;

  // Calculate the size of the editor.
  $('#editor-div').children().each(function() {
    content_height += $(this).outerHeight();
  })
  content_height += $('#editor-div').outerHeight(true) - $('#editor-div').height();

  // Calculate the size of the toolbox.
  $('#toolbox-div').children().each(function() {
    content_height += $(this).outerHeight();
  })
  content_height += $('#toolbox-div').outerHeight(true) - $('#toolbox-div').height();

  // Get the padding for the row.
  block_outer = $('#block-content').parent().outerHeight(true) - $('#block-content').parent().height();
  console.log(block_outer);
  content_height += block_outer;

  // Calculate the height left-over in the window.
  window_height = $(window).height();

  // Subtract the height of the navbars.
  $('#lesson-content').siblings().each(function() {
    window_height -= $(this).outerHeight(true);
  });

  // Take the maximum of window and content height.
  $('#lesson-content').css("height", Math.max(window_height, content_height) + "px");

  // Resize block content.
  console.log($('#block-content').height());
  $('#block-content').css("height", (Math.max(window_height, content_height) - block_outer) + "px");
  console.log($('#block-content').height());
}

function init() {
  resize_content();

  // Get the div for the page, we'll use it to get the data attribute
  // Use the dollar sign to signify that page is a jquery dom element
  var $page_info = $("#page-info");

  // Get the data attributes for the data needed in this javascript file.
  test_case = $page_info.data("test-case");
  expected_output = $page_info.data("ex-output");
  next_task = $page_info.data('next-task');
  task_id = $page_info.data('task-id');
  lesson_id = $page_info.data('lesson-id');
  completed = $page_info.data('completed');

  // Remove the element once done loading from the page.
  $page_info.remove();

  /* Expected output has newlines, we'll turn them in to <br> so it works
   * with html
   */
  expected_output = String(expected_output).replace("\n","<br>") + "<br>";

  /* Makes child elements of editor, toolbox, and holds-one draggable
   * between all elements of those types.
   */  
  $('#editor, #toolbox, .holds-one').sortable({
    connectWith: ".code-placement",
    over: function(e, ui){ 
        resize_content();
    },
    receive: function(event, ui) {
      // Restrict .holds-one to hold one element at a time.
      if ($(this).hasClass('holds-one')) {
        if ($(this).children().length > 1) {
          $(ui.sender).sortable('cancel');
        }
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

  fullCode = codeArray.join('\n');

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

function getCodeBlock(node) {
  return $(node).children().children().children();
}

function getCodeBlockAttr(value) {
  block = {};
  block.block_id = parseInt($(value).attr('id'));
  if (!($(value).attr('data-children'))) {
    block.value = $(value).text();
  }
  else {
    children = [];
    $.each(getCodeBlock($(value)), function(index, v) {
      child_block = getCodeBlockAttr(v);
      children.push(child_block);
    });
    block.children = children;
  }
  return block;
}

function save() {
  var data = {};

  var editor = [];
  var toolbox = [];

  $.each(getCodeBlock($('#editor')), function(index, value) {
    var block = getCodeBlockAttr(value);
    editor.push(block);
  });

  $.each(getCodeBlock($('#toolbox')), function(index, value) {
    var block = getCodeBlockAttr(value);
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
