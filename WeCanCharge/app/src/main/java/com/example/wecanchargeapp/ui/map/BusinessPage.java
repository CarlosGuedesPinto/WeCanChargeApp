package com.example.wecanchargeapp.ui.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.adaptersRecyclers.BusinessesMapAdapterRecycler;
import com.example.wecanchargeapp.adaptersRecyclers.CarsAdapterRecycler;
import com.example.wecanchargeapp.adaptersRecyclers.NotificationsAdapterRecycler;
import com.example.wecanchargeapp.adaptersRecyclers.ReviewsAdapterRecycler;
import com.example.wecanchargeapp.classes.Businesses;
import com.example.wecanchargeapp.classes.Car;
import com.example.wecanchargeapp.classes.CarDocument;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.classes.Review;
import com.example.wecanchargeapp.classes.ReviewDocument;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BusinessPage extends AppCompatActivity {

    private static final String TAG = "ASD";
    private ImageView businessImage;
    private TextView conditionTitle, businessName, businessLocation, businessAddress, businessPhoneNumber, businessClosingTime;
    private TextView conditionClickable;

    private RecyclerView reviewsRV;
    private ReviewsAdapterRecycler reviewsAdapterRecycler;
    private ArrayList<Review> reviews = null;

    private Businesses currentBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_page);

        getRefFromLayout();

        if (GlobalUtils.currentBusiness == null) {
            Log.d(TAG, "There is no current Business selected!");
        }
        else {
            currentBusiness = GlobalUtils.currentBusiness;
            updateUI();
        }
    }

    private void updateUI() {

        Picasso.get().load(currentBusiness.getPhoto()).into(businessImage);

        conditionTitle.setText(currentBusiness.getConditionsTitle());
        businessName.setText(currentBusiness.getName());
        businessLocation.setText(currentBusiness.getLocation());
        businessAddress.setText(currentBusiness.getAddress());
        businessPhoneNumber.setText(currentBusiness.getPhoneNumber());
        businessClosingTime.setText("Closing at " + currentBusiness.getClosingTime());

        getReviews();

    }

    private void getReviews() {

        reviewsRV = findViewById(R.id.commentsRV);
        reviewsRV.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager((getBaseContext()));
        reviewsRV.setLayoutManager(llm);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("businesses")
                .whereEqualTo("uid", currentBusiness.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                List<Review> reviews = document.toObject(ReviewDocument.class).reviews;

                                if (reviews.size() > 0) {
                                    reviewsAdapterRecycler = new ReviewsAdapterRecycler(reviews, getBaseContext());
                                    reviewsRV.setAdapter(reviewsAdapterRecycler);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });


        //Log.d(TAG, reviews.toString());
/*
        if (reviews.size() > 0) {
            reviewsAdapterRecycler = new ReviewsAdapterRecycler(reviews, getBaseContext());
            reviewsRV.setAdapter(reviewsAdapterRecycler);
        }
?*/
    }


    private void getRefFromLayout() {

        businessImage = findViewById(R.id.businessImage);
        conditionTitle = findViewById(R.id.conditionTitle);
        businessName = findViewById(R.id.businessName);
        businessLocation = findViewById(R.id.businessLocation);
        businessAddress = findViewById(R.id.businessAddress);
        businessPhoneNumber = findViewById(R.id.businessPhoneNumber);
        businessClosingTime = findViewById(R.id.businessClosingTime);

        conditionClickable = findViewById(R.id.conditionClickable);

        conditionClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                new AlertDialog.Builder(BusinessPage.this)
                        .setTitle(currentBusiness.getConditionsTitle())
                        .setMessage(currentBusiness.getConditionsText())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                */
                SweetAlertDialog pDialog = new SweetAlertDialog(BusinessPage.this, SweetAlertDialog.WARNING_TYPE);
                pDialog.setTitleText(currentBusiness.getConditionsTitle());
                pDialog.setContentText(currentBusiness.getConditionsText());
                pDialog.show();
                pDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setVisibility(View.GONE);
            }
        });

        reviewsRV = findViewById(R.id.commentsRV);

    }
}
