package com.example.wecanchargeapp.ui.profile.milestones;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.adaptersRecyclers.MilestonesAdapterRecycler;
import com.example.wecanchargeapp.classes.Milestone;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class tab3 extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ArrayList<Milestone> bronzeMilestones;
    private RecyclerView bronzeRV;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "ASD";

    private MilestonesAdapterRecycler milestonesAdapter;

    public tab3() {
        // Required empty public constructor
    }

    public static tab3 newInstance(String param1, String param2) {
        tab3 fragment = new tab3();
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
        return inflater.inflate(R.layout.fragment_tab3_milestones, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUI();
    }

    private void updateUI() {

        bronzeRV = getView().findViewById(R.id.bronzeMilestonesRV);
        bronzeRV.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        bronzeRV.setLayoutManager(llm);
        bronzeMilestones = new ArrayList<Milestone>();

        db.collection("milestones")
                .whereEqualTo("color", "bronze")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bronzeMilestones.add(document.toObject(Milestone.class));

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        if (bronzeMilestones.size() > 0) {
                            milestonesAdapter = new MilestonesAdapterRecycler(bronzeMilestones, getContext());
                            bronzeRV.setAdapter(milestonesAdapter);
                        }
                    }
                });

    }
}