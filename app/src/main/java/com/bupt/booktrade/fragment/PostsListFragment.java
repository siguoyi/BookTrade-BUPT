package com.bupt.booktrade.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.booktrade.MyApplication;
import com.bupt.booktrade.R;
import com.bupt.booktrade.activity.CommentActivity;
import com.bupt.booktrade.adapter.CardsAdapter;
import com.bupt.booktrade.db.DatabaseUtil;
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

public class PostsListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private String TAG;
    private ListView postsList;
    private TextView postsLoading;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int pageNum;
    private int currentIndex;
    private String lastItemTime;

    private ArrayList<Post> mListItems;
    private CardsAdapter mAdapter;

    public enum RefreshType {
        REFRESH, LOAD_MORE
    }

    private RefreshType mRefreshType = RefreshType.LOAD_MORE;

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
        if (mListItems.size() == 0) {
            new FetchDataTask().execute();
        }
        //currentIndex = getArguments().getInt("page");
        LogUtils.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);
        LogUtils.i(TAG, "onCreateView");
        postsList = (ListView) rootView.findViewById(R.id.cards_list);
        postsLoading = (TextView) rootView.findViewById(R.id.posts_loading);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.google_red, R.color.google_blue, R.color.google_green, R.color.google_yellow);
        mSwipeRefreshLayout.setDistanceToTriggerSync(400);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        setupList();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    private void setupList() {
        mAdapter = new CardsAdapter(mContext, mListItems, new ListItemClickListener());
        postsList.setAdapter(mAdapter);
        postsList.setOnItemClickListener(new ListItemClickListener());
    }

    @Override
    public void onRefresh() {

        // TODO Auto-generated method stub
        mRefreshType = RefreshType.REFRESH;

        new FetchDataTask().execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 5000);


    }


    public void fetchData() {
        BmobQuery<Post> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        LogUtils.i(TAG, "SIZE:" + Constant.NUMBERS_PER_PAGE * pageNum);
        query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
        LogUtils.i(TAG, "SIZE:" + Constant.NUMBERS_PER_PAGE * pageNum);
        query.include("author");
        query.findObjects(mContext, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
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
                    if (MyApplication.getMyApplication().getCurrentUser() != null) {
                        list = DatabaseUtil.getInstance(mContext).setFav(list);
                    }
                    mListItems.addAll(list);

                } else {
                    ToastUtils.showToast(mContext, "暂无更多数据", Toast.LENGTH_SHORT);
                    pageNum--;
                }
            }

            @Override
            public void onFinish() {
                postsLoading.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
                super.onFinish();
            }

            @Override
            public void onError(int i, String s) {
                // TODO Auto-generated method stub
                LogUtils.i(TAG, "find failed:" + s);
                pageNum--;
            }
        });
    }



    private class FetchDataTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            fetchData();
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
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
