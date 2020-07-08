package com.example.wecanchargeapp.ui.charging.chargerDetails;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.wecanchargeapp.BottomNav;
import com.example.wecanchargeapp.CreateAccount;
import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.adaptersRecyclers.ChargerAdapterRecycler;
import com.example.wecanchargeapp.classes.Charger;
import com.example.wecanchargeapp.classes.ChargingHistory;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.classes.Milestone;
import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Charging extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ASD";

    //private Charger charger;
    private String chargerId;
    private Button stopChargingBut;
    private CardView cardChargerNormal, cardChargerCharging;
    TextView chargerName, chargerName1, chargerState1, chargerState, chargerAmpere, chargerTimer, remainingTimeCard, energySpentCard;

    private String milestonesText = null;
    private Boolean newMilestone = false;


    private ChargingHistory historySnippet;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Charging() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Charging newInstance(String param1, String param2) {
        Charging fragment = new Charging();
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
        return inflater.inflate(R.layout.fragment_charger_charging, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateInfo();

        stopChargingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCharging(GlobalUtils.currentCharger.getChargerUID());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
    }

    private void updateInfo() {

        chargerId = GlobalUtils.currentCharger.getChargerMqttId();

        chargerName = getView().findViewById(R.id.chargerNamePage);
        chargerName1 = getView().findViewById(R.id.chargerNamePage1);
        chargerState = getView().findViewById(R.id.chargerPageChargerState);
        chargerState1 = getView().findViewById(R.id.chargerPageChargerState1);
        chargerAmpere = getView().findViewById(R.id.chargerAmpere);
        chargerTimer = getView().findViewById(R.id.chargerTimer);

        stopChargingBut = getView().findViewById(R.id.stopChargingBut);
        cardChargerCharging = getView().findViewById(R.id.cardChargerCharging);
        cardChargerNormal = getView().findViewById(R.id.cardChargerNormal);

        energySpentCard = getView().findViewById(R.id.energySpentCard);
        remainingTimeCard = getView().findViewById(R.id.remainingTimeCard);

        chargerName.setText(chargerId);

        if (GlobalUtils.currentCharger.getLastKnowedState().equals("CHARGING")) {
            cardChargerNormal.setVisibility(View.GONE);
            cardChargerCharging.setVisibility(View.VISIBLE);
            stopChargingBut.setVisibility(View.VISIBLE);
        }
        else {
            cardChargerNormal.setVisibility(View.VISIBLE);
            cardChargerCharging.setVisibility(View.GONE);
            stopChargingBut.setVisibility(View.GONE);
        }

        feedUI();
    }

    private void feedUI() {

        chargerName.setText(GlobalUtils.currentCharger.getName());
        chargerName1.setText(GlobalUtils.currentCharger.getName());

        chargerState.setText(GlobalUtils.currentCharger.getLastKnowedState());
        chargerState1.setText(GlobalUtils.currentCharger.getLastKnowedState());

        searchHistory(GlobalUtils.currentCharger.getChargerUID());

    }

    private void searchHistory(String chargerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chargingHistory")
                .whereEqualTo("idCharger", chargerId)
                .whereEqualTo("state", "CHARGING")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                historySnippet = document.toObject(ChargingHistory.class);

                                remainingTimeCard.setText(calculateDifferenceTime(historySnippet.getStartTime(), historySnippet.getEndTime()));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });


    }

    private String calculateDifferenceTime(String start, String end) {

        String response = null;

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(start);
            d2 = format.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = d2.getTime() - d1.getTime();
        long diffHours = diff / (60 * 60 * 1000);
        long diffMinutes = (diff / (60 * 1000)) - diffHours*60;

        response = diffHours + "H:" + diffMinutes + "m";

        return response;
    }

    private void stopCharging(String chargerId) {

        //Change charging variables from current Charger
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chargers")
                .document(chargerId)
                .update("isCharging", false)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            GlobalUtils.currentCharger.setState(false);
                        } else {
                            Toast.makeText(getActivity(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        db.collection("chargers")
                .document(chargerId)
                .update("lastKnowedState", "ONLINE")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            GlobalUtils.currentCharger.setLastKnowedState("ONLINE");
                        } else {
                            Toast.makeText(getActivity(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        final String[] documentID = {null};
        db.collection("chargingHistory")
                .whereEqualTo("idCharger", chargerId)
                .whereEqualTo("state", "CHARGING")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                documentID[0] = document.getString("uid");

                                db.collection("chargingHistory")
                                        .document(documentID[0])
                                        .update("state", "DONE")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                } else {
                                                    Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                Calendar rightNow = Calendar.getInstance();
                                int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
                                int currentMinutes = rightNow.get(Calendar.MINUTE);
                                String currentTimeStr = currentHourIn24Format + ":" + currentMinutes;

                                db.collection("chargingHistory")
                                        .document(documentID[0])
                                        .update("endTime", currentTimeStr)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        public void run() {
                                                            Intent intent = new Intent(getContext(), BottomNav.class);
                                                            startActivity(intent);
                                                        }
                                                    }, 2000);

                                                } else {
                                                    Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        updateMilestones();
        addPointsToUser(1000);
    }

    private void addPointsToUser(final Integer points) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String[] documentId = {null};

        db.collection("users")
                .whereEqualTo("userUID", GlobalUtils.currentUser.getUserUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                        if (task1.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task1.getResult()) {
                                documentId[0] = document.getId();
                            }
                            if (!documentId[0].equals(null)) {

                                db.collection("users")
                                        .document(documentId[0])
                                        .update("points", GlobalUtils.currentUser.getPoints() + points)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "+ " + points + " points!", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                        }
                        else {
                            Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateMilestones() {
        final ArrayList<Milestone> milestones = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("milestones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                milestones.add(document.toObject(Milestone.class));
                            }

                            List<String> userMilestones = GlobalUtils.currentUser.getMilestonesCompleted();
                            if (userMilestones.size() > 0) {

                                for (int i = 0; i < userMilestones.size(); i++) {
                                    for (int j = 0; j < milestones.size(); j++) {
                                        if (milestones.get(j).getUid().equals(userMilestones.get(i))) {
                                            milestones.remove(j);
                                        }
                                    }
                                }

                            }
                            calculateNewMilestones(milestones);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }

    private void calculateNewMilestones(final ArrayList<Milestone> milestones) {

        //Verify Charging Number
        final Integer[] numberOfChargings = {0};

        final String[] documentId = {null};

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("chargingHistory")
                .whereEqualTo("idUser", GlobalUtils.currentUser.getUserUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                numberOfChargings[0]++;
                            }
                            for (int i = 0; i < milestones.size(); i++) {

                                Integer conditional = Integer.parseInt(milestones.get(i).getConditional());
                                String type = milestones.get(i).getType();

                                if (numberOfChargings[0] == conditional && type.equals("chargingCarTimes")) {

                                    newMilestone = true;
                                    milestonesText = milestones.get(i).getText();

                                    GlobalUtils.currentUser.addNewMilestoneCompleted(milestones.get(i).getUid());

                                    db.collection("users")
                                            .whereEqualTo("userUID", GlobalUtils.currentUser.getUserUID())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                    if (task1.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task1.getResult()) {
                                                            documentId[0] = document.getId();
                                                        }
                                                        if (!documentId[0].equals(null)) {

                                                            db.collection("users")
                                                                    .document(documentId[0])
                                                                    .update("milestonesCompleted", GlobalUtils.currentUser.getMilestonesCompleted())
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {

                                                                            }
                                                                            else {
                                                                                Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                            db.collection("users")
                                                                    .document(documentId[0])
                                                                    .update("points", GlobalUtils.currentUser.getPoints() + 1000)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                GlobalUtils.currentUser.updatePoints(GlobalUtils.currentUser.getPoints() + 1000);
                                                                            }
                                                                            else {
                                                                                Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                                SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText("New Milestones Completed");
                                pDialog.setCustomImage(R.drawable.trofeugold);
                                pDialog.setContentText(milestonesText);
                                pDialog.show();
                                pDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setVisibility(View.GONE);


                            }
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }
}
