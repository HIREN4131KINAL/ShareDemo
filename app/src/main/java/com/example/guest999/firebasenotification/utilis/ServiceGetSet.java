package com.example.guest999.firebasenotification.utilis;

/**
 * Created by Joshi on 9/14/2016.
 */
public class ServiceGetSet {
    private String name;
    private int thumbnail;

    public ServiceGetSet() {
    }

    public ServiceGetSet(String name, int thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
