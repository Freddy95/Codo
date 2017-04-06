$( init );

// Strings for grabbing values from Thymeleaf Template.
var test_case = "";
var expected_output = "";
var next_task = "";
var task_id = "";
var lesson_id = "";

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
      $('#output').html() === expected_output) {
    if (next_task < 0) {
      $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" href="/lesson/' + lesson-id + '/task/' + task_id + '"></a>'));
    }
    else {
      $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" href="/user"></a>'));
    }
  }

  // console.log($('#output').html() === "Hello<br>World!<br>");
}
 
function init() {
  //get the div for the page, we'll use it to get the data attribute
    //use the dollar sign to signify that page is a jquery dom element
  var $page = $("#page");
  //get the data attributes for the id's well need for the backend
  test_case = $page.data("test-case");
  expected_output = $page.data("ex-output");
  next_task = $page.data('next-task');
  task_id = $page.data('task-id');
  lesson_id = $page.data('lesson-id');

  $('#test-case').remove();
  $('#ex-output').remove();

  //expected output has newlines, we'll turn them in to <br> so it works
    //  with html
   expected_output = expected_output.replace("\n","<br>") + "<br>";

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
