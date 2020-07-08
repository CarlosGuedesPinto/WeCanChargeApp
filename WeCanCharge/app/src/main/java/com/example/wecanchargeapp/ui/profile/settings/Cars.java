package com.example.wecanchargeapp.ui.profile.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.adaptersRecyclers.CarsAdapterRecycler;
import com.example.wecanchargeapp.adaptersRecyclers.ChargerAdapterRecycler;
import com.example.wecanchargeapp.classes.Car;
import com.example.wecanchargeapp.classes.CarDocument;
import com.example.wecanchargeapp.classes.Charger;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Cars extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    RecyclerView carsRV;
    private CarsAdapterRecycler carsAdapter;
    private String TAG = "ASD";

    public Cars() {
        // Required empty public constructor
    }

    public static Cars newInstance(String param1, String param2) {
        Cars fragment = new Cars();
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
        return inflater.inflate(R.layout.fragment_cars_edit, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        carsRV = getView().findViewById(R.id.carsRV);
        carsRV.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager((getActivity()));
        carsRV.setLayoutManager(llm);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("userUID", GlobalUtils.currentUser.getUserUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                List<Car> cars = document.toObject(CarDocument.class).cars;

                                if (cars.size() > 0) {
                                    carsAdapter = new CarsAdapterRecycler(cars, getContext());
                                    carsRV.setAdapter(carsAdapter);
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }
}
