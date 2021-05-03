package com.battor.fastwxcall;

/**
 * 联系人类
 */
public class Contact {
    private String Id;  // 存数据时的 id
    private int headImgId;  // 头像的图片 id
    private int photoId;    // 照片的 id

    public Contact(String id, int headImgId, int photoId){
        this.Id = id;
        this.headImgId = headImgId;
        this.photoId = photoId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getHeadImgId() {
        return headImgId;
    }

    public void setHeadImgId(int headImgId) {
        this.headImgId = headImgId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }
}
