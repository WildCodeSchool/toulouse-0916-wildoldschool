package fr.wildcodeschool.chantome.wildoldschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputPseudo;
    private final String TAG = "WOS-SignUp";
    private Button btnSignIn, btnSignUp;
    //private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    boolean find = true;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private String pseudo,email,password;
    User monUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPseudo = (EditText) findViewById(R.id.pseudo);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                pseudo = inputPseudo.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Entrer une adresse email!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError("Entrer un mot de passe!");
                    return;
                }else {
                    if (password.length() < 6) {
                        inputPassword.setError("Mot de passe trop court! 6 charactere minimum");
                        return;
                    }
                }

                if(TextUtils.isEmpty(pseudo)){
                    inputPseudo.setError("Entrer un pseudo !");
                    return;
                }

                //progressBar.setVisibility(View.VISIBLE);
                //creation utilisateur
                myRef.child("pseudos").child(pseudo).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Log.i(TAG,"Pseudo "+dataSnapshot.getKey().toString()+" existant !!");
                            TextView textViewToChange = (TextView) findViewById(R.id.error);
                            textViewToChange.setText("le pseudo '"+pseudo+"' existe dejà");
                            textViewToChange.setVisibility(View.VISIBLE);
                            return;
                        }else{
                            Log.i(TAG,"Pseudo non existant !!");
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(SignupActivity.this, "Création erreur!",
                                                        Toast.LENGTH_SHORT).show();
                                                TextView textViewToChange = (TextView) findViewById(R.id.error);
                                                textViewToChange.setText("l'utilisateur "+email+" existe dejà");
                                                textViewToChange.setVisibility(View.VISIBLE);
                                            } else {
                                                String Uid = mAuth.getCurrentUser().getUid();
                                                Log.i(TAG,"UID : "+Uid);
                                                monUser = new User(pseudo,true);

                                                myRef.child("users").child(Uid).setValue(monUser);
                                                myRef.child("pseudos").child(pseudo).setValue(Uid);
                                                Toast.makeText(SignupActivity.this, "Création réussi!! ",
                                                        Toast.LENGTH_SHORT).show();
                                                Log.i(TAG,"Utilisateur ajouté !!");
                                                startActivity(new Intent(SignupActivity.this, AuthActivity.class));
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG,databaseError.getMessage().toString());
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.GONE);
    }
}