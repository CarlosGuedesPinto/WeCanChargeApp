package com.example.wecanchargeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wecanchargeapp.classes.GlobalUtils;
import com.example.wecanchargeapp.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email, password;
    private String TAG = "ASD";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(View view) {

        TextView emailTxt = findViewById(R.id.emailInput);
        TextView passwordTxt = findViewById(R.id.passwordInput);
        email = emailTxt.getText().toString();
        password = passwordTxt.getText().toString();

        firebaseSignIn(email,password,view);

    }

    public void firebaseSignIn(String email, String password, View view) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //Log.d(TAG,user.toString());
                                getUserLogged(mAuth.getUid());
                            }
                            else {
                                Toast.makeText(Login.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_UP) :
                Log.d(TAG,"Action was UP");
                Intent intent = new Intent(getBaseContext(), CreateAccount.class);
                startActivity(intent);

                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    public void getUserLogged(String uid) {

        db.collection("users")
                .whereEqualTo("userUID", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final User user = document.toObject(User.class);
                                GlobalUtils.currentUser = user;

                                Intent intent = new Intent(getBaseContext(), BottomNav.class);
                                startActivity(intent);
                            }
                        } else {
                            Log.w("ASD", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
