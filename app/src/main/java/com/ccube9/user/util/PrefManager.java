package com.ccube9.user.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String APP_SETTINGS = "APP_SETTINGS";


    // properties
    private static  String IS_SIGN_UP = "sign_up";
    private static  String IS_LOGIN = "login";
    private static  String USER_ID = "user_id";
    private static  String USER_NAME = "user_name";
    private static  String USER_FIRST_NAME = "user_f_name";
    private static  String USER_LAST_NAME = "user_l_name";
   // private static  String USER_MOBILE = "user_mobile";
    private static  String USER_EMAIL = "user_email";
    private static String API_TOKEN="api_token";
    private static String CONTACT="contact";
    private static String PROF_PIC="prof_pic";
    static PrefManager prefManager;
    private PrefManager()
    {

    }

    public static PrefManager getInstance(Context context)
    {
        if (prefManager==null)
        {
            prefManager=new PrefManager();
        }

        return prefManager;

    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static boolean IsSignUp(Context context) {
        return getSharedPreferences(context).getBoolean(IS_SIGN_UP , false);
    }

    public static void setSignUp(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_SIGN_UP , newValue);
        editor.commit();
    }


    public static boolean IsLogin(Context context) {
        return getSharedPreferences(context).getBoolean(IS_LOGIN , false);
    }

    public static void setIsLogin(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_LOGIN , newValue);
        editor.commit();
    }


    public static void setUserId(Context context, String user_id) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_ID , user_id);
        editor.commit();
    }

    public static String getUserId(Context context) {
        return getSharedPreferences(context).getString(USER_ID , "");
    }

    public static void setApiToken(Context context, String api_token) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(API_TOKEN , api_token);
        editor.commit();
    }

    public static String getApiToken(Context context) {
        return getSharedPreferences(context).getString(API_TOKEN , "");
    }


    public static void setContact(Context context, String contact) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(CONTACT , contact);
        editor.commit();
    }

    public static String getContact(Context context) {
        return getSharedPreferences(context).getString(CONTACT , "");
    }

    public static void setProfPic(Context context, String prof_pic) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PROF_PIC , prof_pic);
        editor.commit();
    }

    public static String getProfPic(Context context) {
        return getSharedPreferences(context).getString(PROF_PIC , "");
    }

    public static void setUserName(Context context, String user_name) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_NAME , user_name);
        editor.commit();
    }

    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString(USER_NAME , "");
    }
    public static void setUserFirstName(Context context, String user_first_name) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_FIRST_NAME , user_first_name);
        editor.commit();
    }

    public static String getUserFirstName(Context context) {
        return getSharedPreferences(context).getString(USER_FIRST_NAME , "");
    }

    public static void setUserLastName(Context context, String user_last_name) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_LAST_NAME , user_last_name);
        editor.commit();
    }

    public static String getUserLastName(Context context) {
        return getSharedPreferences(context).getString(USER_LAST_NAME , "");
    }


    public static void setUserEmail(Context context, String user_email) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_EMAIL , user_email);
        editor.commit();
    }

    public static String getUserEmail(Context context) {
        return getSharedPreferences(context).getString(USER_EMAIL , "");
    }

//    public static void setUserMobile(Context context, String user_mobile) {
//        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
//        editor.putString(USER_MOBILE , user_mobile);
//        editor.commit();
//    }
//
//    public static String getUserMobile(Context context) {
//        return getSharedPreferences(context).getString(USER_MOBILE , "");
//    }

    public static void LogOut(Context context)
    {
         getSharedPreferences(context).edit().clear().commit();
    }
}
