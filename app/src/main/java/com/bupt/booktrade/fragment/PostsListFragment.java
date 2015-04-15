package com.bupt.booktrade.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.booktrade.MyApplication;
import com.bupt.booktrade.R;
import com.bupt.booktrade.activity.CommentActivity;
import com.bupt.booktrade.adapter.CardsAdapter;
import com.bupt.booktrade.entity.Post;
import com.bupt.booktrade.entity.User;
import com.bupt.booktrade.utils.Constant;
import com.bupt.booktrade.utils.LogUtils;
import com.bupt.booktrade.utils.ToastUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import zrc.widget.SimpleFooter;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;

public class PostsListFragment extends BaseFragment {

    private String TAG;
    private ZrcListView postsList;
    private TextView postsLoading;
    public boolean fetchResult = false;
    private String lastItemTime;
    private Handler handler;
    private ArrayList<Post> mListItems;
    private CardsAdapter mAdapter;
    private BmobDate lastQueryTime, currentTime;
    private View rootView;

    public enum RefreshType {
        REFRESH, LOAD_MORE
    }

    public RefreshType mRefreshType = RefreshType.LOAD_MORE;

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(new Date(System.currentTimeMillis()));
        return time;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        LogUtils.i(TAG, "onAttach");
        super.onAttach(activity);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        LogUtils.i(TAG, "onCreate");
        setRetainInstance(true);
        mListItems = new ArrayList<>();
        lastQueryTime = new BmobDate(new Date(0));
        lastItemTime = getCurrentTime();
        LogUtils.i(TAG, "current time:" + lastItemTime);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i(TAG, "onCreateView");
        handler = new Handler();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);
            LogUtils.i(TAG, "onCreateView:" + "new");
            postsLoading = (TextView) rootView.findViewById(R.id.posts_loading);

            postsList = (ZrcListView) rootView.findViewById(R.id.cards_list);
            // 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
            SimpleHeader header = new SimpleHeader(mContext);
            header.setTextColor(0xff0066aa);
            header.setCircleColor(0xff33bbee);
            postsList.setHeadable(header);

            // 设置加载更多的样式（可选）
            SimpleFooter footer = new SimpleFooter(mContext);
            footer.setCircleColor(0xff33bbee);
            postsList.setFootable(footer);
            //postsList.startLoadMore(); // 开启LoadingMore功能

//            // 设置列表项出现动画（可选）
//            postsList.setItemAnimForTopIn(R.anim.topitem_in);
//            postsList.setItemAnimForBottomIn(R.anim.bottomitem_in);

            // 下拉刷新事件回调（可选）
            postsList.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
                @Override
                public void onStart() {
                    mRefreshType = RefreshType.REFRESH;
                    fetchData(mRefreshType);
                }
            });

            // 加载更多事件回调（可选）
            postsList.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
                @Override
                public void onStart() {
                    LogUtils.i(TAG, "load more");
                    mRefreshType = RefreshType.LOAD_MORE;
                    fetchData(mRefreshType);
                }
            });

            setupList();
            LogUtils.i(TAG, "mListItems.size():" + mListItems.size());
            if (isLogin() && mListItems.size() == 0) {
                postsList.refresh(); // 自动下拉刷新
            } else if (!isLogin()) {
                postsLoading.setVisibility(View.GONE);
                ToastUtils.showToast(mContext, "请先登录", Toast.LENGTH_SHORT);
            }
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    private void setupList() {
        mAdapter = new CardsAdapter(mContext, mListItems);
        postsList.setOnItemClickListener(new ZrcListView.OnItemClickListener() {
            @Override
            public void onItemClick(ZrcListView parent, View view, int position, long id) {
                LogUtils.i(TAG, position);
                //ToastUtils.showToast(mContext, position + "", Toast.LENGTH_SHORT);
                Intent intent = new Intent();
                intent.setClass(getActivity(), CommentActivity.class);
                intent.putExtra("data", mListItems.get(position));
                startActivity(intent);
            }
        });
        postsList.setAdapter(mAdapter);

    }


    public boolean fetchData(RefreshType mRefreshType) {
        BmobQuery<Post> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.include("author");
        //获取15条数据
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        switch (mRefreshType) {
            case REFRESH:
                postsList.stopLoadMore();
                currentTime = new BmobDate(new Date(System.currentTimeMillis()));
                query.addWhereLessThanOrEqualTo("createdAt", currentTime);
                //query.addWhereGreaterThanOrEqualTo("createdAt", lastQueryTime);
                LogUtils.i(TAG, "query time:" + lastQueryTime.getDate() + "-" + currentTime.getDate());
                //lastQueryTime = currentTime;

                query.findObjects(mContext, new FindListener<Post>() {
                    @Override
                    public void onSuccess(List<Post> list) {
                        // TODO Auto-generated method stub
                        //LogUtils.i(TAG, "time:" + getCurrentTime());
                        LogUtils.i(TAG, "find success list.size:" + list.size());
                        postsLoading.setVisibility(View.GONE);
                        //有新帖子
                        if (list.size() != 0 && list.get(list.size() - 1) != null) {
                            fetchResult = true;
                            if (MyApplication.getMyApplication().getCurrentUser() != null) {
                                new SavePostsTask().execute(list);
                            }
                        } else {
                            fetchResult = false;
                            postsList.setRefreshFail("暂无新内容");
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        // TODO Auto-generated method stub
                        LogUtils.i(TAG, "find failed:" + s);
                        if (s.equals("find failed:No cache data.")) {
                            postsList.setRefreshFail("暂无新内容");
                        }
                        if (s.equals("unauthorized")) {
                            postsList.setRefreshFail(s);
                            postsLoading.setText("前往http://www.bmob.cn/申请Application ID后再调试");
                        }
                        fetchResult = false;
                    }
                });
                break;

            case LOAD_MORE:
                //加载更多
                query.setSkip(mListItems.size());

                //query.addWhereLessThan("createdAt", lastPostTime);
                //LogUtils.i(TAG, "query time:" + lastQueryTime.getDate() + "-" + lastQueryTime.getDate());

                query.findObjects(mContext, new FindListener<Post>() {
                    @Override
                    public void onSuccess(final List<Post> list) {
                        // TODO Auto-generated method stub
                        //LogUtils.i(TAG, "time:" + getCurrentTime());
                        LogUtils.i(TAG, "find success list.size:" + list.size());
                        //有新帖子
                        if (list.size() != 0 && list.get(list.size() - 1) != null) {
                            fetchResult = true;
                            if (MyApplication.getMyApplication().getCurrentUser() != null) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        new SavePostsTask().execute(list);
                                    }
                                }, 1500);
                            }
                        } else {
                            fetchResult = false;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    postsList.stopLoadMore();
                                    //ToastUtils.showToast(mContext, "已加载到最底部", Toast.LENGTH_SHORT);
                                    postsList.startLoadMore(); // 开启LoadingMore功能
                                }
                            }, 1500);

                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        // TODO Auto-generated method stub
                        LogUtils.i(TAG, "find failed:" + s);
                        if (s.equals("find failed:No cache data.")) {
                            postsList.setRefreshFail("暂无新内容");
                        }
                        fetchResult = false;
                    }
                });
                break;
        }
        return fetchResult;
    }


    private class SavePostsTask extends AsyncTask<List<Post>, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(List<Post>... list) {
            if (mRefreshType == RefreshType.REFRESH) {
                mListItems.clear();
            }
            return mListItems.addAll(list[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (fetchResult && result) {
                LogUtils.i(TAG, "FetchDataTask:fetchResult" + fetchResult);
                mAdapter.notifyDataSetChanged();
                postsList.setRefreshSuccess("加载成功"); // 通知加载成功
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postsList.startLoadMore(); // 开启LoadingMore功能
                    }
                }, 2000);
            } else {
                LogUtils.i(TAG, "FetchDataTask:fetchResult" + fetchResult);
                postsList.setRefreshFail("暂无新内容");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    /**
     * 判断用户是否登录
     *
     * @return
     */
    private boolean isLogin() {
        BmobUser user = BmobUser.getCurrentUser(mContext, User.class);
        if (user != null) {
            return true;
        }
        return false;
    }
}
