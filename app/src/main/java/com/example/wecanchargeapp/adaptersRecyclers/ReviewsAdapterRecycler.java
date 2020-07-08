package com.example.wecanchargeapp.adaptersRecyclers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.classes.Car;
import com.example.wecanchargeapp.classes.CarDocument;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.classes.Notification;
import com.example.wecanchargeapp.classes.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewsAdapterRecycler extends RecyclerView.Adapter<ReviewsAdapterRecycler.ViewHolder> {

    private List<Review> myReviewsList;
    private Context context;

    private String TAG = "ASD";
    private String documentID;
    public List<Review> reviews;

    public ReviewsAdapterRecycler(List<Review> myReviewsList, Context context) {
        this.myReviewsList = myReviewsList;
        this.context = context;
    }

    ;

    public ReviewsAdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapterRecycler.ViewHolder holder, final int position) {
        final Review item = myReviewsList.get(position);
        getInfoByUID(item.getUserUID(), item, holder);

    }


    @Override
    public int getItemCount() {
        return myReviewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtUserName, txtRating, txtDate, txtComment;
        protected ImageView profilePic;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            txtUserName = itemView.findViewById(R.id.userNameReview);
            txtRating = itemView.findViewById(R.id.reviewRating);
            txtDate = itemView.findViewById(R.id.reviewDate);
            txtComment = itemView.findViewById(R.id.reviewComment);
            profilePic = itemView.findViewById(R.id.profileImageReview);

        }

    }

    private void getInfoByUID(String uid, final Review review, @NonNull final ReviewsAdapterRecycler.ViewHolder holder) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("userUID", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                holder.txtUserName.setText(document.getData().get("name").toString());
                                Picasso.get().load(document.getData().get("profilePic").toString()).into(holder.profilePic);
                                holder.txtComment.setText(review.getReview());
                                holder.txtDate.setText(review.getDate());
                                holder.txtRating.setText(review.getRating());

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

}
