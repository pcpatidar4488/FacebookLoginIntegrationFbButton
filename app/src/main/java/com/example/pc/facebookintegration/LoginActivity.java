package com.example.pc.facebookintegration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    private static final String EMAIL = "email";
    private static final String USER_FRIENDS = "user_friends";
    com.facebook.login.widget.LoginButton loginButton;
    CallbackManager callbackManager;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        loginButton = (com.facebook.login.widget.LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        loginButton.setReadPermissions(Arrays.asList(USER_FRIENDS));

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(LoginActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "onError", Toast.LENGTH_SHORT).show();
            }
        });

        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

      /*  if (!isLoggedIn){
            LoginManager.getInstance().logOut();
            Toast.makeText(LoginActivity.this, "Logout", Toast.LENGTH_SHORT).show();
        }*/
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                if (AccessToken.getCurrentAccessToken()!=null){
                    Toast.makeText(LoginActivity.this, "AccessToken", Toast.LENGTH_SHORT).show();
                }else {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(LoginActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                }
                /*if (AccessToken.getCurrentAccessToken()==null){
                    LoginManager.getInstance().logOut();
                }*/
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
                if (Profile.getCurrentProfile()!=null){
                    Toast.makeText(LoginActivity.this, Profile.getCurrentProfile().getFirstName(), Toast.LENGTH_SHORT).show();
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/" +  Profile.getCurrentProfile().getId() + "/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    Log.e("response", "" + response);
                                    Toast.makeText(LoginActivity.this, "Count " + response, Toast.LENGTH_SHORT).show();
                                }
                            }
                    ).executeAsync();
                }
            }
        };



       // LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

      /*  LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Toast.makeText(LoginActivity.this, ""+loginResult.getRecentlyGrantedPermissions(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(LoginActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(LoginActivity.this, "onError", Toast.LENGTH_SHORT).show();
                    }
                });*/


    }

    @Override
    protected void onStop() {
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
