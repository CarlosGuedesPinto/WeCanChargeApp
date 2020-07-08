package com.example.wecanchargeapp.ui.charging.chargerDetails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.classes.Charger;
import com.example.wecanchargeapp.classes.ChargingHistory;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChargerDetailsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tab1, tab2, tab3;
    public PageAdapter pagerAdapter;
    public ImageView startChargeImg, stopChargingImg, offlineChargingImg;
    public Button backArrow;
    private Integer timer;

    private LinearLayout linearLayoutBackgroundChargerPage;

    public String documentID;
    public String TAG = "ASD";

    MqttAndroidClient client;

    //Charger currentCharger;
    public String currentChargerId;

    TextView chargerName, chargerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charger_details);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tab1 = (TabItem) findViewById(R.id.Carregar);
        tab2 = (TabItem) findViewById(R.id.Informacoes);
        tab3 = (TabItem) findViewById(R.id.Definicoes);
        viewPager = findViewById(R.id.viewpager);
        startChargeImg = findViewById(R.id.startChargeImg);
        stopChargingImg = findViewById(R.id.stopChargingImg);
        offlineChargingImg = findViewById(R.id.offlineChargingImg);

        linearLayoutBackgroundChargerPage = findViewById(R.id.linearLayoutBackgroundChargerPage);
        changeUI(GlobalUtils.currentCharger.getLastKnowedState());

        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChargerDetailsActivity.super.onBackPressed();
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    pagerAdapter.notifyDataSetChanged();
                }
                else if (tab.getPosition() == 1) {
                    pagerAdapter.notifyDataSetChanged();
                }
                else if (tab.getPosition() == 2) {
                    pagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        startChargeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChargerDetailsActivity.this);
                builder.setTitle("Set Timer");

                final TimePicker input = new TimePicker(ChargerDetailsActivity.this);

                builder.setView(input);

                builder.setPositiveButton("Começar Carregamento", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer hour = input.getHour();
                        Integer minutes = input.getMinute();
                        String timerStr = hour.toString() + ":" + minutes.toString();

                        Date c = Calendar.getInstance().getTime();
                        System.out.println("Current time => " + c);

                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = df.format(c);

                        Calendar rightNow = Calendar.getInstance();
                        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
                        int currentMinutes = rightNow.get(Calendar.MINUTE);
                        String currentTimeStr = null;

                        //Log.d(TAG, String.valueOf(rightNow.get(Calendar.MINUTE)));

                        if (String.valueOf(currentMinutes).length() == 1) {
                            currentTimeStr = currentHourIn24Format + ":0" + currentMinutes;
                        }
                        else {
                            currentTimeStr = currentHourIn24Format + ":" + currentMinutes;
                        }

                        ChargingHistory newCharge = new ChargingHistory("", GlobalUtils.currentCharger.getChargerUID(), GlobalUtils.currentUser.getUserUID(), "50", currentTimeStr, timerStr, "CHARGING", formattedDate);

                        addToFirestore(newCharge);

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }

    private void changeUI(String state) {

        if (state.equals("CHARGING")) {
            //Change background color
            stopChargingImg.setVisibility(View.VISIBLE);
            startChargeImg.setVisibility(View.GONE);
            offlineChargingImg.setVisibility(View.GONE);
            linearLayoutBackgroundChargerPage.setBackgroundResource(R.drawable.background);
        }
        else if (state.equals("ONLINE")) {
            stopChargingImg.setVisibility(View.GONE);
            startChargeImg.setVisibility(View.VISIBLE);
            offlineChargingImg.setVisibility(View.GONE);
            linearLayoutBackgroundChargerPage.setBackgroundResource(R.drawable.background_blue);
        }
        else {
            stopChargingImg.setVisibility(View.GONE);
            startChargeImg.setVisibility(View.GONE);
            offlineChargingImg.setVisibility(View.VISIBLE);
            linearLayoutBackgroundChargerPage.setBackgroundResource(R.drawable.background_orange);
        }




    }

    private void addToFirestore(ChargingHistory charge) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chargingHistory")
                .add(charge)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentID = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        db.collection("chargingHistory")
                                .document(documentID)
                                .update("uid", documentID)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChargerDetailsActivity.this, "Your car is already charging.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChargerDetailsActivity.this, "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        db.collection("chargers")
                .document(GlobalUtils.currentCharger.getChargerUID())
                .update("isCharging", true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            GlobalUtils.currentCharger.setState(true);
                        } else {
                            Toast.makeText(ChargerDetailsActivity.this, "Error with Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        db.collection("chargers")
                .document(GlobalUtils.currentCharger.getChargerUID())
                .update("lastKnowedState", "CHARGING")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            GlobalUtils.currentCharger.setLastKnowedState("CHARGING");
                            changeUI(GlobalUtils.currentCharger.getLastKnowedState());
                        } else {
                            Toast.makeText(ChargerDetailsActivity.this, "Error with Firestore.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }



}

