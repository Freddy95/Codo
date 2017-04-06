// Adds tooltips to buttons in user page.

$(function () {
  $('[data-toggle="tooltip"]').tooltip();

});

function onLoad() {
      gapi.load('auth2', function() {
        gapi.auth2.init();
  });
}
function signOut() {
    var baseurl = "http://codo-161419.appspot.com/"; //TODO: change this!
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
        window.location.href = baseurl+"/login";
        document.cookie = 'token=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
   });
  }
    

