package com.app4you.jobscheduler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;

public class MainActivity extends AppCompatActivity {

    int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // scheduleJob();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
            if (checkPermission()) {
                scheduleJob();
            } else {
                requestPermission();
            }
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALL_LOG);
        // int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_BOOT_COMPLETED);

        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{READ_CALL_LOG, READ_CONTACTS, RECEIVE_BOOT_COMPLETED}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0) {

                    boolean callLog = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    //  boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean contacts = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean bootComp = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (callLog && contacts && bootComp) {
                        scheduleJob();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied, You cannot access Call Log data.", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_CALL_LOG)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_CALL_LOG, READ_CONTACTS, RECEIVE_BOOT_COMPLETED},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .create()
                .show();
    }


    private void scheduleJob() {
        SharedPreferences preferences = PreferenceManager.
                getDefaultSharedPreferences(this);

        if (!preferences.getBoolean("firstRunComplete", false)) {
            //schedule the job only once.
            scheduleJobFirebaseToRoomDataUpdate();

            //update shared preference
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstRunComplete", true);
            editor.commit();
        }
    }

    private void scheduleJobFirebaseToRoomDataUpdate() {
       /* JobScheduler jobScheduler = (JobScheduler) getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(this,
                MyStartServiceReceiver.class);
        Log.e("Info ", "   " + SystemClock.elapsedRealtime());
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(1000)
                *//*.setRequiresCharging(true)*//*
                .setPersisted(true).build();
        jobScheduler.schedule(jobInfo);*/

        Data data = new Data.Builder()
                .putString(MyWorker.TASK_DESC, "The task data passed from MainActivity")
                .build();

        Constraints constraints = new Constraints.Builder()
//                .setRequiresCharging(true)
                .build();


       PeriodicWorkRequest mPeriodicWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class,
                15, TimeUnit.MINUTES)
                .addTag("periodicWorkRequest")
                .setInputData(data)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(MainActivity.this).enqueue(mPeriodicWorkRequest);



    }
}