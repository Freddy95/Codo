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

// Convenience methood to get value from a placeholder.
function getCodeBlock(node) {
  return $(node).children().children();
}

// Extracts block values from a node.
function getCodeBlockAttr(value) {
  var block = {};
  block.block_id = parseInt($(value).attr('id'));
  if (($(value).attr('data-children')) === "false") {
    block.value = $(value).text();
  }
  else {
    block.children = [];
    $.each(getCodeBlock($(value)), function(index, v) {
      child_block = getCodeBlockAttr(getCodeBlock($(v)));
      block.children.push(child_block);
    });
  }
  return block;
}

function getCodeBlockValue(value) {
  var s;
  if (($(value).attr('data-children')) === "false") {
    s = $(value).text();
  }
  else {
    // block.children = [];
    // $.each(getCodeBlock($(value)), function(index, v) {
    //   child_block = getCodeBlockAttr($(v).children());
    //   block.children.push(child_block);
    // });
  }
  return s;
}

function resize_content() {
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
  $('#block-content').css("height", (Math.max(window_height, content_height) - block_outer) + "px");
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

  codeArray = [];

  // Add code to the. stored array.
  $.each($('#editor').children(), function(index, value) {
    codeArray.push(getCodeBlockValue(getCodeBlock(value)));
  });

  fullCode = codeArray.join('\n');

  // Redirect console.log and window.one-error to output.
  // var former = window.console.log;
  window.console.log = function(msg) {
    $('#output').append(document.createTextNode(msg)).append($('<br />'));
  }

  window.onerror = function(messageOrEvent, source, lineno, colno, error) {
    $('#output').text(messageOrEvent);
  }

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

// Save data.
function save() {
  var data = {};

  var editor = [];
  var toolbox = [];

  $.each(getCodeBlock($('#editor').children()), function(index, value) {
    var block = getCodeBlockAttr(value);
    editor.push(block);
  });

  $.each(getCodeBlock($('#toolbox').children()), function(index, value) {
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
