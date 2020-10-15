package com.licenta.yogi.ui.deleteusers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeleteUsersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DeleteUsersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Delete users fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}