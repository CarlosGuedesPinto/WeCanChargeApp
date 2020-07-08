package com.example.wecanchargeapp.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.adaptersRecyclers.ChargerAdapterRecycler;
import com.example.wecanchargeapp.classes.Charger;
import com.example.wecanchargeapp.classes.ChargingHistory;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class StatisticsFragment extends Fragment {

    private String TAG = "ASD";

    public ArrayList<ChargingHistory> chargings;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView userChargingsNumber, userKwNumber, userMoneyNumber;
    private LineChartView chargingsNumberChart, kwChart, moneyChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getUIElements();
        calculateStatistics(GlobalUtils.currentUser.getUserUID());
    }

    private void calculateStatistics(String userId) {

        chargings = new ArrayList<ChargingHistory>();

        db.collection("chargingHistory")
                .whereEqualTo("idUser", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                chargings.add(document.toObject(ChargingHistory.class));

                            }
                            updateUICharging(chargings);
                            updateUIKw(chargings);
                            updateUIMoney(chargings);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void updateUIMoney(ArrayList<ChargingHistory> chargings) {
        //Not yet functional
    }

    private void updateUICharging(ArrayList<ChargingHistory> chargings) {

        userChargingsNumber.setText(String.valueOf(chargings.size()));

        List<PointValue> values = new ArrayList<PointValue>();
        Map<String, Integer> valuesFromDatabase = new HashMap<String, Integer>();

        List<AxisValue> xAxisValues = new ArrayList<AxisValue>();
        List<AxisValue> yAxisValues = new ArrayList<AxisValue>();

        for (int i = 0; i < chargings.size(); i++) {
            String dateStart = chargings.get(i).getDateStart();
            if (valuesFromDatabase.containsKey(dateStart)) {
                valuesFromDatabase.put(dateStart, valuesFromDatabase.get(dateStart) + 1);
            }
            else {
                valuesFromDatabase.put(dateStart,  1);
            }
        }

        Integer i = 0;
        for (Map.Entry<String, Integer> entry : valuesFromDatabase.entrySet()) {
            values.add(new PointValue(i, entry.getValue()).setLabel(entry.getKey()));
            xAxisValues.add(new AxisValue(i).setLabel(entry.getKey()));
            yAxisValues.add(new AxisValue(entry.getValue()));
            i++;
        }

        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Dia");
        axisX.setValues(xAxisValues);
        axisX.setHasTiltedLabels(true);

        axisY.setName("Nº Carregamentos");
        axisY.setValues(yAxisValues);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);


        chargingsNumberChart.setLineChartData(data);


    }

    private void updateUIKw(ArrayList<ChargingHistory> chargings) {
        //Calculate kwh
        Integer kwNumber = 0;
        for (int k = 0; k < chargings.size(); k++) {
            kwNumber += Integer.parseInt(chargings.get(k).getKw());
        }

        userKwNumber.setText(kwNumber.toString() + " kw");

        List<PointValue> valuesKw = new ArrayList<PointValue>();
        Map<String, Integer> valuesFromDatabaseKw = new HashMap<String, Integer>();

        List<AxisValue> xAxisValuesKw = new ArrayList<AxisValue>();
        List<AxisValue> yAxisValuesKw = new ArrayList<AxisValue>();

        for (int k = 0; k < chargings.size(); k++) {
            String dateStart = chargings.get(k).getDateStart();
            if (valuesFromDatabaseKw.containsKey(dateStart)) {
                valuesFromDatabaseKw.put(dateStart, valuesFromDatabaseKw.get(dateStart) + Integer.parseInt(chargings.get(k).getKw()));
            }
            else {
                valuesFromDatabaseKw.put(dateStart, Integer.parseInt(chargings.get(k).getKw()));
            }
        }

        Integer k = 0;
        for (Map.Entry<String, Integer> entry : valuesFromDatabaseKw.entrySet()) {
            valuesKw.add(new PointValue(k, entry.getValue()).setLabel(entry.getKey()));
            xAxisValuesKw.add(new AxisValue(k).setLabel(entry.getKey()));
            yAxisValuesKw.add(new AxisValue(entry.getValue()));
            k++;
        }

        Line lineKw = new Line(valuesKw).setColor(Color.GREEN).setCubic(true);
        List<Line> linesKw = new ArrayList<Line>();
        linesKw.add(lineKw);

        Axis axisXKw = new Axis();
        Axis axisYKw = new Axis().setHasLines(true);
        axisXKw.setName("Dia");
        axisXKw.setValues(xAxisValuesKw);
        axisXKw.setHasTiltedLabels(true);

        axisYKw.setName("Kw Gastos");
        axisYKw.setValues(yAxisValuesKw);

        LineChartData dataKw = new LineChartData();
        dataKw.setLines(linesKw);
        dataKw.setAxisXBottom(axisXKw);
        dataKw.setAxisYLeft(axisYKw);

        kwChart.setLineChartData(dataKw);


    }

    private void getUIElements() {

        userChargingsNumber = getView().findViewById(R.id.userChargingsNumber);
        userKwNumber = getView().findViewById(R.id.userKwNumber);

        chargingsNumberChart = getView().findViewById(R.id.chargingsNumberChart);
        kwChart = getView().findViewById(R.id.kwChart);

    }
}
