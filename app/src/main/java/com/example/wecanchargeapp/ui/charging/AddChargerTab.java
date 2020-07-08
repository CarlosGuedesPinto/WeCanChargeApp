package com.example.wecanchargeapp.ui.charging;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.classes.Charger;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddChargerTab extends Fragment {

    public EditText addChargerName, addChargerToken;
    public String chargerName, chargerToken;
    public Button addChargerBut;

    public Charger newCharger;
    public Button testConnectBut, readQRCodeBut;

    private MqttAndroidClient client;

    private Boolean result = false;
    
    public String TAG = "ASD";
    public String documentID;

    public AddChargerTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_charger_tab, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addChargerName = getView().findViewById(R.id.addChargerName);
        addChargerToken = getView().findViewById(R.id.addChargerToken);
        addChargerBut = getView().findViewById(R.id.addChargerBut);

        testConnectBut = getView().findViewById(R.id.testConnectionBut);

        readQRCodeBut = getView().findViewById(R.id.scanQRCodeBut);

        readQRCodeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                    startActivityForResult(intent, 0);

                } catch (Exception e) {

                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);

                }
            }
        });

        addChargerBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                chargerName = addChargerName.getText().toString();
                chargerToken = addChargerToken.getText().toString();

                newCharger = new Charger(GlobalUtils.currentUser.getUserUID(), chargerName, "", "ONLINE", chargerToken, false);

                addChargerToFirestore(newCharger, chargerToken);
            }
        });

        testConnectBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargerToken = addChargerToken.getText().toString();
                if (!chargerToken.equals("")) {
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Testing Connection...");
                    progressDialog.show();

                    testConnection(chargerToken);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (result) {
                                addChargerBut.setEnabled(true);
                                progressDialog.dismiss();

                                SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText("Connection made!");
                                pDialog.show();
                                pDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setVisibility(View.GONE);

                                client.unregisterResources();
                                client.close();
                            }
                            else {
                                addChargerBut.setEnabled(false);
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "The connection could not be made. PLease check the token(Id) or if the charger is pluged correctly.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, 10000);

                }
                else {
                    Toast.makeText(getActivity(), "You must have an token.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void testConnection(final String chargerToken) {

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getContext(), "tcp://89.152.208.73", clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("evwallbox");
        options.setPassword("evw2020evw".toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    subscribe(client, "evwallbox/" + chargerToken + "/sensor/" + chargerToken + "pzem1/state");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    Log.d(TAG, exception.toString());
                }

            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    private void subscribe(MqttAndroidClient client, String topic) {

        int qos = 0;

        try {
            //Test connection with the Mqtt server
            if (client.isConnected()) {

                client.subscribe(topic, qos);
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(final String topic, MqttMessage message) throws Exception {
                        Log.d(TAG, "Message Received!");
                        result = true;
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
                result = false;
            }
        } catch (Exception e) {
            Log.d(TAG,"Error :" + e);
        }
    }

    public void addChargerToFirestore(Charger newCharger, String MqttId) {

        sucessNewCharger();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chargers")
                .add(newCharger)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentID = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        db.collection("chargers")
                                .document(documentID)
                                .update("chargerUID", documentID)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
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

    }

    private void sucessNewCharger() {

        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText("New Charger Added");
        pDialog.setContentText("Já pode ver o estado do seu carregador!");
        pDialog.show();
        pDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setVisibility(View.GONE);

        addChargerName.setText("");
        addChargerToken.setText("");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                addChargerToken.setText(contents);
            }
            if(resultCode == Activity.RESULT_CANCELED){
                //handle cancel
            }
        }
    }
}
