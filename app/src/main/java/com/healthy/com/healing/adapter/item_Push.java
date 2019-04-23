package com.healthy.com.healing.adapter;

public class item_Push {

    private int imageId;

    private String title;

    private String browse;


    public item_Push(int imageId, String title,String browse){
        this.imageId = imageId;
        this.title = title;
        this.browse = browse;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    public String getBrowse() {
        return browse;
    }
}
