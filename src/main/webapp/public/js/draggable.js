$( init );

// Strings for grabbing values from Thymeleaf Template.
var test_case = "";
var expected_output = "";
var next-task = "";
var task-id = "";
var lesson-id = "";

function run() {
  $('#output').empty();

  window.console.log = function(msg) {
    $('#output').append(document.createTextNode(msg)).append($('<br />'));
  }

  window.onerror = function(messageOrEvent, source, lineno, colno, error) {
    $('#output').text(messageOrEvent);
  }

  $.each($('#editor').children(), function(index, value) {
    try {
      eval($(value).children().first().text());
    }
    catch(e) {
      $(value).addClass('flash');
      setTimeout( function(){
        $(value).removeClass('flash');
      }, 1000);
      throw(e);
    }
  });

  if ($('#output-div>.card-title-block').children().length === 1 &&
      $('#output').text() === expected_output) {
    if (next-task > 0) {
      $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" href="/lesson/' + lesson-id + '/task/' + task_id + '"></a>'));
    }
    else {
      $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" href="/user'));
    }
  }

  // console.log($('#output').html() === "Hello<br>World!<br>");
}
 
function init() {
  test_case = $('#test-case').text();
  expected_output = $('#ex-output').text();
  next-task = $('#next-task').text();
  task-id = $('#task-id').text();
  lesson-id = $('#lesson-id').text();

  $('#test-case').remove();
  $('#ex-output').remove();

  console.log(test_case);
  console.log(expected_output);
  
  $('#editor, #toolbox, .holds-one').sortable({
    connectWith: ".code-placement",
    receive: function(event, ui) {
      if ($(this).hasClass('holds-one') && $(this).children().length > 1) {
          $(ui.sender).sortable('cancel');
      }
    }
  }).disableSelection();
}