package com.example.kothayebhai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {


    private RecyclerView commentsList;
    private ImageButton postCommentButton;
    private EditText commentInputText;

    private DatabaseReference UsersRef, postRef;
    private FirebaseAuth mAuth;

    private String post_Key, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        post_Key = getIntent().getExtras().get("postKey").toString();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_Key).child("Comments");

        commentsList = findViewById(R.id.comments_list);
        commentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentsList.setLayoutManager(linearLayoutManager);

        commentInputText = findViewById(R.id.comment_input);
        postCommentButton = findViewById(R.id.post_commend_button);

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Toast.makeText(getApplicationContext(), "data", Toast.LENGTH_SHORT).show();

                            if(dataSnapshot.exists()){
                                Toast.makeText(getApplicationContext(), "exists", Toast.LENGTH_SHORT).show();

                                String userName = dataSnapshot.child("name").getValue().toString();
                                String phone = dataSnapshot.child("phone").getValue().toString();
                                String email = dataSnapshot.child("email").getValue().toString();

                                validateComment(userName, phone, email);

                                commentInputText.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void validateComment(String userName, String phone, String email) {
        String commentText = commentInputText.getText().toString();

        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(getApplicationContext(), "Please place your bid", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar callForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(callForDate.getTime());

            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(callForTime.getTime());

            final String RandomKey = current_user_id + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", current_user_id);
            commentsMap.put("comment", commentText);
            commentsMap.put("date", saveCurrentDate);
            commentsMap.put("time", saveCurrentTime);
            commentsMap.put("username", userName);
            commentsMap.put("phone", phone);
            commentsMap.put("email", email);

            postRef.child(RandomKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Bid was placed Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error occured! ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
