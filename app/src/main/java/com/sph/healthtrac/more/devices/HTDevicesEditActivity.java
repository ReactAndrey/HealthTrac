package com.sph.healthtrac.more.devices;

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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

public class HTDevicesEditActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private static InputMethodManager imm;
    private View mActionBar;
    Calendar calendar;
    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;
    Toast toast;
    ProgressDialog progressDialog;
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
    private Thread myThread = null;

    private Button updateButton;
    private String selectedDevice;
    private List<String> devicesActiveMetricItems = new ArrayList<>();
    private List<String> devicesActiveMetricUserItems = new ArrayList<>();
    private List<String> devicesActiveMetricEnabledItems = new ArrayList<>();
    private boolean selectedDeviceIsLinked;
    private String selectedDeviceLinkUnlinkURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_edit);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        updateButton = (Button) findViewById(R.id.updateBtn);

        selectedDevice = getIntent().getStringExtra("selectedDevice");

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
        mActionBar = HTActionBar.getActionBar(this, selectedDevice, "leftArrow", "Link"); // actually, message compose button

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

        mActionBar.findViewById(R.id.rightButton).setVisibility(View.GONE);

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(HTDevicesEditActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {
            getDeviceValues();
        }
    }

    private void getDeviceValues() {
        selectedDeviceIsLinked = false;

        devicesActiveMetricItems.clear();
        devicesActiveMetricUserItems.clear();
        devicesActiveMetricEnabledItems.clear();

        selectedDeviceLinkUnlinkURL = "";

        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_devices_values"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("selected_device", selectedDevice));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("device_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTDevicesEditActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString;

                                int i = 0;
                                while (XMLFunctions.tagExists(e2, "device_metric_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "device_metric_" + i);
                                    devicesActiveMetricItems.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                i = 0;
                                while (XMLFunctions.tagExists(e2, "device_user_metric_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "device_user_metric_" + i);
                                    devicesActiveMetricUserItems.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                i = 0;
                                while (XMLFunctions.tagExists(e2, "device_active_metric_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "device_active_metric_" + i);
                                    devicesActiveMetricEnabledItems.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                if (XMLFunctions.tagExists(e2, "device_linked")) {
                                    tempString = XMLFunctions.getValue(e2, "device_linked");
                                    if ("1".equals(tempString)) {
                                        selectedDeviceIsLinked = true;
                                    } else {
                                        selectedDeviceIsLinked = false;
                                    }
                                }

                                if (XMLFunctions.tagExists(e2, "device_link_unlink_url")) {
                                    tempString = XMLFunctions.getValue(e2, "device_link_unlink_url");
                                    selectedDeviceLinkUnlinkURL = htGlobals.cleanStringAfterReceiving(tempString);
                                }
                                showDeviceValues();
                            }
                        });
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            HTConnectErrDialog.showDilaog(HTDevicesEditActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void updateDeviceValues() {
        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_devices_values"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("selected_device", selectedDevice));
                    nameValuePairs.add(new BasicNameValuePair("device_user_metric_0", devicesActiveMetricUserItems.get(0)));
                    nameValuePairs.add(new BasicNameValuePair("device_user_metric_1", devicesActiveMetricUserItems.get(1)));
                    nameValuePairs.add(new BasicNameValuePair("device_user_metric_2", devicesActiveMetricUserItems.get(2)));
                    nameValuePairs.add(new BasicNameValuePair("device_user_metric_3", devicesActiveMetricUserItems.get(3)));
                    nameValuePairs.add(new BasicNameValuePair("device_user_metric_4", devicesActiveMetricUserItems.get(4)));

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

                                toast = HTToast.showToast(HTDevicesEditActivity.this, errorMessage, Toast.LENGTH_LONG);
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
                            HTConnectErrDialog.showDilaog(HTDevicesEditActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showDeviceValues() {

        final Button linkUnlinkButton = (Button) mActionBar.findViewById(R.id.rightButton);
        linkUnlinkButton.setVisibility(View.VISIBLE);
        if(selectedDeviceIsLinked && !"".equals(selectedDeviceLinkUnlinkURL))
        {
            linkUnlinkButton.setText("Unlink");
        } else if (!"".equals(selectedDeviceLinkUnlinkURL))
        {
            linkUnlinkButton.setText("Link");
        }
        linkUnlinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new AlertDialog.Builder(HTDevicesEditActivity.this)
                        .setTitle(selectedDevice)
                        .setMessage(String.format("%s %s?",linkUnlinkButton.getText(), selectedDevice))
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(HTDevicesEditActivity.this, HTBrowserActivity.class);
                                intent.putExtra("title", selectedDevice);
                                intent.putExtra("open_link", selectedDeviceLinkUnlinkURL);
                                intent.putExtra("hideBackButton", true);
                                startActivityForResult(intent, 1);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });

        LinearLayout linearLayoutDevices = (LinearLayout) mainContentLayout.findViewById(R.id.layoutDeviceValues);

        LayoutInflater inflater = getLayoutInflater();

        RelativeLayout.LayoutParams params;

        View rowView = null;

        Typeface typeDeviceLabels = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");

        TextView deviceActiveLabel;

        ImageView deviceActiveCheckIcon;

        linearLayoutDevices.removeAllViewsInLayout();

        for (int i = 0; i < devicesActiveMetricItems.size(); i++) {
            final int index = i;
            String itemString = devicesActiveMetricItems.get(i);

            rowView = inflater.inflate(R.layout.device_value_cell, null, true);

            deviceActiveLabel = (TextView) rowView.findViewById(R.id.deviceActiveLabelView);
            deviceActiveCheckIcon = (ImageView) rowView.findViewById(R.id.deviceActiveCheck);

            // textViewMoreLabel
            deviceActiveLabel.setTypeface(typeDeviceLabels);

            deviceActiveLabel.setText(itemString);

            if ("1".equals(devicesActiveMetricEnabledItems.get(i))) {
                final ImageView finalDeviceActiveCheckIcon = deviceActiveCheckIcon;
                View.OnClickListener onClickListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (selectedDeviceIsLinked) {
                            if ("1".equals(devicesActiveMetricUserItems.get(index))) {
                                finalDeviceActiveCheckIcon.setImageResource(R.drawable.ht_check_off_green);
                                devicesActiveMetricUserItems.set(index, "");
                            } else {
                                finalDeviceActiveCheckIcon.setImageResource(R.drawable.ht_check_on_green);
                                devicesActiveMetricUserItems.set(index, "1");
                            }
                            showDeviceValues();
                        } else {
                            linkUnlinkButton.performClick();
                        }
                    }
                };
                deviceActiveCheckIcon.setVisibility(View.VISIBLE);
                if ("1".equals(devicesActiveMetricUserItems.get(i))) {
                    deviceActiveCheckIcon.setImageResource(R.drawable.ht_check_on_green);
                    deviceActiveCheckIcon.setOnClickListener(onClickListener);
                } else {
                    deviceActiveCheckIcon.setImageResource(R.drawable.ht_check_off_green);
                }

                rowView.setOnClickListener(onClickListener);
            }

            linearLayoutDevices.addView(rowView);
        }

        Typeface avenirNextMedium = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        updateButton.setTypeface(avenirNextMedium);
        if (selectedDeviceIsLinked) {
            updateButton.setVisibility(View.VISIBLE);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateDeviceValues();
                }
            });
        } else {
            updateButton.setVisibility(View.GONE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getDeviceValues();
    }
}
