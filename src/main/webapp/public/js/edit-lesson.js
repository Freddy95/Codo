/**
 * Created by Matt on 4/27/17.
 * Javascript for the edit lesson page
 */

 $(document).ready(function() {
  init();
});

 var tasksToDelete=[];
 var isDirty = false;

// Convenience methood to get value from a placeholder.

function init() {

    //hide the alert shown when a lesson is saved
    $("#lesson-saved-alert").hide();

    $('.sortable').sortable({
        connectWith: '.sortable',
        placeholder: "ui-state-highlight",
        stop: function(event, ui) {
          isDirty = true;
        }
    }).disableSelection();

    $('#title, #description, #shared').change(function() {
        isDirty = true;
    });

    $(window).bind('beforeunload', function() {
        if(isDirty){
            return "You have unsaved changes on this page. Do you want to leave this page and discard your changes or stay on this page?";
        }
    });
}

// Save data.
function save(node,taskId) {
    if ($(node).hasClass('disabled')) {
        return;
    }

    if(username===""||username==undefined||username==null){
        $("#usernameModal").modal('show');
        return;
    }

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
    if(taskId !== undefined){
      taskList.push(taskId);
    }

    data.tasks = taskList;
    
    $.ajax({
        headers: {
            'Content-Type': 'application/json'
        },
        method:'POST',
        url: '/createlesson/' + lesson_id + '/post',
        data:JSON.stringify(data),
        success:function(){
            //when the lesson is posted successfully, show an alert
            $("#lesson-saved-alert").fadeTo(2000, 500).slideUp(500, function(){
                $("#lesson-saved-alert").slideUp(500);
            });
        }
    });

    isDirty = false;
}

function addTask(type) {
    $.ajax({
      method:'GET',
      data:{
         "type":type
     },
     url: '/createlesson/' + lesson_id + '/createtask',
     success: function(data, status, xhttp) {
        save($("#save-button"),data);
        //redirect to the newly created task
        window.location.href = '/createlesson/' + lesson_id + '/createtask/'+data;
    }
});
}

function deleteTask(node) {
    var task_block = $(node).closest('.task-block');
    tasksToDelete.push(task_block.attr('id'));
    task_block.parent().remove();
    if ($('#task-list').children().length == 0) {
        $('#save-button').addClass('disabled');
    }
}


/**
 * creates a username if the user doesn't have one
 */
function createUsername() {

    var newUsername = $("#usernametxt").val();
    username = newUsername;


    $.ajax({
        method:'POST',
        url:'/editusername',
        dataType:'text',
        data:newUsername,
        success:function(){
            $("#usernameModal").modal('hide');
        }
    });
}