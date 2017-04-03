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
    s += $(value).children().first().text() + "\n";
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
  // var blocks = [];

  // var placeBlock = "<span class='holds-one code-placement'></span>";

  // for (var i = 0; i < blocks.length; i++) {
  //   if (blocks[i] === "for") {
  //     $('<span class="for-block code-block">' + 'for ('+ placeBlock +  ';' + placeBlock + ';' + placeBlock + ')' + '</span>').data('code', blocks[i])
  //       .attr('id', 'block' + i)
  //       .appendTo('#toolbox');
  //   }
  //   else if (blocks[i] === "if") {
  //     $('<span class="if-block code-block">' + 'if ('+ placeBlock + ')' + '</span>').data('code', blocks[i])
  //       .attr('id', 'block' + i)
  //       .appendTo('#toolbox');
  //   }
  //   else if (blocks[i] === "else if") {
  //     $('<span class="else-if-block code-block">' + 'else if ('+ placeBlock + ')' + '</span>').data('code', blocks[i])
  //       .attr('id', 'block' + i)
  //       .appendTo('#toolbox');
  //   }
  //   else if (blocks[i] === "while") {
  //     $('<span class="while-block code-block">' + 'while ('+ placeBlock + ')' + '</span>').data('code', blocks[i])
  //       .attr('id', 'block' + i)
  //       .appendTo('#toolbox');
  //   }
  //   else {
  //     $('<span class="code-block">' + blocks[i] + '</span>').data('code', blocks[i])
  //       .attr('id', 'block' + i)
  //       .appendTo('#toolbox');
  //   }
  // }

  $('#editor, #toolbox, .holds-one').sortable({
    connectWith: ".code-placement",
    receive: function(event, ui) {
      if ($(this).hasClass('holds-one') && $(this).children().length > 1) {
          $(ui.sender).sortable('cancel');
      }
    }
  }).disableSelection();
}