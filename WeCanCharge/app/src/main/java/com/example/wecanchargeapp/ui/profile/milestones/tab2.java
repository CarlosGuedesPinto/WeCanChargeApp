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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tab2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tab2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Milestone> silverMilestones;
    private RecyclerView silverRV;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "ASD";

    private MilestonesAdapterRecycler milestonesAdapter;

    public tab2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tab2.
     */
    // TODO: Rename and change types and number of parameters
    public static tab2 newInstance(String param1, String param2) {
        tab2 fragment = new tab2();
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
        return inflater.inflate(R.layout.fragment_tab2_milestones, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUI();
    }

    private void updateUI() {

        silverRV = getView().findViewById(R.id.silverMilestonesRV);
        silverRV.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        silverRV.setLayoutManager(llm);
        silverMilestones = new ArrayList<Milestone>();

        db.collection("milestones")
                .whereEqualTo("color", "silver")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                silverMilestones.add(document.toObject(Milestone.class));

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        if (silverMilestones.size() > 0) {
                            milestonesAdapter = new MilestonesAdapterRecycler(silverMilestones, getContext());
                            silverRV.setAdapter(milestonesAdapter);
                        }
                    }
                });

    }
}