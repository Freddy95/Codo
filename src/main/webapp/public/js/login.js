var baseurl = "http://localhost:8080/"; //TODO: change this!
function onSignIn(googleUser) {
    var profile = googleUser.getBasicProfile();
    console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    console.log('Name: ' + profile.getName());
    console.log('Image URL: ' + profile.getImageUrl());
    console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
    var token = googleUser.getAuthResponse().id_token;
    $.ajax({
        url:  baseurl+"login",
        type:'post',
        headers:{
            'Content-Type':'application/x-www-form-urlencoded',
            'Authorization':token
        },
        success:function(data) {
            console.log('Signed in as: ' + data);
            //store the token in a cookie
            document.cookie="token="+token;
            window.location.href = baseurl + "/user";
        },
        error: function (e) {
           console.log(e.getStacktrace()) ;
        }
    });

}
function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
    });
}
