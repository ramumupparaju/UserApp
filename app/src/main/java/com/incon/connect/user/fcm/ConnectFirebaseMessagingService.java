package com.incon.connect.user.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.incon.connect.user.R;
import com.incon.connect.user.AppConstants;
import com.incon.connect.user.ui.home.HomeActivity;

import java.util.Map;

/**
 * ` * Created by radar on 30/12/16.
 */
public class ConnectFirebaseMessagingService extends FirebaseMessagingService
        implements AppConstants.PushConstants {
    private final static String TAG = "ConnectFirebase";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG, "From: " + data);
        Log.d(TAG, "Message from Server: " + remoteMessage);

        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
            Log.d(TAG, entry.getKey() + ": " + entry.getValue());
        }
        processCustomMessage(this, bundle);
    }


    /**
     * ?????????
     *
     * @param context
     * @param bundle
     */

    private void processCustomMessage(Context context, Bundle bundle) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
        String title = bundle.getString(BUNDLE_TITLE);
        String message = bundle.getString(BUNDLE_TEXT);
        String extras = bundle.getString(BUNDLE_EXTRAS);
        NotificationCompat.Builder notification = new NotificationCompat
        .Builder(context);
        notification.setAutoCancel(true)
                .setContentText(message)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher);
        /*if (!TextUtils.isEmpty(extras)) {
            try {
                JSONObject extraJson = new JSONObject(extras);
                if (null != extraJson && extraJson.length() > 0) {
                    String sound = extraJson.getString(BUNDLE_SOUND);
                    if (sound.contains(BUNDLE_SOUND_RED_ALERT)) {
                        notification.setSound(Uri.parse("android.resource://"
                        + context.getPackageName() + "/" + R.raw.red_alert));
                    } else if (sound.contains(BUNDLE_SOUND_SIREN_ALERT)) {
                        notification.setSound(Uri.parse("android.resource://"
                         + context.getPackageName() + "/" + R.raw.siren_alert));
                    } else {
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_NOTIFICATION);
                        notification.setSound(defaultSoundUri);
                    }
                    int showPopup = extraJson.getInt(BUNDLE_SHOW_POPUP);
                    if (showPopup == AppConstants.BooleanConstants.IS_TRUE) {
                        openDialog(context, bundle);
                    }
                }
            } catch (JSONException e) {
            }
        }*/
        int notifyId = 0;
        Intent handleAlarmIntent = new Intent(this, HomeActivity.class);
        handleAlarmIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent
                .getService(context, notifyId, handleAlarmIntent, 0);
        notification.setContentIntent(pendingIntent);
        notificationManager.notify(notifyId, notification.build());
    }

}
