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

        final Post entity = dataList.get(position);
        User user = entity.getAuthor();
        if (user == null) {
            LogUtils.i(TAG, "USER IS NULL");
        }
        if (user.getAvatar() == null) {
            LogUtils.i(TAG, "USER avatar IS NULL");
        }

        //加载头像
        String avatarUrl = null;
        if (user.getAvatar() != null) {
            avatarUrl = user.getAvatar().getFileUrl(context);
        }
        if (user.getSex().equals("male")) {
            ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.userAvatar,
                    MyApplication.getMyApplication().setOptions(R.drawable.avatar_default_m));
        } else {
            ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.userAvatar,
                    MyApplication.getMyApplication().setOptions(R.drawable.avatar_default_f));
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

                MyApplication.getMyApplication().setCurrentPost(entity);
                Intent personalPageIntent = new Intent(context, PersonalHomeActivity.class);
                context.startActivity(personalPageIntent);
            }
        });

        //加载用户名
        viewHolder.userName.setText(entity.getAuthor().getUsername());

        //加载帖子标题
        viewHolder.title.setText(entity.getContent());

        //加载图书图片
        if (entity.getContentfigureurl() == null) {
            viewHolder.postPics.setVisibility(View.GONE);
        } else {
            viewHolder.postPics.setVisibility(View.VISIBLE);
            ImageLoader.getInstance()
                    .displayImage(entity.getContentfigureurl().getFileUrl(context) == null ?
                                    "" : entity.getContentfigureurl().getFileUrl(context),
                            viewHolder.pic1, MyApplication.getMyApplication().setOptions(R.drawable.ic_downloading));
        }

        //加载收藏图标
        if (entity.getMyFav()) {
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite);
        } else {
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_outline);
        }
        //监听收藏
        viewHolder.favoriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFav(viewHolder.favorite, entity);
            }
        });


        //加载点赞
        viewHolder.likeCount.setText(entity.getLove() + "");
        if (entity.getMyLove()) {
            viewHolder.thumb.setImageResource(R.drawable.ic_post_my_like);
            viewHolder.likeCount.setTextColor(R.color.google_red);
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

                if (entity.getMyLove()) {
                    viewHolder.thumb.setImageResource(R.drawable.ic_post_like);
                    viewHolder.likeCount.setTextColor(R.color.button_material_dark);
                    viewHolder.likeCount.setText((entity.getLove() - 1) + "");
                    entity.setLove(entity.getLove() - 1);
                    entity.setMyLove(false);
                    entity.increment("love", -1);

                    entity.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            DatabaseUtil.getInstance(context).insertFav(entity);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            LogUtils.e(TAG, s);
                        }
                    });
                } else {
                    viewHolder.thumb.setImageResource(R.drawable.ic_post_my_like);
                    viewHolder.likeCount.setTextColor(R.color.google_red);
                    viewHolder.likeCount.setText((entity.getLove() + 1) + "");
                    entity.setLove(entity.getLove() + 1);
                    entity.setMyLove(true);
                    entity.increment("love", 1);

                    entity.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            DatabaseUtil.getInstance(context).insertFav(entity);
                            LogUtils.d(TAG, entity.getLove());
                        }

                        @Override
                        public void onFailure(int i, String s) {
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
        viewHolder.commentCount.setText(entity.getComment() + "");
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
                commentIntent.putExtra("data", entity);
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
        User user = BmobUser.getCurrentUser(context, User.class);
        if (user != null && user.getSessionToken() != null) {
            BmobRelation favRelation = new BmobRelation();

            if (!post.getMyFav()) {
                ((ImageView) v).setImageResource(R.drawable.ic_favorite);
                favRelation.add(post);
                user.setFavorite(favRelation);
                ToastUtils.showToast(context, "收藏成功", Toast.LENGTH_SHORT);
                user.update(context, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        DatabaseUtil.getInstance(context).insertFav(post);
                        LogUtils.i(TAG, "收藏成功");
                        //try get fav to see if fav success
//						getMyFavourite();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        LogUtils.i(TAG, arg1);
                        ToastUtils.showToast(context, "收藏失败:" + arg0, Toast.LENGTH_SHORT);
                    }
                });
                post.setMyFav(!post.getMyFav());
            } else {
                ((ImageView) v).setImageResource(R.drawable.ic_favorite_outline);
                favRelation.remove(post);
                user.setFavorite(favRelation);
                ToastUtils.showToast(context, "取消收藏", Toast.LENGTH_SHORT);
                user.update(context, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        DatabaseUtil.getInstance(context).deleteFav(post);
                        LogUtils.i(TAG, "取消收藏");
                        //try get fav to see if fav success
//						getMyFavourite();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        LogUtils.i(TAG, arg1);
                        ToastUtils.showToast(context, "取消收藏失败:" + arg0, Toast.LENGTH_SHORT);
                    }
                });
                post.setMyFav(!post.getMyFav());
            }
        } else {
            //前往登录注册界面
            ToastUtils.showToast(context, "收藏前请先登录", Toast.LENGTH_SHORT);
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }
}
