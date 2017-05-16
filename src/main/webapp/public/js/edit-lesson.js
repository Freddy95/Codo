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
        $('#shared').prop(disabled, true);
        $('#shared').prop(checked, false);
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

function startTutorial() {
    new_user = false;
    // Setup the tutorial intro for the user page
    var tutorial = introJs();
    tutorial.setOptions({
        steps: [
            {
                // Focus: welcome message
                element: '.step-1',
                intro: 'Welcome to Codo! Let\'s show you around before you get started.',
                position: 'bottom'
            },
            {
                // Focus: nav-bar
                element: '.step-2',
                intro: 'First you can use these buttons to navigate to the search and settings page, as well as logout of the site.',
                position: 'left'
            },
            {
                // Focus: main lessons
                element: '.step-3',
                intro: 'The main lessons offered by the site are displayed here, they teach the basics of JavaScript programming.',
                position: 'top'
            },
            {
                // Focus: created lessons
                element: '.step-4',
                intro: 'To find lessons created by different users of the site, click on the \"Shared Lessons\" tab. To create your own, click on the \"Your Lessons\" tab.',
                position: 'top'
            },
            {
                // Focus: current lesson
                element: '.step-5',
                intro: 'After choosing a lesson, the one you are currently working on will be displayed here. To get started, click on the lesson.',
                position: 'bottom'
            }
        ]
    });

    // Setup the page transition for the tutorial
    //tutorial.setOption('doneLabel', 'Create Task').start().oncomplete(function() {
    //    window.location.href = "/lesson/" + lesson_id;
    //});

    tutorial.start();

    $.ajax({
        method:'POST',
        url:"/createlesson/toggletutorial",
        success:function() {
        },error:function() {
        }
    });
}