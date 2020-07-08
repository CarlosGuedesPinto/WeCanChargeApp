package com.example.wecanchargeapp.adaptersRecyclers;

import android.content.Context;
import android.content.Intent;
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
import com.example.wecanchargeapp.classes.Businesses;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.classes.Notification;
import com.example.wecanchargeapp.ui.map.BusinessPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BusinessesMapAdapterRecycler extends RecyclerView.Adapter<BusinessesMapAdapterRecycler.ViewHolder> {

    private List<Businesses> myBusinessesList;
    private Context context;

    private String TAG = "ASD";
    private String documentID;

    public BusinessesMapAdapterRecycler(List<Businesses> myBusinessesList, Context context) {
        this.myBusinessesList = myBusinessesList;
        this.context = context;
    };

    public BusinessesMapAdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BusinessesMapAdapterRecycler.ViewHolder holder, final int position) {
        final Businesses item = myBusinessesList.get(position);

        holder.businessName.setText(item.getName());
        holder.businessCondition.setText(item.getName());
        Picasso.get().load(item.getPhoto()).into(holder.businessImageCard);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalUtils.currentBusiness = item;
                Intent intent = new Intent(context, BusinessPage.class);
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return myBusinessesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView businessName, businessCondition, businessRating;
        protected ImageView businessImageCard;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            businessCondition = itemView.findViewById(R.id.conditionTitleBusinessCard);
            businessName = itemView.findViewById(R.id.businessNameCard);
            businessImageCard = itemView.findViewById(R.id.businessImageCard);
            businessRating = itemView.findViewById(R.id.ratingBusinessCard);

        }

    }

}
