package fr.wildcodeschool.chantome.wildoldschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Intent authActivite;
    private Intent chatsActivite;
    private static final String TAG = "WOS-Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //AUTH FIREBASE INIT
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Check user authentification
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Login
                    chatsActivite = new Intent(MainActivity.this, ListChatsActivity.class);
                    startActivity(chatsActivite);
                    finish();
                }else {
                    // Logout
                    authActivite = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(authActivite);
                    finish();
                }
            }
        };

        mAuthListener.onAuthStateChanged(mFirebaseAuth);

    }
}
