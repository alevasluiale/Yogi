package com.licenta.yogi.ui.displayowner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.licenta.yogi.R;
import com.licenta.yogi.ui.products.ProductsViewModel;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DisplayInitialOwnerFragment extends Fragment {

    private TextView userName;
    private TextView userContractAddress;
    private TextView userRealAddress;
    private TextView userCity;
    private TextView userCountry;
    private TextView productProductionDate;
    private TextView productExpirationDate;
    private TextView productRestrictedCountries;

    private Button goBackButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_display_initial_owner, container, false);

        Bundle bundle = getArguments();

        userName = root.findViewById(R.id.id_display_owner_name);
        userContractAddress = root.findViewById(R.id.id_display_owner_contract_address);
        userRealAddress = root.findViewById(R.id.id_display_owner_address);
        userCity = root.findViewById(R.id.id_display_owner_city);
        userCountry = root.findViewById(R.id.id_display_owner_country);
        productRestrictedCountries = root.findViewById(R.id.id_display_owner_restricted_countries);
        productProductionDate = root.findViewById(R.id.id_display_owner_production_date);
        productExpirationDate = root.findViewById(R.id.id_display_owner_expiration_date);

        goBackButton = root.findViewById(R.id.id_display_owner_button_back);

        userName.setText(bundle.getString("userName"));
        userContractAddress.setText(bundle.getString("userContractAddress"));
        userRealAddress.setText(bundle.getString("userRealAddress"));
        userCity.setText(bundle.getString("userCity"));
        userCountry.setText(bundle.getString("userCountry"));
        productRestrictedCountries.setText(bundle.getString("productRestrictedCountries"));


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);


        productProductionDate.setText(sdf.format(new Date(Long.valueOf(bundle.getString("productProductionDate")))));
        productExpirationDate.setText(sdf.format(new Date(Long.valueOf(bundle.getString("productExpirationDate")))));

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_nav_view_initial_product_owner_to_nav_view_product_item,bundle);
            }
        });
        return root;
    }
}
