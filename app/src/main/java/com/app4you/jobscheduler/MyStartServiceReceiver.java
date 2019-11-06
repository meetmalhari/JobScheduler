package com.app4you.jobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyStartServiceReceiver extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {

        AddContactToFB addContactToFB = new AddContactToFB();
        addContactToFB.callMethod(this);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}