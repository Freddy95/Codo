var correctCards = 0;
$( init );

function handleBlockDrop(event, ui) {
  $(ui.draggable).detach().css({top: 0,left: 0}).appendTo($(this));
}

function handleBlockReturn(event, ui) {
  $(ui.draggable).detach().css({top: 0,left: 0}).appendTo($(this));
}

function run() {
  var s = "";

  $.each($('#editor').children(), function(index, value) {
    s += $(value).data('code') + "\n";
  });

  window.console.log = function(msg) {
    $('#output').text(msg);
  }

  window.onerror = function(messageOrEvent, source, lineno, colno, error) {
    $('#output').text(messageOrEvent);
  }

  eval(s);
}
 
function init() {
  var blocks = ["x += 5;", "x = 2;", "console.log(x);"]

  for (var i = 0; i < blocks.length; i++) {
    $('<span class="code-block">' + blocks[i] + '</span>').data('code', blocks[i])
      .attr('id', 'block' + i)
      .appendTo('#toolbox');
  }

  $('#editor, #toolbox').sortable({
    connectWith: ".code-placement"
  }).disableSelection();
}