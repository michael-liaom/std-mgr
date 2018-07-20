package com.weijie.stdmgr;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SimpleDataPickerActivity extends AppCompatActivity {
    final public static String RETURN_EXTRA_DATA_KEY  = "RETURN_EXTRA_DATA_KEY";
    final public static String PARAM_EXTRA_DATA_KEY   = "PARAM_EXTRA_DATA_KEY";

    private ArrayList<SimplePickItemData> itemList;
    private MyAdapter myAdapter;

    String activityTitle, itemTitle;
    ListView listView;
    boolean isMultiplePick;

    public static class Builder implements Serializable {
        String activityTitle, itemTitle;
        ArrayList<SimplePickItemData> itemList;
        boolean isMultiplePick;

        public Builder (String activityTitle, String itemTitle,
                        ArrayList<SimplePickItemData> itemList,
                        boolean isMultiplePick) {
            this.activityTitle  = activityTitle;
            this.itemTitle      = itemTitle;
            this.itemList       = itemList;
            this.isMultiplePick = isMultiplePick;
        }
    }

    @Override
    public void onBackPressed() {
        if (listView.getVisibility() == View.VISIBLE) {
            ArrayList<Integer> returnList = new ArrayList<>();

            for (SimplePickItemData itemData : itemList){
                if (itemData.isPicked){
                    returnList.add(itemData.id);
                }
            }
            Intent intent = new Intent();
            intent.putIntegerArrayListExtra(RETURN_EXTRA_DATA_KEY, returnList);
            setResult(0, intent);
        }

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_data_picker);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        prepareData();
        initControl();
    }

    private void prepareData() {
        Builder builder = (Builder) getIntent().getSerializableExtra(PARAM_EXTRA_DATA_KEY);
        activityTitle   = builder.activityTitle;
        itemTitle       = builder.itemTitle;
        itemList        = builder.itemList;
        isMultiplePick  = builder.isMultiplePick;
    }

    private void initControl() {
        setTitle(activityTitle);

        listView =  (ListView) findViewById(R.id.item_dynamic);
        TextView titleText = (TextView) findViewById(R.id.item_title_text_iew);
        titleText.setText(itemTitle);

        myAdapter = new MyAdapter(this, itemList, R.layout.content_simple_pick_data_item);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new  AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myAdapter.pickItem(position);
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        private List<SimplePickItemData> dataList;
        private Context context;
        private int resource;

        MyAdapter(Context context, List<SimplePickItemData> dataList, int resource) {
            this.context = context;
            this.dataList = dataList;
            this.resource = resource;

        }

        void pickItem(int position) {
            if (isMultiplePick) {
                dataList.get(position).isPicked = !dataList.get(position).isPicked;
            }
            else {
                for (SimplePickItemData itemData : dataList) {
                    itemData.isPicked = false;
                }

                dataList.get(position).isPicked = true;
            }
        }

        @Override
        public int getCount() {

            return dataList.size();
        }

        @Override
        public Object getItem(int index) {

            return dataList.get(index);
        }

        @Override
        public long getItemId(int index) {

            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(resource, null);
            }

            TextView itemNameTextView   = (TextView) view.findViewById(R.id.item_name_text_view);
            ImageView checkmarkImageView= (ImageView) view.findViewById(R.id.check_mark_image_view);

            // 获取数据显示在各组件
            SimplePickItemData itemData = dataList.get(index);
            itemNameTextView.setText(itemData.name);
            if (itemData.isPicked) {
                int imageId = Resources.getSystem().getIdentifier("checkbox_on_background",
                        "drawable", "android");
                checkmarkImageView.setImageResource(imageId);
            }
            else {
                int imageId = Resources.getSystem().getIdentifier("checkbox_off_background",
                        "drawable", "android");
                checkmarkImageView.setImageResource(imageId);
            }
            checkmarkImageView.setVisibility(View.VISIBLE);

            return view;
        }

    }
}
