package com.example.wecanchargeapp.ui.profile.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.adaptersRecyclers.CarsAdapterRecycler;
import com.example.wecanchargeapp.adaptersRecyclers.NotificationsAdapterRecycler;
import com.example.wecanchargeapp.classes.Car;
import com.example.wecanchargeapp.classes.CarDocument;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.classes.Notification;
import com.example.wecanchargeapp.classes.NotificationDocument;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Notifications extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    RecyclerView notificationsRV;
    private NotificationsAdapterRecycler notsAdapter;
    LinearLayout notsEmptyState;
    private String TAG = "ASD";

    public Notifications() {
        // Required empty public constructor
    }

    public static Notifications newInstance(String param1, String param2) {
        Notifications fragment = new Notifications();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        notsEmptyState = getView().findViewById(R.id.emptyStateLayout);

        notificationsRV = getView().findViewById(R.id.notificationsRV);
        notificationsRV.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager((getActivity()));
        notificationsRV.setLayoutManager(llm);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("userUID", GlobalUtils.currentUser.getUserUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                List<Notification> nots = document.toObject(NotificationDocument.class).notifications;

                                if (nots.size() > 0) {
                                    notsAdapter = new NotificationsAdapterRecycler(nots, getContext());
                                    notificationsRV.setAdapter(notsAdapter);
                                }
                                else {
                                    notsEmptyState.setVisibility(View.VISIBLE);
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

        List<Notification> nots = GlobalUtils.currentUser.getNotifications();



    }
}
