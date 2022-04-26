package com.example.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String LOG_TAG = SettingsActivity.class.getName();

    Spinner spinner;
    RadioGroup accountTypeGroup;

    private SharedPreferences preferences;

    private FirebaseFirestore mFirestore;
    private CollectionReference mDatabase;
    DocumentReference mDBRef;


    private TextView addressText;
    private TextView emailText;
    private EditText addressEditTextNew;

    private String address;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "settings onCreate");
        setContentView(R.layout.activity_settings);

        addressText = (TextView)findViewById(R.id.addressText);
        emailText = (TextView)findViewById(R.id.emailText);
        addressEditTextNew = findViewById(R.id.addressEditText);

        mFirestore = FirebaseFirestore.getInstance();
        mDatabase = mFirestore.collection("Addresses");
        mDBRef = mFirestore.collection("Addresses").document(getUserId());

        if(getUserEmail() != null){
            getStuff();
        }
        else {
            addressText.setText("Jelenlegi cím: Nincs megadva.");
            emailText.setText("E-mail cím: Nincs megadva.");
        }
    }

    public String getUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getEmail();
        return name;
    }

    public String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String name = user.getDisplayName();
        return uid;
    }

    public void getStuff() {
        mDatabase.whereEqualTo("email", getUserEmail().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(LOG_TAG, document.getId() + " => " + document.getString("address"));
                                addressText.setText("Jelenlegi cím: " + document.getString("address"));
                                emailText.setText("E-mail cím: " + document.getString("email"));
                            }
                        }
                        else {
                            Log.d(LOG_TAG, "ERROR GETTING DOCUMENT: " + task.getException());
                            addressText.setText("Jelenlegi cím: Nincs megadva.");
                            emailText.setText("E-mail cím: Nincs megadva.");
                        }
                    }
                });
    }

    public void save(View view) {
        mDatabase.whereEqualTo("email", getUserEmail().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(LOG_TAG, document.getId() + " => " + document.getString("address"));
                                String cim = document.getString("address");
                                String email = document.getString("email");

                                // Set the address to the new address if not empty
                                Log.d(LOG_TAG, String.valueOf(addressEditTextNew.getText().toString().length()));
                                if(addressEditTextNew.getText().toString().length() > 0)
                                {
                                    cim = addressEditTextNew.getText().toString();

                                    // Store data
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("email", email);
                                    data.put("address", cim);

                                    // Szoval a törlés nem sikerül sehogy, ezért mivel a firestore a címet alapból dátum szerint kéri ezért mindig a legfrissebbet fogja a querry megkapni
                                    // Plusz feature, a régiek is el lesznek mentve
                                    // Delete existing data here

                                    mFirestore.collection("Addresses")
                                            .add(data);

                                    Log.d(LOG_TAG, "Sikeres cím változtatás!");
                                }
                                else {
                                    Log.d(LOG_TAG, "Üres címre nem változtathatom.");
                                }
                            }
                        }
                        else {
                            Log.w(LOG_TAG, "Sikertelen cím változtatás!");
                        }
                    }
                });

    }

    public void cancel(View view) {
        Intent intent_logout = new Intent(this, ShopListActivity.class);
        startActivity(intent_logout);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "settings onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "settings onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "settings onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "settings onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "settings onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "settings onRestart");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
