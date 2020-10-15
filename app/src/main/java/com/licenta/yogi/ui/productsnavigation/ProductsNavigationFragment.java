package com.licenta.yogi.ui.productsnavigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.licenta.yogi.R;

public class ProductsNavigationFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("View product");
        View root = inflater.inflate(R.layout.fragment_products_navigation, container, false);
        return root;
    }
}
