package com.example.guest999.firebasenotification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.guest999.firebasenotification.activities.DataSharing_forUser;
import com.example.guest999.firebasenotification.activities.Login;
import com.example.guest999.firebasenotification.activities.UserList;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.guest999.firebasenotification.activities.Login.hasLoggedIn;

/**
 * Created by Guest999 on 10/7/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	Intent resultIntent;
	private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
	Intent pushNotification;
	private NotificationUtils notificationUtils;

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		Log.e(TAG, "From: " + remoteMessage.getFrom());

		if (remoteMessage == null)
			return;

		// Check if message contains a notification payload.
		if (remoteMessage.getNotification() != null) {
			Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
			//		handleNotification(remoteMessage.getNotification().getBody());
			//changed by hiren
			try {
				JSONObject json = new JSONObject(remoteMessage.getData().toString());
				handleDataMessage(json);
			} catch (Exception e) {
				Log.e(TAG, "Exception: " + e.getMessage());
			}
		}

		// Check if message contains a data payload.
		if (remoteMessage.getData().size() > 0) {
			Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

			try {
				JSONObject json = new JSONObject(remoteMessage.getData().toString());
				handleDataMessage(json);
			} catch (Exception e) {
				Log.e(TAG, "Exception: " + e.getMessage());
			}
		}
	}


	private void handleDataMessage(JSONObject json) {
		Log.e(TAG, "push json: " + json.toString());


		try {
			JSONObject data = json.getJSONObject("data");
			String title = data.getString("title");
			String message = data.getString("message");
			boolean isBackground = data.getBoolean("is_background");
			String imageUrl = data.getString("image");
			String timestamp = data.getString("timestamp");
			JSONObject payload = data.getJSONObject("payload");

			Log.e(TAG, "title: " + title);
			Log.e(TAG, "message: " + message);
			Log.e(TAG, "isBackground: " + isBackground);
			Log.e(TAG, "payload: " + payload.toString());
			Log.e(TAG, "imageUrl: " + imageUrl);
			Log.e(TAG, "timestamp: " + timestamp);

			pushNotification = new Intent(Config.PUSH_NOTIFICATION);
			pushNotification.putExtra("message", message);
			LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

	/*		if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
				// app is in background, broadcast the push message

				if (hasLoggedIn) {

					if (Login.type.contains("admin")) {
						// app is in background, show the notification in notification tray
						resultIntent = new Intent(getApplicationContext(), UserList.class);
						resultIntent.putExtra("message", message);
					} else {
						// app is in background, show the notification in notification tray
						resultIntent = new Intent(getApplicationContext(), DataSharing_forUser.class);
						resultIntent.putExtra("message", message);
					}    // check for gallery attachment

					if (TextUtils.isEmpty(imageUrl)) {
						showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
					} else {
						// gallery is present, show notification with gallery
						showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
					}

				}

			} else if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
				// app is in forground, broadcast the push message
				if (hasLoggedIn) {
					if (Login.type.contains("admin")) {
						// app is in forground, show the notification in notification tray
						resultIntent = new Intent(getApplicationContext(), UserList.class);
						resultIntent.putExtra("message", message);
					} else {
						// app is in forground, show the notification in notification tray
						resultIntent = new Intent(getApplicationContext(), DataSharing_forUser.class);
						resultIntent.putExtra("message", message);
					}    // check for gallery attachment
					if (TextUtils.isEmpty(imageUrl)) {
						showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
					} else {
						// gallery is present, show notification with gallery
						showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
					}

				}

			}*/

			if (hasLoggedIn) {
				Log.e(TAG, "handleDataMessage:destroyed ");
				if (Login.type.contains("admin")) {
					// app is in Destroyed, show the notification in notification tray
					resultIntent = new Intent(getApplicationContext(), UserList.class);
					resultIntent.putExtra("message", message);
				} else {
					// app is in Destroyed, show the notification in notification tray
					resultIntent = new Intent(getApplicationContext(), DataSharing_forUser.class);
					resultIntent.putExtra("message", message);
				}    // check for gallery attachment
				if (TextUtils.isEmpty(imageUrl)) {
					showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
				} else {
					// gallery is present, show notification with gallery
					showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
				}
			}


			/*	// play notification sound
				notificationUtils = new NotificationUtils(getApplicationContext());
				notificationUtils.playNotificationSound();*/


		} catch (JSONException e) {
			Log.e(TAG, "Json Exception: " + e.getMessage());
		} catch (Exception e) {
			Log.e(TAG, "Exception: " + e.getMessage());
		}
	}

	/**
	 * Showing notification with text only
	 */
	private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
		notificationUtils = new NotificationUtils(context);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
	}

	/**
	 * Showing notification with text and gallery
	 */
	private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
		notificationUtils = new NotificationUtils(context);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
	}
}
