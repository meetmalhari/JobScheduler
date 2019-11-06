package com.app4you.jobscheduler;

import java.io.Serializable;

public class IncomingCallPoojo implements Serializable {

public   String key,inComingNo, date, time, callDuration, callType;

    public IncomingCallPoojo(String key,String inComingNo, String date, String time, String callDuration, String callType) {
        this.key=key;
        this.inComingNo = inComingNo;
        this.date = date;
        this.time = time;
        this.callDuration = callDuration;
        this.callType = callType;


    }

    public IncomingCallPoojo() {
    }


}
