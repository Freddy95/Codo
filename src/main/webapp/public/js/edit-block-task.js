$(document).ready(function() {
  init();
});

// Strings for grabbing values from Thymeleaf Template.
var test_case = [];
var expected_output = [];
var next_task = "";
var task_id = "";
var lesson_id = "";
var completed = "";
var isDirty = false;

// Convenience methood to get value from a placeholder.

function init() {
  $('#block-content').addClass('h-100');

  // Get the div for the page, we'll use it to get the data attribute
  // Use the dollar sign to signify that page is a jquery dom element

  /* Expected output has newlines, we'll turn them in to <br> so it works
   * with html
   */
   expected_output = String(expected_output).replace("\n","<br>") + "<br>";

  // Function for sortables to receive.
  var receiveFunction = function(event, ui) {
    // Fix styling for items dragged from catalog.
    if (ui.helper) {
      ui.helper.first().removeAttr('style');
    }
    
    // Restrict .holds-one to hold one element at a time.
    if ($(this).hasClass('holds-one')) {
      if ($(this).children().length > 1) {
        // Revert to the previous position if the item is sortable.
        if (ui.item.hasClass('ui-sortable-handle')) {
          $(ui.sender).sortable('cancel');
          return;
        }
        else {
          // Items dragged from the catalog do not have sortable yet, so just remove them.
          ui.helper.remove();
          return;
        }
      }
    }

    isDirty = true;
  };

  // Items in the catalog should be clonable.
  $( "#catalog>*" ).draggable({
    helper: "clone",
    connectToSortable: '.code-placement',
    stop: function(e, ui) {
      ui.helper.find('.code-block').removeAttr('id');
      ui.helper.find('.holds-one, .holds-list').sortable({
        connectWith: ".code-placement",
        cancel: '.code-text',
        receive: receiveFunction
      });
      $('#editor, #toolbox, .holds-one, .holds-list').not('#catalog *').sortable('refresh');
    }
  });

  /* Makes child elements of editor, toolbox, and holds-one draggable
   * between all elements of those types.
   */  
   $('#editor, #toolbox, .holds-one, .holds-list').not('#catalog *').sortable({
    connectWith: ".code-placement",
    cancel: '.code-text',
    receive: receiveFunction
  }).disableSelection();

  // Items that get dumped in trash should be removed from the page.
  $("#trash").droppable({
    accept: ".code-placement div",
    hoverClass: "ui-state-hover",
    drop: function(ev, ui) {
      ui.draggable.remove();
    }
  });

  // Manage editable blocks.
  $('#editor, #toolbox')
  .on('dblclick', '.code-block[data-type="STATIC"] span.code-text', function(){
      // Double clicking a block will create an input.
      new_input = $("<input class='code-text' />").val($(this).text())
      $(this).replaceWith(new_input);
      new_input.focus();
    })
  .on('dblclick blur', '.code-block[data-type="STATIC"] input.code-text', function(){
      // Double clicking an input or clicking away from one will return it into block form.
      try {
        $(this).replaceWith($("<span class='code-text' />").text($(this).val()));
      }
      // For some reason, this throws an exception on blur events, but it seems to work.
      // Squelching the exception.
      catch (e) {}
    })
  .on('keypress', '.code-block[data-type="STATIC"] input.code-text', function(e) {
      // Hitting the enter key on an input will also revert it to block form.
      if(e.which == 13) {
        $(this).replaceWith($("<span class='code-text' />").text($(this).val()));
      }
    }
    );

  $('body').on('keypress', '#instructions, #hint, input#task-title', function() {
    isDirty = true;
  });

  $(window).bind('beforeunload', function() {
    if(isDirty){
      return "You have unsaved changes on this page. Do you want to leave this page and discard your changes or stay on this page?";
    }
  });

  if(task_tutorial) {
    // Remove classes from the draggable.html file, if they exist, so editor and toolbox aren't affected
    if (document.getElementById("editor-div").classList.contains('step-2')) {
       document.getElementById("editor-div").classList.remove('step-2');
       document.getElementById("toolbox-div").classList.remove('step-3');
    }
    // Remove class from the lesson-navbar.html file so the navbar isn't affected
    if(document.getElementById("lesson-navbar").classList.contains('step-6')) {
        document.getElementById("lesson-navbar").classList.remove('step-6');
        // Add class to the lesson-navbar.html file so the navbar is in the correct order
        document.getElementById("lesson-navbar").classList.add('step-1');
    }
    startTutorial();
  }

}

// Runs the output. Does not produce a next arrow.
function run() {
  test_case = [];
  expected_output = [];

  $('#test-case').find('.row-item').each(function() {
    test_case.push($(this).val());
  });
  $('#ex-output').find('.row-item').each(function() {
    expected_output.push($(this).val().replace("\n","<br>") + "<br>");
  });

  try {
    var completed = run_helper();

    if (completed) {
      $('#test-status').replaceWith('<div id="test-status" class="btn btn-outline-success">Correct</div>');
    }
    else {
      $('#test-status').replaceWith('<div id="test-status" class="btn btn-outline-danger">Incorrect</div>');
    }
  }
  catch (e) {
    $('#test-status').replaceWith('<div id="test-status" class="btn btn-outline-danger">Incorrect</div>');
    throw e;
  }
}

// Save data.
function save() {
  var data = {};

  var editor = getBlocksIn($('#editor'));
  var toolbox = getBlocksIn($('#toolbox'));

  var instructions = $('#instructions').val();
  var hint = $('#hint').val();
  var title = $('#task-title').text();

  data.editor = editor;
  data.toolbox = toolbox;
  data.instructions = instructions;
  data.hint = hint;
  data.title = title;

  var t_case = [];
  var ex_output = [];
  $('#test-case').find('.row-item').each(function() {
    t_case.push($(this).val());
  });
  $('#ex-output').find('.row-item').each(function() {
    ex_output.push($(this).val());
  });
  data.test_case = t_case;
  data.expected_output = ex_output;

  $.ajax({
      method: 'POST',
      url: "/createlesson/" + lesson_id + "/createtask/" + task_id,
      contentType: "application/json; charset=utf-8",
      data: JSON.stringify(data),
      'success': function() {
        $("#saveConf").fadeIn('fast');
          window.setTimeout(function() {
            $("#saveConf").fadeOut('fast');
          }, 2000);
      }
  });
  isDirty = false;
}

function deleteTask() {
  $.ajax({
    method:'POST',
    url: '/createlesson/' + lesson_id + '/createtask/' + task_id + '/delete',
    success: function(data, status, xhttp) {
      window.location.replace('/createlesson/' + lesson_id);
    }
  });
}

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

function startTutorial() {
    task_tutorial = false;
    // Setup the tutorial intro for the user page
    var tutorial = introJs();
    tutorial.setOptions({
        steps: [
            {
                // Focus: task title
                element: '.step-1',
                intro: 'First, you can change your task name by pressing on the pencil icon and editing the default text.',
                position: 'bottom'
            },
            {
                // Focus: task navbar
                element: '.step-2',
                intro: 'Use this navigation bar to go back to the Edit Lesson page, or to save your work. You can also navigate between tasks you have already created..',
                position: 'bottom'
            },
            {
                // Focus: instructions
                element: '.step-3',
                intro: 'Type out the instructions for your task in this box.',
                position: 'right'
            },
            {
                // Focus: hint
                element: '.step-4',
                intro: 'If you want to give people hints on how to solve your task, write them here.',
                position: 'right'
            },
            {
                // Focus: test input/output
                element: '.step-5',
                intro: 'To make test cases for your program, write variables to use in your program and assign them values in this box. Then in the \"Test Output\" box on the right, write the expected output for that test case.',
                position: 'top'
            },
            {
                // Focus: block catalogue
                element: '.step-6',
                intro: 'Use these pre-populated blocks to create your program in the Editor and the Toolbox you want people to use to complete your task. If you want to delete a block, drag it onto the red trashcan.',
                position: 'left'
            },
            {
                // Focus: actual output
                element: '.step-7',
                intro: 'Once you\'ve finished creating your program, test how it works by clicking the \"Run\" button. The output will be displayed here and the \"Status\" indicator will let you know if the output is correct.',
                position: 'top'
            }
        ]
    });

    tutorial.start();

    $.ajax({
        method:'POST',
        url:"/createblocktask/toggletutorial",
        success:function() {
        },error:function() {
        }
    });
}