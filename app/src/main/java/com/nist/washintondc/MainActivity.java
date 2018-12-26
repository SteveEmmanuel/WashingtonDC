package com.nist.washintondc;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    PackageList.OnFragmentInteractionListener,
                    DailyReports.OnFragmentInteractionListener,
                    CustomerDetails.OnFragmentInteractionListener,
                    OrderList.OnFragmentInteractionListener,
                    MonthlyReports.OnFragmentInteractionListener,
                    BillDetails.OnFragmentInteractionListener,
                    ExpenseDetails.OnFragmentInteractionListener,
                    PackageDataEditor.OnFragmentInteractionListener,
                    Settings.OnFragmentInteractionListener,
                    PackageCategorySelector.OnFragmentInteractionListener,
                    OrderDetails.OnFragmentInteractionListener{

    boolean doubleBackToExitPressedOnce = false;
    String TAG = "washntonDC Permission";
    static String LCTAG = "Lifecycle";
    private static final String DEBUG_TAG = "WashingtonDC";

    private final MyActivityLifecycleCallbacks mCallbacks = new MyActivityLifecycleCallbacks();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplication().registerActivityLifecycleCallbacks(mCallbacks);

        Log.v(DEBUG_TAG, "oncreate called");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++)
                            Log.e(DEBUG_TAG, " "+getSupportFragmentManager().getBackStackEntryAt(i).getName());

                        Log.e("--------", "-----");
                    }
                });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            OrderList firstFragment = new OrderList();

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, firstFragment).commit();
        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(!bundle.getString("fragment_to_load").equals(null)) {
                String fragment_to_load = bundle.getString("fragment_to_load");

                if(fragment_to_load.equals("BillDetails")){
                    BillDetails newFragment = new BillDetails();

                    String pending_order_id = bundle.getString("pending_order_id");

                    DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
                    dateFormatter.setLenient(false);
                    Date date = new Date();
                    String time = dateFormatter.format(date);

                    bundle.putString("end_time", time);
                    newFragment.setArguments(bundle);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.fragment, newFragment, "billDetails");
                    //transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                }
            }
        }

        isWriteStoragePermissionGranted();
        isReadStoragePermissionGranted();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                //setRepeatingAlarm(getApplicationContext());
            }
        }
        else{
            //setRepeatingAlarm(getApplicationContext());
        }
    }

    @Override
    public void onBackPressed() {
        Log.v(DEBUG_TAG, "backPressed"+ Integer.toString(getSupportFragmentManager().getBackStackEntryCount()));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Checking for fragment count on backstack
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                Log.v(DEBUG_TAG, "backPressed"+ "greater than 0");
                getSupportFragmentManager().popBackStackImmediate();
            } else if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
                return;
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fm = getSupportFragmentManager(); // or 'getSupportFragmentManager();'
        int count = fm.getBackStackEntryCount();
        for(int i = 0; i < count; i++) {
            fm.popBackStack();
        }
        int id = item.getItemId();

        if (id == R.id.orderList) {
            OrderList newFragment = new OrderList();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment, newFragment, "orderList");


            // Commit the transaction
            transaction.commit();

        } else if (id == R.id.dailyReports) {
            // Create fragment and give it an argument specifying the article it should show
            DailyReports newFragment = new DailyReports();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment, newFragment, "dailyReports");


            // Commit the transaction
            transaction.commit();

        } else if (id == R.id.monthlyReports) {
            // Create fragment and give it an argument specifying the article it should show
            MonthlyReports newFragment = new MonthlyReports();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment, newFragment, "monthlyReports");


            // Commit the transaction
            transaction.commit();

        } else if (id == R.id.packageDataEdior) {
            // Create fragment and give it an argument specifying the article it should show
            PackageCategorySelector newFragment = new PackageCategorySelector();
            Bundle bundle = new Bundle();
            bundle.putString("package_fragment", "edit");
            newFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment, newFragment, "packageDataEditor");


            // Commit the transaction
            transaction.commit();

        }else if (id == R.id.settings) {

            Settings newFragment = new Settings();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment, newFragment, "settings");

            // Commit the transaction
            transaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onPackageListFragmentInteraction(Uri uri) {

    }
    @Override
    public void onDailyReportFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCustomerDetailsFragmentInteraction(Uri uri) {

    }
    @Override
    public void onOrderListFragmentInteraction(Uri uri) {

    }
    @Override
    public void onMonthlyRepotsFragmentInteraction(Uri uri){

    }

    @Override
    public void onBillDetailsFragmentInteraction(Uri uri){

    }

    @Override
    public void onExpenseDetailsFragmentInteraction(Uri uri){

    }

    @Override
    public void onPackageDataEditorFragmentInteraction(Uri uri){

    }

    @Override
    public void onSettingsFragmentInteraction(Uri uri){

    }

    @Override
    public void onPackageCategorySelectorFragmentInteraction(Uri uri){

    }

    @Override
    public void onOrderDetailFragmentInteraction(Uri uri){

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);

                }else{

                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);

                }else{

                }
                break;
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
//                setRepeatingAlarm(getApplicationContext());
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG, "ondestroy ");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i(LCTAG, "onCreate(Bundle)");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.i(LCTAG, "onStart()");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.i(LCTAG, "onResume()");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.i(LCTAG, "onPause()");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.i(LCTAG, "onSaveInstanceState(Bundle)");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.i(LCTAG, "onStop()");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(LCTAG, "onDestroy()");
            NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            Log.i(LCTAG, "Notifications Destroyed");
        }
    }

}
