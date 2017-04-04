$( init );

var test_case = "";
var expected_output = "";

function handleBlockDrop(event, ui) {
  $(ui.draggable).detach().css({top: 0,left: 0}).appendTo($(this));
}

function handleBlockReturn(event, ui) {
  $(ui.draggable).detach().css({top: 0,left: 0}).appendTo($(this));
}

function run() {
  var s = "";

  $.each($('#editor').children(), function(index, value) {
    s += $(value).children().first().text() + "\n";
  });

  window.console.log = function(msg) {
    $('#output').text(msg);
  }

  window.onerror = function(messageOrEvent, source, lineno, colno, error) {
    $('#output').text(messageOrEvent);
  }

  eval(s);

  if ($('#output-div>.card-title-block').children().length === 1 &&
      $('#output').text() === expected_output) {
    $('#output-div>.card-title-block').append($('<a id="next-arrow" class="fa fa-lg fa-vc fa-arrow-right pull-right" href="/debug-block-task"></a>'));
  }
}
 
function init() {
  test_case = $('#test-case').text();
  expected_output = $('#ex-output').text();

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