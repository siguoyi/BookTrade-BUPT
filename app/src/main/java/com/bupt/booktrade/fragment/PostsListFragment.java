package com.bupt.booktrade.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.bupt.booktrade.R;
import com.bupt.booktrade.adapter.CardsAdapter;
import com.bupt.booktrade.utils.ToastUtils;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class PostsListFragment extends BaseFragment {

    private ListView postsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);
        final PtrFrameLayout frame = (PtrFrameLayout) rootView.findViewById(R.id.material_style_ptr_frame);
        // header
        final MaterialHeader header = new MaterialHeader(mContext);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, PtrLocalDisplay .dp2px(10));
        header.setPtrFrameLayout(frame);

        frame.setLoadingMinTime(1000);
        frame.setDurationToCloseHeader(1500);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);

        frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.autoRefresh(false);
            }
        }, 100);


        postsList = (ListView) rootView.findViewById(R.id.posts_list);
        setupList();
        return rootView;
    }

    private void setupList() {
        postsList.setAdapter(createAdapter());
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


}
