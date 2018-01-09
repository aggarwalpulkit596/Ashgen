package com.example.pulkit_mac.ashgen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UserData extends AppCompatActivity {

    @BindView(R.id.circleImageView)
    CircleImageView circleImageView;
    @BindView(R.id.textView)
    TextView userName;
    @BindView(R.id.firstname)
    TextInputLayout firstName;
    @BindView(R.id.lastname)
    TextInputLayout lastName;

    private DatabaseReference mRootref;
    private StorageReference mStorageRef;

    private ProgressDialog mProgessDialog;

    private String username , download_url, thumb_download_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        ButterKnife.bind(this);

        username = getIntent().getStringExtra("username");

        userName.setText(username);

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void SetUserImage(View view) {

        CropImage.activity()
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropWindowSize(500, 500)
                 .start(UserData.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgessDialog = new ProgressDialog(this);
                mProgessDialog.setMessage("Please wait while we upload the image");
                mProgessDialog.setTitle("Uploading Image...");
                mProgessDialog.setCanceledOnTouchOutside(true);
                mProgessDialog.show();


                Uri resultUri = result.getUri();

                File thumb_file = new File(resultUri.getPath());
                final byte[] thumb_byte;

                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                thumb_byte = baos.toByteArray();

                StorageReference filepath = mStorageRef.child("profile_images").child(username + ".jpg");
                final StorageReference thumb_filepath = mStorageRef.child("profile_images").child("thumbs").child(username + ".jpg");

                final Bitmap finalThumb_bitmap = thumb_bitmap;
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            download_url = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    if (task.isSuccessful()) {
                                        thumb_download_url = task.getResult().getDownloadUrl().toString();
                                        mProgessDialog.dismiss();
                                        Toast.makeText(UserData.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                        circleImageView.setImageBitmap(finalThumb_bitmap);

                                    } else {
                                        mProgessDialog.dismiss();
                                        Toast.makeText(UserData.this, "Try Again Later", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });


                        } else {
                            mProgessDialog.dismiss();
                            Toast.makeText(UserData.this, "Try Again Later", Toast.LENGTH_SHORT).show();

                        }

                    }
                });


            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void startChatRoom(View view) {
        Intent i = new Intent(UserData.this, ChatRoom.class);
        i.putExtra("username",username);
        startActivity(i);
    }
}
