package com.sph.healthtrac.learn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTBrowserActivity;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.more.support.HTVideoTutorialsActivity;

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

/**
 * Created by rcr on 19/05/15.
 */
public class LearnDetailActivity extends Activity {

    private static RelativeLayout relativeLayoutDetail;
    private TextView titleView;
    private TextView descriptionView;
    ScrollView scrollViewLearnDetails;
    LinearLayout layoutLearnDetails;

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

    private boolean learningModuleHasWorksheet = false;
    private boolean doneUpdatingLearningModule = false;

    private String learningModuleTitle = "";
    private String learningModuleDescription = "";
    private String learningModuleStatus = "";
    private String learningModuleIntroVideoTitle = "";
    private String learningModuleIntroVideoPath = "";

    private List<String> learningModuleVideoTitles = new ArrayList<>();
    private List<String> learningModuleVideoPaths = new ArrayList<>();
    private List<String> learningModuleDocumentTitles = new ArrayList<>();
    private List<String> learningModuleDocumentPaths = new ArrayList<>();

    private String selectedAssetPath = "";

    Typeface learnHeaderTitleFont;
    Typeface learnDescriptionFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_detail);

        learnHeaderTitleFont = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Light.ttf");
        learnDescriptionFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");

        learningModuleID = getIntent().getStringExtra("learningModuleID");
        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        relativeLayoutDetail = (RelativeLayout) findViewById(R.id.learnDetailContainer);
        titleView = (TextView) findViewById(R.id.titleTextView);
        descriptionView = (TextView) findViewById(R.id.descTextView);

        titleView.setTypeface(learnHeaderTitleFont);
        descriptionView.setTypeface(learnDescriptionFont);

        titleView.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        descriptionView.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        scrollViewLearnDetails = (ScrollView) findViewById(R.id.scrollViewLearnDetail);
        layoutLearnDetails = (LinearLayout) findViewById(R.id.layoutLearnDetails);

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
        View mActionBar = HTActionBar.getActionBar(this, "Learning Modules", "leftArrow", "");

        relativeLayoutDetail.addView(mActionBar);

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

                imm.hideSoftInputFromWindow(relativeLayoutDetail.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });


        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else {
            getLearningModule();
        }
    }

    private void getLearningModule() {
        learningModuleHasWorksheet = false;
        doneUpdatingLearningModule = false;

        learningModuleTitle = "";
        learningModuleDescription = "";
        learningModuleStatus = "";
        learningModuleIntroVideoTitle = "";
        learningModuleIntroVideoPath = "";

        learningModuleVideoTitles.clear();
        learningModuleVideoPaths.clear();
        learningModuleDocumentTitles.clear();
        learningModuleDocumentPaths.clear();

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

                                toast = HTToast.showToast(LearnDetailActivity.this, errorMessage, Toast.LENGTH_LONG);
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

                                //learning module status
                                if (XMLFunctions.tagExists(e2, "learning_module_status")) {
                                    tempString = XMLFunctions.getValue(e2, "learning_module_status");
                                    learningModuleStatus = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                //learning module description
                                if (XMLFunctions.tagExists(e2, "learning_module_description")) {
                                    tempString = XMLFunctions.getValue(e2, "learning_module_description");
                                    learningModuleDescription = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                //learning module has worksheet
                                if (XMLFunctions.tagExists(e2, "learning_module_has_worksheet")) {
                                    tempString = XMLFunctions.getValue(e2, "learning_module_has_worksheet");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if ("1".equals(tempString)) {
                                        learningModuleHasWorksheet = true;
                                    } else {
                                        learningModuleHasWorksheet = false;
                                    }
                                }

                                //learning module intro video title
                                if (XMLFunctions.tagExists(e2, "learning_module_intro_video_title")) {
                                    tempString = XMLFunctions.getValue(e2, "learning_module_intro_video_title");
                                    learningModuleIntroVideoTitle = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                //learning module intro video path
                                if (XMLFunctions.tagExists(e2, "learning_module_intro_video_path")) {
                                    tempString = XMLFunctions.getValue(e2, "learning_module_intro_video_path");
                                    learningModuleIntroVideoPath = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "learning_module_video_title_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "learning_module_video_title_" + i);
                                    learningModuleVideoTitles.add(htGlobals.cleanStringAfterReceiving(tempString));

                                    tempString = XMLFunctions.getValue(e2, "learning_module_video_path_" + i);
                                    learningModuleVideoPaths.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "learning_module_document_title_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "learning_module_document_title_" + i);
                                    learningModuleDocumentTitles.add(htGlobals.cleanStringAfterReceiving(tempString));

                                    tempString = XMLFunctions.getValue(e2, "learning_module_document_path_" + i);
                                    learningModuleDocumentPaths.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                showLearningModule();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(LearnDetailActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(LearnDetailActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showLearningModule() {


        titleView.setText(learningModuleTitle);
        descriptionView.setText(learningModuleDescription);


        // intro video
        if (!"".equals(learningModuleIntroVideoTitle) && !"".equals(learningModuleIntroVideoPath)) {
            View introVideoCell = HTLearningModule.getLearningDetailItemView(this, learningModuleIntroVideoTitle);
            ImageView iconView = (ImageView) introVideoCell.findViewById(R.id.imageView);
            iconView.setImageResource(R.drawable.ht_learning_module_video);
            introVideoCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //playVideoWithURL(learningModuleIntroVideoPath);
                    Intent intent = new Intent(LearnDetailActivity.this, HTBrowserActivity.class);
                    intent.putExtra("title", "Learning Modules");
                    intent.putExtra("open_link", learningModuleIntroVideoPath);
                    intent.putExtra("isPDFDocument", false);
                    startActivityForResult(intent, 1);
                }
            });

            layoutLearnDetails.addView(introVideoCell);
        }

        // videos
        for (int i = 0; i < learningModuleVideoTitles.size(); i++) {
            final int index = i;
            View videoCell = HTLearningModule.getLearningDetailItemView(this, learningModuleVideoTitles.get(i));
            ImageView iconView = (ImageView) videoCell.findViewById(R.id.imageView);
            iconView.setImageResource(R.drawable.ht_learning_module_video);
            videoCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //playVideoWithURL(learningModuleVideoPaths.get(index));
                    Intent intent = new Intent(LearnDetailActivity.this, HTBrowserActivity.class);
                    intent.putExtra("title", "Learning Modules");
                    intent.putExtra("open_link", learningModuleVideoPaths.get(index));
                    intent.putExtra("isPDFDocument", false);
                    startActivityForResult(intent, 1);
                }
            });

            layoutLearnDetails.addView(videoCell);
        }

        // documents
        for (int i = 0; i < learningModuleDocumentTitles.size(); i++) {
            final int index = i;
            View docmentCell = HTLearningModule.getLearningDetailItemView(this, learningModuleDocumentTitles.get(i));
            ImageView iconView = (ImageView) docmentCell.findViewById(R.id.imageView);
            iconView.setImageResource(R.drawable.ht_learning_module_document);
            docmentCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDocument(learningModuleDocumentPaths.get(index));
                }
            });

            layoutLearnDetails.addView(docmentCell);
        }

        // worksheet
        if (learningModuleHasWorksheet) {
            View worksheetCell = HTLearningModule.getLearningDetailItemView(this, "Worksheet Questions");
            ImageView iconView = (ImageView) worksheetCell.findViewById(R.id.imageView);
            iconView.setImageResource(R.drawable.ht_learning_module_worksheet);
            worksheetCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LearnDetailActivity.this, LearnWorksheetActivity.class);
                    intent.putExtra("learningModuleID", learningModuleID);
                    startActivityForResult(intent, 1);
                }
            });

            layoutLearnDetails.addView(worksheetCell);
        }

        if (!"DONE".equals(learningModuleStatus)) {
            LinearLayout buttonLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.topMargin = (int) (20 * displayDensity);
            buttonLayout.setLayoutParams(params);
            buttonLayout.setGravity(Gravity.BOTTOM);
            Button markAsCompleteButton = new Button(this);
            markAsCompleteButton.setText("MARK AS COMPLETE");
            markAsCompleteButton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf"));
            markAsCompleteButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            markAsCompleteButton.setTextColor(Color.WHITE);
            markAsCompleteButton.setBackgroundColor(Color.rgb(113, 202, 94));
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (60 * displayDensity));
            markAsCompleteButton.setLayoutParams(params);
            markAsCompleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateLearningModuleStatus();
                }
            });

            buttonLayout.addView(markAsCompleteButton);
            layoutLearnDetails.addView(buttonLayout);
        }

        scrollViewLearnDetails.scrollTo(0, 0);

    }

    private void playVideoWithURL(String videoPath){
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(videoPath));
        intent.setDataAndType(Uri.parse(videoPath), "video/*");
        startActivity(intent);
    }

    private void showDocument(String documentUrl){
        Intent intent = new Intent(this, HTBrowserActivity.class);
        intent.putExtra("title", "Learning Modules");
        intent.putExtra("open_link", documentUrl);
        intent.putExtra("isPDFDocument", true);
        startActivity(intent);
    }

    private void updateLearningModuleStatus() {
        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    learningModuleStatus = "DONE";
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_learning_module_status"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("session_id", learningModuleID));
                    nameValuePairs.add(new BasicNameValuePair("status", learningModuleStatus));

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

                                toast = HTToast.showToast(LearnDetailActivity.this, errorMessage, Toast.LENGTH_LONG);
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

//                            toast = HTToast.showToast(LearnDetailActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(LearnDetailActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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
