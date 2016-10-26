package com.example.guest999.firebasenotification.utilis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by HIREN AMALIYAR on 12-10-2016.
 */

public class MarshmallowPermissions {
    Context mcontaxt;

    public MarshmallowPermissions(Context applicationContext) {
        mcontaxt = applicationContext;
    }

    public boolean chkpermissions() {

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }
        return true;
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(mcontaxt, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            //Toast.makeText(this, "Plz allow permision for sending file and photos", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions((Activity) mcontaxt, new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

}
