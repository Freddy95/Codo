/**
 * Created by Matt on 4/15/17.
 */
$(function(){
    //on document load fill in the fields for the settings page
    //enable tooltips
    $('[data-toggle="tooltip"]').tooltip()


    //TODO: set up handler for reset less progress
    //TODO: set up handler for pulling new info from google
    //TODO: set up account deletion handler
    //get the buttons we need
    var $usernameSubmitBtn=$("#username-submit-btn"),
        $showUserNameEditBtn=$("#username_edit"),
        $userNameEditForm=$("#username-edit-form"),
        $userNameTextbox=$("#username-textbox"),
        $userNameNoBtn=$("#username-no-btn"),
        $resetLessonBtn=$("#reset-lessons-btn"),
        $deleteAcctBtn=$("#delete-account-btn"),
        $userNameDone=$("#username-done");

    //the base url
    var baseUrl="http://localhost:8080";

    //handler for the edit username button being clicked
    $showUserNameEditBtn.click(function () {
        $userNameDone.hide();
        $userNameEditForm.show();
    });

    //handler to hide the username edit form
    $userNameNoBtn.click(function(){
        $userNameEditForm.hide();
        $userNameDone.show();
    });

    $usernameSubmitBtn.click(function(){
        //get the username from the
        var newUsername = $userNameTextbox.val();
        var data = {
            username:newUsername,
        };
        $.ajax({
            method:"POST",
            url:'/settings/updateusername',
            data:data,
            success:function(){
                $("#username-text").text(newUsername);
                $showUserNameEditBtn.show();
                $userNameEditForm.hide();
            },
            error:function(e){
                console.error(e.getStacktrace());
            }
        });
    });

    $resetLessonBtn.click(function(){
      //send a reset request to the server
        $.ajax({
            method:'POST',
            url:"/settings/resetall",
            success:function () {
                alert("Progress successfully reset!");
            },error:function () {
                alert("Couldn't reset!");
            }
        });
    });
   $deleteAcctBtn.click(function(){
      //send a reset request to the server
        $.ajax({
            method:'DELETE',
            url:"/settings/deleteuser",
            success:function () {
              //go back to the login page
                window.location.href=baseUrl+"/login";
            },error:function () {
                alert("Couldn't delete user!");
            }
        });
    });

});
