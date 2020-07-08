package com.example.wecanchargeapp.ui.charging.chargerDetails;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.classes.Charger;
import com.example.wecanchargeapp.classes.GlobalUtils;

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

public class Informations extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView chargerVoltageInfo, chargerCurrentInfo, chargerPowerInfo, chargerEnergyInfo, chargerTemperatureInfo, chargerHumidityInfo, chargerNameInfoPage, chargerPageChargerStateInfoPage;
    private String TAG = "ASD";

    MqttAndroidClient client;

    public Informations() {
        // Required empty public constructor
    }

    public static Informations newInstance(String param1, String param2) {
        Informations fragment = new Informations();
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
        return inflater.inflate(R.layout.fragment_charger_informations, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getElementsFromUI();
        connectMqttp(GlobalUtils.currentCharger.getChargerMqttId());
    }

    private void getElementsFromUI() {

        chargerCurrentInfo = getView().findViewById(R.id.chargerCurrentInfo);
        chargerVoltageInfo = getView().findViewById(R.id.chargerVoltageInfo);
        chargerPowerInfo = getView().findViewById(R.id.chargerPowerInfo);
        chargerEnergyInfo = getView().findViewById(R.id.chargerEnergyInfo);
        chargerTemperatureInfo = getView().findViewById(R.id.chargerTemperatureInfo);
        chargerHumidityInfo = getView().findViewById(R.id.chargerHumidityInfo);
        chargerPageChargerStateInfoPage = getView().findViewById(R.id.chargerPageChargerStateInfoPage);

        chargerNameInfoPage = getView().findViewById(R.id.chargerNameInfoPage);
        chargerNameInfoPage.setText(GlobalUtils.currentCharger.getName());
        chargerPageChargerStateInfoPage.setText(GlobalUtils.currentCharger.getLastKnowedState());

    }

    private void connectMqttp(final String idCharger) {

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
                    //Subscribe to obtain Voltage, Current, Power and Energy Info
                    subscribe(client, "evwallbox/" + idCharger + "/sensor/" + idCharger + "pzem1/state");
                    subscribe(client, "evwallbox/" + idCharger + "/sensor/" + idCharger + "temp/state");

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
                        //Payload Received
                        String payload = new String(message.getPayload());

                        //Algorithm to convert payload to HashMap
                        payload = payload.replace("{","");
                        payload = payload.replace("}","");
                        payload = payload.replace("\"","");

                        final Map<String, String> state = new HashMap<String, String>();
                        String[] pairs = payload.split(",");

                        for (int i=0;i<pairs.length;i++) {
                            String pair = pairs[i];
                            String[] keyValue = pair.split(":");
                            if (!keyValue[0].equals("pf") && !keyValue[0].equals("frequency")) {
                                state.put(keyValue[0], (keyValue[1]));
                            }
                        }



                        //Clear work from the activity, make the feeding of the page happen in the Ui Thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (topic.contains("pzem1")) {
                                    feedPageInfo(state, "Voltage");
                                }
                                else {
                                    feedPageInfo(state, "Humidity");
                                }

                            }
                        });


                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
            }
        } catch (Exception e) {
            Log.d(TAG,"Error :" + e);
        }
    }


    public void feedPageInfo(Map<String, String> state, String type) {

        if (type == "Voltage") {
            for (Map.Entry<String, String> entry: state.entrySet()) {
                switch (entry.getKey()) {
                    case "voltage":
                        chargerVoltageInfo.setText(entry.getValue());
                        break;
                    case "current":
                        chargerCurrentInfo.setText(entry.getValue());
                        break;
                    case "power":
                        chargerPowerInfo.setText(entry.getValue());
                        break;
                    case "energy":
                        chargerEnergyInfo.setText(entry.getValue());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid key on Mqtt server response: " + entry.getKey());
                }

            }
        }
        else if (type == "Humidity") {
            for (Map.Entry<String, String> entry : state.entrySet()) {
                switch (entry.getKey()) {
                    case "humidity":
                        chargerHumidityInfo.setText(entry.getValue());
                        break;
                    case "temperature_1":
                        chargerTemperatureInfo.setText(entry.getValue());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid key on Mqtt server response: " + entry.getKey());
                }

            }
        } else {
            Log.d(TAG, "Incorrect type of topic indicated!");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        client.unregisterResources();
        client.close();

    }
}
