package com.licenta.yogi.ui.addproducts;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licenta.yogi.MainActivity;
import com.licenta.yogi.R;
import com.licenta.yogi.generatedcontracts.VolumeCoin;
import com.licenta.yogi.models.Product;
import com.licenta.yogi.services.AddProductAsyncTask;
import com.licenta.yogi.services.AddProductService;
import com.licenta.yogi.utils.MultiSelectionSpinner;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddProductsFragment extends Fragment {

    private AddProductsViewModel addProductsViewModel;
    MultiSelectionSpinner restrictedCountriesSpinner;
    private EditText productName;
    private EditText productSymbol;
    private EditText productTotal;
    private EditText productLength;
    private EditText productWidth;
    private EditText productHeight;
    private EditText productProductionDate;
    private EditText productExpirationDate;
    final Calendar myCalendar = Calendar.getInstance();
    private Credentials credentials;

    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addProductsViewModel =
                ViewModelProviders.of(this).get(AddProductsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_products, container, false);
        productName = root.findViewById(R.id.id_add_products_name);
        productSymbol = root.findViewById(R.id.id_add_products_symbol);
        productTotal = root.findViewById(R.id.id_add_products_total);
        productLength = root.findViewById(R.id.id_add_products_length);
        productWidth = root.findViewById(R.id.id_add_products_width);
        productHeight = root.findViewById(R.id.id_add_products_height);
        productProductionDate = root.findViewById(R.id.id_add_products_production_date);
        productExpirationDate = root.findViewById(R.id.id_add_products_expiration_date);


        DatePickerDialog.OnDateSetListener dateProductionDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(productProductionDate);
            }
        };
        DatePickerDialog.OnDateSetListener dateExpirationDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(productExpirationDate);
            }
        };
        productProductionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(root.getContext(), dateProductionDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        productExpirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(root.getContext(), dateExpirationDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        addProductsViewModel.getProductName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                productName.setText(s);
            }
        });
        addProductsViewModel.getProductSymbol().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                productSymbol.setText(s);
            }
        });
        addProductsViewModel.getProductTotal().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                productTotal.setText(s);
            }
        });
        addProductsViewModel.getProductHeight().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                productHeight.setText(s);
            }
        });
        addProductsViewModel.getProductLength().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                productLength.setText(s);
            }
        });
        addProductsViewModel.getProductWidth().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                productWidth.setText(s);
            }
        });

        ArrayList<String> items = new ArrayList<>();

        items.add("Germania");
        items.add("Ucraina");
        items.add("Franta");
        items.add("Anglia");
        items.add("Irak");
        items.add("Rusia");
        items.add("Spania");
        items.add("USA");
        items.add("Iran");
        items.add("Romania");

        restrictedCountriesSpinner = (MultiSelectionSpinner) root.findViewById(R.id.id_add_products_multiple_countries_spinner);

        restrictedCountriesSpinner.setItems(items);

        root.findViewById(R.id.id_add_products_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateProductFields() == false) return;
                else {
                    SimpleDateFormat sdfs = new SimpleDateFormat("dd/MM/yy",Locale.US);
                    Product product = new Product();
                    try {
                        product = new Product(
                                productName.getText().toString(),
                                productSymbol.getText().toString(),
                                restrictedCountriesSpinner.getSelectedItems()
                                        .stream().map(Object::toString).collect(Collectors.joining(", ")),
                                Long.valueOf(productTotal.getText().toString()),
                                Long.valueOf(productLength.getText().toString()),
                                Long.valueOf(productWidth.getText().toString()),
                                Long.valueOf(productHeight.getText().toString()),
                                sdfs.parse(productProductionDate.getText().toString()).getTime(),
                                sdfs.parse(productExpirationDate.getText().toString()).getTime()
                                );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    clearFields();

                    new AddProductAsyncTask(product).execute();
//                    Intent intent = new Intent(root.getContext(), AddProductService.class);
//                    intent.putExtra("Product", product);
//                    getActivity().startService(intent);
//                    createProductAndDeployToBlockchain(product);
                }
            }
        });

        return root;
    }

    private void updateLabel(EditText date) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean validateProductFields() {
        String name = productName.getText().toString();
        String symbol = productSymbol.getText().toString();
        String total = productTotal.getText().toString();
        String prodDate = productProductionDate.getText().toString();
        String expDate = productExpirationDate.getText().toString();
        String length = productLength.getText().toString();
        String height = productHeight.getText().toString();
        String width = productWidth.getText().toString();

        boolean validation = true;
        if(TextUtils.isEmpty(width)) {
            productHeight.setError("Please insert width.");
            validation = false;
        }
        if(TextUtils.isEmpty(height)) {
            productHeight.setError("Please insert height.");
            validation = false;
        }
        if(TextUtils.isEmpty(length)) {
            productLength.setError("Please insert length.");
            validation = false;
        }
        if(TextUtils.isEmpty(expDate)) {
            productExpirationDate.setError("Insert expiration date.");
            validation = false;
        }
        if(TextUtils.isEmpty(name)) {
            productName.setError("Please type product name.");
            validation = false;
        }
        if(TextUtils.isEmpty(symbol)) {
            productSymbol.setError("Please type symbol.");
            validation = false;
        }
        if(TextUtils.isEmpty(total)) {
            productTotal.setError("Insert product quantity.");
            validation = false;
        }
        if(TextUtils.isEmpty(prodDate)) {
            productProductionDate.setError("Insert production date.");
            validation = false;
        }
        return validation;
    }
    public void clearFields() {
        productName.getText().clear();
        productSymbol.getText().clear();
        productTotal.getText().clear();
        productWidth.getText().clear();
        productHeight.getText().clear();
        productLength.getText().clear();
        productExpirationDate.getText().clear();
        productProductionDate.getText().clear();
        restrictedCountriesSpinner.setSelection(new ArrayList<>());
    }

    public void createProductAndDeployToBlockchain(Product product) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        product.setId(UUID.randomUUID().toString());
        mDatabase.child("Users").child(userUid).child("Products").child(product.getId()).setValue(product);

        try
        {
            Web3j web3j = Web3j.build(new HttpService(MainActivity.INFURA_URL));
            Web3ClientVersion clientVersion = web3j.web3ClientVersion().sendAsync().get();

            mDatabase.child("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    credentials = Credentials.create(snapshot.child("walletPrivateKey").getValue(String.class));
                    ContractGasProvider gasProvider = new StaticGasProvider(BigInteger.valueOf(5000000000000L), BigInteger.valueOf(3000000));
//                    try {
////                        VolumeCoin coinProductContract = VolumeCoin.deploy(
////                                web3j, credentials,
////                                gasProvider,
////                                BigInteger.valueOf(product.getTotalSupply()),
////                                product.getName(),
////                                product.getSymbol()
////                        ).sendAsync().get();
////                        mDatabase.child("Users").child(userUid)
////                                .child("Products").child(product.getId())
////                                .child("contractAddress").setValue(coinProductContract.getContractAddress());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if(!clientVersion.hasError()){
                Log.i("","Connected to infura");
            }
            else {
                Log.i("WalletActivity",clientVersion.getError().getMessage());
            }
        }
        catch(Exception ex) {
            Log.i("AddProducts",ex.getMessage());
        }

    }
}