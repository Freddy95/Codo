//Matthew Del Signore
//JS file for logging in
function makeLogin(){
    var uiConfig = {
        'signInSuccessUrl': '/',
        'signInOptions': [
            // Leave the lines as is for the providers you want to offer your users.
            firebase.auth.GoogleAuthProvider.PROVIDER_ID,
            firebase.auth.FacebookAuthProvider.PROVIDER_ID,
            firebase.auth.TwitterAuthProvider.PROVIDER_ID,
            firebase.auth.GithubAuthProvider.PROVIDER_ID,
            firebase.auth.EmailAuthProvider.PROVIDER_ID
        ],
        // Terms of service url
        'tosUrl': ' ',
    };

    var ui = new firebaseui.auth.AuthUI(firebase.auth());
    ui.start('#firebase-login-container', uiConfig);
}