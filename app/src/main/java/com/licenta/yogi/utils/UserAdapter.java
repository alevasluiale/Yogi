package com.licenta.yogi.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.licenta.yogi.R;
import com.licenta.yogi.models.User;

import java.util.List;

public class UserAdapter extends BaseAdapter {

    private Context userContext;
    private List<UserForView> userList;
    private static class userViewHolder {

        public TextView nameView;
        public TextView countryView;
        public TextView cityView;
        public TextView addressView;
        public TextView contractAddressView;
    }
    public UserAdapter(Context context, List<UserForView> users) {
        userList = users;
        userContext = context;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        userViewHolder holder;

        if (view ==null){
            LayoutInflater userInflater = (LayoutInflater)
                    userContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = userInflater.inflate(R.layout.user_view_for_adapter_table, null);

            holder = new userViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.table_user_name);
            holder.countryView = (TextView) view.findViewById(R.id.table_user_country);
            holder.cityView = (TextView) view.findViewById(R.id.table_user_city);
            holder.addressView = (TextView) view.findViewById(R.id.table_user_address);
            holder.contractAddressView = (TextView) view.findViewById(R.id.table_user_contract_address);
            view.setTag(holder);

        }else {
            holder = (userViewHolder) view.getTag();
        }

        UserForView user = (UserForView) getItem(i);
        holder.nameView.setText(user.getName());
        holder.countryView.setText(user.getCountry());
        holder.cityView.setText(String.valueOf(user.getCity()));
        holder.contractAddressView.setText(String.valueOf(user.getSold()));

        return view;
    }
    @Override
    public int getCount() {
        return userList.size();
    }
    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
}