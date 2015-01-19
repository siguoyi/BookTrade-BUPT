package com.bupt.booktrade.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupt.booktrade.R;
import com.bupt.booktrade.fragment.model.NavDrawerItem;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseAdapter {

    private final int View_TYPE = 3;
    private final int TYPE_ME = 0;
    private final int TYPE_LIST = 1;
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    public int getCount() {
        return navDrawerItems.size();
    }


    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }


    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ME;
        } else {
            return TYPE_LIST;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            switch (type) {
                case TYPE_ME:
                    convertView = mInflater.inflate(R.layout.drawer_list_me, null);
                    ImageView avatar = (ImageView) convertView.findViewById(R.id.drawer_avatar);
                    TextView userName = (TextView) convertView.findViewById(R.id.drawer_user_name);
                    break;
                case TYPE_LIST:
                    convertView = mInflater.inflate(R.layout.drawer_list_item, null);
                    ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
                    TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
                    TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

                    imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
                    txtTitle.setText(navDrawerItems.get(position).getTitle());

                    // displaying count
                    // check whether it set visible or not
                    if (navDrawerItems.get(position).getCounterVisibility()) {
                        txtCount.setText(navDrawerItems.get(position).getCount());
                    } else {
                        // hide the counter view
                        txtCount.setVisibility(View.GONE);
                    }

                    break;
                default:
                    break;
            }

        }

        return convertView;
    }


}
