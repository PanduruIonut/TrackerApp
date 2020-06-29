package com.tracker.ui;

import java.io.Serializable;

public class ItemsModel implements Serializable {

    private String name;
    private String email;
    private int images;
    private int percent;
    private String statusDesc;

    public ItemsModel(String name, String email, int images, int percent, String statusDesc) {
        this.name = name;
        this.email = email;
        this.images = images;
        this.percent = percent;
        this.statusDesc = statusDesc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getImages() {
        return images;
    }

    public void setImages(int images) {
        this.images = images;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String desc) {
        this.statusDesc = desc;
    }
}
