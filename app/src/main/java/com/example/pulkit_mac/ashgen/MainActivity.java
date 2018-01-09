package com.example.pulkit_mac.ashgen;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout textInputLayout;
    private DatabaseReference mRootRef;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputLayout = findViewById(R.id.username);

        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void StartChat(View view) {
        username = textInputLayout.getEditText().getText().toString();
        Log.i("AG", "StartChat: "+username);
        if (username.isEmpty()) {
            textInputLayout.setError("Please Enter A Username");
        } else {
            mRootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(username)) {
                        Intent i = new Intent(MainActivity.this, UserData.class);
                        i.putExtra("username",username);
                        startActivity(i);
                    }else{
                        Intent i = new Intent(MainActivity.this, ChatRoom.class);
                        i.putExtra("username",username);
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
