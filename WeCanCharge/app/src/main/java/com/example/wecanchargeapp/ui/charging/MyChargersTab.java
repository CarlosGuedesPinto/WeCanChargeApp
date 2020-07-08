package com.example.wecanchargeapp.ui.charging;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.adaptersRecyclers.ChargerAdapterRecycler;
import com.example.wecanchargeapp.classes.Charger;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyChargersTab extends Fragment {

    RecyclerView rvChargers;
    private ArrayList<Charger> chargers;

    private ChargerAdapterRecycler chargerAdapter;
    private LinearLayout emptyState;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "ASD";

    public MyChargersTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_my_chargers_tab, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getChargers();

    }


    public void getChargers() {

        rvChargers = getView().findViewById(R.id.chargersRV);
        rvChargers.setHasFixedSize(true);

        emptyState = getView().findViewById(R.id.emptyState);

        LinearLayoutManager llm = new LinearLayoutManager((getActivity()));
        rvChargers.setLayoutManager(llm);
        chargers = new ArrayList<Charger>();

        db.collection("chargers")
                .whereEqualTo("userUID", GlobalUtils.currentUser.getUserUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                chargers.add(document.toObject(Charger.class));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        if (chargers.size() > 0) {
                            chargerAdapter = new ChargerAdapterRecycler(chargers, getContext());
                            rvChargers.setAdapter(chargerAdapter);
                        }
                        else {
                            emptyState.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

}
