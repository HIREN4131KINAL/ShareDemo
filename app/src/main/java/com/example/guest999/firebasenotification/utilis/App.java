package com.example.guest999.firebasenotification.utilis;

import android.app.Application;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

/**
 * Created by HIREN AMALIYAR on 01-12-2016.
 */

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// Don't do this! This is just so cold launches take some time
		SystemClock.sleep(TimeUnit.SECONDS.toMillis(3));
	}
}