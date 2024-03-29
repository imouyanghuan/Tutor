package com.tutor.model;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "AVATAR".
 */
public class Avatar {

    /** Not-null value. */
    private String id;
    private String avatar;
    private String userName;
    private String nickName;

    public Avatar() {
    }

    public Avatar(String id) {
        this.id = id;
    }

    public Avatar(String id, String avatar, String userName, String nickName) {
        this.id = id;
        this.avatar = avatar;
        this.userName = userName;
        this.nickName = nickName;
    }

    /** Not-null value. */
    public String getId() {
        return id;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}
