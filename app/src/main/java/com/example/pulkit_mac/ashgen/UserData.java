package com.example.pulkit_mac.ashgen;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserData extends AppCompatActivity {

    @BindView(R.id.circleImageView) private CircleImageView circleImageView;
    @BindView(R.id.textView) private TextView userName;
    @BindView(R.id.firstname) private TextInputLayout firstName;
    @BindView(R.id.lastname) private TextInputLayout lastName;

    private DatabaseReference mRootref;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        ButterKnife.bind(this);
    }
}
