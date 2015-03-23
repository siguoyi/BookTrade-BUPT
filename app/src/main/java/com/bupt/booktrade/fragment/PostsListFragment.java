package com.bupt.booktrade.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bupt.booktrade.R;
import com.bupt.booktrade.adapter.CardsAdapter;
import com.bupt.booktrade.utils.ToastUtils;

import java.util.ArrayList;

public class PostsListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView cardsList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);
        cardsList = (ListView) rootView.findViewById(R.id.cards_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.yellow, R.color.green);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        setupList();
        return rootView;
    }

    private void setupList() {
        cardsList.setAdapter(createAdapter());
        cardsList.setOnItemClickListener(new ListItemClickListener());
    }

    private CardsAdapter createAdapter() {
        ArrayList<String> items = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            items.add(i, "[出]几本书 " + i);
        }

        return new CardsAdapter(getActivity(), items, new ListItemClickListener());
    }

    @Override
    public void onRefresh() {

        // TODO Auto-generated method stub

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);


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

}
