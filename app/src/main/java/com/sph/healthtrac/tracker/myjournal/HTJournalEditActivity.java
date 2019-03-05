package com.sph.healthtrac.tracker.myjournal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTDatePicker;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
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

public class HTJournalEditActivity extends Activity {

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

    private static String journal;

    Toast toast;

    private static RelativeLayout relativeLayoutMyJournal;

    private static EditText editTextMyJournal;

    View datePicker;

    Calendar calendar;

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    private Thread myThread = null;

    private boolean hasNing;
    private boolean shareToNing;
    private String ningAlertTitle;
    private String ningAlertText;
    private boolean leftDateButtonClicked;
    private boolean rightDateButtonClicked;

    ProgressDialog progressDialog;

    private static InputMethodManager imm;

    private View mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_journal_edit);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        relativeLayoutMyJournal = (RelativeLayout) findViewById(R.id.myJournalContainer);
        editTextMyJournal = (EditText) findViewById(R.id.editTextMyJournal);

        Typeface typeMyJournal = Typeface.createFromAsset(this.getAssets(), "fonts/AvenirNext-Regular.ttf");

        editTextMyJournal.setTypeface(typeMyJournal);
        editTextMyJournal.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        editTextMyJournal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

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

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        configureActionBar(HTJournalEditActivity.this, "My Journal", "Cancel", "Done");

//        editTextMyJournal.setEnabled(true);
//        editTextMyJournal.setFocusableInTouchMode(true);
//        editTextMyJournal.setHint("");
//        editTextMyJournal.setGravity(Gravity.NO_GRAVITY);
//        imm.showSoftInput(editTextMyJournal, 0);

        // date picker
        datePicker = HTDatePicker.getDatePicker(HTJournalEditActivity.this);

        relativeLayoutMyJournal.addView(datePicker);

        params = (RelativeLayout.LayoutParams) datePicker.getLayoutParams();
        params.height = topBarHeight;
        params.setMargins(0, topBarHeight, 0, 0);  // left, top, right, bottom

        datePicker.setLayoutParams(params);

        if (login.equals("") || pw.equals("")) {

            Intent intent = new Intent(HTJournalEditActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            imm.hideSoftInputFromWindow(editTextMyJournal.getWindowToken(), 0);
            finish();

        } else {

            getMyJournal();
        }
    }

    private void showNingAlertDialog() {
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(this);
        AlertDialog alert;
        builder.setTitle(ningAlertTitle)
                .setMessage(ningAlertText)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        shareToNing = true;
                        updateMyJournal();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        shareToNing = false;
                        updateMyJournal();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void leftDateButtonClicked() {
        leftDateButtonClicked = true;

        HTGlobals.getInstance().plannerShouldRefresh = true;
        if (hasNing && !"".equals(editTextMyJournal.getText().toString())) {
            showNingAlertDialog();
        } else {
            updateMyJournal();
        }
    }

    private void rightDateButtonClicked() {
        rightDateButtonClicked = true;

        HTGlobals.getInstance().plannerShouldRefresh = true;
        if (hasNing && !"".equals(editTextMyJournal.getText().toString())) {
            showNingAlertDialog();
        } else {
            updateMyJournal();
        }
    }

    private void configureActionBar(Context context, String titleText, String leftButtonText, String rightButtonText) {

        // action bar
        RelativeLayout.LayoutParams params;

        relativeLayoutMyJournal.removeView(mActionBar);

        mActionBar = HTActionBar.getActionBar(context, titleText, leftButtonText, rightButtonText);

        relativeLayoutMyJournal.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // Cancel button
        mActionBar.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(editTextMyJournal.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });

        // Done button
        mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasNing && !"".equals(editTextMyJournal.getText().toString())) {
                    showNingAlertDialog();
                } else {
                    updateMyJournal();
                }
            }
        });

    }

    private void getMyJournal() {
        progressDialog.show();

        journal = "";

        leftDateButtonClicked = false;
        rightDateButtonClicked = false;

        final ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);
        final ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);

        if (passDate.getTime() > currentDate.getTime()) {

            // get the current date
            calendar = Calendar.getInstance();
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            currentMonth = calendar.get(Calendar.MONTH);
            currentYear = calendar.get(Calendar.YEAR);

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
                    nameValuePairs.add(new BasicNameValuePair("action", "get_journal"));
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
                    NodeList nodes = doc.getElementsByTagName("journal_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTJournalEditActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                String tempString;

                                if (XMLFunctions.tagExists(e2, "journal")) {
                                    journal = XMLFunctions.getValue(e2, "journal");

                                    journal = HTGlobals.getInstance().cleanStringAfterReceiving(journal);
                                }

                                if (XMLFunctions.tagExists(e2, "has_ning")) {
                                    tempString = XMLFunctions.getValue(e2, "has_ning");
                                    if ("1".equals(tempString))
                                        hasNing = true;
                                }

                                if (XMLFunctions.tagExists(e2, "ning_alert_title")) {
                                    ningAlertTitle = XMLFunctions.getValue(e2, "ning_alert_title");
                                }

                                if (XMLFunctions.tagExists(e2, "ning_alert_text")) {
                                    ningAlertText = XMLFunctions.getValue(e2, "ning_alert_text");
                                }

                                showMyJournal();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(HTJournalEditActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(HTJournalEditActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void updateMyJournal() {
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_journal"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    journal = editTextMyJournal.getText().toString();

                    journal = HTGlobals.getInstance().cleanStringBeforeSending(journal);

                    nameValuePairs.add(new BasicNameValuePair("journal", journal));
                    if (shareToNing)
                        nameValuePairs.add(new BasicNameValuePair("ning", "1"));

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

                                toast = HTToast.showToast(HTJournalEditActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

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

                                    getMyJournal();

                                } else if (rightDateButtonClicked) {

                                    // add one day
                                    passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, true);
                                    passDay = HTGlobals.getInstance().passDay;
                                    passMonth = HTGlobals.getInstance().passMonth;
                                    passYear = HTGlobals.getInstance().passYear;

                                    getMyJournal();

                                } else { // DONE
                                    imm.hideSoftInputFromWindow(editTextMyJournal.getWindowToken(), 0);
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

//                            toast = HTToast.showToast(HTJournalEditActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(HTJournalEditActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                        }
                    });

                }
            }
        };

        myThread.start();
    }

    private void showMyJournal() {

        editTextMyJournal.setText(journal);
        editTextMyJournal.setSelection(journal.length(), journal.length());
        editTextMyJournal.requestFocus();
        editTextMyJournal.postDelayed(new Runnable() {
            @Override
            public void run() {
                imm.showSoftInput(editTextMyJournal, 0);
            }
        }, 50);
    }
}