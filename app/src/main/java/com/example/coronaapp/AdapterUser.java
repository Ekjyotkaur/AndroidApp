package com.example.coronaapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterUser extends FirestoreRecyclerAdapter<UserModel, AdapterUser.UserHolder> {

    private OnItemClickListener listener;

    public AdapterUser(@NonNull FirestoreRecyclerOptions<UserModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull UserModel model) {
        holder.fullNameText.setText(model.getFirstName() + " " + model.getLastName());
        holder.bloodGroupText.setText(model.getBloodGroup());
        holder.ageText.setText(model.getAge());

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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnClickListener (OnItemClickListener listener){
        this.listener = listener;
    }
}
