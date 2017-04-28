// Convenience methood to get value from a placeholder.
function getCodeBlocks(node) {
  return $(node).children().children();
}

// Extracts block values from a node.
function getCodeBlockAttr(value) {
  var block = {};
  block.block_id = parseInt($(value).attr('id'));

  block.type = $(value).attr('data-type');
  block.can_edit = $(value).attr('data-children')
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
  return $(value).text();
}

function getBlocksIn(node) {
  node_list = [];
  $.each(getCodeBlocks(node), function(index, value) {
    var block = getCodeBlockAttr(value);
    node_list.push(block);
  });
  return node_list;
}

function run_helper(arrow) {
  // Empty the output when running.
  $('#output').empty();

  codeArray = [];

  // Add code to the. stored array.
  $.each(getCodeBlocks($('#editor')), function(index, value) {
    codeArray.push(getCodeBlockValue(value));
  });

  fullCode = codeArray.join('\n');

  var codeLines = [];
  for (var code_lines_inc = 0; code_lines_inc < codeArray.length; code_lines_inc++) {
    if (codeArray[code_lines_inc] != '{') {
      codeLines.push(codeArray[code_lines_inc]);
    }
    else {
      s = codeArray[code_lines_inc];
      openBrackets = 1;
      while (openBrackets > 0) {
        code_lines_inc++;
        s += codeArray[code_lines_inc] + '\n';
        if (codeArray[code_lines_inc] === '{') {
          openBrackets += 1
        }
        else if (codeArray[code_lines_inc] === '}') {
          openBrackets -= 1
        }
      }
      codeLines[codeLines.length-1] += s;
    }
  }

  // Redirect console.log and window.one-error to output.
  console.log = function(msg) {
    // former_log(msg);
    $('#output').append(document.createTextNode(msg)).append($('<br />'));
  }

  window.onerror = function(messageOrEvent, source, lineno, colno, error) {
    $('#output').append(document.createTextNode(messageOrEvent)).append($('<br />'));
  }

  var correct = true;

  var total_output = "";
  for (test_case_inc = 0; test_case_inc < test_case.length; test_case_inc++) {
    try {
      // Try running the full program.
      eval(test_case[test_case_inc]);
      eval(fullCode);
    } catch (e) {
      // If it doesn't work, evaluate line by line.
      for (code_lines_inc = 0; code_lines_inc < codeLines.length; code_lines_inc++) {
        try {
          eval(test_case[test_case_inc]);
          eval(codeLines[code_lines_inc]);
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
    total_output += expected_output[test_case_inc]
    if ($('#output').html() != total_output) {
      correct = false;
      break;
    }
  }

  /* Adds a next arrow if it doesn't exist already and if the solution is correct.
   */
  if ($('#output-div>.card-title-block').children().length === 1 &&
      correct && arrow) {
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

  return correct;
}