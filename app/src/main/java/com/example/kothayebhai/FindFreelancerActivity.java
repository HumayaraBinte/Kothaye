package com.example.kothayebhai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class FindFreelancerActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private ImageButton selectPostImage;
    private Button updatePostButton;
    private EditText postDescription;
    private static final int Gallery_pick=1;
    private Uri ImageUri;
    private String description;

    private StorageReference postImagesReference;
    private DatabaseReference userRef, postRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_freelancer);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        postImagesReference = FirebaseStorage.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        selectPostImage = findViewById(R.id.imageButton4);
        updatePostButton = findViewById(R.id.postButtonId);
        postDescription = findViewById(R.id.editText3);
        loadingBar = new ProgressDialog(this);

        mToolbar = findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePostInfo();
            }
        });
    }

    private void validatePostInfo() {
        description = postDescription.getText().toString();
        if(ImageUri == null){
            Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(getApplicationContext(), "Please provide description for the job", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Add a new post");
            loadingBar.setMessage("Please wait while we're updating your post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            storingImage();
        }
    }

    private void storingImage() {
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = postImagesReference.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(getApplicationContext(), "Error" + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadUrl = task.getResult().toString();
                            Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            savingPostInformation();
                        }
                        else {
                            String message = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
/*
        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    downloadUrl = filePath.getDownloadUrl().toString();

                    Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                    savingPostInformation();
                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    */
    }

    private void savingPostInformation() {
        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userName = dataSnapshot.child("name").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", description);
                    postsMap.put("postImage", downloadUrl);
                    postsMap.put("name", userName);

                    postRef.child(current_user_id + postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                sendUserBack();
                                Toast.makeText(getApplicationContext(), "Pots is Updated Successfully ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Error occurred while updating yout post ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_pick && resultCode==RESULT_OK){
            ImageUri = data.getData();
            //selectPostImage.setImageURI(ImageUri);

            //Input Stream
            try {
                InputStream inputStream = getContentResolver().openInputStream(ImageUri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                selectPostImage.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            sendUserBack();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserBack() {
        Intent intent = new Intent(FindFreelancerActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
