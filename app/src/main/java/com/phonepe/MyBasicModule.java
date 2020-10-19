package com.phonepe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MyBasicModule extends ReactContextBaseJavaModule  implements  ActivityEventListener{

    public static String PHONEPE_PACKAGE_NAME = "com.phonepe.app.preprod";
    public static int PHONEPE_REQUEST = 200;
    private Callback successHandler;
    private Callback failureHandler;
    private String FAILURE = "FAILURE";
    private final Gson gson = new Gson();
    long phonePeVersionCode = -1L;


    ReactApplicationContext mReactApplicationContext = getReactApplicationContext();
    Activity currentActivity = getCurrentActivity();


    public MyBasicModule(@Nullable ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @NonNull
    @Override
    public String getName() {
        return "MyModule";
    }

    @ReactMethod
    public void navigateToNative(String redirectUrl, Callback successHandler, Callback failureHandler)
    {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;

        Log.d("PhonePe Logs","Hello World"+redirectUrl);

        if (redirectUrl.equalsIgnoreCase("undefined"))
        {
            this.failureHandler.invoke("Redirection url is empty");
            return;
        }

        if (doesPhonePeExist()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(redirectUrl));
            i.setPackage(PHONEPE_PACKAGE_NAME);
            getCurrentActivity().startActivityForResult(i, PHONEPE_REQUEST);
        }
        else {
            final JSONObject responseData = new JSONObject();
            try{
                responseData.put("message", "UPI supporting app not installed");
                responseData.put("status", FAILURE);
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
       //     this.failureHandler.invoke(gson.toJson(responseData));
        }
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }


    public boolean doesPhonePeExist()
    {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getCurrentActivity().getPackageManager().getPackageInfo(PHONEPE_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            phonePeVersionCode = packageInfo.versionCode;
            WritableMap params = Arguments.createMap();
            params = Arguments.createMap();
            params.putString("PhonePeVersionCode", ""+phonePeVersionCode);
            sendEvent(mReactApplicationContext, "EventReminder", params);

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", String.format("failed to get package info for package name = {%s}, exception message = {%s}",
                    PHONEPE_PACKAGE_NAME, e.getMessage()));
        }
        if (packageInfo == null) {
            return false;
        }
        if (phonePeVersionCode > 94033) {
            return true;
        }
        return false;
    }

    @ReactMethod
    public void getPhonePeVersionCode()
    {
        PackageInfo packageInfo = null;
        long phonePeVersionCode = -1L;
        try {
            packageInfo = getCurrentActivity().getPackageManager().getPackageInfo(PHONEPE_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            phonePeVersionCode = packageInfo.versionCode;
          //  callbackPhonepeVersion.invoke(phonePeVersionCode);
       //     this.successHandler.invoke(phonePeVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", String.format("failed to get package info for package name = {%s}, exception message = {%s}",
                    PHONEPE_PACKAGE_NAME, e.getMessage()));
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == PHONEPE_REQUEST) {
            if (resultCode == RESULT_OK) {
               this.successHandler.invoke("UI redirection Success full");
            } else if (resultCode == RESULT_CANCELED) {
                this.failureHandler.invoke("Payment failure: Back button pressed");
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}
