package com.healthy.com.healing.adapter;

public class item_Push_Activity {

    private int imageId;

    private String title;

    private String join;

    public item_Push_Activity(int imageId,String title,String join){
        this.imageId = imageId;
        this.title = title;
        this.join = join;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    public String getJoin() {
        return join;
    }
}
