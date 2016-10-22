package com.example.guest999.firebasenotification.utilis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Created by HIREN AMALIYAR on 12-10-2016.
 */

public class MarshmallowPermissions {
    Context mcontaxt;

    public MarshmallowPermissions(Context applicationContext) {
        mcontaxt = applicationContext;
    }

    public void requestpermissions() {
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ActivityCompat.requestPermissions((Activity) mcontaxt,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    1);
        }
    }

    public void AllowedManually(View vg) {
        Snackbar.make(vg, "Unable to share files and pictures without permission.Please allow permission", Snackbar.LENGTH_INDEFINITE)
                .setAction("Allow ME", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent i = new Intent();
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + mcontaxt.getPackageName()));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        mcontaxt.startActivity(i);
                    }

                })
                .show();

    }

    public boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(mcontaxt, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(mcontaxt, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(mcontaxt, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (result1 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

}
