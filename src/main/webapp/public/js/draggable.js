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
  return $(value).text().replace(/\s+/g, " ").trim();
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

  resize_height = Math.max(window_height, content_height);

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
  $('#editor, #toolbox, .holds-one, .holds-list').sortable({
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

    startTutorial();
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

  var codeLines = [];
  for (var i = 0; i < codeArray.length; i++) {
    if (codeArray[i] != '{') {
      codeLines.push(codeArray[i]);
    }
    else {
      s = codeArray[i];
      openBrackets = 1;
      while (openBrackets > 0) {
        i++;
        s += codeArray[i] + '\n';
        if (codeArray[i] === '{') {
          openBrackets += 1
        }
        else if (codeArray[i] === '}') {
          openBrackets -= 1
        }
      }
      codeLines[codeLines.length-1] += s;
    }
  }

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
    for (i = 0; i < codeLines.length; i++) {
      try {
        eval(codeLines[i]);
      }
      // Flash on a block that errors.
      catch(e) {
        value = $('#editor').children().eq(i).find('.code-block');
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
      $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" href="/lesson/'
                                                        + lesson_id + '/task/' + next_task + '" onClick="save()"></a>'));
    }
    // If last lesson, just redirect to user page.
    else {
      $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right"' + 
                                                      'href="/user" onClick="save()"></a>'));
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

function startTutorial() {
    // Setup the tutorial intro for the user page
    var tutorial = introJs();
    tutorial.setOptions({
        steps: [
            {
                // Focus: instructions
                element: '.step-1',
                intro: 'These are the instructions for completing the task you are on. Follow these instructions closely to move on in the lesson.',
                position: 'right'
            },
            {
                // Focus: editor
                element: '.step-2',
                intro: 'This is the Editor where you will build your program using code blocks',
                position: 'left'
            },
            {
                // Focus: toolbox
                element: '.step-3',
                intro: 'By dragging the blocks from this Toolbox into the Editor you can make programs.',
                position: 'top'
            },
            {
                // Focus: run button
                element: '.step-4',
                intro: 'To test your program to see if you are ready to move on to the next task, click this button and it will run the code you built with blocks in the Editor.',
                position: 'right'
            },
            {
                // Focus: output
                element: '.step-5',
                intro: 'This box will display the output given by running your program. This output needs to be the correct output as specified in the instructions in order to move on to the next task.',
                position: 'left'
            },
            {
                // Focus: hint button
                element: '.step-6',
                intro: 'If you are having trouble getting the right output for your program, click this button to get a hint.',
                position: 'right'
            },
            {
                // Focus: save task
                element: '.step-7',
                intro: 'You can click this button at any time to save the progress you\'ve made.',
                position: 'left'
            },
            {
                // Focus: undo changes
                element: '.step-8',
                intro: 'Pressing this button will undo all of the changes made since your last save.',
                position: 'bottom'
            },
            {
                // Focus: restart task
                element: '.step-9',
                intro: 'To restart the task you are on from the beginning click this button.',
                position: 'bottom'
            },
            {
                // Focus: restart lesson
                element: '.step-10',
                intro: 'To restart all of the tasks in the lesson click this button.',
                position: 'bottom'
            },
            {
                // Focus: task navigation
                element: '.step-11',
                intro: 'Once you have completed a task you can navigate to it directly at anytime using this task navigation bar. Hover over the numbers in the bar to get the title of that particular task.',
                position: 'bottom'
            },
            {
                // Focus: home button
                element: '.step-12',
                intro: 'To go back to the home page where you were before, click this button. You\'re now ready to complete your first task!',
                position: 'bottom'
            }
        ]
    });

    // Setup the page transition for the tutorial to navigate home?
    //tutorial.setOption('doneLabel', 'Start Lesson').start().oncomplete(function() {
    //    window.location.href = "/lesson/" + lesson_id;
    //});

    //tutorial.start();
}