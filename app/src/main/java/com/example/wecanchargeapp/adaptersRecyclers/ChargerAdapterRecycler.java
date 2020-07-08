package com.example.wecanchargeapp.adaptersRecyclers;

import android.content.Context;
import android.content.Intent;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.ui.charging.chargerDetails.ChargerDetailsActivity;
import com.example.wecanchargeapp.classes.Charger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChargerAdapterRecycler extends RecyclerView.Adapter<ChargerAdapterRecycler.ViewHolder> {

    private ArrayList<Charger> myChargersList;
    //private List<String> myChargersList;
    private Context context;

    public ChargerAdapterRecycler(ArrayList<Charger> myChargersList, Context context) { this.myChargersList = myChargersList; this.context = context;};

    public ChargerAdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.charger_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChargerAdapterRecycler.ViewHolder holder, int position) {
        final Charger item = myChargersList.get(position);
        holder.txtCarregador.setText(item.getName());

        holder.txtStatus.setText(item.getLastKnowedState());
        /*HASH MAP ESTÀ AQUI*/
        /*
        HashMap<String, String> status = item.getStatus();

        for (Map.Entry<String, String> entry: status.entrySet()) {
            if (entry.getKey().equals("state")) {
                holder.txtStatus.setText(entry.getValue());
            }
        }
*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Update GlobalInf
                GlobalUtils.currentCharger = item;
                Intent intent = new Intent(context, ChargerDetailsActivity.class);
                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return myChargersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtCarregador, txtStatus;
        protected Button deleteChargerButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCarregador = itemView.findViewById(R.id.txtEditAccount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            deleteChargerButton = itemView.findViewById(R.id.deleteChargerButton);

        }

    }

}
