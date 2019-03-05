package com.sph.healthtrac.more.myaccount;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTBrowserActivity;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.more.devices.HTDevicesEditActivity;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HTMyAccountChangePasswordActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private static InputMethodManager imm;
    private LinearLayout linearLayoutMyAccount;

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;
    Toast toast;
    ProgressDialog progressDialog;
    private Thread myThread = null;

    Calendar calendar;
    private Date passDate;
    private int passYear;
    private int passMonth;
    private int passDay;
    private int currentDayOfMonth;
    private int currentMonth;
    private int currentYear;
    private Date currentDate;
    private String login;
    private String pw;

    private TextView curPasswordLabel;
    private TextView newPasswordLabel;
    private TextView confirmPasswordLabel;
    private TextView showPasswordLabel;

    private EditText curPasswordEdit;
    private EditText newPasswordEdit;
    private EditText confirmPasswordEdit;

    private RelativeLayout showPasswordLayout;
    private ImageView showPasswordChek;
    private boolean isShowPassword = false;

    private Button changePasswordBtn;

    private String curPassword;
    private String newPassword;
    private String confirmPassword;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount_changepassword);
        mSharedPreferences = getSharedPreferences("SPHPrefs", MODE_PRIVATE);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        linearLayoutMyAccount = (LinearLayout) findViewById(R.id.linearLayoutMyAccount);

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);

        // get the current date, if none was passed in
        calendar = Calendar.getInstance();
        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        currentDate = calendar.getTime();

        if (HTGlobals.getInstance().passDay != 0) {

            passDate = HTGlobals.getInstance().passDate;
            passDay = HTGlobals.getInstance().passDay;
            passMonth = HTGlobals.getInstance().passMonth;
            passYear = HTGlobals.getInstance().passYear;

        } else {

            passDate = currentDate;
            passDay = currentDayOfMonth;
            passMonth = currentMonth;
            passYear = currentYear;

            HTGlobals.getInstance().passDate = currentDate;
            HTGlobals.getInstance().passDay = currentDayOfMonth;
            HTGlobals.getInstance().passMonth = currentMonth;
            HTGlobals.getInstance().passYear = currentYear;
        }

        login = HTGlobals.getInstance().passLogin;
        pw = HTGlobals.getInstance().passPw;

        RelativeLayout.LayoutParams params;

        // action bar
        View mActionBar = HTActionBar.getActionBar(this, "Change Password", "leftArrow", ""); // actually, message compose button

        mainContentLayout.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // left arrow button
        mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(HTMyAccountChangePasswordActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {
            showChangePassword();
        }
    }

    private void showChangePassword() {
        Typeface avenirNextRegular = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface avenirNextMedium = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");

        curPasswordLabel = (TextView) findViewById(R.id.curPasswordLabel);
        newPasswordLabel = (TextView) findViewById(R.id.newPasswordLabel);
        confirmPasswordLabel = (TextView) findViewById(R.id.confirmPasswordLabel);
        showPasswordLabel = (TextView) findViewById(R.id.showPasswordLabel);

        curPasswordEdit = (EditText) findViewById(R.id.curPasswordEdit);
        newPasswordEdit = (EditText) findViewById(R.id.newPasswordEdit);
        confirmPasswordEdit = (EditText) findViewById(R.id.confirmPasswordEdit);

        showPasswordLayout = (RelativeLayout) findViewById(R.id.showPasswordLayout);
        showPasswordChek = (ImageView) findViewById(R.id.showPasswordCheck);

        curPasswordLabel.setTypeface(avenirNextRegular);
        newPasswordLabel.setTypeface(avenirNextRegular);
        confirmPasswordLabel.setTypeface(avenirNextRegular);
        showPasswordLabel.setTypeface(avenirNextRegular);

        curPasswordEdit.setTypeface(avenirNextMedium);
        newPasswordEdit.setTypeface(avenirNextMedium);
        confirmPasswordEdit.setTypeface(avenirNextMedium);

        View.OnClickListener checkClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowPassword = !isShowPassword;
                if(isShowPassword) {
                    showPasswordChek.setImageResource(R.drawable.ht_check_on_green);
                    curPasswordEdit.setTransformationMethod(null);
                    newPasswordEdit.setTransformationMethod(null);
                    confirmPasswordEdit.setTransformationMethod(null);
                } else {
                    showPasswordChek.setImageResource(R.drawable.ht_check_off_green);
                    curPasswordEdit.setTransformationMethod(new PasswordTransformationMethod());
                    newPasswordEdit.setTransformationMethod(new PasswordTransformationMethod());
                    confirmPasswordEdit.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        };
        showPasswordLayout.setOnClickListener(checkClickListener);
        showPasswordChek.setOnClickListener(checkClickListener);

        changePasswordBtn = (Button) findViewById(R.id.changePasswordBtn);
        changePasswordBtn.setTypeface(avenirNextMedium);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(curPasswordEdit.getText().toString().trim())) {
                    HTToast.showToast(HTMyAccountChangePasswordActivity.this, "Please enter your current password", Toast.LENGTH_LONG);
                    curPasswordEdit.requestFocus();
                } else if ("".equals(newPasswordEdit.getText().toString().trim())) {
                    HTToast.showToast(HTMyAccountChangePasswordActivity.this, "Please enter your new password", Toast.LENGTH_LONG);
                    newPasswordEdit.requestFocus();
                } else if ("".equals(confirmPasswordEdit.getText().toString().trim())) {
                    HTToast.showToast(HTMyAccountChangePasswordActivity.this, "Please confirm your new password", Toast.LENGTH_LONG);
                    confirmPasswordEdit.requestFocus();
                } else if (!newPasswordEdit.getText().toString().trim().equals(confirmPasswordEdit.getText().toString().trim())) {
                    HTToast.showToast(HTMyAccountChangePasswordActivity.this, "New password fields do not match", Toast.LENGTH_LONG);
                    confirmPasswordEdit.requestFocus();
                } else {
                    changePassword();
                }
            }
        });
    }

    private void changePassword() {
        imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);

        progressDialog.show();

        curPassword = curPasswordEdit.getText().toString().trim();
        newPassword = newPasswordEdit.getText().toString().trim();
        confirmPassword = confirmPasswordEdit.getText().toString().trim();

        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "change_password"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("current_password", curPassword));
                    nameValuePairs.add(new BasicNameValuePair("new_password", newPassword));
                    nameValuePairs.add(new BasicNameValuePair("confirm_new_password", confirmPassword));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("my_account_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                String alertMessageContent = errorMessage.replace("\\n", "\n");
                                alertMessageContent = alertMessageContent.replace("*", "\u2022");
                                new AlertDialog.Builder(HTMyAccountChangePasswordActivity.this)
                                        .setTitle("Problem with Password")
                                        .setMessage(alertMessageContent)
                                        .setCancelable(true)
                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create().show();
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString = null;

                                if (XMLFunctions.tagExists(e2, "new_password")) {
                                    tempString = XMLFunctions.getValue(e2, "new_password");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if(tempString.equals(newPassword)) {
                                    htGlobals.passPw = newPassword;
                                    // there is saved password information
                                    if (!"".equals(mSharedPreferences.getString("login", ""))
                                            && !"".equals(mSharedPreferences.getString("password", ""))
                                            && !"".equals(mSharedPreferences.getString("practice", ""))) {
                                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                                        editor.putString("password", newPassword);
                                        editor.commit();
                                    }
                                    Dialog dialog = new AlertDialog.Builder(HTMyAccountChangePasswordActivity.this)
                                            .setTitle("Password Changed")
                                            .setMessage("Your password has been successfully changed")
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            }).create();
                                    dialog.show();
                                }
                            }
                        });
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            HTConnectErrDialog.showDilaog(HTMyAccountChangePasswordActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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
        };

        myThread.start();
    }
}
