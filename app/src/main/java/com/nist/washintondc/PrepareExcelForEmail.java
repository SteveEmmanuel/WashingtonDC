package com.nist.washintondc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.annotation.NonNull;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.nist.washintondc.InitializeDataForConsumption.generateDailyReportExcelSheet;
import static com.nist.washintondc.InitializeDataForConsumption.initializeEmailList;

public class PrepareExcelForEmail  extends Job {

    static final String TAG = "prepare_excel_job_tag";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.v(TAG, "Prepare Email job started running.");
        File file;
        List<EmailRecipients> emails = new ArrayList<>();
        DaoSession daoSession;
        Context context = getContext().getApplicationContext();

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        String today = dateFormatter.format(new Date());

        file = generateDailyReportExcelSheet(context, today);

        Looper.prepare();

        daoSession = ((App)context).getDaoSession();

        EmailJobs emailJob = new EmailJobs();
        emailJob.setCompleted(false);
        emailJob.setFile_path(file.toString());
        daoSession.getEmailJobsDao().insert(emailJob);

        PrepareExcelForEmail.scheduleNextJob(context, false);
        SendExcelEmailJob.scheduleEmail();
        return Result.SUCCESS;
    }

    static void scheduleNextJob(Context context, Boolean update) {
        Log.v(TAG, "Prepare Email Job Scheduled");
        SharedPreferences emailPreferences = context.getSharedPreferences("emailPreferences", MODE_PRIVATE);

        String emailTime = emailPreferences.getString("emailTime", "13:24");

        String[] separated = emailTime.split(":");
        Calendar c = Calendar.getInstance();

        DaoSession daoSession;
        daoSession = ((App)context).getDaoSession();
        QueryBuilder queryBuilder = daoSession.getEmailJobsDao().queryBuilder();;


        if(queryBuilder.list().size() == 0){
            //first start
        }

        if(update == false) {
            c.add(Calendar.DATE, 1);
        }else if(c.get(Calendar.HOUR_OF_DAY) == Integer.parseInt(separated[0]) &&
                c.get(Calendar.MINUTE) == Integer.parseInt(separated[1])) {

            c.add(Calendar.SECOND, 5);
        }
        else{
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(separated[0]));
            c.set(Calendar.MINUTE, Integer.parseInt(separated[1]));
            c.set(Calendar.SECOND, 0);
        }

        Log.v("timerr",c.getTime().toString());
        long timeInMillis = (c.getTimeInMillis()-System.currentTimeMillis());
        Log.v("timerr"," "+c.getTimeInMillis());
        Log.v("timerr"," "+System.currentTimeMillis());

        if(timeInMillis < 0){
            timeInMillis = 86400000L + timeInMillis;
        }
        new JobRequest.Builder(PrepareExcelForEmail.TAG)
                .setExact(timeInMillis)
                .setUpdateCurrent(false)
                .build()
                .schedule();
    }
}