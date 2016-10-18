package fr.wildcodeschool.chantome.wildoldschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
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
    private RecyclerView chatMessages;
    private EditText editMessage;
    private Button send;
    private String monChat, monMessage, monUser,temp_key;
    private FirebaseAuth Auth;
    private Message message;
    private RecyclerView recycler;
    private Map<String,String> groupUsers= new HashMap<String, String>();
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> mAdapter;
    private TextView introText;
    private String chatName, monTs;
    private Long tsLong;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.i(TAG,"What's Up ?! ");

        Auth = FirebaseAuth.getInstance();
        monChat = (String) getIntent().getStringExtra("chatKey");
        rootChat = FirebaseDatabase.getInstance().getReference().child("chats").child(monChat);
        rootChatMessages = rootChat.child("messages");
        chatMessages = (RecyclerView) findViewById(R.id.conversation);
        editMessage = (EditText) findViewById(R.id.send_message);
        send = (Button) findViewById(R.id.send);
        introText = (TextView) findViewById(R.id.intro);

        getGroupUsers();

        rootChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatName = dataSnapshot.child("name").getValue().toString();
                Log.i(TAG,"Le nom du Chat est "+chatName);
                introText.setText("Bienvenu dans le chat : "+chatName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG,databaseError.getMessage().toString());
            }
        });

        mLayoutManager = new LinearLayoutManager(ChatActivity.this);
        //mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recycler = (RecyclerView) findViewById(R.id.conversation);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(mLayoutManager);

        mAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.list_messages,
                MessageViewHolder.class,
                rootChatMessages
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.nameText.setText(groupUsers.get(model.getUid().toString()));
                viewHolder.messageText.setText(model.getMessage().toString());

                long dv = Long.valueOf(model.getCreated_on().toString());// date value
                Date df = new java.util.Date(dv);//date format
                String vv = new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(df);
                viewHolder.time.setText(vv);
                recycler.scrollToPosition(mAdapter.getItemCount());
            }
        };

        recycler.setAdapter(mAdapter);
        //recycler.scrollToPosition(mAdapter.getItemCount());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Envoi message..");
                monUser = Auth.getCurrentUser().getUid().toString();
                monMessage = editMessage.getText().toString();

                //Tableau temporaire des clés messages
                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = rootChatMessages.push().getKey().toString();
                rootChatMessages.updateChildren(map);

                //INCREMENTATION NBRS_MESSAGE
                /*
                rootChat.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if (currentData.child("nbrs_messages").getValue() == null) {
                            currentData.child("nbrs_messages").setValue(1);
                        } else {
                            currentData.child("nbrs_messages").setValue((Long) currentData.getValue() + 1);
                        }

                        return Transaction.success(currentData.child("nbrs_messages"));
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (databaseError != null) {
                            Log.i(TAG,"Firebase counter increment failed.");
                        } else {
                            Log.i(TAG,"Firebase counter increment succeeded.");
                        }
                    }
                });*/

                tsLong = System.currentTimeMillis();
                monTs = tsLong.toString();

                rootChatMessage = rootChatMessages.child(temp_key);
                message = new Message(monUser,monMessage,monTs);
                rootChatMessage.setValue(message);
                Log.i(TAG,"c'est bon..");


                //Efface le text du champs message aprés l'envoi de celui-ci
                recycler.scrollToPosition(mAdapter.getItemCount());
                editMessage.setText("");
                Log.i(TAG,"on nettoit..");
            }
        });

    }

    private static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView messageText;
        TextView time;

        public MessageViewHolder(View v) {
            super(v);
            nameText = (TextView) v.findViewById(R.id.user);
            messageText = (TextView) v.findViewById(R.id.message);
            time = (TextView) v.findViewById(R.id.date);
        }
    }

    private void getGroupUsers(){
        Log.i(TAG,"Récupérer les utilisateurs dans le chat..");
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

    public void onBackPressed() {
        Log.i(TAG, "onBackPressed Called");

        Map<String,String> map = new HashMap<String, String>();

        rootChatMessages.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG,"1:Last Message Date : "+dataSnapshot.child("message").getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(TAG,"2:Last Message Date : "+dataSnapshot.child("message").getValue().toString());
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

        //map.put(monChat,);
        Log.i(TAG,"Activity !");
        //Intent chatsActivite = new Intent(ChatActivity.this, ListChatsActivity.class);
        //startActivity(chatsActivite);
        Log.i(TAG,"Return !");
        finish();
    }

}
