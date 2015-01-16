package com.bupt.booktrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bupt.booktrade.R;

import java.util.List;

public class CardsAdapter extends BaseAdapter {

    private final OnItemClickListener itemClickListener;
    private final Context context;
    private List<String> items;

    public CardsAdapter(Context context, List<String> items, OnItemClickListener itemClickListener) {
        this.context = context;
        this.items = items;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_card, null);

            holder = new ViewHolder();
            holder.itemText = (TextView) convertView.findViewById(R.id.post_title);
            //holder.itemButton1 = (Button) convertView.findViewById(R.id.list_item_card_button_1);
            //holder.itemButton2 = (Button) convertView.findViewById(R.id.list_item_card_button_2);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemText.setText(items.get(position));
/*
        if (itemButtonClickListener != null) {
            holder.itemButton1.setOnClickListener(OnItemClickListener);
            holder.itemButton2.setOnClickListener(OnItemClickListener);
        }
        */
        return convertView;
    }

    private static class ViewHolder {
        private TextView itemText;
    }

}
