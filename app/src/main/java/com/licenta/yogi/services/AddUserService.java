package com.licenta.yogi.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AddUserService extends Service {
    public AddUserService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
