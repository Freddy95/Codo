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
}

// Save data.
function save() {
    var data = {};

    // Delete every task to delete first.
    while (tasksToDelete.length > 0) {
        //TODO: Send delete request for each task.
        var currentTaskId = tasksToDelete.pop();
        $.ajax({
            method:'POST',
            url: '/createlesson/' + lesson_id + '/createtask/' + currentTaskId + '/delete'
        });
    }

    // Grab detail fields.
    data.title = $('#title').val();
    data.description = $('#description').val();
    data.shared = $('#shared').is(":checked");

    // Grab current order of tasks.
    var taskList = [];
    $('#task-list').find('.task-block').each(function() {
        taskList.push($(this).attr('id'));
    });

    data.tasks = taskList;
    
    console.log(data);

    $.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
      method:'POST',
      url: '/createlesson/' + lesson_id + '/post',
      dataType:'json',
      data:JSON.stringify(data),
    });
}

function addTask(type) {
    $.ajax({
      method:'GET',
        data:{
         "type":type
        },
      url: '/createlesson/' + lesson_id + '/createtask',
      success: function(data, status, xhttp) {
        //redirect to the newly created task
        window.location.href = '/createlesson/' + lesson_id + '/createtask/'+data;
      }
    });
}

function deleteTask(node) {
    var task_block = $(node).closest('.task-block');
    tasksToDelete.push(task_block.attr('id'));
    task_block.remove();
}
