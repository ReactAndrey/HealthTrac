package com.sph.healthtrac.tracker.colormyday;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTDatePicker;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.tracker.HTTrackerReminderActivity;
import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ColorMyDayActivity extends Activity {

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

    private static String overallColor;
    private static String eatColor;
    private static String moveColor;
    private static String sleepColor;
    private static String stressColor;

    private static ListView listViewColorMyDay;

    Toast toast;

    private static List<String> colorMyDayLabels = new ArrayList<>();
    private static List<String> colorMyDayValues = new ArrayList<>();
    private static List<String> colorMyDayReminder = new ArrayList<>();

    private static RelativeLayout relativeLayout;

    View datePicker;

    Calendar calendar;

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    private Thread myThread = null;

    private boolean leftDateButtonClicked;
    private boolean rightDateButtonClicked;
    private boolean reminderIconClicked;

    private static HTColorMyDayListView adapter;

    ProgressDialog progressDialog;

    public static ImageView colorReminderIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_my_day);

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);

        relativeLayout = (RelativeLayout) findViewById(R.id.colorMyDayContainer);

        // get the current date, if none was passed in
        calendar = Calendar.getInstance();
        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth =  calendar.get(Calendar.MONTH);
        currentYear =  calendar.get(Calendar.YEAR);

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
        View mActionBar = HTActionBar.getActionBar(this, "Color My Day", "Cancel", "Done");

        relativeLayout.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int)(dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // Cancel button
        mActionBar.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            setResult(1);
            finish();
            }
        });

        // Done button
        mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            updateColorMyDay();
            }
        });

        // date picker
        datePicker = HTDatePicker.getDatePicker(ColorMyDayActivity.this);

        relativeLayout.addView(datePicker);

        params = (RelativeLayout.LayoutParams) datePicker.getLayoutParams();
        params.height = topBarHeight;
        params.setMargins(0, topBarHeight, 0, 0);  // left, top, right, bottom

        datePicker.setLayoutParams(params);

        reminderIconClicked = false;

        if(login.equals("") || pw.equals("")) {

            Intent intent = new Intent(ColorMyDayActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {

            adapter = new HTColorMyDayListView(ColorMyDayActivity.this, colorMyDayLabels, colorMyDayValues, colorMyDayReminder);

            getColorMyDay();
        }
    }

    private void leftDateButtonClicked() {

        leftDateButtonClicked = true;

        updateColorMyDay();
    }

    private void rightDateButtonClicked() {

        rightDateButtonClicked = true;

        updateColorMyDay();
    }

    private void getColorMyDay() {

        progressDialog.show();

        colorMyDayLabels.clear();
        colorMyDayValues.clear();
        colorMyDayReminder.clear();

        overallColor = "";
        eatColor = "";
        moveColor = "";
        sleepColor = "";
        stressColor = "";

        leftDateButtonClicked = false;
        rightDateButtonClicked = false;
        reminderIconClicked = false;

        final ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);
        final ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);

        if (passDate.getTime() > currentDate.getTime()) {

            // get the current date
            calendar = Calendar.getInstance();
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            currentMonth =  calendar.get(Calendar.MONTH);
            currentYear =  calendar.get(Calendar.YEAR);

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            currentDate = calendar.getTime();

            passDate = currentDate;
            passDay = currentDayOfMonth;
            passMonth = currentMonth;
            passYear = currentYear;

            HTGlobals.getInstance().passDate = currentDate;
            HTGlobals.getInstance().passDay = currentDayOfMonth;
            HTGlobals.getInstance().passMonth = currentMonth;
            HTGlobals.getInstance().passYear = currentYear;
        }

        if (currentDate.equals(passDate)) {

            // Today
            TextView mTitleTextView = (TextView) datePicker.findViewById(R.id.date_text);
            mTitleTextView.setText("Today");

            leftArrow.setImageResource(R.drawable.ht_arrow_left_blue);
            rightArrow.setImageResource(R.drawable.ht_arrow_right_gray);

            leftArrow.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View v) {

                    leftDateButtonClicked();
                }
            });

            rightArrow.setOnClickListener(null);

        } else if (currentDate.equals(HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, false))) {

            // Yesterday
            TextView mTitleTextView = (TextView) datePicker.findViewById(R.id.date_text);
            mTitleTextView.setText("Yesterday");

            leftArrow.setImageResource(R.drawable.ht_arrow_left_blue);
            rightArrow.setImageResource(R.drawable.ht_arrow_right_blue);

            leftArrow.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View v) {

                    leftDateButtonClicked();
                }
            });

            rightArrow.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View v) {

                    rightDateButtonClicked();
                }
            });

        } else {

            // other past dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

            TextView mTitleTextView = (TextView) datePicker.findViewById(R.id.date_text);

            calendar = Calendar.getInstance();
            calendar.setTime(passDate);

            mTitleTextView.setText(dateFormat.format(calendar.getTime()));

            leftArrow.setImageResource(R.drawable.ht_arrow_left_blue);
            rightArrow.setImageResource(R.drawable.ht_arrow_right_blue);

            leftArrow.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View v) {

                    leftDateButtonClicked();
                }
            });

            rightArrow.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View v) {

                    rightDateButtonClicked();
                }
            });
        }

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_color_my_day"));
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
                    NodeList nodes = doc.getElementsByTagName("color_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(ColorMyDayActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    }
                    else {

                        final Element e2 = (Element)nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                colorMyDayReminder.add(XMLFunctions.getValue(e2, "color_my_day_reminder"));

                                overallColor = XMLFunctions.getValue(e2, "overall");
                                colorMyDayLabels.add("OVERALL");
                                colorMyDayValues.add(overallColor);

                                eatColor = XMLFunctions.getValue(e2, "eat");
                                colorMyDayLabels.add("EAT");
                                colorMyDayValues.add(eatColor);

                                moveColor = XMLFunctions.getValue(e2, "move");
                                colorMyDayLabels.add("MOVE");
                                colorMyDayValues.add(moveColor);

                                sleepColor = XMLFunctions.getValue(e2, "sleep");
                                colorMyDayLabels.add("SLEEP");
                                colorMyDayValues.add(sleepColor);

                                stressColor = XMLFunctions.getValue(e2, "stress");
                                colorMyDayLabels.add("STRESS");
                                colorMyDayValues.add(stressColor);

                                adapter.notifyDataSetChanged();

                                showColorMyDay();

                                /*
                                // activate the reminder icon
                                colorReminderIcon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        reminderIconClicked = true;

                                        updateColorMyDay();
                                    }
                                });
                                */
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(ColorMyDayActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(ColorMyDayActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    public void updateColorMyDay() {

        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_color_my_day"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    nameValuePairs.add(new BasicNameValuePair("overall", overallColor + ""));
                    nameValuePairs.add(new BasicNameValuePair("eat", eatColor + ""));
                    nameValuePairs.add(new BasicNameValuePair("move", moveColor + ""));
                    nameValuePairs.add(new BasicNameValuePair("sleep", sleepColor + ""));
                    nameValuePairs.add(new BasicNameValuePair("stress", stressColor + ""));

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

                                progressDialog.dismiss();

                                toast = HTToast.showToast(ColorMyDayActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    }
                    else {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                progressDialog.dismiss();

                                if (leftDateButtonClicked) {

                                    // subtract one day
                                    passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, -1, true);
                                    passDay = HTGlobals.getInstance().passDay;
                                    passMonth = HTGlobals.getInstance().passMonth;
                                    passYear = HTGlobals.getInstance().passYear;
                                    HTGlobals.getInstance().plannerShouldRefresh = true;
                                    getColorMyDay();

                                } else if (rightDateButtonClicked) {

                                    // add one day
                                    passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, true);
                                    passDay = HTGlobals.getInstance().passDay;
                                    passMonth = HTGlobals.getInstance().passMonth;
                                    passYear = HTGlobals.getInstance().passYear;
                                    HTGlobals.getInstance().plannerShouldRefresh = true;
                                    getColorMyDay();

                                } else if (reminderIconClicked) {

                                    Intent intent = new Intent(ColorMyDayActivity.this, HTTrackerReminderActivity.class);
                                    intent.putExtra("metric", "color");
                                    ColorMyDayActivity.this.startActivityForResult(intent, 1);

                                } else { // DONE

                                    setResult(1);
                                    finish();
                                }
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            progressDialog.dismiss();

//                            toast = HTToast.showToast(ColorMyDayActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(ColorMyDayActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                        }
                    });

                }
            }
        };

        myThread.start();
    }

    private static void showColorMyDay() {

        listViewColorMyDay = (ListView)relativeLayout.findViewById(R.id.listViewColorMyDay);
        listViewColorMyDay.setAdapter(adapter);
    }

    public static void colorButtonClicked(String whichButtonType, String whichColor) {

        if (whichButtonType.equals("OVERALL")) {

            if (whichColor.equals("GREEN")) {

                if (overallColor.equals("GREEN")) {

                    overallColor = "";

                } else {

                    overallColor = "GREEN";
                }
            } else if (whichColor.equals("YELLOW")) {

                if (overallColor.equals("YELLOW")) {

                    overallColor = "";

                } else {

                    overallColor = "YELLOW";
                }
            } else { // RED

                if (overallColor.equals("RED")) {

                    overallColor = "";

                } else {

                    overallColor = "RED";
                }
            }

        } else if (whichButtonType.equals("EAT")) {

            if (whichColor.equals("GREEN")) {

                if (eatColor.equals("ONTRACK")) {

                    eatColor = "";

                } else {

                    eatColor = "ONTRACK";
                }
            } else if (whichColor.equals("YELLOW")) {

                if (eatColor.equals("OK")) {

                    eatColor = "";

                } else {

                    eatColor = "OK";
                }
            } else { // RED

                if (eatColor.equals("OFF")) {

                    eatColor = "";

                } else {

                    eatColor = "OFF";
                }
            }

        } else if (whichButtonType.equals("MOVE")) {

            if (whichColor.equals("GREEN")) {

                if (moveColor.equals("ONTRACK")) {

                    moveColor = "";

                } else {

                    moveColor = "ONTRACK";
                }
            } else if (whichColor.equals("YELLOW")) {

                if (moveColor.equals("OK")) {

                    moveColor = "";

                } else {

                    moveColor = "OK";
                }
            } else { // RED

                if (moveColor.equals("OFF")) {

                    moveColor = "";

                } else {

                    moveColor = "OFF";
                }
            }

        } else if (whichButtonType.equals("SLEEP")) {

            if (whichColor.equals("GREEN")) {

                if (sleepColor.equals("GOOD")) {

                    sleepColor = "";

                } else {

                    sleepColor = "GOOD";
                }
            } else if (whichColor.equals("YELLOW")) {

                if (sleepColor.equals("FAIR")) {

                    sleepColor = "";

                } else {

                    sleepColor = "FAIR";
                }
            } else { // RED

                if (sleepColor.equals("POOR")) {

                    sleepColor = "";

                } else {

                    sleepColor = "POOR";
                }
            }

        } else { // STRESS

            if (whichColor.equals("GREEN")) {

                if (stressColor.equals("LOW")) {

                    stressColor = "";

                } else {

                    stressColor = "LOW";
                }
            } else if (whichColor.equals("YELLOW")) {

                if (stressColor.equals("MEDIUM")) {

                    stressColor = "";

                } else {

                    stressColor = "MEDIUM";
                }
            } else { // RED

                if (stressColor.equals("HIGH")) {

                    stressColor = "";

                } else {

                    stressColor = "HIGH";
                }
            }
        }

        colorMyDayLabels.clear();
        colorMyDayValues.clear();

        colorMyDayLabels.add("OVERALL");
        colorMyDayValues.add(overallColor);

        colorMyDayLabels.add("EAT");
        colorMyDayValues.add(eatColor);

        colorMyDayLabels.add("MOVE");
        colorMyDayValues.add(moveColor);

        colorMyDayLabels.add("SLEEP");
        colorMyDayValues.add(sleepColor);


        colorMyDayLabels.add("STRESS");
        colorMyDayValues.add(stressColor);

        adapter.notifyDataSetChanged();

        showColorMyDay();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

//        getColorMyDay();
    }
}