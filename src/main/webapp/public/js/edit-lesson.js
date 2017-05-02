/**
 * Created by Matt on 4/27/17.
 * Javascript for the edit lesson page
 */

$(document).ready(function() {
  init();
});

var tasksToDelete=[];

// Convenience methood to get value from a placeholder.

function init() {
    $('.sortable').sortable({
        placeholder: "ui-state-highlight"
    }).disableSelection();
};

// Save data.
function save() {
    var data = {};

    // Delete every task first.
    for(taskToDeleteInc = 0; taskToDeleteInc < tasksToDelete.length; taskToDeleteInc++) {
        //TODO: Send delete request for each task.
        console.log(tasksToDelete[taskToDeleteInc])
    }

    // Grab detail fields.
    data.title = $('#title').val();
    data.description = $('#description').val();
    data.shared = $('#shared').is(":checked");

    // Grab current order of tasks.

    console.log(data);

    $.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
      method:'POST',
      url: '/createlesson/' + lesson_id,
      dataType:'json',
      data:JSON.stringify(data),
    });

}