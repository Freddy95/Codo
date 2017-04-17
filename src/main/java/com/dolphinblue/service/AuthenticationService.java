package com.dolphinblue.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Created by Matt on 4/2/17.
 */
@Service
public class AuthenticationService {

    public AuthenticationService(){

    }

    public GoogleIdToken getIdToken(String token,JacksonFactory jsonFactory,NetHttpTransport transport){
        String CLIENT_ID = "441622834163-c890l3f1krej8tfgv0sl78b3tqqo10fo.apps.googleusercontent.com";
        String idTokenString = token;


        GoogleIdTokenVerifier verifier =  new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();


        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return idToken;
    }
    public boolean isAuthenticated(String token, JacksonFactory jsonFactory, NetHttpTransport transport){
        if(token != null && !token.equals("")){
            String CLIENT_ID = "441622834163-c890l3f1krej8tfgv0sl78b3tqqo10fo.apps.googleusercontent.com";
            String idTokenString = token;

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    // Or, if multiple clients access the backend:
                    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                    .build();

            GoogleIdToken idToken = null;
            try {
                idToken = verifier.verify(idTokenString);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (idToken != null) {

                GoogleIdToken.Payload payload = idToken.getPayload();
                return true;

            } else {
                System.out.println("Invalid ID token.");
                return false;
            }
        }else{
            return false;
        }
    }
}
