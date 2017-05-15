$(document).ready(function() {
    init();
});

// Adds tooltips to buttons in user page.
var lesson_id = "";
var new_user = "";

$(function () {
  $('[data-toggle="tooltip"]').tooltip();
});

function init() {
    // Get the div for the page, we'll use it to get the data attribute
    // Use the dollar sign to signify that page is a jquery dom element
    var $page_info = $("#page-info");

    // Get the data attributes for the data needed in this javascript file.
    lesson_id = $page_info.data("lesson-id");
    new_user = $page_info.data("new-user");

    // Remove the element once done loading from the page.
    $page_info.remove();

    console.log("The new user is: " + new_user);

    if (new_user) {
        // Start the tutorial
        startTutorial();
    }
}

function onLoad() {
    gapi.load('auth2', function() {
        gapi.auth2.init();
    });
}
function signOut() {
    var baseurl = "http://localhost:8080"; //TODO: change this!
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
        window.location.href = baseurl+"/login";
        document.cookie = 'token=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
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
    tutorial.setOption('doneLabel', 'Start Lesson').start().oncomplete(function() {
        window.location.href = "/lesson/" + lesson_id;
    });

    $.ajax({
        method:'POST',
        url:"/user/toggle",
        success:function() {
        },error:function() {
        }
    });
}