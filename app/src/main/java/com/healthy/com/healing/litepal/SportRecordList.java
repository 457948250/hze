package com.healthy.com.healing.litepal;

import org.litepal.crud.DataSupport;

public class SportRecordList extends DataSupport {

    private String useraccount;
    private double distance;
    private long starttime;
    private long endtime;
    private double speed;
    private String sportstyle;

    public String getUseraccount() {
        return useraccount;
    }

    public void setUseraccount(String useraccount) {
        this.useraccount = useraccount;
    }
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getSportstyle() {
        return sportstyle;
    }

    public void setSportstyle(String sportstyle) {
        this.sportstyle = sportstyle;
    }
}
