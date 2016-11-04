package fr.wildcodeschool.chantome.wildoldschool;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chantome on 03/10/2016.
 */
public class ChatActivity extends AppCompatActivity{

    private DatabaseReference rootChat,rootChatMessages,rootChatMessage;
    private String TAG = "WOS-Chat";
    private EditText editMessage;
    private Button send;
    private String monChat, monMessage,temp_key;
    private FirebaseAuth Auth;
    private Message theMessage;
    private RecyclerView recycler;
    private Map<String,String> groupUsers= new HashMap<String, String>();
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mAdapter;
    private TextView introText;
    private String chatName, monTs,currentId,author,authorName,message;
    private Long tsLong;
    private LinearLayoutManager mLayoutManager;
    Toolbar myToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //AUTH FIREBASE INIT
        Auth = FirebaseAuth.getInstance();
        //GET USER ID
        currentId = Auth.getCurrentUser().getUid().toString();
        //GET CHAT ID
        monChat = (String) getIntent().getStringExtra("chatKey");
        //FIREBASE REF INIT
        rootChat = FirebaseDatabase.getInstance().getReference().child("chats").child(monChat);
        rootChatMessages = rootChat.child("messages");
        //RECYCLER
        recycler = (RecyclerView) findViewById(R.id.conversation);
        //EDITTEXT
        editMessage = (EditText) findViewById(R.id.send_message);
        //BUTTON
        send = (Button) findViewById(R.id.send);
        //TEXTVIEW
        introText = (TextView) findViewById(R.id.intro);
        //TOOLBAR
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Set Toolbar background color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#149988"));
        }

        //get users collection
        getGroupUsers();

        //get infos chat
        rootChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatName = dataSnapshot.child("name").getValue().toString();
                introText.setText(chatName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG,databaseError.getMessage().toString());
            }
        });

        //Recycler conversation init
        mLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLayoutManager.setStackFromEnd(true);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(mLayoutManager);

        //Messages list
        mAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.message_test,
                MessageViewHolder.class,
                rootChatMessages
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {

                //INFOS CHAT INIT
                author = model.getUid().toString();
                authorName = groupUsers.get(author);
                message = model.getMessage().toString();

                if(!authorName.isEmpty() && !message.isEmpty()){

                    //Get chat created date timestamp
                    long dv = Long.valueOf(model.getCreated_on().toString());// date value
                    Date df = new java.util.Date(dv);//date format
                    String vv = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(df);

                    if(author.equals(currentId)){
                        //Message on right and green
                        viewHolder.time.setText(vv);
                        viewHolder.userL.setText(authorName);
                        viewHolder.messageR.setText(message);
                        viewHolder.linear1.setVisibility(View.GONE);
                    }else{
                        //Message on left and orange
                        viewHolder.time.setText(vv);
                        viewHolder.userR.setText(authorName);
                        viewHolder.messageL.setText(message);
                        viewHolder.linear2.setVisibility(View.GONE);
                    }

                    //Clear author and message
                    authorName="";
                    message="";
                }

                recycler.scrollToPosition(mAdapter.getItemCount());

            }
        };
        //Load messages list
        recycler.setAdapter(mAdapter);

        //Button send message
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get message
                monMessage = editMessage.getText().toString();

                if (monMessage.isEmpty()){
                    editMessage.setError("vide");
                    return;
                }else{
                    //Random key for message
                    Map<String,Object> map = new HashMap<String, Object>();
                    temp_key = rootChatMessages.push().getKey().toString();
                    rootChatMessages.updateChildren(map);

                    //Date Timestamp
                    tsLong = System.currentTimeMillis();
                    monTs = tsLong.toString();

                    //Add message on chat
                    rootChatMessage = rootChatMessages.child(temp_key);
                    theMessage = new Message(currentId,monMessage,monTs);
                    rootChatMessage.setValue(theMessage);

                    //clear message edittext
                    editMessage.setText("");
                }
            }
        });
    }

    //Message init elements Linear, Textview
    private static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userR,userL,messageR,messageL;
        LinearLayout linear1, linear2;
        TextView time;

        public MessageViewHolder(View v) {
            super(v);
            linear1 = (LinearLayout) v.findViewById(R.id.layout1);
            linear2 = (LinearLayout) v.findViewById(R.id.layout2);
            userR = (TextView) v.findViewById(R.id.userR);
            userL = (TextView) v.findViewById(R.id.userL);
            messageR = (TextView) v.findViewById(R.id.messageR);
            messageL = (TextView) v.findViewById(R.id.messageL);
            time = (TextView) v.findViewById(R.id.date);
        }
    }

    //Get user collection
    private void getGroupUsers(){
        rootChat.child("groupUser").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                groupUsers.put(dataSnapshot.getKey().toString(),dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                groupUsers.put(dataSnapshot.getKey().toString(),dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG,databaseError.getMessage());
            }
        });
    }

    //Click on Back Button
    public void onBackPressed() {
        finish();
    }

    //set items toolbar
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navbar, menu);
        return true;
    }

    //on item toolbar clicked
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chats:
                //List Chats
                Intent chatIntent = new Intent(ChatActivity.this,ListChatsActivity.class);
                startActivity(chatIntent);
                finish();
                return true;

            case R.id.action_profil:
                //Profils
                Intent profileIntent = new Intent(ChatActivity.this,ProfileActivity.class);
                startActivity(profileIntent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
