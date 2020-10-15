package com.licenta.yogi.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.licenta.yogi.R;
import com.licenta.yogi.models.Product;

import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private Context productContext;
    private List<Product> productList;
    private static class ProductViewHolder {

        public TextView nameView;
        public TextView symbolView;
        public TextView totalView;
        public TextView leftView;
        public TextView soldView;
    }
    public ProductAdapter(Context context, List<Product> products) {
        productList = products;
        productContext = context;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ProductViewHolder holder;

        if (view ==null){
            LayoutInflater productInflater = (LayoutInflater)
                    productContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = productInflater.inflate(R.layout.product_view_for_adapter_table, null);

            holder = new ProductViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.table_product_name);
            holder.symbolView = (TextView) view.findViewById(R.id.table_product_symbol);
            holder.totalView = (TextView) view.findViewById(R.id.table_product_total);
            holder.leftView = (TextView) view.findViewById(R.id.table_product_left);
            holder.soldView = (TextView) view.findViewById(R.id.table_product_sold);
            view.setTag(holder);

        }else {
            holder = (ProductViewHolder) view.getTag();
        }

        Product product = (Product) getItem(i);
        holder.nameView.setText(product.getName());
        holder.symbolView.setText(product.getSymbol());
        holder.totalView.setText(String.valueOf(product.getTotalSupply()));
        holder.leftView.setText(String.valueOf(product.getLeft()));
        holder.soldView.setText(String.valueOf(product.getSold()));

        return view;
    }
    @Override
    public int getCount() {
        return productList.size();
    }
    @Override
    public Object getItem(int i) {
        return productList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
}