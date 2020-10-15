package com.licenta.yogi.ui.deleteusers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.licenta.yogi.R;

public class DeleteUsersFragment extends Fragment {

    private DeleteUsersViewModel deleteUsersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        deleteUsersViewModel =
                ViewModelProviders.of(this).get(DeleteUsersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_delete_users, container, false);
        final TextView textView = root.findViewById(R.id.id_delete_users_text_view);
        deleteUsersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}