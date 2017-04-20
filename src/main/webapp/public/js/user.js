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
                intro: 'Welcome to Codo! Let\'s show you around a bit before you get started.',
                position: 'bottom'
            },
            {
                // Focus: search
                element: '.step-2',
                intro: 'First, we\'ll go over some basic navigation. This is the search button, it will bring you to a page where you can search through all of the lessons offered on our site.',
                position: 'bottom'
            },
            {
                // Focus: settings
                element: '.step-3',
                intro: 'Click this button to go to the settings page where you can change your username and reset all lesson progress.',
                position: 'bottom'
            },
            {
                // Focus: logout
                element: '.step-4',
                intro: 'Click this button to logout when you\'re done working on your lessons.',
                position: 'bottom'
            },
            {
                // Focus: lesson tabs
                element: '.step-5',
                intro: 'You can use these tabs to navigate between different lessons offered on our site. \"Main Lessons\" are created by us, \"Shared Lessons\" are created by other users, and \"Your Lessons\" are lessons you have made.',
                position: 'right'
            },
            {
                // Focus: lessons offered in each tab
                element: '.step-6',
                intro: 'To navigate to a lesson within a category, click on the lesson that interests you.',
                position: 'top'
            },
            {
                // Focus: current lesson
                element: '.step-7',
                intro: 'The lesson you are currently working on will be displayed here. Click on the lesson to get started.',
                position: 'bottom'
            }
        ]
    });

    // Setup the page transition for the tutorial
    tutorial.setOption('doneLabel', 'Start Lesson').start().oncomplete(function() {
        window.location.href = "/lesson/" + lesson_id;
    });

    //tutorial.start();
}