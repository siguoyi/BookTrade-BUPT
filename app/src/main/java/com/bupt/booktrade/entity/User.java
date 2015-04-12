package com.bupt.booktrade.entity;

import com.bupt.booktrade.utils.Constant;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by LiuYan on 2015/3/17.
 */
public class User extends BmobUser {
    public static final String TAG = "User";
    private String signature;
    private BmobFile avatar;
    private BmobRelation favorite;
    private String sex = Constant.SEX_MALE;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public BmobRelation getFavorite() {
        return favorite;
    }

    public void setFavorite(BmobRelation favorite) {
        this.favorite = favorite;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "User [sex=" + getSex() + ", avatar=" + getAvatar()
                + ", favorite=" + getFavorite() + "]";
    }
}
