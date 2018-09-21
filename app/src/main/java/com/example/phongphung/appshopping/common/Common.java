package com.example.phongphung.appshopping.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;


import com.example.phongphung.appshopping.model.User;
import com.example.phongphung.appshopping.remote.IGoogleService;
import com.example.phongphung.appshopping.remote.RetrofitGoogleAPI;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Common {

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PASSWORD_KEY = "Password";
    public static final String INTENT_FOOD_ID = "FoodId";

    public static User currentUser;

}
