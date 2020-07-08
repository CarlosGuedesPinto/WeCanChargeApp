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
import com.example.wecanchargeapp.classes.Charger;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.classes.Milestone;
import com.example.wecanchargeapp.classes.UserMilestones;
import com.example.wecanchargeapp.classes.UserMilestonesDocument;
import com.example.wecanchargeapp.ui.profile.settings.Cars;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MilestonesAdapterRecycler extends RecyclerView.Adapter<MilestonesAdapterRecycler.ViewHolder> {

    private List<Milestone> myMilestonesList;
    private Context context;
    private String TAG = "ASD";
    private List<String> milestonesUser;

    public MilestonesAdapterRecycler(List<Milestone> myMilestonesList, Context context) { this.myMilestonesList = myMilestonesList; this.context = context;};

    public MilestonesAdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.milestone_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MilestonesAdapterRecycler.ViewHolder holder, int position) {
        final Milestone item = myMilestonesList.get(position);

        holder.textMilestoneCard.setText(item.getText());

        updateIcon(holder, item.getUid(), item.getColor());

    }


    @Override
    public int getItemCount() {
        return myMilestonesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView trophyIconMilestoneCard;
        protected TextView textMilestoneCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trophyIconMilestoneCard = itemView.findViewById(R.id.trophyIconMilestoneCard);
            textMilestoneCard = itemView.findViewById(R.id.textMilestoneCard);
        }

    }

    private void updateIcon(@NonNull final MilestonesAdapterRecycler.ViewHolder holder, final String itemId, final String itemColor) {

        if (GlobalUtils.currentUser.getMilestonesCompleted().size() > 0) {
            if (itemColor.equals("bronze")) {
                if (GlobalUtils.currentUser.getMilestonesCompleted().contains(itemId)) {
                    holder.trophyIconMilestoneCard.setImageResource(R.drawable.trofeubronze1);
                } else {
                    holder.trophyIconMilestoneCard.setImageResource(R.drawable.trofeubronze);
                }
            }
            else if (itemColor.equals("silver")) {
                if (GlobalUtils.currentUser.getMilestonesCompleted().contains(itemId)) {
                    holder.trophyIconMilestoneCard.setImageResource(R.drawable.trofeusilver1);
                } else {
                    holder.trophyIconMilestoneCard.setImageResource(R.drawable.trofeusilver);
                }
            }
            else if (itemColor.equals("gold")) {
                if (GlobalUtils.currentUser.getMilestonesCompleted().contains(itemId)) {
                    holder.trophyIconMilestoneCard.setImageResource(R.drawable.trofeugold1);
                } else {
                    holder.trophyIconMilestoneCard.setImageResource(R.drawable.trofeugold);
                }
            }
        }
        else {
            //EMpty state
        }

    }

}
