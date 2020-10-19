package com.phonepe;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;

public class MainActivity extends ReactActivity {


  public static String PHONEPE_PACKAGE_NAME = "com.phonepe.app.preprod";
  long phonePeVersionCode = -1L;


  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "PhonePe";
  }

  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new ReactActivityDelegate(this, getMainComponentName()) {
      @Override
      protected Bundle getLaunchOptions() {

        PackageInfo packageInfo = null;
        try {
          packageInfo = getApplicationContext().getPackageManager().getPackageInfo(PHONEPE_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
          phonePeVersionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
          Log.e("TAG", String.format("failed to get package info for package name = {%s}, exception message = {%s}",
                  PHONEPE_PACKAGE_NAME, e.getMessage()));
        }

        Bundle initialProperties = new Bundle();
        initialProperties.putString("var_1","Im the first var" +phonePeVersionCode);
        return initialProperties;
      }
    };
  }
}
