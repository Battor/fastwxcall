package com.battor.fastwxcall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * 联系人 类
 */
public class Contact {

    @Expose
    @SerializedName("Id")
    private String id;

    @Expose
    @SerializedName("WXName")
    private String name;

    @Expose
    @SerializedName("HeadImgId")
    private int headImgId;

    private String photoImgPath;

    @Expose
    @SerializedName("CreateTime")
    private Date createTime;

    private Date updateTime;

    public Contact(){

    }

    public Contact(String id, String name, int headImgId, String photoImgPath,
                   Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.headImgId = headImgId;
        this.photoImgPath = photoImgPath;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeadImgId() {
        return headImgId;
    }

    public void setHeadImgId(int headImgId) {
        this.headImgId = headImgId;
    }

    public String getPhotoImgPath() {
        return photoImgPath;
    }

    public void setPhotoImgPath(String photoImgPath) {
        this.photoImgPath = photoImgPath;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTimestamp(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTimestamp(Date updateTime) {
        this.updateTime = updateTime;
    }
}
