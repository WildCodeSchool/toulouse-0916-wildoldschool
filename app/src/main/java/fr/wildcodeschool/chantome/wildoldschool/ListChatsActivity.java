package fr.wildcodeschool.chantome.wildoldschool;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chantome on 27/09/2016.
 */
public class ListChatsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private String chatName,chatDesc="",temp_key,current_id, userkey,chatKey,author;
    private Button createBtn;
    private FirebaseAuth Auth;
    private Intent chatActivite;
    private View v_iew;
    private boolean status = true;
    private boolean access = false;
    private static final String TAG = "WOS-ListChats";
    private User user;
    private Map<String ,User> users = new HashMap<String,User>();
    private ArrayList<Chat> mesChats = new ArrayList<Chat>();
    private ArrayList<String> chatKeys= new ArrayList<String>();
    private Map<String,String> groupUsers = new HashMap<String,String>();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference rootChats = FirebaseDatabase.getInstance().getReference().child("chats");
    private DatabaseReference rootProfil;
    private ListView list;
    private Long tsLong;
    private String ts,formation;
    Toolbar myToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chats);

        //AUTH FIREBASE INIT
        Auth = FirebaseAuth.getInstance();

        //BUTTON
        createBtn = (Button) findViewById(R.id.create_chat);
        //LIST
        list = (ListView) findViewById(R.id.list_chats);
        //USER ID
        current_id = Auth.getCurrentUser().getUid().toString();
        //FIREBASE REF
        rootProfil = FirebaseDatabase.getInstance().getReference().child("users").child(current_id);
        //TOOLBAR
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Set toolbar background color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#149988"));
        }

        //Get users and chats keys in collections
        getUsers();
        getChatKeys();

        //list public chats
        FirebaseListAdapter<Chat> fireadapter = new FirebaseListAdapter<Chat>(
                ListChatsActivity.this,
                Chat.class,
                R.layout.list_chats,
                rootChats.orderByChild("access").equalTo(false)//ICI
        ) {
            @Override
            protected void populateView(View v, Chat model, int position) {

                //Get INFOS CHAT INIT
                String monName = (String) model.getName().toString();
                String monAuthor = (String) model.getAuthor().toString();
                String maDesc = (String) model.getDesc().toString();
                String maFormation = (String) model.getCategorieChat().toString();
                boolean monStatus = Boolean.valueOf(model.isStatus());
                boolean monAccess = Boolean.valueOf(model.isAccess());

                Map<String, String> monGroupUser = new HashMap<String, String>();
                monGroupUser = model.getGroupUser();
                //Get chat creation timestamp
                String monTs = (String) model.getCreated_on().toString();

                Chat monChat = new Chat(monName, monAuthor, maDesc, maFormation, monStatus, monAccess, monGroupUser,monTs);

                //add chat on collection
                mesChats.add(monChat);

                //TEXTVIEW
                TextView txtName = (TextView) v.findViewById(R.id.chat_name);
                TextView txtFav = (TextView) v.findViewById(R.id.chat_fav);

                //Set content
                txtName.setText(model.getName().toString());
                txtFav.setText(model.getCategorieChat().toString());
            }
        };

        list.setAdapter(fireadapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Add user on chat usersGroup
                rootChats.child(chatKeys.get(position).toString()).child("groupUser").child(current_id).setValue(users.get(current_id).getPseudo().toString());

                //go on conversation
                chatActivite = new Intent(ListChatsActivity.this, ChatActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("chatKey",chatKeys.get(position).toString());
                chatActivite.putExtras(mBundle);
                startActivity(chatActivite);
            }
        });

        //Button create chat
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewChat();
            }
        });
    }

    //get chat key on collection
    public void getChatKeys(){
        rootChats.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chatKey = dataSnapshot.getKey().toString();
                chatKeys.add(chatKey);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                chatKey = dataSnapshot.getKey().toString();
                chatKeys.add(chatKey);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG,"Meh !! "+databaseError.getMessage().toString());
            }
        });
    }

    //get users on collection
    public void getUsers(){
        //Log.i(TAG,"getUsers..");

        root.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Log.i(TAG,"onChildAdded..");
                userkey = dataSnapshot.getKey().toString();
                if(dataSnapshot.hasChildren()) {
                    user = new User(dataSnapshot.child("pseudo").getValue().toString());
                    users.put(userkey, user);
                    //Log.i(TAG,"Utilisateur ajouté dans la collection..");
                }else{
                    //Log.i(TAG,"dataSnapshot vide..");
                }
                //Log.i(TAG,"Next..");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Log.i(TAG,"onChildChanged..");
                userkey = dataSnapshot.getKey().toString();
                if(dataSnapshot.hasChildren()) {
                    user = new User(dataSnapshot.child("pseudo").getValue().toString());
                    users.put(userkey, user);
                    //Log.i(TAG,"Utilisateur ajouté dans la collection..");
                }else{
                    //Log.i(TAG,"dataSnapshot vide..");
                }
                //Log.i(TAG,"Next..");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG,"Meh !! "+databaseError.getMessage());
            }
        });
    }

    //create chat
    public void addNewChat(){
        //POPUP INIT
        AlertDialog.Builder builder = new AlertDialog.Builder(ListChatsActivity.this);
        builder.setTitle("Créer un chat");
        LayoutInflater inflater = ListChatsActivity.this.getLayoutInflater();
        v_iew = inflater.inflate(R.layout.form_add_chat, null) ;
        builder.setView(v_iew);

        //Button list formations
        Spinner spinnerFormations = (Spinner) v_iew.findViewById(R.id.formations_spinner);
        ArrayAdapter<CharSequence> adapterFormations = ArrayAdapter.createFromResource(ListChatsActivity.this,
                R.array.array_formations, android.R.layout.simple_spinner_item);
        adapterFormations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormations.setAdapter(adapterFormations);
        spinnerFormations.setOnItemSelectedListener(ListChatsActivity.this);

        //Button OK POPUP
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //EDITTEXT
                EditText editName, editDesc;
                editName = (EditText) v_iew.findViewById(R.id.chat_name);
                //editDesc = (EditText) v_iew.findViewById(R.id.chat_desc);

                //Get form content
                chatName = editName.getText().toString();
                //chatDesc = editDesc.getText().toString();

                //Random key for chat
                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = rootChats.push().getKey();
                root.updateChildren(map);

                //Set FIREBASE REF
                DatabaseReference rootChat = rootChats.child(temp_key);

                //add author on collection
                author = users.get(current_id).getPseudo().toString();
                groupUsers.put(current_id,author);

                //get current timestamp
                tsLong = System.currentTimeMillis();
                ts = tsLong.toString();

                //add chat on database
                Chat monChat = new Chat(chatName,current_id,chatDesc,formation,status,access,groupUsers,ts);
                rootChat.setValue(monChat);
            }
        });

        //Button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //Build and show popup
        builder.create();
        builder.show();
    }

    /*
    //Private chat checkboxe
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.checkbox_access:
                if (checked){
                    //Chat Privé
                    access = true;
                }
                else
                {
                    //Chat Publique
                    access = false;
                }
                break;
        }
    }
    */

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
                return true;

            case R.id.action_profil:
                Intent profileIntent = new Intent(ListChatsActivity.this,ProfileActivity.class);
                startActivity(profileIntent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        formation = parent.getItemAtPosition(pos).toString();
        Log.i(TAG,formation);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}