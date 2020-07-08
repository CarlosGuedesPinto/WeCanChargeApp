package com.example.wecanchargeapp.adaptersRecyclers;

import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.classes.Car;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.classes.Notification;
import com.example.wecanchargeapp.classes.NotificationDocument;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Iterator;
import java.util.List;

public class NotificationsAdapterRecycler extends RecyclerView.Adapter<NotificationsAdapterRecycler.ViewHolder> {

    private List<Notification> myNotificationsList;
    private Context context;

    private String TAG = "ASD";
    private String documentID;
    public List<Notification> nots;

    public NotificationsAdapterRecycler(List<Notification> myNotificationsList, Context context) {
        this.myNotificationsList = myNotificationsList;
        this.context = context;
    }

    ;

    public NotificationsAdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapterRecycler.ViewHolder holder, final int position) {
        final Notification item = myNotificationsList.get(position);
        holder.txtText.setText(item.getText());
        holder.txtDate.setText(item.getDate());
        holder.deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myNotificationsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                updateFirestore();
            }
        });

    }


    @Override
    public int getItemCount() {
        return myNotificationsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtDate, txtText;
        protected Button deleteBut;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            txtDate = itemView.findViewById(R.id.notificationDate);
            txtText = itemView.findViewById(R.id.notificationText);
            deleteBut = itemView.findViewById(R.id.deleteNotBut);

        }

    }

    public void updateFirestore() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("userUID", GlobalUtils.currentUser.getUserUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                documentID = document.getId();
                                if (documentID != null) {
                                    db.collection("users")
                                            .document(documentID)
                                            .update("notifications", myNotificationsList);
                                    Log.d(TAG, "User information updated with success.");
                                } else {
                                    Log.d(TAG, "Current User not found in the Database.");
                                }
                            }
                        }

                    }

                });
    }
}
