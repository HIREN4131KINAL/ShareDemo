package com.example.guest999.firebasenotification.utilis;

import android.Manifest;
import android.app.Activity;
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
    private Activity activity;

    public MarshmallowPermissions(Activity activity) {
        this.activity = activity;
    }

    public void requestpermissions() {
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    1);
        }
    }

/*

	public void showSettingsAlert() {
		AlertDialog alertDialog = new AlertDialog.Builder(mcontaxt).create();
		alertDialog.setTitle("Alert");
		alertDialog.setCancelable(false);
		alertDialog.setMessage("Permissions is necessary for to share or read/write files and pictures");
	 */
/*   alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
				new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });*//*

		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						AllowedManually();

					}
				});
		alertDialog.show();
	}
*/

    public void AllowedManually(View vg) {
        Snackbar.make(vg, "Without permission you are unable to share files and pictures.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Allow ME", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent i = new Intent();
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + activity.getPackageName()));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

                        activity.startActivity(i);
                    }

                })
                .show();

    }

    public boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
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
