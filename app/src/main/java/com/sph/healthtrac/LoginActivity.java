package com.sph.healthtrac;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTTabBar;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.XMLFunctions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    private EditText editTextEmail;
    private EditText editTextPassword;

    private CheckBox checkBoxRememberMe;

    private WebView webView;

    private String appVersion;
    private String appDevice;
    private String appDeviceManufacturer;
    private String appDeviceBrand;
    private String appDeviceProduct;
    private String appDeviceModel;
    private String appDeviceOSVersion;

    private String customConnErrorTitle = "";
    private String customConnError = "";

    private String bannerURL = "";

    Toast toast;

    private boolean shouldHidePlanner = false;
    private boolean shouldShowBanners = false;

    private BroadcastReceiver mRegistrationBroadCastReceiver;
    private boolean isReceiverRegistered;
    private String gcmToken = null;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "LoginActivity";
    private final static String PROPERTY_REG_ID = "registration_id";

    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPreferences = getSharedPreferences("SPHPrefs", MODE_PRIVATE);

        mRegistrationBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                gcmToken = mSharedPreferences.getString(PROPERTY_REG_ID, null);
            }
        };

        bannerURL = getString(R.string.app_banner_url);

        Typeface typeRegular = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface typeMedium = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);

        webView = (WebView) findViewById(R.id.webView);

        if ("1".equals(mSharedPreferences.getString("banners", ""))) {
            shouldShowBanners = true;
        }

        if (shouldShowBanners) {
            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new MyWebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setBackgroundColor(Color.TRANSPARENT);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(bannerURL);
        } else {
            webView.setVisibility(View.GONE);
        }

        TextView loginLabel = (TextView) findViewById(R.id.loginLabel);
        TextView introTextLabel = (TextView) findViewById(R.id.introTextLabel);
        TextView rememberMeLabel = (TextView) findViewById(R.id.rememberMeLabel);

        final Button buttonLogin = (Button) findViewById(R.id.buttonLogin);

        loginLabel.setTypeface(typeRegular);
        loginLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        introTextLabel.setTypeface(typeRegular);
        introTextLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
        introTextLabel.setEllipsize(TextUtils.TruncateAt.END);

        rememberMeLabel.setTypeface(typeRegular);
        rememberMeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        editTextEmail.setTypeface(typeRegular);
        editTextEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        editTextPassword.setTypeface(typeRegular);
        editTextPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        buttonLogin.setTypeface(typeMedium);
        buttonLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);


        editTextEmail.setText(mSharedPreferences.getString("login", "").toUpperCase());
        editTextPassword.setText(mSharedPreferences.getString("password", ""));
        String practice = mSharedPreferences.getString("practice", "");

        if (practice.equals("")) {

            introTextLabel.setText(getString(R.string.welcome));

        } else {

            introTextLabel.setText(practice);
        }

        if (editTextEmail.getText().toString().equals("")) {

            editTextEmail.requestFocus();

        } else {

            editTextEmail.clearFocus();
        }

        if (mSharedPreferences.getBoolean("remember", false)) {

            checkBoxRememberMe.setChecked(true);
        }

        try	{

            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {

            //Log.v(tag, e.getMessage());
        }

        appDevice = android.os.Build.DEVICE;
        appDeviceManufacturer = android.os.Build.MANUFACTURER;
        appDeviceBrand = android.os.Build.BRAND;
        appDeviceProduct = android.os.Build.PRODUCT;
        appDeviceModel = android.os.Build.MODEL;
        appDeviceOSVersion = android.os.Build.VERSION.RELEASE;

        HTGlobals.getInstance().plannerShouldRefresh = true;

        // progress dialog
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(LoginActivity.this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

				/*
                if(gcmToken == null)
                    return;
				*/

                shouldHidePlanner = false;
                shouldShowBanners = false;
                progressDialog.show();

                new Thread() {

                    @Override
                    public void run() {

                        try {

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(buttonLogin.getWindowToken(), 0);

                            String login = editTextEmail.getText().toString().toUpperCase();
                            String pw = editTextPassword.getText().toString();

                            HttpPost post = new HttpPost(getString(R.string.web_service_url));

                            final String versionString;
                            final String swVersionString;
                            final String deviceString;

                            //versionString = "Android " + appVersion + " - OS " + appDeviceOSVersion + " - Device " + appDevice + " - " + appDeviceManufacturer +  " - " + appDeviceBrand + " - " + appDeviceProduct + " - " + appDeviceModel;
                            versionString = "Android " + appVersion;
                            swVersionString = appDeviceOSVersion;
                            deviceString = appDeviceModel;

                            List<NameValuePair> nameValuePairs = new ArrayList<>(7);
                            nameValuePairs.add(new BasicNameValuePair("action", "login"));
                            nameValuePairs.add(new BasicNameValuePair("userid", login));
                            nameValuePairs.add(new BasicNameValuePair("pw", pw));
                            nameValuePairs.add(new BasicNameValuePair("version", versionString));
                            nameValuePairs.add(new BasicNameValuePair("swVersion", swVersionString));
                            nameValuePairs.add(new BasicNameValuePair("device", deviceString));
                            nameValuePairs.add(new BasicNameValuePair("gcmid", gcmToken));

                            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                            HttpClient client = new DefaultHttpClient();
                            HttpResponse response = client.execute(post);
                            HttpEntity entity = response.getEntity();

                            String responseText = EntityUtils.toString(entity);

                            Document doc = XMLFunctions.XMLfromString(responseText);

                            NodeList error = doc.getElementsByTagName("error");
                            NodeList nodes = doc.getElementsByTagName("login_info");

                            Element e1 = (Element) error.item(0);
                            final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                            if (!errorMessage.equals("")) {

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        toast = HTToast.showToast(LoginActivity.this, errorMessage, Toast.LENGTH_LONG);
                                    }
                                });
                            }
                            else {

                                Element e2 = (Element) nodes.item(0);

                                String practice = XMLFunctions.getValue(e2, "practice");

                                SharedPreferences.Editor editor = mSharedPreferences.edit();

                                if (checkBoxRememberMe.isChecked()) {

                                    editor.putString("login", login);
                                    editor.putString("password", pw);
                                    editor.putString("practice", practice);
                                    editor.putBoolean("remember", true);

                                } else {

                                    editor.putString("login", "");
                                    editor.putString("password", "");
                                    editor.putString("practice", practice);
                                    editor.putBoolean("remember", false);

                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            editTextEmail.setText("");
                                            editTextPassword.setText("");
                                        }
                                    });
                                }

                                if(XMLFunctions.tagExists(e2, "show_banners")){
                                    String bannerString = XMLFunctions.getValue(e2, "show_banners");
                                    if("1".equals(bannerString)){
                                        shouldShowBanners = true;
                                    }
                                }

                                if (shouldShowBanners) {
                                    editor.putString("banners", "1");
                                } else {
                                    editor.putString("banners", "");
                                }

                                editor.apply();

                                HTGlobals.getInstance().passLogin = login;
                                HTGlobals.getInstance().passPw = pw;

                                if(XMLFunctions.tagExists(e2, "hide_planner")){
                                    String tempString = XMLFunctions.getValue(e2, "hide_planner");
                                    if("1".equals(tempString)){
                                        shouldHidePlanner = true;
                                    }
                                }

                                HTGlobals.getInstance().hidePlanner = shouldHidePlanner;

                                if(XMLFunctions.tagExists(e2, "custom_connection_error_title")){
                                    customConnErrorTitle= XMLFunctions.getValue(e2, "custom_connection_error_title");
                                }

                                if(XMLFunctions.tagExists(e2, "custom_connection_error")){
                                    customConnError= XMLFunctions.getValue(e2, "custom_connection_error");
                                }

                                if (!"".equals(customConnErrorTitle) && !"".equals(customConnError)) {
                                    HTGlobals.getInstance().customConnErrorTitle = customConnErrorTitle;
                                    HTGlobals.getInstance().customConnError = customConnError;
                                }

                                Intent intent = new Intent(LoginActivity.this, HTTabBar.class);
                                startActivity(intent);
                            }

                        } catch (Exception e) {

                            final String error = e.getMessage();

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
//                                    toast = HTToast.showToast(LoginActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(LoginActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        } finally {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                }.start();
            }
        });

        registerReceiver();

        if(checkPlayServices()){
            //Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver();

        if (shouldShowBanners) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadCastReceiver);
        isReceiverRegistered = false;
        super.onPause();

        if (shouldShowBanners) {
            webView.onPause();
        }
    }

    private void registerReceiver(){
        if(!isReceiverRegistered){
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadCastReceiver, new IntentFilter("registrationComplete"));
            isReceiverRegistered = true;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (bannerURL.equals(url)) {
                view.loadUrl(url);
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }

            return true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //Log.i(TAG, "This device is not supported.");
                //finish();
            }
            return false;
        }
        return true;
    }

}
