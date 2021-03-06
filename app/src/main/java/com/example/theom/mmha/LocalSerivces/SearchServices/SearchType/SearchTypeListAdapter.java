package com.example.theom.mmha.LocalSerivces.SearchServices.SearchType;

/**
 * Created by theom on 01/03/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.theom.mmha.R;

import java.util.ArrayList;


public class SearchTypeListAdapter extends ArrayAdapter{
        ArrayList<SearchTypeItem> searchTypeItems = new ArrayList<SearchTypeItem>();
        Context context;

        public SearchTypeListAdapter(Context context, ArrayList<SearchTypeItem> resource) {
                super(context, R.layout.location_type_item,resource);
                // TODO Auto-generated constructor stub
                this.context = context;
                this.searchTypeItems = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                convertView = inflater.inflate(R.layout.location_type_item, parent, false);
                TextView name = (TextView) convertView.findViewById(R.id.textView1);
                CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);

                //Listener to check for when user makes a change to the checkbox
                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                //method used to change the state of the checkbox
                                setChecked(position);
                        }
                });

                name.setText(searchTypeItems.get(position).getName());
                if(searchTypeItems.get(position).isChecked() == true) {
                    cb.setChecked(true);
                }else {
                    cb.setChecked(false);
                }


                return convertView;
        }

        @Override
        public SearchTypeItem getItem(int position){
                return searchTypeItems.get(position);
        }

        public ArrayList<SearchTypeItem> getArray(){
            return searchTypeItems;
        }

        //return the type locations that are
        public ArrayList<SearchTypeItem> getCheckedItems(){
                ArrayList<SearchTypeItem> checkedSearchItems = new ArrayList<SearchTypeItem>();
                for (int i=0;i<searchTypeItems.size();i++){
                        if (searchTypeItems.get(i).isChecked() == true) {
                                checkedSearchItems.add(searchTypeItems.get(i));
                        }
                }
                return checkedSearchItems;
        }

        //Method used to change the state of the checkbox
        public void setChecked(Integer position){
                searchTypeItems.get(position).setChecked(!searchTypeItems.get(position).isChecked());
                System.out.println(searchTypeItems.get(position).getName()+" "+searchTypeItems.get(position).isChecked());
        }
}