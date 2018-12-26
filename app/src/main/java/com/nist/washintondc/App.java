package com.nist.washintondc;

/**
 * Created by oldtrafford on 05/06/18.
 */

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.greenrobot.greendao.database.Database;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.util.JobUtil;

public class App extends Application {
    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = true;

    private DaoSession daoSession;

    private static final String DEBUG_TAG = "WashingtonDC";

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        JobManager.create(this).addJobCreator(new ExcelJobCreator());
        JobManager.instance().getAllJobRequests();
        DatabaseUpgradeHelper helper = new DatabaseUpgradeHelper(this, "washinton-dc-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        Log.v(DEBUG_TAG, "Job Manager "+ "status "+ JobManager.instance().getJobRequest(15));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("version_"+Integer.toString(BuildConfig.VERSION_CODE), false)) {

            Log.v(DEBUG_TAG, "Job scheduled: First Time "+ "version_"+Integer.toString(BuildConfig.VERSION_CODE));

            //JobManager.instance().cancelAllForTag(PrepareExcelForEmail.TAG);
            //JobManager.instance().cancelAllForTag(SendExcelEmailJob.TAG);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("version_"+Integer.toString(BuildConfig.VERSION_CODE), true);
            editor.commit();
            SharedPreferences emailPreferences = getApplicationContext().getSharedPreferences("emailPreferences", MODE_PRIVATE);

            String emailTime = emailPreferences.getString("emailTime", "00:15");

            if(!emailPreferences.contains("emailTime")){
                editor = getApplicationContext().getSharedPreferences("emailPreferences", MODE_PRIVATE).edit();
                editor.putString("emailTime", emailTime);
                editor.apply();
            }
            editor.commit();

            PrepareExcelForEmail.scheduleNextJob(getApplicationContext(), true);
        }

        if(daoSession.getPackagesDao().loadAll().size() == 0){
            // Insert Default Values
            Price price = new Price();
            price.setValues(2500L, 2700L, 3000L);
            Long priceId = daoSession.getPriceDao().insert(price);
            Packages packages = new Packages();
            packages.setValues("WDC Executive", priceId,
                    "Full,washing, 1 coat polishing, vacuuming, Paint restoration, Super Wash, Mat Washing, Glass Cleaning, Tyre polishing, Foam wash, Dashboard Polishing",
                    true,
                    "executive",
                    "detailing_package",
                    1260);
            daoSession.getPackagesDao().insert(packages);

            price = new Price();
            price.setValues(1000L, 1000L, 1500L);
            priceId = daoSession.getPriceDao().insert(price);
            packages = new Packages();
            packages.setValues("Glass Water mark removing", priceId,
                    "Full Washing, Water Mark Removing",
                    false,
                    "watermark",
                    "detailing_package",
                    1260);
            daoSession.getPackagesDao().insert(packages);

            price = new Price();
            price.setValues(1500L, 1700L, 1900L);
            priceId = daoSession.getPriceDao().insert(price);
            packages = new Packages();
            packages.setValues("Silencer Coating", priceId,
                    "Full Washing, Silencer Coating",
                    false,
                    "silencer",
                    "detailing_package",
                    1260);
            daoSession.getPackagesDao().insert(packages);

            price = new Price();
            price.setValues(1500L, 1700L, 1900L);
            priceId = daoSession.getPriceDao().insert(price);
            packages = new Packages();
            packages.setValues("Underbody Coating", priceId ,
                    "Full Washing, Underbody Coating",
                    false,
                    "underbody",
                    "detailing_package",
                    1260);
            daoSession.getPackagesDao().insert(packages);

            price = new Price();
            price.setValues(300L, 350L, 400L);
            priceId = daoSession.getPriceDao().insert(price);
            packages = new Packages();
            packages.setValues("WDC Signature Wash", priceId,
                    "Body Wash, Dashboard Cleaning, Foam wash, Tyre Polishing, Interior vacuuming, Mat washing, Door Pad Cleaning and Polish, Underbody Wash",
                    false,
                    "signature",
                    "wash_package",
                    1260);
            daoSession.getPackagesDao().insert(packages);

            price = new Price();
            price.setValues(250L, 300L, 350L);
            priceId = daoSession.getPriceDao().insert(price);
            packages = new Packages();
            packages.setValues("WDC Express Wash", priceId,
                    "Body Wash, Mat Washing, Foam Wash, Tyre Polishing",
                    false,
                    "express",
                    "wash_package",
                    1260);
            daoSession.getPackagesDao().insert(packages);

            price = new Price();
            price.setValues(600L, 700L, 800L);
            priceId = daoSession.getPriceDao().insert(price);
            packages = new Packages();
            packages.setValues("WDC Super Wash", priceId,
                    "Body Wash(Wurth Premium Shampoo, Interior Cleaning and polishing, Dashboard Cleaning and Polish, Mat Washing, Door Pad Cleaning and Polish, Underbody Wash, Body Waxing, Interior Vacuuming, Foam Wash",
                    false,
                    "superwash",
                    "wash_package",
                    1260);
            daoSession.getPackagesDao().insert(packages);

            price = new Price();
            price.setValues(0L, 0L, 0L);
            priceId = daoSession.getPriceDao().insert(price);
            packages = new Packages();
            packages.setValues("WDC Detailing", priceId,
                    "Full Washing, Interior Cleaning and Polishing, Final Washing, " +
                            "Fiber parts Cleaning and Waxing, Polishing, Alloy Wheel nd Tyre Dressing" +
                            "Glass Cleaning, Engine room Dressing, Body, Glass Clay Treatment, Full Beading Dressing, " +
                            "Two Layer Body Protection, Body Full Detailing, Cutting(Scratch and Water Mark Removing)",
                    false,
                    "superwash",
                    "detailing_package",
                    1260);
            daoSession.getPackagesDao().insert(packages);

            Log.d(DEBUG_TAG, "all package insertions done: ");
        }
        else{
        }

        if(daoSession.getEmailRecipientsDao().loadAll().size() == 0){
            EmailRecipients email = new EmailRecipients();
            email.setEmail("steve.emmanuelck@gmail.com");
            daoSession.getEmailRecipientsDao().insert(email);
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public void onTerminate() {
        Log.d(DEBUG_TAG, "onterminate");
        super.onTerminate();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancelAll();
    }
}