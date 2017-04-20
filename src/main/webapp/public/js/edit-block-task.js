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
function getCodeBlocks(node) {
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
    $.each($(value).children(), function(index, v) {
      child_block = getCodeBlockAttr(getCodeBlocks($(v)));
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
    block.children = [];
    $.each($(value).children(), function(index, v) {
      child_block = getCodeBlockValue(getCodeBlocks($(v)));
      block.children.push(child_block);
    });
  }
  return s;
}
function init() {
  $('#block-content').addClass('h-100');

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

  // Items in the catalog should be clonable.
  $( "#catalog>*" ).draggable({
    helper: "clone",
    connectToSortable: '.code-placement',
    stop: function(e, ui) {
      var code_block = ui.helper.children()
      if (code_block.attr('data-children') === "false" &&
            code_block.attr('data-type') === "STATIC") {
        code_block.attr('contenteditable', 'true')
          .keydown(function(e) {
            // trap the return key being pressed
            if (e.keyCode === 13) {
              return false;
            }
          });
      }
    }
  });

  /* Makes child elements of editor, toolbox, and holds-one draggable
   * between all elements of those types.
   */  
  $('#editor, #toolbox, .holds-one, .holds-list').not('#catalog *').sortable({
    connectWith: ".code-placement",
    cancel: '.code-text',
    receive: function(event, ui) {
      // Restrict .holds-one to hold one element at a time.
      if ($(this).hasClass('holds-one')) {
        if ($(this).children().length > 1) {
          $(ui.sender).sortable('cancel');
        }
      }
    }
  });

  // Items that get dumped in trash should be removed from the page.
  $("#trash").droppable({
    accept: ".code-placement div",
    hoverClass: "ui-state-hover",
    drop: function(ev, ui) {
        ui.draggable.remove();
    }
  });

  $('#editor .code-block[data-type="STATIC"], #toolbox .code-block[data-type="STATIC"]')
    .attr('contenteditable', 'true')
    .keydown(function(e) {
        // trap the return key being pressed
        if (e.keyCode === 13) {
          return false;
        }
      });
}

// Save data.
function save() {
  var data = {};

  var editor = [];
  var toolbox = [];

  $.each(getCodeBlocks($('#editor')), function(index, value) {
    var block = getCodeBlockAttr(value);
    editor.push(block);
  });

  $.each(getCodeBlocks($('#toolbox')), function(index, value) {
    var block = getCodeBlockAttr(value);
    toolbox.push(block);
  });

  // data.task_id = task_id;
  data.editor = editor;
  data.toolbox = toolbox;
  data.completed = completed;
  console.log(data);
}

function addOutput() {
  if ($('#ex-output').children().length === 1) {
      $('#ex-output').find('i').removeClass('fa-disabled');
      $('#test-case').find('i').removeClass('fa-disabled');
  }
  var newInputRow = $('<div class="row"/>');
  var newOutputRow = newInputRow.clone();

  newInputRow.appendTo('#test-case');
  newOutputRow.appendTo('#ex-output');

  newInputRow.append('<input type="text" class="row-item"></input>' + 
                  '<i class="fa fa-vc fa-minus pull-right" onClick="minusOutput(this)"></i>');
  newOutputRow.append('<input type="text" class="row-item"></input>' + 
                  '<i class="fa fa-vc fa-minus pull-right" onClick="minusOutput(this)"></i>');
}

function minusOutput(node) {
  if ($('#ex-output').children().length > 1) {
    var index = $(node).parent().index();
    $('#ex-output').children().eq(index).remove();
    $('#test-case').children().eq(index).remove();

    if ($('#ex-output').children().length === 1) {
      $('#ex-output, #test-case').find('i').addClass('fa-disabled');
    }
  }
}