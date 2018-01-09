package com.example.pulkit_mac.ashgen;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRoom extends AppCompatActivity {

    private String username;
    @BindView(R.id.chat_msgview)
    EditText mMsgView;
    @BindView(R.id.messageslist)
    RecyclerView mMessagesList;
    private final List<messages> MessageList = new ArrayList<>();
    private MessageAdapter mAdapter;
    private LinearLayoutManager mLinearLayout;

    //Firebas
    private DatabaseReference mRootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ButterKnife.bind(this);

        username = getIntent().getStringExtra("username");
        mRootRef = FirebaseDatabase.getInstance().getReference();


        //RecyclerView
        mAdapter = new MessageAdapter(MessageList, getApplicationContext());
        mMessagesList.setHasFixedSize(true);
        mLinearLayout = new LinearLayoutManager(this);
        mMessagesList.setLayoutManager(mLinearLayout);
        mMessagesList.setAdapter(mAdapter);

        loadMessages();

    }

    private void loadMessages() {
        mRootRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                messages mMessages = dataSnapshot.getValue(messages.class);

                MessageList.add(mMessages);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(View view) {

        String message = mMsgView.getText().toString();

        DatabaseReference userMessagePush = mRootRef.child("messages").push();

        String push_id = userMessagePush.getKey();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("seen", false);
        messageMap.put("type", "text");
        messageMap.put("time", ServerValue.TIMESTAMP);
        messageMap.put("from", username);

        mMsgView.setText("");

        mRootRef.child("messages").child(push_id).updateChildren(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });


    }
}
