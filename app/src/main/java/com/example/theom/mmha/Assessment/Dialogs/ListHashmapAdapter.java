package com.example.theom.mmha.Assessment.Dialogs;

/**
 * Created by theom on 27/02/2017.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.theom.mmha.R;

import java.util.ArrayList;

public class ListHashmapAdapter extends BaseAdapter {
    private final ArrayList mData;

    public ListHashmapAdapter(ArrayList races) {
        mData = new ArrayList();
        mData.addAll(races);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_adapter_item, parent, false);
        } else {
            result = convertView;
        }

        String item = getItem(position);

        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(android.R.id.text1)).setText(item);
        //((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());

        return result;
    }
}