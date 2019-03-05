package com.sph.healthtrac.learn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by rcr on 21/05/15.
 */
public class LearnWorksheetActivity extends Activity {

    private static RelativeLayout relativeLayoutWorksheet;
    ScrollView scrollViewWorksheet;
    LinearLayout layoutWorksheet;
    private TextView titleView;

    private static InputMethodManager imm;
    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;
    Calendar calendar;

    float displayDensity;
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

    private String learningModuleID = "";

    private String learningModuleTitle = "";
    private List<String> learningModuleWorksheetIDs = new ArrayList<>();
    private List<String> learningModuleWorksheetQuestions = new ArrayList<>();
    private List<String> learningModuleWorksheetAnswers = new ArrayList<>();

    Typeface learnHeaderTitleFont;
    Typeface learnLabelFont;
    Typeface editTextFont;


    private List<EditText> worksheetEditTexts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_worksheet);

        learningModuleID = getIntent().getStringExtra("learningModuleID");
        learnHeaderTitleFont = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Light.ttf");
        learnLabelFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        editTextFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        relativeLayoutWorksheet = (RelativeLayout) findViewById(R.id.learnWorksheetContainer);

        scrollViewWorksheet = (ScrollView) findViewById(R.id.scrollViewWorksheet);
        layoutWorksheet = (LinearLayout) findViewById(R.id.layoutWorksheet);

        titleView = (TextView) findViewById(R.id.titleTextView);
        titleView.setTypeface(learnHeaderTitleFont);
        titleView.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

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
        View mActionBar = HTActionBar.getActionBar(this, "Learning Modules", "leftArrow", "Done");

        relativeLayoutWorksheet.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // left arrow button
        mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(relativeLayoutWorksheet.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });

        // Done button
        mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(relativeLayoutWorksheet.getWindowToken(), 0);
                updateLearningModuleWorksheet();
            }
        });


        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else {
            getLearningModuleWorksheet();
        }
    }


    private void getLearningModuleWorksheet() {
        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_learning_module_details"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("session_id", learningModuleID));
                    nameValuePairs.add(new BasicNameValuePair("worksheet", "true"));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();

                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    final NodeList error = doc.getElementsByTagName("error");
                    final NodeList nodes = doc.getElementsByTagName("learn_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(LearnWorksheetActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Element e2 = (Element) nodes.item(0);
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString;

                                //learning module title
                                if (XMLFunctions.tagExists(e2, "learning_module_title")) {
                                    tempString = XMLFunctions.getValue(e2, "learning_module_title");
                                    learningModuleTitle = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                //learning_module_worksheet
                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "learning_module_worksheet_id_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "learning_module_worksheet_id_" + i);
                                    learningModuleWorksheetIDs.add(htGlobals.cleanStringAfterReceiving(tempString));

                                    if (XMLFunctions.tagExists(e2, "learning_module_worksheet_question_" + i)) {
                                        tempString = XMLFunctions.getValue(e2, "learning_module_worksheet_question_" + i);
                                        learningModuleWorksheetQuestions.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    }

                                    if (XMLFunctions.tagExists(e2, "learning_module_worksheet_response_" + i)) {
                                        tempString = XMLFunctions.getValue(e2, "learning_module_worksheet_response_" + i);
                                        learningModuleWorksheetAnswers.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    }
                                    i++;
                                }

                                showLearningModuleWorksheet();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(LearnWorksheetActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(LearnWorksheetActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void updateLearningModuleWorksheet() {
        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_learning_module_worksheet"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("session_id", learningModuleID));
                    nameValuePairs.add(new BasicNameValuePair("worksheet", "true"));

                    HTGlobals htGlobals = HTGlobals.getInstance();
                    String tmpString = "";
                    for (int i = 0; i < worksheetEditTexts.size(); i++) {
                        tmpString = worksheetEditTexts.get(i).getText().toString();
                        tmpString = URLEncoder.encode(tmpString, "utf-8");
                        nameValuePairs.add(new BasicNameValuePair("ws_answer_" + (i + 1), tmpString));
                    }

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();


                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    final NodeList error = doc.getElementsByTagName("error");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(LearnWorksheetActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                setResult(1);
                                finish();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(LearnWorksheetActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(LearnWorksheetActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showLearningModuleWorksheet() {

        layoutWorksheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(relativeLayoutWorksheet.getWindowToken(), 0);
            }
        });
        LinearLayout.LayoutParams params;
        titleView.setText(learningModuleTitle);


        layoutWorksheet.addView(getWhiteSeperator());

        for (int i = 0; i < learningModuleWorksheetIDs.size(); i++) {
            TextView labelView = new TextView(this);
            labelView.setTypeface(learnLabelFont);
            labelView.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            labelView.setText(learningModuleWorksheetQuestions.get(i));
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins((int) (16 * displayDensity), (int) (8 * displayDensity), (int) (16 * displayDensity), 0);
            labelView.setLayoutParams(params);
            layoutWorksheet.addView(labelView);

            EditText searchEditText = new EditText(this);
            searchEditText.setTypeface(editTextFont);
            searchEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            searchEditText.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
            searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            searchEditText.setBackgroundResource(R.drawable.ht_text_field_worksheet);
            searchEditText.setGravity(Gravity.START);
            searchEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    return false;
                }
            });
            worksheetEditTexts.add(searchEditText);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (74 * displayDensity));
            params.setMargins((int) (16 * displayDensity), (int) (10 * displayDensity), (int) (16 * displayDensity), (int) (16 * displayDensity));
            searchEditText.setLayoutParams(params);
            searchEditText.setText(learningModuleWorksheetAnswers.get(i));
            layoutWorksheet.addView(searchEditText);

            layoutWorksheet.addView(getWhiteSeperator());
        }

        scrollViewWorksheet.scrollTo(0, 0);
    }

    private View getWhiteSeperator() {
        View whiteSeperator = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (4 * displayDensity));
        whiteSeperator.setBackgroundColor(Color.WHITE);
        whiteSeperator.setLayoutParams(params);
        return whiteSeperator;
    }

}
