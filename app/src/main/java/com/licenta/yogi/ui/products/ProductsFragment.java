package com.licenta.yogi.ui.products;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licenta.yogi.R;
import com.licenta.yogi.models.Product;
import com.licenta.yogi.ui.viewproductitem.ViewProductItemFragment;
import com.licenta.yogi.utils.ProductAdapter;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {

    private ProductsViewModel productsViewModel;
    private ProductAdapter productAdapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        productsViewModel =
                ViewModelProviders.of(this).get(ProductsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_products, container, false);
        final ListView productsRecordsView = (ListView) root.findViewById(R.id.products_records_view);

        productsRecordsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("product",product);

                Navigation.findNavController(view).navigate(R.id.action_nav_products_to_nav_view_product_item,bundle);

            }
        });
        mAuth = FirebaseAuth.getInstance();
        String userUid = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Users").child(userUid).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot products) {

                ArrayList<Product> productArrayList = new ArrayList<>();

                for (DataSnapshot product : products.getChildren()) {
                        productArrayList.add(product.getValue(Product.class));
                }

                productAdapter = new ProductAdapter(root.getContext(), productArrayList);
                productsRecordsView.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }
}