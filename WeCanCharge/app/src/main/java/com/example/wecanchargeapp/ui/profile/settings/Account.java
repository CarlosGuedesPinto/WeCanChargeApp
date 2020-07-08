package com.example.wecanchargeapp.ui.profile.settings;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.wecanchargeapp.CreateAccount;
import com.example.wecanchargeapp.Login;
import com.example.wecanchargeapp.R;
import com.example.wecanchargeapp.classes.GlobalUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;

public class Account extends Fragment {


    EditText name, email;
    private String namePrev, emailPrev, documentID, newProfilePicUrl;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage;
    StorageReference storageReference;

    ImageView profilePicView;

    private Uri profilePicUrl = null;
    private static final Integer IMAGE_PICK_CODE = 1000;
    private static final Integer PERMISSION_CODE = 1001;
    private static final String TAG = "ASD";

    public Account() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        name = getView().findViewById(R.id.nameEdit);
        email = getView().findViewById(R.id.emailEdit);
        profilePicView = getView().findViewById(R.id.profilePicView);

        db.collection("users")
                .whereEqualTo("userUID", GlobalUtils.currentUser.getUserUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                    name.setText(document.get("name").toString());
                                    email.setText(document.get("email").toString());

                                    namePrev = name.getText().toString();
                                    emailPrev = email.getText().toString();

                                }
                        } else {
                            Log.w("ASD", "Error getting documents.", task.getException());
                        }
                    }
                });

        final Button but = getView().findViewById(R.id.editBut);
        but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateInfo(v);
            }
        });

        final Button butEditPic = getView().findViewById(R.id.editProfilePic);
        butEditPic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeProfilePic(v);
            }
        });

        final Button butLogout = getView().findViewById(R.id.logoutBut);
        butLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout(v);
            }
        });
    }

    public void changeProfilePic(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //permission not granted, request it
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //show popup for runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            }
            else {
                //permission already granted
                pickImageFromGallery();
            }
        }
    }

    private void pickImageFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set URL
            profilePicUrl = data.getData();
            profilePicView.setImageURI(profilePicUrl);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission was granted
                pickImageFromGallery();
            }
            else {
                //permission was denied
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private <StorageReference> void uploadImageToFirebase() {
        if (profilePicUrl != null) {

            Task profilePicUploadTask;
            File file = new File(String.valueOf(profilePicUrl));

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final com.google.firebase.storage.StorageReference ref  = storageReference.child("ProfilePictures/" + UUID.randomUUID().toString());

            profilePicUploadTask = ref.putFile(profilePicUrl).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    //Toast.makeText(getActivity(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            newProfilePicUrl = uri.toString();
                                            uploadImageAndUpdateInfo();
                                        }
                                    });
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                    });
        }
}

    public void uploadImageAndUpdateInfo() {
        if (newProfilePicUrl != null) {
            db.collection("users")
                    .whereEqualTo("userUID", GlobalUtils.currentUser.getUserUID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        documentID = document.getId();
                                        if (documentID != null) {

                                            if (!document.getData().get("profilePic").equals("")) {
                                                deletePreviousProfilePic(document.getString("profilePic"));
                                            }

                                            db.collection("users")
                                                    .document(documentID)
                                                    .update("name", name.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                GlobalUtils.currentUser.setName(name.getText().toString());
                                                            } else {
                                                                Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                            db.collection("users")
                                                    .document(documentID)
                                                    .update("email", email.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                GlobalUtils.currentUser.setEmail(email.getText().toString());
                                                            } else {
                                                                Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                            db.collection("users")
                                                    .document(documentID)
                                                    .update("profilePic", newProfilePicUrl)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                GlobalUtils.currentUser.setProfilePic(newProfilePicUrl);
                                                            } else {
                                                                Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            Toast.makeText(getContext(), "Current User not found in the Database.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                        }
                    });
        }
        else {
            Toast.makeText(getContext(), "Couldn't get image link.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePreviousProfilePic(String oldProfilePic) {

        StorageReference photoRef = storage.getReferenceFromUrl(oldProfilePic);

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d("ASD", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d("ASD", "onFailure: did not delete file");
            }
        });
    }

    public void updateInfo(View v) {

        if (namePrev.equals(name.getText().toString()) && emailPrev.equals(email.getText().toString()) && profilePicUrl.toString().equals(null)) {
            Toast.makeText(getContext(), "No changes to be made", Toast.LENGTH_SHORT).show();
        }
        else {
            if (profilePicUrl != (null)) {
                uploadImageToFirebase();
            }
            else {
                db.collection("users")
                        .whereEqualTo("userUID", GlobalUtils.currentUser.getUserUID())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                            documentID = document.getId();
                                            if (documentID != null) {
                                                db.collection("users")
                                                        .document(documentID)
                                                        .update("name", name.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    GlobalUtils.currentUser.setName(name.getText().toString());
                                                                } else {
                                                                    Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                db.collection("users")
                                                        .document(documentID)
                                                        .update("email", email.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    GlobalUtils.currentUser.setEmail(email.getText().toString());
                                                                }
                                                                else {
                                                                    Toast.makeText(getContext(), "Error with Firestore.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                                pDialog.setTitleText("User information updated with success!");
                                                pDialog.show();
                                                pDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setVisibility(View.GONE);
                                            }
                                            else {
                                                Toast.makeText(getContext(), "Current User not found in the Database.", Toast.LENGTH_SHORT).show();
                                            }
                                    }
                                } else {
                                    Log.w("ASD", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }

        }

    }

    public void logout(View v) {

        FirebaseAuth.getInstance().signOut();
        Log.d(TAG, "User is logged out");

        GlobalUtils.currentUser = null;
        Intent intent = new Intent(getContext(), Login.class);
        startActivity(intent);
    }
}
