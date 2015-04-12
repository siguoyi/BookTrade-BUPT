package com.bupt.booktrade.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.booktrade.MyApplication;
import com.bupt.booktrade.R;
import com.bupt.booktrade.adapter.CommentAdapter;
import com.bupt.booktrade.db.DatabaseUtil;
import com.bupt.booktrade.entity.Comment;
import com.bupt.booktrade.entity.Post;
import com.bupt.booktrade.entity.User;
import com.bupt.booktrade.utils.Constant;
import com.bupt.booktrade.utils.LogUtils;
import com.bupt.booktrade.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class CommentActivity extends BaseActivity implements View.OnClickListener {
    private String TAG;
    private ListView commentList;
    private TextView loadMore;
    private EditText commentContent;
    private Button commentCommit;

    private ImageView userAvatar;
    private TextView userName;
    private TextView timeStamp;
    private ImageView favorite;
    private TextView postTitle;
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

    private Post post;
    private String commentEdit = "";

    private CommentAdapter mAdapter;

    private List<Comment> comments = new ArrayList<Comment>();

    private int pageNum;
    private boolean isFav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        setContentView(R.layout.activity_comment);
        getActionBar().setTitle(R.string.title_activity_comment);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        setListeners();
        setupViews(savedInstanceState);
    }

    @Override
    protected void onStop() {
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        super.onStop();
    }
    private void findViews() {
        commentList = (ListView) findViewById(R.id.comment_list);
        loadMore = (TextView) findViewById(R.id.load_more);

        commentContent = (EditText) findViewById(R.id.comment_content);
        commentCommit = (Button) findViewById(R.id.comment_commit);

        //post entity
        userAvatar = (ImageView) findViewById(R.id.user_avatar_post);
        userName = (TextView) findViewById(R.id.user_name_post);
        timeStamp = (TextView) findViewById(R.id.timestamp_post);
        favorite = (ImageView) findViewById(R.id.favorite_post);
        favoriteLayout = (RelativeLayout) findViewById(R.id.favorite_post_layout);
        postTitle = (TextView) findViewById(R.id.post_title);
        postPics = (RelativeLayout) findViewById(R.id.pics_post);
        pic1 = (ImageView) findViewById(R.id.pic1);
        pic2 = (ImageView) findViewById(R.id.pic2);
        pic3 = (ImageView) findViewById(R.id.pic3);
        pic4 = (ImageView) findViewById(R.id.pic4);
        shareCount = (TextView) findViewById(R.id.count_post_share);
        commentCount = (TextView) findViewById(R.id.count_post_comment);
        thumb = (ImageView) findViewById(R.id.post_thumb);
        likeCount = (TextView) findViewById(R.id.count_post_like);
        likePost = (LinearLayout) findViewById(R.id.post_like);
        sharePost = (LinearLayout) findViewById(R.id.post_share);
        commentPost = (LinearLayout) findViewById(R.id.post_comment);
    }

    protected void setListeners() {
        // TODO Auto-generated method stub
        loadMore.setOnClickListener(this);
        commentCommit.setOnClickListener(this);
        userAvatar.setOnClickListener(this);
        favorite.setOnClickListener(this);
        likePost.setOnClickListener(this);
        sharePost.setOnClickListener(this);
        commentPost.setOnClickListener(this);
    }


    private void setupViews(Bundle bundle) {
        post = (Post) getIntent().getSerializableExtra("data");//MyApplication.getInstance().getCurrentPost();
        pageNum = 0;

        mAdapter = new CommentAdapter(CommentActivity.this, comments);
        fetchComment();
        commentList.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(commentList);
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                ToastUtils.showToast(CommentActivity.this, "po" + position, Toast.LENGTH_SHORT);
            }
        });

        commentList.setCacheColorHint(0);
        commentList.setScrollingCacheEnabled(false);
        commentList.setScrollContainer(false);
        commentList.setFastScrollEnabled(true);
        commentList.setSmoothScrollbarEnabled(true);

        initMoodView(post);

    }

    private void initMoodView(Post mood2) {
        // TODO Auto-generated method stub
        if (mood2 == null) {
            return;
        }
        //头像
        User user = post.getAuthor();
        BmobFile avatar = user.getAvatar();
        if (null != avatar) {
            int defaultAvatar = user.getSex().equals(Constant.SEX_MALE) ? R.drawable.avatar_default_m : R.drawable.avatar_default_f;
            ImageLoader.getInstance()
                    .displayImage(avatar.getFileUrl(this), userAvatar,
                            MyApplication.getMyApplication().setOptions(defaultAvatar),
                            new SimpleImageLoadingListener() {

                                @Override
                                public void onLoadingComplete(String imageUri, View view,
                                                              Bitmap loadedImage) {
                                    // TODO Auto-generated method stub
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    LogUtils.i(TAG, "load personal icon completed.");
                                }
                            });
        }
        //用户名
        userName.setText(post.getAuthor().getUsername());
        //标题
        postTitle.setText(post.getContent());
        //时间戳
        timeStamp.setText(post.getCreatedAt());
        //图片
        if (null == post.getContentfigureurl()) {
            postPics.setVisibility(View.GONE);
        } else {
            postPics.setVisibility(View.VISIBLE);

            ImageLoader.getInstance()
                    .displayImage(post.getContentfigureurl().getFileUrl(this) == null ? "" : post.getContentfigureurl().getFileUrl(this), pic1,
                            MyApplication.getMyApplication().setOptions(R.drawable.ic_downloading),
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view,
                                                              Bitmap loadedImage) {
                                    // TODO Auto-generated method stub
                                    super.onLoadingComplete(imageUri, view, loadedImage);
//                                    float[] cons = getBitmapConfiguration(loadedImage, pic1, 1.0f);
//                                    RelativeLayout.LayoutParams layoutParams =
//                                            new RelativeLayout.LayoutParams((int) cons[0], (int) cons[1]);
//                                    layoutParams.addRule(RelativeLayout.BELOW, R.id.post_title);
//                                    pic1.setLayoutParams(layoutParams);
                                }

                            });
        }

        //点赞
        likeCount.setText(post.getLove() + "");
        if (post.getMyLove()) {
            thumb.setImageResource(R.drawable.ic_post_my_like);
            likeCount.setTextColor(R.color.google_red);
        } else {
            thumb.setImageResource(R.drawable.ic_post_like);
            likeCount.setTextColor(R.color.button_material_dark);
        }

        //收藏图标
        if (post.getMyFav()) {
            favorite.setImageResource(R.drawable.ic_favorite);
        } else {
            favorite.setImageResource(R.drawable.ic_favorite_outline);
        }

        //加载评论数量
        commentCount.setText(post.getComment() + "");

    }


    @Override
    public void onClick(View v) {

        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.user_avatar_post:
                onClickUserLogo();
                break;
            case R.id.load_more:
                onClickLoadMore();
                break;
            case R.id.comment_commit:
                onClickCommit();
                break;
            case R.id.favorite_post_layout:
                onClickFav(v);
                break;
            case R.id.post_like:
                onClickLove();
                break;
            case R.id.post_share:
                onClickShare();
                break;
            case R.id.post_comment:
                onClickComment();
                break;
            default:
                break;
        }

    }

    private void onClickUserLogo() {
        // TODO Auto-generated method stub
        //跳转到个人信息界面
        User currentUser = BmobUser.getCurrentUser(this, User.class);
        if (currentUser != null) {//已登录
            Intent intent = new Intent();
            intent.setClass(MyApplication.getMyApplication().getTopActivity(), PersonalHomeActivity.class);
            mContext.startActivity(intent);
        } else {//未登录
            ToastUtils.showToast(CommentActivity.this, "请先登录", Toast.LENGTH_SHORT);
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivityForResult(intent, Constant.GO_SETTINGS);
        }
    }

    private void onClickLoadMore() {
        // TODO Auto-generated method stub
        fetchComment();
    }

    private void onClickCommit() {
        // TODO Auto-generated method stub
        User currentUser = BmobUser.getCurrentUser(this, User.class);
        if (currentUser != null) {//已登录
            commentEdit = commentContent.getText().toString().trim();
            if (TextUtils.isEmpty(commentEdit)) {
                ToastUtils.showToast(CommentActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT);
                return;
            }
            //comment now
            publishComment(currentUser, commentEdit);
        } else {//未登录
            ToastUtils.showToast(CommentActivity.this, "发表评论前请先登录", Toast.LENGTH_SHORT);
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivityForResult(intent, Constant.PUBLISH_COMMENT);
        }

    }

    private void publishComment(User user, String content) {

        final Comment comment = new Comment();
        comment.setUser(user);
        comment.setCommentContent(content);
        comment.save(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                ToastUtils.showToast(CommentActivity.this, "评论成功", Toast.LENGTH_SHORT);
                if (mAdapter.getDataList().size() < Constant.NUMBERS_PER_PAGE) {
                    mAdapter.getDataList().add(comment);
                    mAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
                }
                commentContent.setText("");
                hideSoftInput();

                //将该评论与强语绑定到一起
                BmobRelation relation = new BmobRelation();
                relation.add(comment);
                post.setRelation(relation);
                post.update(mContext, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        LogUtils.i(TAG, "更新评论成功");
//						fetchData();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        LogUtils.i(TAG, "更新评论失败" + arg1);
                    }
                });

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ToastUtils.showToast(CommentActivity.this, "评论失败。请检查网络", Toast.LENGTH_SHORT);
            }
        });
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);
    }


    private void onClickFav(View v) {
        // TODO Auto-generated method stub
        final User user = BmobUser.getCurrentUser(this, User.class);
        if (user != null && user.getSessionToken() != null) {
            final BmobRelation favRelation = new BmobRelation();
            if (!post.getMyFav()) {
                ((ImageView) v).setImageResource(R.drawable.ic_favorite);
                post.setMyFav(true);
                favRelation.add(post);
                user.setFavorite(favRelation);
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ToastUtils.showToast(CommentActivity.this, "收藏成功", Toast.LENGTH_SHORT);
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
                        ToastUtils.showToast(CommentActivity.this, "收藏失败:" + arg0, Toast.LENGTH_SHORT);
                    }
                });

                post.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        DatabaseUtil.getInstance(CommentActivity.this).insertFav(post);
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
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ToastUtils.showToast(CommentActivity.this, "取消收藏成功", Toast.LENGTH_SHORT);
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
                        ToastUtils.showToast(CommentActivity.this, "取消收藏失败:" + arg0, Toast.LENGTH_SHORT);
                    }
                });

                post.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        DatabaseUtil.getInstance(CommentActivity.this).deleteFav(post);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        post.setMyFav(true);
                    }
                });
            }
        } else {
            //前往登录注册界面
            ToastUtils.showToast(CommentActivity.this, "收藏前请先登录", Toast.LENGTH_SHORT);
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        }
    }


    private void onClickLove() {
        if (MyApplication.getMyApplication().getCurrentUser() == null) {
            ToastUtils.showToast(this, "请先登录", Toast.LENGTH_SHORT);
            Intent loginIntent = new Intent(this, LoginActivity.class);
            this.startActivity(loginIntent);
            return;
        }

        if (post.getMyLove()) {
            thumb.setImageResource(R.drawable.ic_post_like);
            likeCount.setTextColor(R.color.button_material_dark);
            if (post.getLove() > 0) {
                likeCount.setText((post.getLove() - 1) + "");
            }
            if (post.getLove() > 0) {
                post.setLove(post.getLove() - 1);
            }
            post.increment("love", -1);
            post.setMyLove(false);
            post.update(this, new UpdateListener() {
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
            thumb.setImageResource(R.drawable.ic_post_my_like);
            likeCount.setTextColor(R.color.google_red);
            likeCount.setText((post.getLove() + 1) + "");
            post.setMyLove(true);
            post.setLove(post.getLove() + 1);
            post.increment("love", 1);
            post.update(this, new UpdateListener() {
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


    private void fetchComment() {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereRelatedTo("relation", new BmobPointer(post));
        query.include("user");
        query.order("createdAt");
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
        query.findObjects(this, new FindListener<Comment>() {

            @Override
            public void onSuccess(List<Comment> data) {
                // TODO Auto-generated method stub
                LogUtils.i(TAG, "get comment success!" + data.size());
                if (data.size() != 0 && data.get(data.size() - 1) != null) {
                    if (data.size() < Constant.NUMBERS_PER_PAGE) {
                        ToastUtils.showToast(mContext, "已加载完所有评论", Toast.LENGTH_SHORT);
                        loadMore.setText("暂无更多评论");
                    }
                    mAdapter.getDataList().clear();
                    mAdapter.getDataList().addAll(data);
                    mAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(commentList);
                    LogUtils.i(TAG, "refresh");
                } else {
                    ToastUtils.showToast(mContext, "暂无更多评论", Toast.LENGTH_SHORT);
                    loadMore.setText("暂无更多评论");
                    pageNum--;
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ToastUtils.showToast(CommentActivity.this, "获取评论失败,请检查网络", Toast.LENGTH_SHORT);
                pageNum--;
            }
        });
    }

    private void onClickShare() {
        ToastUtils.showToast(CommentActivity.this, "share", Toast.LENGTH_SHORT);
    }

    private void onClickComment() {
        commentContent.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentContent, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 动态设置listview的高度
     * item 总布局必须是linearLayout
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
                + 15;
        listView.setLayoutParams(params);
    }


    public static int[] getScreenSize() {
        int[] screens;
        // if (Constants.screenWidth > 0) {
        // return screens;
        // }
        DisplayMetrics dm = MyApplication.getMyApplication().getResources().getDisplayMetrics();
        screens = new int[]{dm.widthPixels, dm.heightPixels};
        return screens;
    }

    public static float[] getBitmapConfiguration(Bitmap bitmap, ImageView imageView, float screenRadio) {
        int screenWidth = getScreenSize()[0];
        float rawWidth = 0;
        float rawHeight = 0;
        float width = 0;
        float height = 0;
        if (bitmap == null) {
            // rawWidth = sourceWidth;
            // rawHeight = sourceHeigth;
            width = (float) (screenWidth / screenRadio);
            height = (float) width;
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            rawWidth = bitmap.getWidth();
            rawHeight = bitmap.getHeight();
            if (rawHeight > 10 * rawWidth) {
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            } else {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            float radio = rawHeight / rawWidth;

            width = (float) (screenWidth / screenRadio);
            height = (float) (radio * width);
        }
        return new float[]{width, height};
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.PUBLISH_COMMENT:
                    //登录完成
                    commentCommit.performClick();
                    break;
                case Constant.SAVE_FAVOURITE:
                    favorite.performClick();
                    break;
                case Constant.GET_FAVOURITE:

                    break;
                case Constant.GO_SETTINGS:
                    userAvatar.performClick();
                    break;
                default:
                    break;
            }
        }
    }
}
