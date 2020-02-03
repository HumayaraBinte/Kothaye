package com.example.kothayebhai.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.kothayebhai.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private EditText profileNameEditText, profileEmailEditText, profilePhoneEditText,
            profileEducationEditText, profileJobEditText, profileNextJobEditText;
    private Button saveButton;
    private ProgressBar progressBar;

    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        profileNameEditText = root.findViewById(R.id.profileNameEditTextId);
        profileEmailEditText = root.findViewById(R.id.profileEmailEditTextId);
        profilePhoneEditText = root.findViewById(R.id.profilePhoneEditTextId);
        profileEducationEditText = root.findViewById(R.id.profileEducationEditTextId);
        profileJobEditText = root.findViewById(R.id.profileJobEditTextId);
        profileNextJobEditText = root.findViewById(R.id.profileNextJobEditTextId);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String profileName = dataSnapshot.child("name").getValue().toString();
                    String profileEmail = dataSnapshot.child("email").getValue().toString();
                    String profilePhone = dataSnapshot.child("phone").getValue().toString();
                    String profileEducation = dataSnapshot.child("education").getValue().toString();
                    String profileJob = dataSnapshot.child("current_job").getValue().toString();
                    String profileNextJob = dataSnapshot.child("future_job").getValue().toString();

                    profileNameEditText.setText(profileName);
                    profileEmailEditText.setText(profileEmail);
                    profilePhoneEditText.setText(profilePhone);
                    profileEducationEditText.setText(profileEducation);
                    profileJobEditText.setText(profileJob);
                    profileNextJobEditText.setText(profileNextJob);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }
}