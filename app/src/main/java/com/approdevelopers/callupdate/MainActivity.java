package com.approdevelopers.callupdate;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    SwitchCompat switchAlert;
    TextInputLayout textLayoutStatus;
    TextInputEditText editTextStatus;

    IncomingCallReceiver incomingCallReceiver ;

    private String[] permissions ;
    private List<String> permissionToAsk ;
    private Button btnSave;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI hooks
        switchAlert = findViewById(R.id.switch_call_alert);
        textLayoutStatus = findViewById(R.id.text_layout_status);
        editTextStatus = findViewById(R.id.edit_text_status);
        btnSave = findViewById(R.id.btn_save_prefs);


        permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.SEND_SMS};

        checkPermissions();


        btnSave.setOnClickListener(v->{
            saveUserPreferences();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndUpdateUserPrefs();

    }

    private void fetchAndUpdateUserPrefs() {

        SharedPreferences sharedPreferences = getSharedPreferences("User_Preferences",Context.MODE_PRIVATE);

        String status_text = sharedPreferences.getString("custom_status","");
        boolean alert_state = sharedPreferences.getBoolean("alert_state",false);

        if (!status_text.equals("")){
        editTextStatus.setText(status_text);
        }
        if (alert_state){
            registerIncomingCallReceiver();
        }
        switchAlert.setChecked(alert_state);

    }

    private void saveUserPreferences() {

        String statusText = Objects.requireNonNull(editTextStatus.getText()).toString();
        boolean alertState = switchAlert.isChecked();


        SharedPreferences sharedPreferences = getSharedPreferences("User_Preferences",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("custom_status",statusText);
        editor.putBoolean("alert_state",alertState);
        editor.apply();

        if (alertState){
            registerIncomingCallReceiver();
        }else {
            unregisterIncomingCallReceiver();
        }

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

    }

    private void checkPermissions(){
        permissionToAsk = new ArrayList<>();
        for (String permi: permissions){
            Log.i("Permissions", "checkPermissions: "+ permi);
            if (ContextCompat.checkSelfPermission(this, permi) != PackageManager.PERMISSION_GRANTED) {
                permissionToAsk.add(permi);

            }
        }
        if (permissionToAsk.size()>0){
            ActivityCompat.requestPermissions(this,permissionToAsk.toArray(new String[permissionToAsk.size()]) , 101);

        }


        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterIncomingCallReceiver();
    }

    private void registerIncomingCallReceiver(){
        incomingCallReceiver = new IncomingCallReceiver();
        IntentFilter intentFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        this.registerReceiver(incomingCallReceiver,intentFilter);
    }

    private void unregisterIncomingCallReceiver(){
        if (incomingCallReceiver!=null){
            this.unregisterReceiver(incomingCallReceiver);
        }
    }
}