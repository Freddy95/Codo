$(document).ready(function() {
  init();
});

var isDirty = false;

// Strings for grabbing values from Thymeleaf Template.

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
  /* Expected output has newlines, we'll turn them in to <br> so it works
   * with html
   */
  var stringRegex = /\n/g;//need to use regex to find all occurences
  for (i = 0; i < expected_output.length; i++) {
    expected_output[i] = String(expected_output[i]).replace(stringRegex,"<br>") + "<br>";
  }

  /* Makes child elements of editor, toolbox, and holds-one draggable
   * between all elements of those types.
   */  
   $('#editor, #toolbox, .holds-one, .holds-list').sortable({
    connectWith: ".code-placement",
    over: function(e, ui){ 
      resize_content();
    },
    create: function(event,ui){
      var brackCounter = 0;
      $(this).children().each(function(i){
          var d = $(this),
          cb = d.find('.code-block');
        //iterate over each element and increase the margins
        if(cb.data('type') === 'CURL'){
          d.css('margin-left',(brackCounter*15)+'px;');
          if(cb.text().includes('{')){
            brackCounter++;
           }else{
             if(cb.text().includes('}')){
               brackCounter--;
             }
           }
        }else{
          d.css('margin-left',(brackCounter*15)+'px');
        }
      });
    },
    update: function(event,ui){
      var brackCounter = 0;
      $(this).children().each(function(i){
          var d = $(this),
          cb = d.find('.code-block');
    
        //iterate over each element and increase the margins
        if(cb.data('type') === 'CURL'){
          d.css('margin-left',(brackCounter*15)+'px;');
          if(cb.text().includes('{')){
            brackCounter++;
           }else{
             if(cb.text().includes('}')){
               brackCounter--;
             }
           }
        }else{
          d.css('margin-left',(brackCounter*15)+'px');
        }

      });
    },
    receive: function(event, ui) {

      var brackCounter = 0;
      //handle indentation
      $(this).children().each(function(i){
          var d = $(this),
          cb = d.find('.code-block');
    
        //iterate over each element and increase the margins
        if(cb.data('type') === 'CURL'){
          d.css('margin-left',(brackCounter*15)+'px;');
          if(cb.text().includes('{')){
            brackCounter++;
           }else{
             if(cb.text().includes('}')){
               brackCounter--;
             }
           }
        }else{
          d.css('margin-left',(brackCounter*15)+'px');
        }
      });
 
      if ($(this).hasClass('holds-one')) {
        if ($(this).children().length > 1) {
          $(ui.sender).sortable('cancel');
          return;
        }
      }
      isDirty = true;
    }
  }).disableSelection();

   $(window).bind('beforeunload', function() {
    if(isDirty){
      return "You have unsaved changes on this page. Do you want to leave this page and discard your changes or stay on this page?";
    }
  });

   if(new_lesson) {
    startTutorial();
  }

  //load the ratings and make the stars
  loadRatings();
}

function run() {
  var correct = run_helper();

  // If the arrow doesn't already exist and the output is correct, then mark the task as complete and show the arrow.
  if ($('#output-div>.card-title-block').children().length === 1 &&
    correct) {
    completed = true;
    // Adding next arrow to next task.
    if (next_task > 0) {
      $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" onClick="saveRedirect()"></a>'));
    }
    // If last lesson, just redirect to user page.
    else {
      $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" onClick="saveRedirect()"></a>'));
    }
  }
}

function saveRedirect() {
  save();
  if (next_task > 0) {
    window.location.replace('/lesson/' + lesson_id + '/task/' + next_task);
  }
  else {
    window.location.replace('/user');
  }
}

// Save data.
function save() {
  var data = {};

  var editor = [];
  var toolbox = [];

  editor = getBlocksIn($('#editor'));
  toolbox = getBlocksIn($('#toolbox'));

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
  });

  isDirty = false;
}

function startTutorial() {
  new_lesson = false;
    // Setup the tutorial intro for the user page
    var tutorial = introJs();
    tutorial.setOptions({
      steps: [
          {
              // Focus: instructions
              element: '.step-1',
              intro: 'To begin each task, look at the instructions found here. By following them closely, you can learn new concepts that will help you move on to the next task in the lesson.',
              position: 'right'
          },
          {
              // Focus: editor
              element: '.step-2',
              intro: 'This is the Editor where you build your program and run the code by pressing the \"Run\" button. If you\'re having trouble getting the right output, press the \"?\" at the top for a hint.',
              position: 'left'
          },
          {
              // Focus: toolbox
              element: '.step-3',
              intro: 'Drag the blocks in this Toolbox into the Editor to build a program that produces the right output.',
              position: 'top'
          },
          {
              // Focus: output
              element: '.step-4',
              intro: 'This box displays the output given by running your program. The output needs to be the correct output as specified in the instructions in order to move on to the next task.',
              position: 'left'
          },
          {
              // Focus: task navbar
              element: '.step-5',
              intro: 'This is the task navigation bar. On the right are buttons for restarting the current task or lesson. You can use the middle numbers to move to any task you\'ve completed within the lesson. And finally on the right you can save the task, as well as revert the current changes.',
              position: 'bottom'
          },
          {
              // Focus: lesson navbar
              element: '.step-6',
              intro: 'This is the lesson navigation bar where you can see the title of the current lesson you are working on. You can also rate the lesson using the star rating system and navigate home. ',
              position: 'bottom'
          }
      ]
    });

    tutorial.start();

    $.ajax({
      method:'POST',
      url:"/lesson/toggletutorial",
      success:function() {
      },error:function() {
      }
    });
  }
