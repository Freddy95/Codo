function onSignIn(googleUser) {
    var profile = googleUser.getBasicProfile();
    var token = googleUser.getAuthResponse().id_token;
    $.ajax({
        url:  "/login",
        type:'post',
        headers:{
            'Content-Type':'application/x-www-form-urlencoded',
            'Authorization':token
        },
        success:function(data) {
            //store the token in a cookie
            document.cookie="token="+token;
            window.location.href = "/user";
        },
        error: function (e) {
           console.log(e.getStacktrace());
        }
    });

}
function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        //console.log('User signed out.');
    });
}
