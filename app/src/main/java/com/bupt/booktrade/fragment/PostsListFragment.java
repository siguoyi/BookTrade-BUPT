package com.bupt.booktrade.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bupt.booktrade.R;
import com.bupt.booktrade.adapter.CardsAdapter;
import com.bupt.booktrade.utils.ToastUtils;
import com.demievil.pulldownlistview.EyeView;
import com.demievil.pulldownlistview.PullDownListView;
import com.demievil.pulldownlistview.YProgressView;

import java.util.ArrayList;

public class PostsListFragment extends BaseFragment {

    private ListView postsList;
    private PullDownListView pullDownListView;
    private RelativeLayout layoutHeader, layoutFooter;
    private YProgressView progressView;
    private EyeView eyeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);
        pullDownListView =
                (PullDownListView) rootView.findViewById(R.id.posts_list);
        layoutHeader = (RelativeLayout) rootView.findViewById(R.id.layoutHeader);
        layoutFooter = (RelativeLayout) rootView.findViewById(R.id.layoutFooter);
        pullDownListView.setLayoutHeader(layoutHeader);
        pullDownListView.setLayoutFooter(layoutFooter);
        progressView = (YProgressView) rootView.findViewById(R.id.progressView);
        eyeView = (EyeView) rootView.findViewById(R.id.eyeView);
        postsList = pullDownListView.getListView();
        setupList();
        return rootView;
    }

    private void setupList() {
        postsList.setAdapter(createAdapter());

        pullDownListView.setOnPullHeightChangeListener(new PullDownListView.OnPullHeightChangeListener() {

            @Override
            public void onTopHeightChange(int headerHeight, int pullHeight) {
                // TODO Auto-generated method stub
                float progress = (float) pullHeight / (float) headerHeight;

                if (progress < 0.5) {
                    progress = 0.0f;
                } else {
                    progress = (progress - 0.5f) / 0.5f;
                }


                if (progress > 1.0f) {
                    progress = 1.0f;
                }

                if (!pullDownListView.isRefreshing()) {
                    eyeView.setProgress(progress);
                }
            }

            @Override
            public void onBottomHeightChange(int footerHeight, int pullHeight) {
                // TODO Auto-generated method stub
                float progress = (float) pullHeight / (float) footerHeight;

                if (progress < 0.5) {
                    progress = 0.0f;
                } else {
                    progress = (progress - 0.5f) / 0.5f;
                }

                if (progress > 1.0f) {
                    progress = 1.0f;
                }

                if (!pullDownListView.isRefreshing()) {
                    progressView.setProgress(progress);
                }

            }

            @Override
            public void onRefreshing(final boolean isTop) {
                // TODO Auto-generated method stub
                if (isTop) {
                    eyeView.startAnimate();
                } else {
                    progressView.startAnimate();
                }

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pullDownListView.pullUp();
                        if (isTop) {
                            eyeView.stopAnimate();
                        } else {
                            progressView.stopAnimate();
                        }
                    }

                }, 3000);
            }

        });


        postsList.setOnItemClickListener(new ListItemClickListener());
    }

    private CardsAdapter createAdapter() {
        ArrayList<String> items = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            items.add(i, "[出]几本书 " + i);
        }

        return new CardsAdapter(getActivity(), items, new ListItemClickListener());
    }

    /*
        private final class ListItemButtonClickListener implements OnClickListener {
            @Override
            public void onClick(View v) {
                for (int i = cardsList.getFirstVisiblePosition(); i <= cardsList.getLastVisiblePosition(); i++) {
                    if (v == cardsList.getChildAt(i - cardsList.getFirstVisiblePosition()).findViewById(R.id.list_item_card_button_1)) {
                        // PERFORM AN ACTION WITH THE ITEM AT POSITION i
                        ToastUtils.showToast(getActivity(), "Left:" + i, Toast.LENGTH_SHORT);
                    } else if (v == cardsList.getChildAt(i - cardsList.getFirstVisiblePosition()).findViewById(R.id.list_item_card_button_2)) {
                        // PERFORM ANOTHER ACTION WITH THE ITEM AT POSITION i
                        ToastUtils.showToast(getActivity(), "Right:" + i, Toast.LENGTH_SHORT);
                    }
                }
            }
        }
    */
    private final class ListItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ToastUtils.showToast(getActivity(), "Clicked on List Item " + position, Toast.LENGTH_SHORT);
        }
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context
                .getResources().getDisplayMetrics());
    }

}
