package com.example.kothayebhai.ui.tools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kothayebhai.Posts;
import com.example.kothayebhai.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    private RecyclerView postList;
    private DatabaseReference postRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);

        postList = root.findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        //displayAllUsersPost();


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(postRef, Posts.class).build();

        FirebaseRecyclerAdapter<Posts, PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder postsViewHolder, int i, @NonNull Posts posts) {

                postsViewHolder.userName.setText(posts.getName());
                postsViewHolder.date.setText(posts.getDate());
                postsViewHolder.postTime.setText(posts.getTime());
                postsViewHolder.description.setText(posts.getDescription());
                Picasso.get().load(posts.getPostImage()).into(postsViewHolder.postImage);

            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                PostsViewHolder viewHolder = new PostsViewHolder(view);
                return viewHolder;
            }
        };

        postList.setAdapter(adapter);

        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, postTime, date, description;
        ImageView postImage;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.post_profile_name);
            date = itemView.findViewById(R.id.date);
            postTime = itemView.findViewById(R.id.posts_time);
            description = itemView.findViewById(R.id.post_description);
            postImage = itemView.findViewById(R.id.jobs_post_image);
        }
    }
}