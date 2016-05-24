package com.hz.view;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by asus on 2016/5/24.
 */
public class KuaYueLine {

    private String id;
    private String type;
    private String height;
    private String comment;
    private List<Bitmap> list;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Bitmap> getList() {
        return list;
    }

    public void setList(List<Bitmap> list) {
        this.list = list;
    }
}
