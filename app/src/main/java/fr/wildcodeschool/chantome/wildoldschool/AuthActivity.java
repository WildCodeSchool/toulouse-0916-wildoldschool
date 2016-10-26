package fr.wildcodeschool.chantome.wildoldschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by chantome on 21/09/2016.
 */
public class AuthActivity extends AppCompatActivity {

    private String email;
    private String password;
    private FirebaseAuth mAuth;
    private static final String TAG = "WOS-Auth";
    private EditText editEmail;
    private EditText editPassword;
    private Button button;
    private Button btnRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        editEmail = (EditText) findViewById(R.id.email_address);
        editPassword = (EditText) findViewById(R.id.password);
        button = (Button) findViewById(R.id.connect);
        btnRegister = (Button) findViewById(R.id.registerbtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //Log.i(TAG,"Tentative de connexion...en cours..");

            email = editEmail.getText().toString();
            password = editPassword.getText().toString();

            if(email.isEmpty() || password.isEmpty()){
                //Log.i(TAG,"les champs sont vides..");
                editEmail.setError("champ vide !");
            }
            else{
                //Log.i(TAG,"les champs sont bien rempli..");

                mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                //Log.i(TAG,"Erreur d'authentification..");
                                //editEmail.setError("Email ou mot de passe incorrect !");
                                TextView textViewToChange = (TextView) findViewById(R.id.error);
                                textViewToChange.setText("Email ou mot de passe incorrect !");
                                textViewToChange.setVisibility(View.VISIBLE);
                            }else{
                                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                }
            });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(AuthActivity.this, SignupActivity.class);
                startActivity(intent2);
            }
        });

    }


}
