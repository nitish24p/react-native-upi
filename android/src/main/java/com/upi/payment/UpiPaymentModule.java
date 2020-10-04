package com.upi.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class UpiPaymentModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private static final int REQUEST_CODE = 123;
    private final Gson gson = new Gson();
    private Callback successHandler;
    private Callback failureHandler;
    private String FAILURE = "FAILURE";

    public UpiPaymentModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "UpiPayment";
    }

    @ReactMethod
    public void intializePayment(ReadableMap config, Callback successHandler, Callback failureHandler) {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;


        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(config.getString("upiString")));
        Context currentContext = getCurrentActivity().getApplicationContext();
        if (intent != null) {
            Intent chooser = Intent.createChooser(intent, "Choose a upi app");
            if (isCallable(chooser, currentContext)) {
                getCurrentActivity().startActivityForResult(chooser, REQUEST_CODE);
            } else {
                final JSONObject responseData = new JSONObject();
                try {
                    responseData.put("message", "UPI supporting app not installed");
                    responseData.put("status", FAILURE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                this.failureHandler.invoke(gson.toJson(responseData));
            }
        }
    }

    private boolean isCallable(Intent intent, Context context) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        final JSONObject responseData = new JSONObject();
        try {
            if (data == null) {
                responseData.put("status", FAILURE);
                responseData.put("message", "No action taken");
                if(this.failureHandler!=null){
                    this.failureHandler.invoke(gson.toJson(responseData));
                }
                return;
            }

            if (requestCode == REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                if (data.getStringExtra("Status").trim().equals("SUCCESS")){
                    responseData.put("status", data.getStringExtra("Status"));
                    responseData.put("message", bundle.getString("response"));
                    this.successHandler.invoke(gson.toJson(responseData));

                } else {
                    responseData.put("status", data.getStringExtra("Status"));
                    responseData.put("message", bundle.getString("response"));
                    this.failureHandler.invoke(gson.toJson(responseData));
                }
            } else {
                responseData.put("message", "Request Code Mismatch");
                responseData.put("status", FAILURE);
                if(this.failureHandler!=null){
                    this.failureHandler.invoke(gson.toJson(responseData));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}
