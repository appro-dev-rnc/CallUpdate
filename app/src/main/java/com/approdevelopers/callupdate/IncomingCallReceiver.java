package com.approdevelopers.callupdate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class IncomingCallReceiver extends BroadcastReceiver {

    Context context;

    private static final String CHANNEL_ID = "0";
    private static final String CHANNEL_NAME = "Alert Channel";

    public static final int NOTIFICATION_ID = 1002;

    private SharedPreferences sharedPreferences;

    private boolean call_rang,call_received;
    private String callerNumber;


    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences("User_Preferences", Context.MODE_PRIVATE);
        this.context = context;
        Log.i("IncomingCallReceiver", "onReceive: ");
        String action = intent.getAction();
        if (action != null && action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state != null) {
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    call_rang = true;
                    // Incoming call ringing
                    if (checkAlertState()) {
                        showToastNotification();
                    }
                    callerNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    // Call ended, either missed or denied
                    Toast.makeText(context, "Call state idle (missed or denied call)", Toast.LENGTH_SHORT).show();
                    Log.i("IncomingCallReceiver", "onReceive: State idle");
                    
                    if (call_rang && !call_received){
                        String status = sharedPreferences.getString("custom_status","");
                        sendMessageToCaller(callerNumber,status
                        );
                        call_received = false;
                        call_rang = false;

                    }

                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    call_received = true;
                    // Call answered (off-hook state)
                    Toast.makeText(context, "Call state off hook", Toast.LENGTH_SHORT).show();
                    Log.i("IncomingCallReceiver", "onReceive: State offhook");

                }
            }
        }
    }

    private void sendMessageToCaller(String phoneNumber,String message) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, "Call missed with status: "+ message, null, null);
    }

    private void showToastNotification() {
        String statusText = sharedPreferences.getString("custom_status", "");
        if (!statusText.equals("")) {
            Toast.makeText(context, statusText, Toast.LENGTH_SHORT).show();
            createNotification(statusText);
        }
    }

    private boolean checkAlertState() {

        return sharedPreferences.getBoolean("alert_state", false);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(String title) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define.
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}
