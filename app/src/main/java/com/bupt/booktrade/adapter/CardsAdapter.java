package com.bupt.booktrade.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.booktrade.MyApplication;
import com.bupt.booktrade.R;
import com.bupt.booktrade.activity.CommentActivity;
import com.bupt.booktrade.activity.LoginActivity;
import com.bupt.booktrade.activity.PersonalHomeActivity;
import com.bupt.booktrade.db.DatabaseUtil;
import com.bupt.booktrade.entity.Post;
import com.bupt.booktrade.entity.User;
import com.bupt.booktrade.utils.Constant;
import com.bupt.booktrade.utils.LogUtils;
import com.bupt.booktrade.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

public class CardsAdapter extends BaseAdapter {

    private final String TAG = "CardsAdapter";
    private final OnItemClickListener itemClickListener;
    private final Context context;
    private List<Post> dataList;

    public CardsAdapter(Context context, List<Post> dataList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Post getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_card, null);
            viewHolder = new ViewHolder();
            viewHolder.userAvatar = (ImageView) convertView.findViewById(R.id.user_avatar_post);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.user_name_post);
            viewHolder.timeStamp = (TextView) convertView.findViewById(R.id.timestamp_post);
            viewHolder.favorite = (ImageView) convertView.findViewById(R.id.favorite_post);
            viewHolder.favoriteLayout = (RelativeLayout) convertView.findViewById(R.id.favorite_post_layout);
            viewHolder.title = (TextView) convertView.findViewById(R.id.post_title);
            viewHolder.postPics = (RelativeLayout) convertView.findViewById(R.id.pics_post);
            viewHolder.pic1 = (ImageView) convertView.findViewById(R.id.pic1);
            viewHolder.pic2 = (ImageView) convertView.findViewById(R.id.pic2);
            viewHolder.pic3 = (ImageView) convertView.findViewById(R.id.pic3);
            viewHolder.pic4 = (ImageView) convertView.findViewById(R.id.pic4);
            viewHolder.shareCount = (TextView) convertView.findViewById(R.id.count_post_share);
            viewHolder.commentCount = (TextView) convertView.findViewById(R.id.count_post_comment);
            viewHolder.thumb = (ImageView) convertView.findViewById(R.id.post_thumb);
            viewHolder.likeCount = (TextView) convertView.findViewById(R.id.count_post_like);
            viewHolder.likePost = (LinearLayout) convertView.findViewById(R.id.post_like);
            viewHolder.sharePost = (LinearLayout) convertView.findViewById(R.id.post_share);
            viewHolder.commentPost = (LinearLayout) convertView.findViewById(R.id.post_comment);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Post post = dataList.get(position);
        User user = post.getAuthor();

        if (user == null) {
            LogUtils.i(TAG, "USER IS NULL");
        }
        if (user.getAvatar() == null) {
            LogUtils.i(TAG, "USER avatar IS NULL");
        }

        //加载头像
        if (user.getAvatar() != null) {
            String avatarUrl = user.getAvatar().getFileUrl(context);
            int defaultAvatar = user.getSex().equals(Constant.SEX_MALE) ? R.drawable.avatar_default_m : R.drawable.avatar_default_f;
            ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.userAvatar,
                    MyApplication.getMyApplication().setOptions(defaultAvatar));
        }

        //监听点击头像，跳转到个人主页
        viewHolder.userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getMyApplication().getCurrentUser() == null) {
                    ToastUtils.showToast(context, "请先登录", Toast.LENGTH_SHORT);
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    return;
                }

                MyApplication.getMyApplication().setCurrentPost(post);
                Intent personalPageIntent = new Intent(context, PersonalHomeActivity.class);
                context.startActivity(personalPageIntent);
            }
        });

        //加载用户名
        viewHolder.userName.setText(user.getUsername());
        //加载时间戳
        viewHolder.timeStamp.setText(post.getCreatedAt());
        //加载帖子标题
        viewHolder.title.setText(post.getContent());

        //加载图书图片
        if (post.getContentfigureurl() == null) {
            viewHolder.postPics.setVisibility(View.GONE);
        } else {
            viewHolder.postPics.setVisibility(View.VISIBLE);
            ImageLoader.getInstance()
                    .displayImage(post.getContentfigureurl().getFileUrl(context) == null ?
                                    "" : post.getContentfigureurl().getFileUrl(context),
                            viewHolder.pic1, MyApplication.getMyApplication().setOptions(R.drawable.ic_downloading));
        }

        //加载收藏图标
        if (post.getMyFav()) {
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite);
        } else {
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_outline);
        }
        //监听收藏
        viewHolder.favoriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFav(viewHolder.favorite, post);
            }
        });


        //加载点赞
        viewHolder.likeCount.setText(post.getLove() + "");
        if (post.getMyLove()) {
            viewHolder.thumb.setImageResource(R.drawable.ic_post_my_like);
            viewHolder.likeCount.setTextColor(R.color.google_red);
        } else {
            viewHolder.thumb.setImageResource(R.drawable.ic_post_like);
            viewHolder.likeCount.setTextColor(R.color.button_material_dark);
        }

        //监听点赞
        viewHolder.likePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getMyApplication().getCurrentUser() == null) {
                    ToastUtils.showToast(context, "请先登录", Toast.LENGTH_SHORT);
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    return;
                }

                if (post.getMyLove()) {
                    viewHolder.thumb.setImageResource(R.drawable.ic_post_like);
                    viewHolder.likeCount.setTextColor(R.color.button_material_dark);
                    if (post.getLove() > 0) {
                        viewHolder.likeCount.setText((post.getLove() - 1) + "");
                    }
                    if (post.getLove() > 0) {
                        post.setLove(post.getLove() - 1);
                    }
                    post.increment("love", -1);
                    post.setMyLove(false);
                    post.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            //DatabaseUtil.getInstance(context).insertFav(post);
                            LogUtils.i(TAG, post.getLove());
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            post.setMyLove(true);
                            post.setLove(post.getLove() + 1);
                            LogUtils.e(TAG, s);
                        }
                    });
                } else {
                    viewHolder.thumb.setImageResource(R.drawable.ic_post_my_like);
                    viewHolder.likeCount.setTextColor(R.color.google_red);
                    viewHolder.likeCount.setText((post.getLove() + 1) + "");
                    post.setMyLove(true);
                    post.setLove(post.getLove() + 1);
                    post.increment("love", 1);
                    post.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            //DatabaseUtil.getInstance(context).insertFav(post);
                            LogUtils.d(TAG, post.getLove());
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            if (post.getLove() > 0) {
                                post.setMyLove(false);
                                post.setLove(post.getLove() - 1);
                            }
                            LogUtils.e(TAG, s);
                        }
                    });
                }
            }
        });


        //监听分享
        viewHolder.sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(context, "等待分享", Toast.LENGTH_SHORT);
            }
        });

        //加载评论数量
        viewHolder.commentCount.setText(post.getComment() + "");
        //监听点击评论
        viewHolder.commentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getMyApplication().getCurrentUser() == null) {
                    ToastUtils.showToast(context, "请先登录", Toast.LENGTH_SHORT);
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    return;
                }
                Intent commentIntent = new Intent(context, CommentActivity.class);
                commentIntent.putExtra("data", post);
                context.startActivity(commentIntent);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        private ImageView userAvatar;
        private TextView userName;
        private TextView timeStamp;
        private ImageView favorite;
        private TextView title;
        private ImageView pic1;
        private ImageView pic2;
        private ImageView pic3;
        private ImageView pic4;
        private TextView shareCount;
        private TextView commentCount;
        private ImageView thumb;
        private TextView likeCount;
        private RelativeLayout postPics;
        private LinearLayout likePost;
        private LinearLayout sharePost;
        private LinearLayout commentPost;
        private RelativeLayout favoriteLayout;
    }


    //点击收藏
    private void onClickFav(View v, final Post post) {
        // TODO Auto-generated method stub
        final User user = BmobUser.getCurrentUser(context, User.class);
        if (user != null && user.getSessionToken() != null) {
            final BmobRelation favRelation = new BmobRelation();
            if (!post.getMyFav()) {
                ((ImageView) v).setImageResource(R.drawable.ic_favorite);
                post.setMyFav(true);
                favRelation.add(post);
                user.setFavorite(favRelation);
                user.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ToastUtils.showToast(context, "收藏成功", Toast.LENGTH_SHORT);
                        LogUtils.i(TAG, "收藏成功");
                        //try get fav to see if fav success
//						getMyFavourite();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        post.setMyFav(false);
                        favRelation.remove(post);
                        user.setFavorite(favRelation);
                        LogUtils.i(TAG, arg1);
                        ToastUtils.showToast(context, "收藏失败:" + arg0, Toast.LENGTH_SHORT);
                    }
                });

                post.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        DatabaseUtil.getInstance(context).insertFav(post);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        post.setMyFav(false);
                    }
                });
            } else {
                ((ImageView) v).setImageResource(R.drawable.ic_favorite_outline);
                post.setMyFav(false);
                favRelation.remove(post);
                user.setFavorite(favRelation);
                user.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ToastUtils.showToast(context, "取消收藏成功", Toast.LENGTH_SHORT);
                        LogUtils.i(TAG, "取消收藏");
                        //try get fav to see if fav success
//						getMyFavourite();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        post.setMyFav(true);
                        favRelation.add(post);
                        user.setFavorite(favRelation);
                        LogUtils.i(TAG, arg1);
                        ToastUtils.showToast(context, "取消收藏失败:" + arg0, Toast.LENGTH_SHORT);
                    }
                });

                post.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        DatabaseUtil.getInstance(context).deleteFav(post);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        post.setMyFav(true);
                    }
                });
            }
        } else {
            //前往登录注册界面
            ToastUtils.showToast(context, "收藏前请先登录", Toast.LENGTH_SHORT);
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }
}
