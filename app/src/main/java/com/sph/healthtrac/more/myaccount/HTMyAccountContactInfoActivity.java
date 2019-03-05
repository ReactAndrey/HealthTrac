package com.sph.healthtrac.more.myaccount;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HTMyAccountContactInfoActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private static InputMethodManager imm;
    private LinearLayout linearLayoutContactInfo;

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

    private TextView firstNameLabel;
    private TextView lastNameLabel;
    private TextView emailLabel;
    private TextView mobilePhoneLabel;
    private TextView mobileCarrierLabel;
    private TextView okToTextLabel;
    private TextView timezoneLabel;
    private TextView address1Label;
    private TextView address2Label;
    private TextView cityLabel;
    private TextView stateLabel;
    private TextView zipcodeLabel;
    private TextView yesLabel;
    private TextView noLabel;

    private EditText firstNameEdit;
    private EditText lastNameEdit;
    private EditText emailEdit;
    private EditText mobilePhoneEdit;
    private EditText address1Edit;
    private EditText address2Edit;
    private EditText cityEdit;
    private EditText zipcodeEdit;

    private EditText mobileCarrierEdit;
    private EditText timezoneEdit;
    private EditText stateEdit;

    private ImageView yesCheckView;
    private ImageView noCheckView;

    private Button updateBtn;

    private Dialog chooseCarrierDlg;
    private Dialog chooseStateDlg;
    private Dialog chooseTimeZoneDlg;

    private String selectedFirstName;
    private String selectedLastName;
    private String selectedEmail;
    private String selectedMobilePhone;
    private String selectedMobilePhoneCarrier;
    private String selectedOKToText;
    private String selectedTimeZone;
    private String selectedAddressOne;
    private String selectedAddressTwo;
    private String selectedCity;
    private String selectedState;
    private String selectedZip;

    private int selectedCarrierIndex;
    private int selectedTimeZoneIndex;
    private int selectedStateIndex;

    private List<String> selectedMobilePhoneCarrierPickerValues = new ArrayList<>();
    private List<String> selectedMobilePhoneCarrierPickerLabelValues = new ArrayList<>();
    private List<String> selectedTimeZonePickerValues = new ArrayList<>();
    private List<String> selectedTimeZonePickerLabelValues = new ArrayList<>();
    private List<String> selectedStatePickerValues = new ArrayList<>();
    private List<String> selectedStatePickerLabelValues = new ArrayList<>();
    private String[] selectedMobileCarrierValues;
    private String[] selectedTimeZoneValues;
    private String[] selectedStateValues;

    private Typeface avenirNextReqularFont;
    private Typeface avenirNextMediumFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount_contactinfo);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        linearLayoutContactInfo = (LinearLayout) findViewById(R.id.linearLayoutContactInfo);

        avenirNextReqularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");

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
        View mActionBar = HTActionBar.getActionBar(this, "Contact Information", "leftArrow", ""); // actually, message compose button

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

            Intent intent = new Intent(HTMyAccountContactInfoActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {
            firstNameLabel = (TextView) findViewById(R.id.firstNameLabel);
            lastNameLabel = (TextView) findViewById(R.id.lastNameLabel);
            emailLabel = (TextView) findViewById(R.id.emailLabel);
            mobilePhoneLabel = (TextView) findViewById(R.id.mobilePhoneLabel);
            mobileCarrierLabel = (TextView) findViewById(R.id.mobileCarrierLabel);
            okToTextLabel = (TextView) findViewById(R.id.okToTextLabel);
            timezoneLabel = (TextView) findViewById(R.id.timezoneLabel);
            address1Label = (TextView) findViewById(R.id.address1Label);
            address2Label = (TextView) findViewById(R.id.address2Label);
            cityLabel = (TextView) findViewById(R.id.cityLabel);
            stateLabel = (TextView) findViewById(R.id.stateLabel);
            zipcodeLabel = (TextView) findViewById(R.id.zipcodeLabel);
            yesLabel = (TextView) findViewById(R.id.yesLabel);
            noLabel = (TextView) findViewById(R.id.noLabel);

            firstNameLabel.setTypeface(avenirNextReqularFont);
            lastNameLabel.setTypeface(avenirNextReqularFont);
            emailLabel.setTypeface(avenirNextReqularFont);
            mobilePhoneLabel.setTypeface(avenirNextReqularFont);
            mobileCarrierLabel.setTypeface(avenirNextReqularFont);
            okToTextLabel.setTypeface(avenirNextReqularFont);
            timezoneLabel.setTypeface(avenirNextReqularFont);
            address1Label.setTypeface(avenirNextReqularFont);
            address2Label.setTypeface(avenirNextReqularFont);
            cityLabel.setTypeface(avenirNextReqularFont);
            stateLabel.setTypeface(avenirNextReqularFont);
            zipcodeLabel.setTypeface(avenirNextReqularFont);
            yesLabel.setTypeface(avenirNextReqularFont);
            noLabel.setTypeface(avenirNextReqularFont);

            firstNameEdit = (EditText) findViewById(R.id.firstNameEdit);
            lastNameEdit = (EditText) findViewById(R.id.lastNameEdit);
            emailEdit = (EditText) findViewById(R.id.emailEdit);
            mobilePhoneEdit = (EditText) findViewById(R.id.mobilePhoneEdit);
            address1Edit = (EditText) findViewById(R.id.address1Edit);
            address2Edit = (EditText) findViewById(R.id.address2Edit);
            cityEdit = (EditText) findViewById(R.id.cityEdit);
            zipcodeEdit = (EditText) findViewById(R.id.zipcodeEdit);
            mobileCarrierEdit = (EditText) findViewById(R.id.mobileCarrierEdit);
            timezoneEdit = (EditText) findViewById(R.id.timezoneEdit);
            stateEdit = (EditText) findViewById(R.id.stateEdit);
            mobileCarrierEdit.setKeyListener(null);
            timezoneEdit.setKeyListener(null);
            stateEdit.setKeyListener(null);

            firstNameEdit.setTypeface(avenirNextMediumFont);
            lastNameEdit.setTypeface(avenirNextMediumFont);
            emailEdit.setTypeface(avenirNextMediumFont);
            mobilePhoneEdit.setTypeface(avenirNextMediumFont);
            address1Edit.setTypeface(avenirNextMediumFont);
            address2Edit.setTypeface(avenirNextMediumFont);
            cityEdit.setTypeface(avenirNextMediumFont);
            zipcodeEdit.setTypeface(avenirNextMediumFont);
            mobileCarrierEdit.setTypeface(avenirNextMediumFont);
            timezoneEdit.setTypeface(avenirNextMediumFont);
            stateEdit.setTypeface(avenirNextMediumFont);

            yesCheckView = (ImageView) findViewById(R.id.yesCheck);
            noCheckView = (ImageView) findViewById(R.id.noCheck);

            updateBtn = (Button) findViewById(R.id.updateBtn);
            updateBtn.setTypeface(avenirNextMediumFont);
            getContactInfoValues();
        }
    }

    private void getContactInfoValues() {
        selectedFirstName = "";
        selectedLastName = "";
        selectedEmail = "";
        selectedMobilePhone = "";
        selectedMobilePhoneCarrier = "";
        selectedOKToText = "";
        selectedTimeZone = "";
        selectedAddressOne = "";
        selectedAddressTwo = "";

        selectedMobilePhoneCarrierPickerValues.clear();
        selectedMobilePhoneCarrierPickerLabelValues.clear();
        selectedTimeZonePickerValues.clear();
        selectedTimeZonePickerLabelValues.clear();
        selectedStatePickerValues.clear();
        selectedStatePickerLabelValues.clear();

        selectedMobilePhoneCarrierPickerValues.add("");
        selectedMobilePhoneCarrierPickerLabelValues.add("None");
        selectedTimeZonePickerValues.add("");
        selectedTimeZonePickerLabelValues.add("None");
        selectedStatePickerValues.add("");
        selectedStatePickerLabelValues.add("None");

        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_personal_info_values"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("personal_info");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTMyAccountContactInfoActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString;

                                if (XMLFunctions.tagExists(e2, "client_first_name")) {
                                    tempString = XMLFunctions.getValue(e2, "client_first_name");
                                    selectedFirstName = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_last_name")) {
                                    tempString = XMLFunctions.getValue(e2, "client_last_name");
                                    selectedLastName = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_email")) {
                                    tempString = XMLFunctions.getValue(e2, "client_email");
                                    selectedEmail = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_mobile_phone")) {
                                    tempString = XMLFunctions.getValue(e2, "client_mobile_phone");
                                    selectedMobilePhone = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_mobile_phone")) {
                                    tempString = XMLFunctions.getValue(e2, "client_mobile_phone");
                                    selectedMobilePhone = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_mobile_phone_carrier")) {
                                    tempString = XMLFunctions.getValue(e2, "client_mobile_phone_carrier");
                                    selectedMobilePhoneCarrier = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_ok_to_text")) {
                                    tempString = XMLFunctions.getValue(e2, "client_ok_to_text");
                                    selectedOKToText = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_time_zone")) {
                                    tempString = XMLFunctions.getValue(e2, "client_time_zone");
                                    selectedTimeZone = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_address_one")) {
                                    tempString = XMLFunctions.getValue(e2, "client_address_one");
                                    selectedAddressOne = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_address_two")) {
                                    tempString = XMLFunctions.getValue(e2, "client_address_two");
                                    selectedAddressTwo = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_city")) {
                                    tempString = XMLFunctions.getValue(e2, "client_city");
                                    selectedCity = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_state")) {
                                    tempString = XMLFunctions.getValue(e2, "client_state");
                                    selectedState = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_zip")) {
                                    tempString = XMLFunctions.getValue(e2, "client_zip");
                                    selectedZip = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "mobile_carrier_values_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "mobile_carrier_values_" + i);
                                    selectedMobilePhoneCarrierPickerValues.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "mobile_carrier_label_values_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "mobile_carrier_label_values_" + i);
                                    selectedMobilePhoneCarrierPickerLabelValues.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "time_zone_values_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "time_zone_values_" + i);
                                    selectedTimeZonePickerValues.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "time_zone_label_values_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "time_zone_label_values_" + i);
                                    selectedTimeZonePickerLabelValues.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "state_values_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "state_values_" + i);
                                    selectedStatePickerValues.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "state_label_values_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "state_label_values_" + i);
                                    selectedStatePickerLabelValues.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }
                                showContactInfo();
                            }
                        });
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            HTConnectErrDialog.showDilaog(HTMyAccountContactInfoActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showContactInfo() {

        firstNameEdit.setText(selectedFirstName);
        lastNameEdit.setText(selectedLastName);
        emailEdit.setText(selectedEmail);
        mobilePhoneEdit.setText(selectedMobilePhone);
        address1Edit.setText(selectedAddressOne);
        address2Edit.setText(selectedAddressTwo);
        cityEdit.setText(selectedCity);
        zipcodeEdit.setText(selectedZip);

        if("Y".equals(selectedOKToText)) {
            yesCheckView.setImageResource(R.drawable.ht_color_button_green_on);
            noCheckView.setImageResource(R.drawable.ht_color_button_green_off);
        } else {
            yesCheckView.setImageResource(R.drawable.ht_color_button_green_off);
            noCheckView.setImageResource(R.drawable.ht_color_button_green_on);
        }

        yesCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesCheckView.setImageResource(R.drawable.ht_color_button_green_on);
                noCheckView.setImageResource(R.drawable.ht_color_button_green_off);
                selectedOKToText = "Y";
            }
        });

        noCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesCheckView.setImageResource(R.drawable.ht_color_button_green_off);
                noCheckView.setImageResource(R.drawable.ht_color_button_green_on);
                selectedOKToText = "";
            }
        });

        selectedCarrierIndex = selectedMobilePhoneCarrierPickerValues.indexOf(selectedMobilePhoneCarrier);
        selectedTimeZoneIndex = selectedTimeZonePickerValues.indexOf(selectedTimeZone);
        selectedStateIndex = selectedStatePickerValues.indexOf(selectedState);

        if (selectedCarrierIndex == 0)
            mobileCarrierEdit.setText("");
        else
            mobileCarrierEdit.setText(selectedMobilePhoneCarrierPickerLabelValues.get(selectedCarrierIndex));

        if (selectedTimeZoneIndex == 0)
            timezoneEdit.setText("");
        else
            timezoneEdit.setText(selectedTimeZonePickerLabelValues.get(selectedTimeZoneIndex));

        if (selectedStateIndex == 0)
            stateEdit.setText("");
        else
            stateEdit.setText(selectedStatePickerLabelValues.get(selectedStateIndex));

        selectedMobileCarrierValues = new String[selectedMobilePhoneCarrierPickerLabelValues.size()];
        for (int i = 0 ; i < selectedMobilePhoneCarrierPickerLabelValues.size(); i++) {
            selectedMobileCarrierValues[i] = selectedMobilePhoneCarrierPickerLabelValues.get(i);
        }
        chooseCarrierDlg = new AlertDialog.Builder(HTMyAccountContactInfoActivity.this)
                .setTitle("Mobile Carrier")
                .setSingleChoiceItems(selectedMobileCarrierValues, selectedCarrierIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)
                            mobileCarrierEdit.setText("");
                        else
                            mobileCarrierEdit.setText(selectedMobilePhoneCarrierPickerLabelValues.get(which));
                        selectedCarrierIndex = which;
                        dialog.dismiss();

                    }
                }).create();
        mobileCarrierEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                chooseCarrierDlg.show();
            }
        });

        selectedTimeZoneValues = new String[selectedTimeZonePickerLabelValues.size()];
        for (int i = 0 ; i < selectedTimeZonePickerLabelValues.size(); i++) {
            selectedTimeZoneValues[i] = selectedTimeZonePickerLabelValues.get(i);
        }
        chooseTimeZoneDlg = new AlertDialog.Builder(HTMyAccountContactInfoActivity.this)
                .setTitle("Time Zone")
                .setSingleChoiceItems(selectedTimeZoneValues, selectedTimeZoneIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)
                            timezoneEdit.setText("");
                        else
                            timezoneEdit.setText(selectedTimeZonePickerLabelValues.get(which));
                        selectedTimeZoneIndex = which;
                        dialog.dismiss();

                    }
                }).create();
        timezoneEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                chooseTimeZoneDlg.show();
            }
        });

        selectedStateValues = new String[selectedStatePickerLabelValues.size()];
        for (int i = 0 ; i < selectedStatePickerLabelValues.size(); i++) {
            selectedStateValues[i] = selectedStatePickerLabelValues.get(i);
        }
        chooseStateDlg = new AlertDialog.Builder(HTMyAccountContactInfoActivity.this)
                .setTitle("State/Province")
                .setSingleChoiceItems(selectedStateValues, selectedStateIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)
                            stateEdit.setText("");
                        else
                            stateEdit.setText(selectedStatePickerLabelValues.get(which));
                        selectedStateIndex = which;
                        dialog.dismiss();

                    }
                }).create();
        stateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                chooseStateDlg.show();
            }
        });

        linearLayoutContactInfo.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.VISIBLE);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContactInfoValues();
            }
        });
    }

    private void updateContactInfoValues() {
        imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);

        HTGlobals htGlobals = HTGlobals.getInstance();
        selectedMobilePhoneCarrier = htGlobals.cleanStringBeforeSending(selectedMobilePhoneCarrierPickerValues.get(selectedCarrierIndex).replace("|", "***pipe***"));
        selectedTimeZone = selectedTimeZonePickerValues.get(selectedTimeZoneIndex);
        selectedState = htGlobals.cleanStringBeforeSending(selectedStatePickerValues.get(selectedStateIndex));
        selectedFirstName = htGlobals.cleanStringBeforeSending(firstNameEdit.getText().toString().trim());
        selectedLastName = htGlobals.cleanStringBeforeSending(lastNameEdit.getText().toString().trim());
        selectedEmail = htGlobals.cleanStringBeforeSending(emailEdit.getText().toString().trim());
        selectedMobilePhone = htGlobals.cleanStringBeforeSending(mobilePhoneEdit.getText().toString().trim());
        selectedAddressOne = htGlobals.cleanStringBeforeSending(address1Edit.getText().toString().trim());
        selectedAddressTwo = htGlobals.cleanStringBeforeSending(address2Edit.getText().toString().trim());
        selectedCity = htGlobals.cleanStringBeforeSending(cityEdit.getText().toString().trim());
        selectedZip = htGlobals.cleanStringBeforeSending(zipcodeEdit.getText().toString().trim());

        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_contact_info_values"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("client_first_name", selectedFirstName));
                    nameValuePairs.add(new BasicNameValuePair("client_last_name", selectedLastName));
                    nameValuePairs.add(new BasicNameValuePair("client_email", selectedEmail));
                    nameValuePairs.add(new BasicNameValuePair("client_mobile_phone", selectedMobilePhone));
                    nameValuePairs.add(new BasicNameValuePair("client_mobile_phone_carrier", selectedMobilePhoneCarrier));
                    nameValuePairs.add(new BasicNameValuePair("client_ok_to_text", selectedOKToText));
                    nameValuePairs.add(new BasicNameValuePair("client_time_zone", selectedTimeZone));
                    nameValuePairs.add(new BasicNameValuePair("client_address_one", selectedAddressOne));
                    nameValuePairs.add(new BasicNameValuePair("client_address_two", selectedAddressTwo));
                    nameValuePairs.add(new BasicNameValuePair("client_city", selectedCity));
                    nameValuePairs.add(new BasicNameValuePair("client_state", selectedState));
                    nameValuePairs.add(new BasicNameValuePair("client_zip", selectedZip));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTMyAccountContactInfoActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        finish();
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            HTConnectErrDialog.showDilaog(HTMyAccountContactInfoActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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
