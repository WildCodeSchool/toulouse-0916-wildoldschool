package fr.wildcodeschool.chantome.wildoldschool;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chantome on 27/10/2016.
 */

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private final static String TAG = "WOS-Profil";
    private View v_iewFormations,v_iewSchools,v_iew,v_iewFav;
    private int year;
    private int month;
    private int day;
    private boolean newGenre;
    static final int DATE_DIALOG_ID = 100;
    private Toolbar myToolbar;
    private TextView txtPseudo,txtLastname,txtFirstname,txtGenre,txtDescriptif,txtSchool,txtFormation,txtFavories,txtBirthday;
    private ImageButton button,modifPseudo,modifLastname,modifFirstname,modifGenre,modifDescriptif,modifFavories,modifSchool,modifFormation;
    private Button btnDeco;
    private String newPseudo,newLastname,newFirstname,newDescriptif;
    private EditText editPseudo,editLastname,editFirstname,editDescriptif;
    private DatabaseReference rootPseudos,rootProfil,rootFavs;
    private FirebaseAuth mAuth;
    private String mUser,newSchool,newDescription,newFormation,tempUid,mesNewFavs="";
    private User mProfil;
    private LinearLayout linearFav;
    private ListView list;
    private LinearLayout.LayoutParams txtLp,chkLp;
    private Map<String,String> listFavs = new HashMap<String, String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser().getUid().toString();

        rootProfil = FirebaseDatabase.getInstance().getReference().child("users").child(mUser);
        rootFavs = FirebaseDatabase.getInstance().getReference().child("categories");
        rootPseudos = FirebaseDatabase.getInstance().getReference().child("pseudos");

        //TOOLBAR
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //TEXTVIEWS
        txtPseudo = (TextView) findViewById(R.id.txtPseudo);
        txtLastname = (TextView) findViewById(R.id.txtLastname);
        txtFirstname = (TextView) findViewById(R.id.txtFirstname);
        txtGenre = (TextView) findViewById(R.id.txtGenre);
        txtBirthday = (TextView) findViewById(R.id.txtBirthday);
        txtDescriptif = (TextView) findViewById(R.id.txtDescriptif);
        txtSchool = (TextView) findViewById(R.id.txtSchool);
        txtFormation = (TextView) findViewById(R.id.txtFormation);
        txtFavories = (TextView) findViewById(R.id.txtFavories);

        //IMAGE BUTTONS
        modifPseudo = (ImageButton) findViewById(R.id.modifPseudo);
        modifLastname = (ImageButton) findViewById(R.id.modifLastname);
        modifFirstname = (ImageButton) findViewById(R.id.modifFirstname);
        modifGenre = (ImageButton) findViewById(R.id.modifGenre);
        modifDescriptif = (ImageButton) findViewById(R.id.modifDescriptif);
        modifSchool = (ImageButton) findViewById(R.id.modifSchool);
        modifFormation = (ImageButton) findViewById(R.id.modifFormation);
        modifFavories = (ImageButton) findViewById(R.id.modifFavories);

        //BUTTON
        btnDeco = (Button) findViewById(R.id.deco);

        //GET CONTENT
        rootFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()){
                    //Set favories collection
                    for (DataSnapshot item : dataSnapshot.getChildren()){
                        listFavs.put(item.getKey().toString(),item.child("name").getValue().toString());
                    }

                    rootProfil.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                //Get infos user
                                String pseudo,firstname,lastname,descriptif,birthday,school,formation,favories,strGenre,strOnline;
                                boolean genre,online;

                                pseudo = dataSnapshot.child("pseudo").getValue().toString();
                                firstname = dataSnapshot.child("firstname").getValue().toString();
                                lastname = dataSnapshot.child("lastname").getValue().toString();
                                descriptif = dataSnapshot.child("desc").getValue().toString();
                                birthday = dataSnapshot.child("birthday").getValue().toString();
                                school = dataSnapshot.child("school").getValue().toString();
                                formation = dataSnapshot.child("formation").getValue().toString();
                                favories = dataSnapshot.child("favories").getValue().toString();
                                strGenre = dataSnapshot.child("genre").getValue().toString();
                                strOnline = dataSnapshot.child("online").getValue().toString();
                                genre = Boolean.valueOf(strGenre);
                                online = Boolean.valueOf(strOnline);

                                mProfil = new User(pseudo,firstname,lastname,descriptif,birthday,school,formation,favories,genre,online);

                                //ADD CONTENT
                                txtPseudo.setText(mProfil.getPseudo());
                                txtFirstname.setText(mProfil.getFirstname());
                                txtLastname.setText(mProfil.getLastname());

                                if (mProfil.isGenre()){
                                    txtGenre.setText("Femme");
                                }else {
                                    txtGenre.setText("Homme");
                                }

                                txtDescriptif.setText(mProfil.getDesc());
                                txtSchool.setText(mProfil.getSchool());
                                txtFormation.setText(mProfil.getFormation());

                                String str="";
                                for (int i=0; i < mProfil.getFavories().length() ;i++){
                                    String index = String.valueOf(mProfil.getFavories().charAt(i));
                                    str += listFavs.get(index)+", ";
                                }
                                str.substring(0,str.length()-2);
                                str+=".";
                                txtFavories.setText(str);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i(TAG,"Meh !! "+databaseError.getMessage().toString());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG,"Meh !! "+databaseError.getMessage().toString());
            }
        });

        //ITEMS
        //Calendar
        setCurrentDate();

        //POPUPS
        addButtonListener();
        updatePseudo();
        updateLastname();
        updateFirstname();
        updateGenre();
        updateDescriptif();
        updateSchool();
        updateFormation();
        updateFavories();

        //Déconnexion de l'utilisateur
        btnDeco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //users.get(current_id).setOnline(false);
                mAuth.signOut();
                Intent profilActivite = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(profilActivite);
                finish();
            }
        });

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radioHomme:
                if (checked){
                    newGenre=false;
                }
                    break;
            case R.id.radioFemme:
                if (checked){
                    newGenre=true;
                }
                    break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chats:
                Intent chatIntent = new Intent(ProfileActivity.this,ListChatsActivity.class);
                startActivity(chatIntent);
                finish();
                return true;

            case R.id.action_profil:
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void setCurrentDate() {

        txtBirthday = (TextView) findViewById(R.id.txtBirthday);
        final Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        rootProfil.child("birthday").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtBirthday.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG,"Meh !! "+databaseError.getMessage().toString());
            }
        });

    }

    public void addButtonListener() {

        button = (ImageButton) findViewById(R.id.modifBirthday);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, datePickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            month = month+1;

            txtBirthday.setText(new StringBuilder().append(month)
                    .append("/").append(day).append("/").append(year));

            rootProfil.child("birthday").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String date = day+"/"+month+"/"+year;
                        rootProfil.child("birthday").setValue(date);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i(TAG,"Meh !! "+databaseError.getMessage().toString());
                }
            });
        }
    };

    //POPUPS
    public void updatePseudo(){
        modifPseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderPseudo = new AlertDialog.Builder(ProfileActivity.this);
                builderPseudo.setTitle("Modifier mon pseudo ?");
                LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
                v_iew = inflater.inflate(R.layout.update_pseudo, null) ;
                builderPseudo.setView(v_iew);
                builderPseudo.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.i(TAG,"onClick..");
                        editPseudo = (EditText) v_iew.findViewById(R.id.edit_pseudo);
                        newPseudo = editPseudo.getText().toString();

                        if (newPseudo.isEmpty()) {
                            Toast.makeText(ProfileActivity.this, "entrez votre pseudo", Toast.LENGTH_LONG);
                            return;
                        }

                        rootPseudos.child(mProfil.getPseudo().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    tempUid = dataSnapshot.getValue().toString();
                                    rootPseudos.child(newPseudo).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Log.i(TAG, "Nouveau pseudo existe deja..");
                                                TextView textViewToChange = (TextView) findViewById(R.id.error);
                                                textViewToChange.setText("le pseudo '" + newPseudo + "' existe dejà");
                                                textViewToChange.setVisibility(View.VISIBLE);
                                                return;
                                            } else {
                                                rootProfil.child("pseudo").setValue(newPseudo);
                                                rootPseudos.child(mProfil.getPseudo().toString()).removeValue();
                                                rootPseudos.child(newPseudo).setValue(tempUid);
                                                txtPseudo.setText(newPseudo);
                                                Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.i(TAG, "Meh !! " + databaseError.getMessage().toString());
                                        }

                                    });
                                } else {
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i(TAG, "Meh !! " + databaseError.getMessage().toString());
                            }
                        });

                        }
                    });
                    builderPseudo.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builderPseudo.create();
                    builderPseudo.show();
            }
        });
    }

    public void updateLastname(){
        modifLastname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderLastname = new AlertDialog.Builder(ProfileActivity.this);
                builderLastname.setTitle("Modifier mon nom ?");
                LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
                v_iew = inflater.inflate(R.layout.update_lastname, null) ;
                builderLastname.setView(v_iew);
                builderLastname.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.i(TAG,"onClick..");

                        editLastname = (EditText) v_iew.findViewById(R.id.edit_lastname);
                        newLastname = editLastname.getText().toString();

                        rootProfil.child("lastname").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getValue().toString().equals(newLastname)) {
                                    Log.i(TAG, "Entrer un nom différent de l'existant..");
                                    TextView textViewToChange = (TextView) findViewById(R.id.error);
                                    textViewToChange.setText("Le nom "+newLastname+" est identique à l'ancien..");
                                    textViewToChange.setVisibility(View.VISIBLE);
                                    return;
                                } else {
                                    rootProfil.child("lastname").setValue(newLastname);
                                    Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i(TAG, "Meh !! " + databaseError.getMessage().toString());
                            }

                        });


                    }
                });
                builderLastname.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builderLastname.create();
                builderLastname.show();
            }
        });
    }

    public void updateGenre(){
        modifGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderGenre = new AlertDialog.Builder(ProfileActivity.this);
                builderGenre.setTitle("Modifier mon genre ?");
                LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
                v_iew = inflater.inflate(R.layout.update_genre, null) ;
                builderGenre.setView(v_iew);
                builderGenre.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.i(TAG,"onClick..");
                        rootProfil.child("genre").setValue(newGenre);
                    }
                });
                builderGenre.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builderGenre.create();
                builderGenre.show();
            }
        });
    }

    public void updateFirstname(){
        modifFirstname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderFirstname = new AlertDialog.Builder(ProfileActivity.this);
                builderFirstname.setTitle("Modifier mon prénom ?");
                LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
                v_iew = inflater.inflate(R.layout.update_firstname, null) ;
                builderFirstname.setView(v_iew);
                builderFirstname.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.i(TAG,"onClick..");

                        editFirstname = (EditText) v_iew.findViewById(R.id.edit_firstname);
                        newFirstname = editFirstname.getText().toString();

                        rootProfil.child("lastname").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getValue().toString().equals(newLastname)) {
                                    Log.i(TAG, "Entrer un nom différent de l'existant..");
                                    TextView textViewToChange = (TextView) findViewById(R.id.error);
                                    textViewToChange.setText("Le prénom "+newFirstname+" est identique à l'ancien..");
                                    textViewToChange.setVisibility(View.VISIBLE);
                                    return;
                                } else {
                                    rootProfil.child("firstname").setValue(newFirstname);
                                    Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i(TAG, "Meh !! " + databaseError.getMessage().toString());
                            }

                        });
                    }
                });
                builderFirstname.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builderFirstname.create();
                builderFirstname.show();
            }
        });
    }

    public void updateDescriptif(){
        modifDescriptif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderDescription = new AlertDialog.Builder(ProfileActivity.this);
                builderDescription.setTitle("Modifier ma description ?");
                LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
                v_iew = inflater.inflate(R.layout.update_descriptif, null) ;
                builderDescription.setView(v_iew);
                builderDescription.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.i(TAG,"onClick..");
                        editDescriptif = (EditText) v_iew.findViewById(R.id.edit_descriptif);
                        newDescription = editDescriptif.getText().toString();

                        rootProfil.child("desc").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getValue().toString().equals(newDescription)) {
                                    Log.i(TAG, "Entrer un nom différent de l'existant..");
                                    TextView textViewToChange = (TextView) findViewById(R.id.error);
                                    textViewToChange.setText("Le descriptif est identique à l'ancien..");
                                    textViewToChange.setVisibility(View.VISIBLE);
                                    return;
                                } else {
                                    rootProfil.child("desc").setValue(newDescription);
                                    Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i(TAG, "Meh !! " + databaseError.getMessage().toString());
                            }

                        });
                    }
                });
                builderDescription.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builderDescription.create();
                builderDescription.show();
            }
        });
    }

    public void updateSchool(){
        modifSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSchool = new AlertDialog.Builder(ProfileActivity.this);
                builderSchool.setTitle("Modifier mon école ?");
                LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
                v_iewSchools = inflater.inflate(R.layout.update_school, null) ;
                builderSchool.setView(v_iewSchools);

                //List Button schools
                Spinner spinnerSchools = (Spinner) v_iewSchools.findViewById(R.id.schools_spinner);
                ArrayAdapter<CharSequence> adapterSchools = ArrayAdapter.createFromResource(ProfileActivity.this,
                        R.array.array_schools, android.R.layout.simple_spinner_item);
                adapterSchools.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSchools.setAdapter(adapterSchools);
                spinnerSchools.setOnItemSelectedListener(ProfileActivity.this);

                builderSchool.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.i(TAG,"onClick..");
                        if(!newSchool.isEmpty()){
                            rootProfil.child("school").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.getValue().toString().equals(newSchool)) {
                                        Log.i(TAG, "choisir une ecole différente de l'existant..");
                                        TextView textViewToChange = (TextView) findViewById(R.id.error);
                                        textViewToChange.setText("L'école "+newSchool+" est identique à l'ancien..");
                                        textViewToChange.setVisibility(View.VISIBLE);
                                        return;
                                    } else {
                                        rootProfil.child("school").setValue(newSchool);
                                        Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.i(TAG, "Meh !! " + databaseError.getMessage().toString());
                                }

                            });
                        }
                    }
                });
                builderSchool.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builderSchool.create();
                builderSchool.show();
            }
        });
    }

    public void updateFormation(){
        modifFormation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderFormation = new AlertDialog.Builder(ProfileActivity.this);
                builderFormation.setTitle("Modifier ma formation ?");
                LayoutInflater inflater = ProfileActivity.this.getLayoutInflater();
                v_iewFormations = inflater.inflate(R.layout.update_formation, null) ;
                builderFormation.setView(v_iewFormations);

                //List Button formations
                Spinner spinnerFormations = (Spinner) v_iewFormations.findViewById(R.id.formations_spinner);
                ArrayAdapter<CharSequence> adapterFormations = ArrayAdapter.createFromResource(ProfileActivity.this,
                        R.array.array_formations, android.R.layout.simple_spinner_item);
                adapterFormations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFormations.setAdapter(adapterFormations);
                spinnerFormations.setOnItemSelectedListener(ProfileActivity.this);

                builderFormation.setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.i(TAG,"onClick..");
                        if(!newFormation.isEmpty()){
                            rootProfil.child("formation").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.getValue().toString().equals(newFormation)) {
                                        Log.i(TAG, "choisir une formation différente de l'existant..");
                                        TextView textViewToChange = (TextView) findViewById(R.id.error);
                                        textViewToChange.setText("La formation "+newFormation+" est identique à l'ancien..");
                                        textViewToChange.setVisibility(View.VISIBLE);
                                        return;
                                    } else {
                                        rootProfil.child("formation").setValue(newFormation);
                                        Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.i(TAG, "Meh !! " + databaseError.getMessage().toString());
                                }

                            });
                        }
                    }
                });
                builderFormation.setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builderFormation.create();
                builderFormation.show();
            }
        });
    }

    public void updateFavories(){

        modifFavories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UpdateCategoriesActivity.class);
                startActivity(intent);
            }
        });
    }

    //SPINNERS
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        switch (parent.getId()) {
            case R.id.schools_spinner:
                newSchool = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.formations_spinner:
                newFormation = parent.getItemAtPosition(pos).toString();
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    //CHECKBOXES
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch(view.getTag().toString()) {
            case "check_php":
                if (checked){
                    mesNewFavs += "0";
                }
                else
                {
                    mesNewFavs = mesNewFavs.replace("0", "");
                }
                break;
            case "check_ruby":
                if (checked){
                    mesNewFavs+="1";
                }
                else
                {
                    mesNewFavs = mesNewFavs.replace("1", "");
                }
                break;
            case "check_javascript":
                if (checked){
                    mesNewFavs+="2";
                }
                else
                {
                    mesNewFavs = mesNewFavs.replace("2", "");
                }
                break;
            case "check_java":
                if (checked){
                    mesNewFavs+="3";
                }
                else
                {
                    mesNewFavs = mesNewFavs.replace("3", "");
                }
                break;
            case "check_ui/ux":
                if (checked){
                    mesNewFavs+="4";
                }
                else
                {
                    mesNewFavs = mesNewFavs.replace("4", "");
                }
                break;
            case "check_veille tech.":
                if (checked){
                    mesNewFavs+="5";
                }
                else
                {
                    mesNewFavs = mesNewFavs.replace("5", "");
                }
                break;
            case "check_jobs":
                if (checked){
                    mesNewFavs+="6";
                }
                else
                {
                    mesNewFavs = mesNewFavs.replace("6", "");
                }
                break;
            case "check_android":
                if (checked){
                    mesNewFavs+="7";
                }
                else
                {
                    mesNewFavs = mesNewFavs.replace("7", "");
                }
                break;
            default:
                mesNewFavs="6";
        }
    }

}