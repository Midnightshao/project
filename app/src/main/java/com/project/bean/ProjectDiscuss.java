package com.project.bean;

import java.io.Serializable;

/**
 * Created by guanghaoshao on 16/6/13.
 */
public class ProjectDiscuss implements Serializable{
    private int rid_id;
    private String content;
    private String name;
    private String image_type;
    private String time;

    public int getRid_id() {
        return rid_id;
    }

    public void setRid_id(int rid_id) {
        this.rid_id = rid_id;
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

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ProjectDiscuss{" +
                "rid_id=" + rid_id +
                ", content='" + content + '\'' +
                ", name='" + name + '\'' +
                ", image_type='" + image_type + '\'' +
                '}';
    }
}
