package com.nist.washintondc;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.nist.washintondc.InitializeDataForConsumption.generateDailyReportExcelSheet;
import static com.nist.washintondc.InitializeDataForConsumption.initializeEmailList;

public class SendExcelEmailJob  extends Job {

    static final String TAG = "send_email_job_tag";

    private ThreadHandler myHandlerThread;
    File file;
    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.v(TAG, "Email job started running.");
        List<EmailRecipients> emails = new ArrayList<>();
        List<EmailJobs> jobs = new ArrayList<>();
        final Context context = getContext().getApplicationContext();
        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        dateFormatter.setLenient(false);
        final String today = dateFormatter.format(new Date());
        final DaoSession daoSession;
        daoSession = ((App)context).getDaoSession();

        QueryBuilder queryBuilder = daoSession.getEmailJobsDao().queryBuilder()
                .where(EmailJobsDao.Properties.Completed.eq(false));;
        jobs = queryBuilder.list();

        for(final EmailJobs job : jobs){
            file = new File(job.getFile_path());
            emails = initializeEmailList(context.getApplicationContext());


            for(final EmailRecipients email : emails) {
                Log.v(TAG, context.getApplicationContext().toString());
                myHandlerThread = new ThreadHandler("TimerThread");
                final Runnable myRunnable = new Runnable() {

                    @Override
                    public void run() {
                        BackgroundMail.newBuilder(context.getApplicationContext())
                                .withUsername("washingtondccwash@gmail.com")
                                .withPassword("washingtondccarwashapp2018july&")
                                .withMailto(email.getEmail())
                                .withType(BackgroundMail.TYPE_PLAIN)
                                .withSubject("Sales Report for " + today + " sent from " + android.os.Build.MODEL)
                                .withBody("Check Attachment.")
                                .withAttachments(file.getAbsolutePath())
                                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                                    @Override
                                    public void onSuccess() {
                                        job.setCompleted(true);
                                        daoSession.getEmailJobsDao().update(job);
                                        //file.delete();
                                        myHandlerThread.interrupt();
                                        myHandlerThread.quit();
                                        return;
                                    }
                                })
                                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                                    @Override
                                    public void onFail() {
                                        myHandlerThread.interrupt();
                                        myHandlerThread.quit();
                                        return;
                                    }
                                })
                                .send();
                    }
                };
                myHandlerThread.start();
                myHandlerThread.prepareHandler();
                myHandlerThread.postTask(myRunnable);
            }
        }

        return Result.SUCCESS;
    }

    static void scheduleEmail() {
        Log.v(TAG, "Email Job Scheduled");
        new JobRequest.Builder(SendExcelEmailJob.TAG)
                .setExecutionWindow(1000L, 2000L)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .build()
                .schedule();
    }
}