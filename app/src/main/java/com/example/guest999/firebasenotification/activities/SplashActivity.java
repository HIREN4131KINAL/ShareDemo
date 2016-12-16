package com.example.guest999.firebasenotification.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.guest999.firebasenotification.R;
import com.example.guest999.firebasenotification.utilis.MarshmallowPermissions;

/**
 * Created by HIREN AMALIYAR on 30-11-2016.
 */

public class SplashActivity extends AppCompatActivity {
	MarshmallowPermissions marsh;
	View parentLayout;
	private static final int REQUEST_PERMISSION = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		marsh = new MarshmallowPermissions(SplashActivity.this);
		parentLayout = findViewById(android.R.id.content);

		if (!marsh.checkIfAlreadyhavePermission()) {
			marsh.requestpermissions();
		} else {
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
			finish();
		}


	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_PERMISSION) {
			// for each permission check if the user grantet/denied them
			// you may want to group the rationale in a single dialog,
			// this is just an example
			for (int i = 0, len = permissions.length; i < len; i++) {
				final String permission = permissions[i];
				if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
					boolean showRationale = shouldShowRequestPermissionRationale(permission);
					if (!showRationale) {
						// user denied flagging NEVER ASK AGAIN
						// you can either enable some fall back,
						// disable features of your app
						// or open another dialog explaining
						// again the permission and directing to
						// the app setting
						marsh.AllowedManually(parentLayout);
					} else if (Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission) || Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
						// showRationale(permission, R.string.permission_denied);
						// user denied WITHOUT never ask again
						// this is a good place to explain the user
						// why you need the permission and ask if he want
						// to accept it (the rationale)
						marsh.AllowedManually(parentLayout);
					}

				} else {
					if (marsh.checkIfAlreadyhavePermission()) {
						Intent intent = new Intent(this, Login.class);
						startActivity(intent);
						finish();
					} else {
						marsh.AllowedManually(parentLayout);
					}
				}
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (marsh.checkIfAlreadyhavePermission()) {
			Intent intent = new Intent(this, Login.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (!marsh.checkIfAlreadyhavePermission()) {
			marsh.requestpermissions();
		}
	}

}
