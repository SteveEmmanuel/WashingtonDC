package com.nist.washintondc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;
import static com.nist.washintondc.InitializeDataForConsumption.generateDailyReportExcelSheet;
import static com.nist.washintondc.InitializeDataForConsumption.generateMonthlyReportExcelSheet;

import java.io.File;


public class ExcelGeneratorJobIntentService extends JobIntentService {
    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 12345;
    private static final String DEBUG_TAG = "WashingtonDC";
    File file;
    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ExcelGeneratorJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(Intent intent) {
        //Toast.makeText(ExcelGeneratorJobIntentService.this, "Report will be generated in the background.", Toast.LENGTH_SHORT);
        int type = intent.getIntExtra("type", 0);
        String date = intent.getStringExtra("date");
        if(type == 0){//Daily Reports
            file = generateDailyReportExcelSheet(getApplicationContext(), date);
        }
        else if(type == 1){//Monthly Reports
            file = generateMonthlyReportExcelSheet(getApplicationContext(), date);
        }
        Log.i(DEBUG_TAG, "Completed excel generation @ " + SystemClock.elapsedRealtime());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkURI = FileProvider.getUriForFile(
                    this,
                    this.getApplicationContext()
                            .getPackageName() + ".provider", file);
            intent.setDataAndType(apkURI, "application/vnd.ms-excel");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
        }

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Report generated.")
                .setContentText("Click to open report.")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent)
                .setTicker("WashingtonDC");

        notificationManager.notify(/*notification id*/1, notificationBuilder.build());



    }

    final Handler mHandler = new Handler();

    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(ExcelGeneratorJobIntentService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}