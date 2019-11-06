package com.app4you.jobscheduler;

public class IncomingCallPoojo {

    String inComingNo, date, time, callDuration, callType;

    public IncomingCallPoojo(String inComingNo, String date, String time, String callDuration, String callType) {
        this.inComingNo = inComingNo;
        this.date = date;
        this.time = time;
        this.callDuration = callDuration;
        this.callType = callType;

    }

    public IncomingCallPoojo() {
    }
}
