package com.sph.healthtrac.more.devices;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.sph.healthtrac.more.support.HTSupportActivity;

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

public class HTDevicesActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private static InputMethodManager imm;
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

    private List<String> devicesItems = new ArrayList<>();
    private List<String> devicesItemsLinked = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);

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
        View mActionBar = HTActionBar.getActionBar(this, "Devices", "leftArrow", ""); // actually, message compose button

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

            Intent intent = new Intent(HTDevicesActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {
            getDevicesItems();
        }
    }

    private void getDevicesItems() {
        devicesItems.clear();
        devicesItemsLinked.clear();

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

                                toast = HTToast.showToast(HTDevicesActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString;

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "device_name_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "device_name_" + i);
                                    devicesItems.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "device_linked_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "device_linked_" + i);
                                    devicesItemsLinked.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }
                                showDevices();
                            }
                        });
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            HTConnectErrDialog.showDilaog(HTDevicesActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showDevices() {
        LinearLayout linearLayoutDevices = (LinearLayout) mainContentLayout.findViewById(R.id.linearLayoutDevices);

        LayoutInflater inflater = getLayoutInflater();

        RelativeLayout.LayoutParams params;

        View rowView = null;

        Typeface typeDeviceLabels = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");

        ImageView imageViewDeviceIcon;

        TextView textViewDeviceLabel;

        linearLayoutDevices.removeAllViewsInLayout();

        for (int i = 0; i < devicesItems.size(); i++) {
            final String itemString = devicesItems.get(i);

            rowView = inflater.inflate(R.layout.more_row, null, true);

            textViewDeviceLabel = (TextView) rowView.findViewById(R.id.textViewMoreLabel);
            imageViewDeviceIcon = (ImageView) rowView.findViewById(R.id.imageViewMoreIcon);

            // textViewMoreLabel
            textViewDeviceLabel.setTypeface(typeDeviceLabels);
            textViewDeviceLabel.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
            textViewDeviceLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            textViewDeviceLabel.setText(itemString);

            // imageViewMoreIcon
            if("1".equals(devicesItemsLinked.get(i)))
                imageViewDeviceIcon.setImageResource(R.drawable.ht_devices_linked);
            else
                imageViewDeviceIcon.setImageResource(R.drawable.ht_devices_unlinked);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTDevicesActivity.this, HTDevicesEditActivity.class);
                    intent.putExtra("selectedDevice", itemString);
                    startActivityForResult(intent, 1);
                }
            });

            linearLayoutDevices.addView(rowView);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getDevicesItems();
    }
}
