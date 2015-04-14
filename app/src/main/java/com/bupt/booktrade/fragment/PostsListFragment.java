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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.booktrade.MyApplication;
import com.bupt.booktrade.R;
import com.bupt.booktrade.activity.CommentActivity;
import com.bupt.booktrade.adapter.CardsAdapter;
import com.bupt.booktrade.entity.Post;
import com.bupt.booktrade.utils.Constant;
import com.bupt.booktrade.utils.LogUtils;
import com.bupt.booktrade.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import zrc.widget.SimpleFooter;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView;

public class PostsListFragment extends BaseFragment {

    private String TAG;
    private ZrcListView postsList;
    private TextView postsLoading;
    private int pageNum;
    private int currentIndex;
    public boolean fetchResult = false;
    private String lastItemTime;
    private Handler handler;
    private ArrayList<Post> mListItems;
    private CardsAdapter mAdapter;

    private View rootView;

    public enum RefreshType {
        REFRESH, LOAD_MORE
    }

    public RefreshType mRefreshType = RefreshType.LOAD_MORE;

    /*
    public static BaseFragment newInstance(int index) {
        BaseFragment fragment = new PostsListFragment();
        Bundle args = new Bundle();
        args.putInt("page", index);
        fragment.setArguments(args);
        return fragment;
    }
*/
    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(new Date(System.currentTimeMillis()));
        return time;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        setRetainInstance(true);
        mListItems = new ArrayList<>();
        pageNum = 0;
        lastItemTime = getCurrentTime();
        LogUtils.i(TAG, "current time:" + lastItemTime);

        //currentIndex = getArguments().getInt("page");
        LogUtils.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i(TAG, "onCreateView");
        handler = new Handler();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);
            LogUtils.i(TAG, "onCreateView");
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

//            // 设置列表项出现动画（可选）
//            postsList.setItemAnimForTopIn(R.anim.topitem_in);
//            postsList.setItemAnimForBottomIn(R.anim.bottomitem_in);

            // 下拉刷新事件回调（可选）
            postsList.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
                @Override
                public void onStart() {
                    mRefreshType = RefreshType.REFRESH;
                    new FetchDataTask().execute();
                }
            });

            // 加载更多事件回调（可选）
            postsList.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
                @Override
                public void onStart() {
                    mRefreshType = RefreshType.REFRESH;
                    // loadMore();
                }
            });
            setupList();
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        //if (mListItems.size() == 0) {
            postsList.refresh(); // 主动下拉刷新
       // }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    private void setupList() {
        mAdapter = new CardsAdapter(mContext, mListItems, new ZrcListView.OnItemClickListener() {
            @Override
            public void onItemClick(ZrcListView parent, View view, int position, long id) {
                LogUtils.i(TAG, position);
                Intent intent = new Intent();
                intent.setClass(getActivity(), CommentActivity.class);
                intent.putExtra("data", mListItems.get(position));
                startActivity(intent);
            }
        });
        postsList.setAdapter(mAdapter);
        postsList.refresh(); // 主动下拉刷新
    }

    private void refresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               fetchData();
            }
        }, 2 * 1000);
    }

    public boolean fetchData() {
        BmobQuery<Post> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        //LogUtils.i(TAG, "SIZE:" + Constant.NUMBERS_PER_PAGE * pageNum);
        query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
        //LogUtils.i(TAG, "SIZE:" + Constant.NUMBERS_PER_PAGE * pageNum);
        query.include("author");
        query.findObjects(mContext, new FindListener<Post>() {
            @Override
            public void onSuccess(final List<Post> list) {
                // TODO Auto-generated method stub
                LogUtils.i(TAG, "time:" + getCurrentTime());
                LogUtils.i(TAG, "find success:" + list.size());
                postsLoading.setVisibility(View.GONE);
                if (list.size() != 0 && list.get(list.size() - 1) != null) {
                    if (mRefreshType == RefreshType.REFRESH) {
                        mListItems.clear();
                    }
                    if (list.size() < Constant.NUMBERS_PER_PAGE) {
                        LogUtils.i(TAG, "已加载完所有数据");
                    }
                    if (MyApplication.getMyApplication().getCurrentUser() != null)
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //list = DatabaseUtil.getInstance(mContext).setFav(list);
                                mListItems.addAll(list);
                            }
                        }).start();
                } else {
                    ToastUtils.showToast(mContext, "暂无更多数据", Toast.LENGTH_SHORT);
                    pageNum--;
                }
                fetchResult = true;
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onError(int i, String s) {
                // TODO Auto-generated method stub
                LogUtils.i(TAG, "find failed:" + s);
                if (s.equals("find failed:No cache data.")) {
                    postsList.setRefreshFail("暂无新内容");
                }
                pageNum--;
                fetchResult = false;
            }
        });
        return fetchResult;
    }


    private class FetchDataTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            fetchData();
            return fetchResult;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (fetchResult) {
                LogUtils.i(TAG, fetchResult + "");
                postsLoading.setVisibility(View.GONE);
                postsList.setRefreshSuccess("加载成功"); // 通知加载成功
                postsList.startLoadMore(); // 开启LoadingMore功能
            } else {

                postsList.setRefreshFail("暂无新内容");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private final class ListItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LogUtils.i(TAG, position);
            Intent intent = new Intent();
            intent.setClass(getActivity(), CommentActivity.class);
            intent.putExtra("data", mListItems.get(position));
            startActivity(intent);
        }
    }
}
