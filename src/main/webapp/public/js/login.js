var baseurl = "http://localhost:8080"; //TODO: change this!
function onSignIn(googleUser) {
    var profile = googleUser.getBasicProfile();
    console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    console.log('Name: ' + profile.getName());
    console.log('Image URL: ' + profile.getImageUrl());
    console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
    var token = googleUser.getAuthResponse().id_token;
    var xhr = new XMLHttpRequest();
    xhr.open('POST',baseurl + "/login" );
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.setRequestHeader('Authorization',token);
    xhr.onload = function() {
        console.log('Signed in as: ' + xhr.responseText);
        window.location.href = baseurl + "/user";
    };
    xhr.send();
    // $.ajax({
    //     url: baseurl + "/login",
    //     method: "POST",
    //     contentType: "text/html; charset=utf-8",
    //     cache: false,
    //     beforeSend: function (xhr) {
    //         /* Authorization header */
    //         xhr.setRequestHeader("Authorization",  token);
    //     },success:function () {
    //        window.location = baseurl + "/user" ;
    //     },erro
    // });
}
function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
    });
}