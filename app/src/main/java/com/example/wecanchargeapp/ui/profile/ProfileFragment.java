package com.example.wecanchargeapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.wecanchargeapp.CreateAccount;
import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.classes.Car;
import com.example.wecanchargeapp.classes.CarDocument;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.ui.profile.milestones.MilestonesActivity;
import com.example.wecanchargeapp.ui.profile.settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    String TAG = "ASD";

    TextView email, name, userPoints, carModel, licensePlate, userPointsMissing;
    ImageView profilePic, carImage;
    CircularProgressBar levelProgressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        return root;
    }

    public void updateInformation() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        email = getView().findViewById(R.id.userEmail);
        name = getView().findViewById(R.id.userName);
        userPoints = getView().findViewById(R.id.userPoints);
        userPointsMissing = getView().findViewById(R.id.userPointsMissingLvlUp);
        profilePic = getView().findViewById(R.id.profilePic);

        licensePlate = getView().findViewById(R.id.carLicensePlate);
        carModel = getView().findViewById(R.id.carModel);
        carImage = getView().findViewById(R.id.carProfilePic);

        levelProgressBar = getView().findViewById(R.id.progressBarLevel);

        name.setText(GlobalUtils.currentUser.getName());
        email.setText(GlobalUtils.currentUser.getEmail());
        if (!GlobalUtils.currentUser.getProfilePic().equals("")) {
            Picasso.get().load(GlobalUtils.currentUser.getProfilePic()).into(profilePic);
        }



        String userCurPoints = GlobalUtils.currentUser.getPoints().toString();
        //userPoints.setText(userCurPoints + " Points");
        userPoints.setText( "Level " + Integer.parseInt(userCurPoints)/1000);
        Integer pointsAchievedInThousand = null;
        //CAlculate missing points to lvl up
        if (userCurPoints.length() > 3) {
            pointsAchievedInThousand = Integer.parseInt(userCurPoints.substring(1));
        }
        else {
            pointsAchievedInThousand = Integer.parseInt(userCurPoints);
        }

        Integer missingPointsToLvlUp = 1000 - pointsAchievedInThousand;

        userPointsMissing.setText(missingPointsToLvlUp + " to Level Up");

        levelProgressBar.setProgress(pointsAchievedInThousand/10);

        Map<String, String> firstCar = (Map)GlobalUtils.currentUser.getFirstCar();

        for (Map.Entry<String, String> entry : firstCar.entrySet()) {
            if (entry.getKey().equals("licensePlate")) {
                licensePlate.setText(entry.getValue());
            }
            if (entry.getKey().equals("name")) {
                carModel.setText(entry.getValue());
            }
            if (entry.getKey().equals("photo")) {
                Picasso.get().load(entry.getValue()).into(carImage);
            }
        }
    }

    public void onResume() {
        
        super.onResume();
        updateInformation();

    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        final Button but = getView().findViewById(R.id.settingsBut);
        but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToSettings(v);
            }
        });

        final Button butMilestones = getView().findViewById(R.id.milestonesBut);
        butMilestones.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MilestonesActivity.class);
                startActivity(intent);
            }
        });

        updateInformation();

    }

    public void goToSettings(View view) {

        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);

    }
}
