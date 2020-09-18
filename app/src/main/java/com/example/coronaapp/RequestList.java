package com.example.coronaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RequestList extends Fragment {

    private RecyclerView requestList;
    private FirebaseFirestore fStore;
    private AdapterUser adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_request_list, container, false);

        requestList = view.findViewById(R.id.userList);
        fStore = FirebaseFirestore.getInstance();

        //Query
        Query query = fStore.collection("users");
//        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                List<UserModel> users = value.toObjects(UserModel.class);
//            }
//        });


        //Recycler Options
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class)
                .build();

        adapter = new AdapterUser(options);

        requestList.setHasFixedSize(true);
        requestList.setLayoutManager(new LinearLayoutManager(getContext()));
        requestList.setAdapter(adapter);

        return view;
    }

    public class AdapterUser extends FirestoreRecyclerAdapter<UserModel, AdapterUser.UserHolder> {

        public AdapterUser(@NonNull FirestoreRecyclerOptions<UserModel> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull UserHolder holder, final int position, @NonNull final UserModel model) {
            holder.fullNameText.setText(model.getFirstName() + " " + model.getLastName());
            holder.bloodGroupText.setText(model.getBloodGroup());
            holder.ageText.setText(model.getAge());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserListViewFragment list = new UserListViewFragment();
                    Bundle args = new Bundle();
                    args.putString("id",model.getUserId());
                    list.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,list,"User list Fragment")
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        @NonNull
        @Override
        public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist,parent,false);
            return new UserHolder(view);
        }

        public class UserHolder extends RecyclerView.ViewHolder {

            private TextView fullNameText, bloodGroupText, ageText;
            private View view;

            public UserHolder(@NonNull View itemView) {
                super(itemView);

                fullNameText = itemView.findViewById(R.id.fullNameText1);
                bloodGroupText = itemView.findViewById(R.id.bloodGroupText1);
                ageText = itemView.findViewById(R.id.ageText1);
                view = itemView;

//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION && listener != null){
//                            listener.onItemClick(getSnapshots().getSnapshot(position), position);
//                        }
//                    }
//                });
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}