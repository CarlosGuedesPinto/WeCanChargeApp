package com.example.wecanchargeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wecanchargeapp.classes.Car;
import com.example.wecanchargeapp.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String TAG = "ASD";
    private String email, password1, password2;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp(View view) {
        TextView emailTxt = findViewById(R.id.emailInput);
        TextView passwordTxt1 = findViewById(R.id.passwordInput);
        TextView passwordTxt2 = findViewById(R.id.passwordInput2);

        password1 = passwordTxt1.getText().toString();
        password2 = passwordTxt2.getText().toString();

        if (passwordTxt1.equals(passwordTxt2)) {
            Toast.makeText(CreateAccount.this, "Passwords don't match.",
                    Toast.LENGTH_SHORT).show();
        }
        else {

            email = emailTxt.getText().toString();
            firebaseSignUp(email, password1, view);

        }
    }

    public void firebaseSignUp(String email, String password, View view) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Sucess");
                                                Toast.makeText(CreateAccount.this, "Please verify your email.",
                                                        Toast.LENGTH_SHORT).show();
                                                addNewUser(mAuth.getCurrentUser().getUid());
                                            }
                                            else {
                                                Toast.makeText(CreateAccount.this, task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            Log.d(TAG, "createUserWithEmail:success");

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    protected void addNewUser(String userUID) {
        Log.d(TAG, "addNewUser");
        db = FirebaseFirestore.getInstance();
        TextView nameTxt = findViewById(R.id.nameInput);
        TextView emailTxt = findViewById(R.id.emailInput);
        List a = new ArrayList<>();
        List cars = new ArrayList<Car>();
        Car testingCar = new Car("1", "62-67-XZ", "https://cdn.motor1.com/images/mgl/X94Al/s1/electric-peugeot-e-208-in-detail-specs-images-videos.jpg", "Peugeout e-208");

        cars.add(testingCar);

        User newUser = new User(
                nameTxt.getText().toString(),
                emailTxt.getText().toString(),
                userUID,
                "",
                cars,
                a,
                a,
                a,
                0);

        db.collection("users")
                .add(newUser)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        Intent intent = new Intent(getBaseContext(), Login.class);
        startActivity(intent);
    }
}
