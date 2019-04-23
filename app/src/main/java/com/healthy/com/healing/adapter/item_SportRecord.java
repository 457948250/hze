package com.healthy.com.healing.adapter;


public class item_SportRecord {
    private int imageId;
    private String distance;
    private String time;
    private String speed;
    private String day;
    private long starttime;
    private long endtime;



    public item_SportRecord(String distance, String time, String speed,String day,int imageId,long starttime,long endtime) {
        this.distance = distance;
        this.time = time;
        this.speed = speed;
        this.imageId = imageId;
        this.day = day;
        this.starttime = starttime;
        this.endtime = endtime;
    }
    public String getDay() {
        return day;
    }

    public int getImageId() {
        return imageId;
    }

    public String getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }

    public String getSpeed() {
        return speed;
    }

    public long getStarttime() {
        return starttime;
    }

    public long getEndtime() {
        return endtime;
    }
}
