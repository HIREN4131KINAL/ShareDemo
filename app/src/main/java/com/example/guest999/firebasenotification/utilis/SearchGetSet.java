package com.example.guest999.firebasenotification.utilis;

/**
 * Created by Guest999 on 11/10/2016.
 */

public class SearchGetSet {
    private String name, no;
    private String thumbnail_pic;

    public SearchGetSet() {
    }

    public SearchGetSet(String full_name, String full_no, String thumbnail) {
        this.name = full_name;
        this.no = full_no;
        this.thumbnail_pic = thumbnail;
    }

    public String getSearchName() {
        return this.name;
    }

    public void setSearchName(String name) {
        this.name = name;
    }

    public String getSearchNo() {
        return this.no;
    }

    public void setSearchNo(String no) {
        this.no = no;
    }

    public String getThumbnail() {
        return thumbnail_pic;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail_pic = thumbnail;
    }
}
