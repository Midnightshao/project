package com.project.bean;

import java.io.Serializable;

/**
 * Created by guanghaoshao on 16/6/1.
 */
public class project implements Serializable{
    private String name;
    private String content;
    private String title;
    private String image_type;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "project{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", image_type='" + image_type + '\'' +
                ", id=" + id +
                '}';
    }
}
