package com.example.wecanchargeapp.adaptersRecyclers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.classes.Car;

import java.util.List;

public class CarsAdapterRecycler extends RecyclerView.Adapter<CarsAdapterRecycler.ViewHolder> {

    private List<Car> myCarsList;
    private Context context;
    private String TAG = "ASD";

    public CarsAdapterRecycler(List<Car> myCarsList, Context context) { this.myCarsList = myCarsList; this.context = context;};

    public CarsAdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cars_edit_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarsAdapterRecycler.ViewHolder holder, int position) {
        final Car item = myCarsList.get(position);

        holder.txtName.setText(item.getName());
        /*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Update GlobalInf
                GlobalUtils.currentCharger = item;
                Intent intent = new Intent(context, ChargerDetailsActivity.class);
                intent.putExtra("currentCharger", item);
                context.startActivity(intent);
            }
        });*/

    }


    @Override
    public int getItemCount() {
        return myCarsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtName;
        protected Button editBut;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.carNameCard);
            editBut = itemView.findViewById(R.id.carEditButCard);
        }

    }

}
