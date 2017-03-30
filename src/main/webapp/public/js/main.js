//Matthew Del Signore
//JS file for logging in and other user related functions that we user throughout the program
var userIdToken; // token we use to authorize requests
function makeLogin(){
    var uiConfig = {
        'signInSuccessUrl': '/',
        'signInOptions': [
            // Leave the lines as is for the providers you want to offer your users.
            firebase.auth.GoogleAuthProvider.PROVIDER_ID,
            // firebase.auth.FacebookAuthProvider.PROVIDER_ID,
            // firebase.auth.TwitterAuthProvider.PROVIDER_ID,
            // firebase.auth.GithubAuthProvider.PROVIDER_ID,
            firebase.auth.EmailAuthProvider.PROVIDER_ID
        ],
        // Terms of service url
        'tosUrl': ' ',
    };

    var ui = new firebaseui.auth.AuthUI(firebase.auth());
    ui.start('#firebase-login-container', uiConfig);
}
var startApp = function(){
    firebase.auth().onAuthStateChanged(function(user) {
        if (user) {
            $('#logged-out').hide();
            var name = user.displayName;

            /* If the provider gives a display name, use the name for the
             personal welcome message. Otherwise, use the user's email. */
            var welcomeName = name ? name : user.email;

            user.getToken().then(function (idToken) {
                userIdToken = idToken;

                console.log("Got the token.\n");
                $("logged-in").show();
            });

        } else {
            $('.logged-in').hide();
            $('.logged-out').show();

        }
    });
};

//when the window loads get the user status and account token
window.addEventListener('load',function(){
    startApp();
});

//function to get the userIdToken so we can make requests
function getToken(){
    return userIdToken;
}
