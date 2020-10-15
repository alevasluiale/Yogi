package com.licenta.yogi.ui.addusers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddUsersViewModel extends ViewModel {

    private MutableLiveData<String> userFirstName,userLastName,userEmail,userPassword,userRetypedPassword,userCountry,userCity;

    public AddUsersViewModel() {
        userFirstName = new MutableLiveData<>();
        userLastName = new MutableLiveData<>();
        userEmail = new MutableLiveData<>();
        userPassword = new MutableLiveData<>();
        userRetypedPassword = new MutableLiveData<>();
        userCountry = new MutableLiveData<>();
        userCity = new MutableLiveData<>();
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }

    public MutableLiveData<String> getUserFirstName() {
        return userFirstName;
    }

    public MutableLiveData<String> getUserLastName() {
        return userLastName;
    }

    public MutableLiveData<String> getUserPassword() {
        return userPassword;
    }

    public MutableLiveData<String> getUserRetypedPassword() {
        return userRetypedPassword;
    }

    public MutableLiveData<String> getUserCountry() {
        return userCountry;
    }

    public MutableLiveData<String> getUserCity() {
        return userCity;
    }
}