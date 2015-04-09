package com.bupt.booktrade.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by LiuYan on 2015/4/9.
 */
public class Comment extends BmobObject {


    public static final String TAG = "Comment";

    private User user;
    private String commentContent;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
