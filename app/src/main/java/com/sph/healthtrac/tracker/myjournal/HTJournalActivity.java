package com.sph.healthtrac.tracker.myjournal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTDatePicker;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HTJournalActivity extends Activity {

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

    private List<String> journalDateList = new ArrayList<>();
    private List<String> journalColorList = new ArrayList<>();
    private List<String> journalTextList = new ArrayList<>();

    Toast toast;

    private static RelativeLayout relativeLayoutMyJournal;
    private ScrollView scrollViewMyJournal;
    private LinearLayout linearLayoutMyJournal;

    View datePicker;

    Calendar calendar;

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    private Thread myThread = null;

    ProgressDialog progressDialog;

    LayoutInflater mInflater;
    private static InputMethodManager imm;

    private View mActionBar;

    Typeface dateFont;
    Typeface journalTextFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_journal);
        mInflater = LayoutInflater.from(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dateFont = Typeface.createFromAsset(this.getAssets(), "fonts/AvenirNext-DemiBold.ttf");
        journalTextFont = Typeface.createFromAsset(this.getAssets(), "fonts/AvenirNext-Regular.ttf");

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        relativeLayoutMyJournal = (RelativeLayout) findViewById(R.id.myJournalContainer);
        scrollViewMyJournal = (ScrollView) findViewById(R.id.scrollViewMyJournal);
        linearLayoutMyJournal = (LinearLayout) findViewById(R.id.linearLayoutMyJournal);

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

        configureActionBar(HTJournalActivity.this, "My Journal", "leftArrow", "Edit");

        // date picker
        datePicker = HTDatePicker.getDatePicker(HTJournalActivity.this);

        relativeLayoutMyJournal.addView(datePicker);

        params = (RelativeLayout.LayoutParams) datePicker.getLayoutParams();
        params.height = topBarHeight;
        params.setMargins(0, topBarHeight, 0, 0);  // left, top, right, bottom

        datePicker.setLayoutParams(params);

        if (login.equals("") || pw.equals("")) {

            Intent intent = new Intent(HTJournalActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {

            getMyJournal();
        }
    }

    private void leftDateButtonClicked() {

        ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);

        imm.hideSoftInputFromWindow(leftArrow.getWindowToken(), 0);


        // subtract one day
        passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, -1, true);
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;
        HTGlobals.getInstance().plannerShouldRefresh = true;
        getMyJournal();
    }

    private void rightDateButtonClicked() {

        ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);

        imm.hideSoftInputFromWindow(rightArrow.getWindowToken(), 0);

        // add one day
        passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, true);
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;
        HTGlobals.getInstance().plannerShouldRefresh = true;
        getMyJournal();
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


        mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });


        mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HTJournalActivity.this, HTJournalEditActivity.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    private void getMyJournal() {

        progressDialog.show();

        journal = "";
        journalDateList.clear();
        journalColorList.clear();
        journalTextList.clear();

        final ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);
        final ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);

        configureActionBar(HTJournalActivity.this, "My Journal", "leftArrow", "Edit");

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

                                toast = HTToast.showToast(HTJournalActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                String tempString;

                                journal = XMLFunctions.getValue(e2, "journal");

                                journal = HTGlobals.getInstance().cleanStringAfterReceiving(journal);

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "journal_date_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "journal_date_" + i);
                                    tempString = tempString.replace("|*|lt|*|", "<").replace("|*|gt|*|", ">")
                                            .replace("|*|and|*|", "&").replace("<br>", "\n").replace("<br />", "\n")
                                            .replace("<br/>", "\n").replace("&#39;", "'");
                                    journalDateList.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "journal_color_" + i);
                                    tempString = tempString.replace("|*|lt|*|", "<").replace("|*|gt|*|", ">")
                                            .replace("|*|and|*|", "&").replace("<br>", "\n").replace("<br />", "\n")
                                            .replace("<br/>", "\n").replace("&#39;", "'");
                                    journalColorList.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "journal_text_" + i);
                                    tempString = tempString.replace("|*|lt|*|", "<").replace("|*|gt|*|", ">")
                                            .replace("|*|and|*|", "&").replace("<br>", "\n").replace("<br />", "\n")
                                            .replace("<br/>", "\n").replace("&#39;", "'");
                                    journalTextList.add(tempString);

                                    i++;
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

//                            toast = HTToast.showToast(HTJournalActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(HTJournalActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showMyJournal() {
        if (linearLayoutMyJournal.getChildCount() > 0)
            linearLayoutMyJournal.removeAllViews();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy", Locale.US);
        String passDateString = dateFormat.format(passDate);
        String dateString, colorString;

        View focusJournalView = null;
        for (int i = 0; i < journalDateList.size(); i++) {
            View journalHeaderCell = mInflater.inflate(R.layout.journal_header_cell, null);

            // color?
            colorString = journalColorList.get(i);
            ImageView colorView = (ImageView) journalHeaderCell.findViewById(R.id.colorView);
            if ("GREEN".equals(colorString)) {
                colorView.setImageResource(R.drawable.ht_calendar_green);
            } else if ("YELLOW".equals(colorString)) {
                colorView.setImageResource(R.drawable.ht_calendar_yellow);
            } else if ("RED".equals(colorString)) {
                colorView.setImageResource(R.drawable.ht_calendar_red);
            } else {
                colorView.setVisibility(View.GONE);
            }

            dateString = journalDateList.get(i);
            TextView journalDateView = (TextView) journalHeaderCell.findViewById(R.id.dateView);
            journalDateView.setTypeface(dateFont);
            journalDateView.setText(dateString);

            ImageButton editPencilBtn = (ImageButton) journalHeaderCell.findViewById(R.id.editPencilBtn);
            final String finalDateString = dateString;

            // only show editPencilBtn going back 1 year
            Date testDate;
            boolean showEditPencilBtn = true;

            try {

                testDate = dateFormat.parse(finalDateString);

                if (HTGlobals.getInstance().addNumberOfMonthsToPassDate(currentDate, -12, false).after(testDate)) {

                    showEditPencilBtn = false;
                }

            } catch (ParseException e) {

                //
            }

            if (!showEditPencilBtn) {

                editPencilBtn.setVisibility(View.GONE);

            } else {

                editPencilBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();

                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        Date newDate;
                        try {

                            newDate = dateFormat.parse(finalDateString);
                            calendar.setTime(newDate);

                            HTGlobals.getInstance().passDate = newDate;
                            HTGlobals.getInstance().passDay = calendar.get(Calendar.DAY_OF_MONTH);
                            HTGlobals.getInstance().passMonth = calendar.get(Calendar.MONTH);
                            HTGlobals.getInstance().passYear = calendar.get(Calendar.YEAR);
                            HTGlobals.getInstance().plannerShouldRefresh = true;
                            Intent intent = new Intent(HTJournalActivity.this, HTJournalEditActivity.class);
                            startActivityForResult(intent, 1);

                        } catch (ParseException e) {

                            //
                        }
                    }
                });
            }

            linearLayoutMyJournal.addView(journalHeaderCell);

            if (dateString.equals(passDateString)) {
                focusJournalView = journalHeaderCell;
            }

            View journalTextCell = mInflater.inflate(R.layout.journal_text_cell, null);
            TextView journalTextView = (TextView) journalTextCell.findViewById(R.id.journalTextView);
            journalTextView.setTypeface(journalTextFont);
            journalTextView.setText(journalTextList.get(i));
            linearLayoutMyJournal.addView(journalTextCell);
        }

        if (focusJournalView != null) {
            final View finalFocusJournalView = focusJournalView;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    scrollViewMyJournal.smoothScrollTo(0, finalFocusJournalView.getTop());
                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getMyJournal();
    }
}