package com.licenta.yogi.ui.transferproduct;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licenta.yogi.R;
import com.licenta.yogi.models.Product;
import com.licenta.yogi.models.User;
import com.licenta.yogi.services.TransferProductAsyncTask;
import com.licenta.yogi.utils.HintArrayAdapter;
import com.licenta.yogi.utils.ProductAdapter;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TransferProductFragment extends Fragment {

    private SearchableSpinner searchableSpinner;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView userWalletAddress;
    private TextView amountLeft;
    private EditText amountToTransfer;

    private Button transferButton;
    private Button cancelButton;

    private User selectedUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Transfer product");
        View root = inflater.inflate(R.layout.fragment_transfer_product, container, false);

        amountLeft = root.findViewById(R.id.id_transfer_left_quantity_label);
        amountToTransfer = root.findViewById(R.id.id_transfer_product_amount);
        userWalletAddress = root.findViewById(R.id.id_transfer_product_user_wallet_address);
        transferButton = root.findViewById(R.id.id_transfer_product_transfer_button);
        cancelButton = root.findViewById(R.id.id_transfer_product_cancel_button);

        Bundle bundle = getArguments();
        Product product = bundle.getParcelable("product");

        amountLeft.setText(String.valueOf(product.getLeft())+"x LEFT");

        searchableSpinner = root.findViewById(R.id.id_transfer_product_search_spinner);
        searchableSpinner.setTitle("Users");
        searchableSpinner.setPositiveButton("APASA");

        HintArrayAdapter adapter = new HintArrayAdapter(root.getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAuth = FirebaseAuth.getInstance();
        String userUid = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot users) {

                ArrayList<User> usersArrayList = new ArrayList<>();

                for (DataSnapshot user : users.getChildren()) {
                    if(user.getKey().equals(userUid)) continue;
                    User iterator = user.getValue(User.class);
                    iterator.setId(user.getKey());
                    usersArrayList.add(iterator);
                }

                adapter.addAll(usersArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchableSpinner.setAdapter(adapter);

        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUser = (User) searchableSpinner.getSelectedItem();
                userWalletAddress.setText(selectedUser.getWalletAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("product",product);
                Navigation.findNavController(v).navigate(R.id.action_nav_view_transfer_product_to_nav_view_product_item,bundle);
            }
        });

        transferButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(amountToTransfer.getText().toString())) {
                    amountToTransfer.setError("Insert amount to transfer.");
                    amountToTransfer.requestFocus();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setCancelable(true);
                builder.setTitle("Confirm transfer to: "+ selectedUser.getFirstName() + " " + selectedUser.getLastName());
                builder.setMessage("Are you sure you want to send "+ amountToTransfer.getText().toString() + " " + product.getName());
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(v.getContext(),"Transfer in progress.",Toast.LENGTH_LONG).show();
                                try {
                                    boolean transferStatus = new TransferProductAsyncTask(
                                            product,
                                            selectedUser.getWalletAddress(),
                                            BigInteger.valueOf(Long.valueOf(amountToTransfer.getText().toString()))
                                            ).execute().get();
                                    if(transferStatus == true) {
                                        Toast.makeText(v.getContext(),"Transfer success",Toast.LENGTH_LONG).show();

                                        Long transferedAmount = Long.valueOf(amountToTransfer.getText().toString());

                                        product.setSold(product.getSold() + transferedAmount);
                                        product.setLeft(product.getLeft() - transferedAmount);

                                        Log.i("TransferSuccess",product.getId());
                                        mDatabase.child("Users").child(userUid).child("Products").child(product.getId()).setValue(product);

                                        product.setLeft(transferedAmount);
                                        product.setSold(0L);

                                        mDatabase.child("Users").child(selectedUser.getId()).child("Products").child(product.getId()).setValue(product);


                                    }
                                    else Toast.makeText(v.getContext(),"Transfer failed",Toast.LENGTH_LONG).show();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return root;
    }
}
