package com.example.guest999.firebasenotification;

/**
 * Created by Guest999 on 10/7/2016.
 */

public class Config {
    /**
     * FOR NOTIFICATION
     */
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String SHARED_PREF = "ah_firebase";
    /**
     * FOR OTP
     */
    /*public static final String REGISTER_URL = "http://10.0.2.2/firebase/register.php";
    public static final String LOGIN_URL = "http://10.0.2.2/firebase/login.php";
    public static final String CONFIRM_URL = "http://10.0.2.2/firebase/confirm.php";*/
    //URLs to register.php and confirm.php file http://laxmisecurity.com/android/firebase/

    public static final String REGISTER_URL = "http://laxmisecurity.com/android/firebase/register.php";
    public static final String LOGIN_URL = "http://laxmisecurity.com/android/firebase/login.php";
    public static final String CHECK_USERTYPE = "http://laxmisecurity.com/android/firebase/usertype.php";
    public static final String CONFIRM_URL = "http://laxmisecurity.com/android/firebase/confirm.php";
    public static final String CHECKING_URL = "http://laxmisecurity.com/android/checking.php";
    //for api sending and loading data
    public static final String LOAD_USERDATA = "http://laxmisecurity.com/android/firebase/fileshare.php";
    public static final String SEND_USERDATA = "http://laxmisecurity.com/android/firebase/filesend.php";
    public static final String CHANGE_PASSWORD = "http://laxmisecurity.com/android/firebase/changepass.php";

    public static final String USER_SCREEN = "http://laxmisecurity.com/android/firebase/user_screen.php";
    public static final String FILESEND_USER = "http://laxmisecurity.com/android/firebase/filesend_user.php";
    public static final String COTACTSEND_ADMIN = "http://laxmisecurity.com/android/firebase/contact_send.php";
    public static final String COTACTSEND_USER = "http://laxmisecurity.com/android/firebase/contact_send_user.php";

    public static final String INTERNAL_IMAGE_PATH_URI = "http://www.laxmisecurity.com/android/uploads/";
    //Keys to send username, password, phone and otp
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_TYPE = "type";
    public static final String KEY_A_PHONE = "aphone";
    public static final String KEY_OTP = "otpcode";
    public static final String KEY_FIREBASE_ID = "firebaseid";
    //JSON Tag from response from server
    public static final String TAG_RESPONSE = "ErrorMessage";
    public static final String TAG_Active = "Active";
    public static final String TAG_Inactive = "Inactive";
    public static final String TAG_CHECK = "success";
    //For Loading userlist
    public static final String TAG_DATA = "data";
    //for storing and sending current date and time
    public static final String CURRENT_DATE = "ad_date";
    public static final String CURRENT_TIME = "ad_time";
    // id to handle the notification in the notification tray
    static final int NOTIFICATION_ID = 100;
    static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    //for storing phone from url and device
    public static String PhoneFromURl, PhoneFromDevice;

}
