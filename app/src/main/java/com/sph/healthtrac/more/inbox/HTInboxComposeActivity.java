package com.sph.healthtrac.more.inbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

public class HTInboxComposeActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private ScrollView scrollViewCompose;
    private static InputMethodManager imm;
    Calendar calendar;
    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;
    EditText searchEditText;
    ListView listViewInbox;
    HTInboxListView adapter;
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

    private LinearLayout generalMsgHeader;
    private TextView toLabelView;
    private TextView destLabelView;
    private EditText subjectEditView;

    // for support message
    private LinearLayout supportMsgHeader;
    private TextView supportHeaderLabel;
    private TextView categoryView;
    private TextView categoryDescView;

    private EditText messageEditView;

    private String selectedMessageID;
    private boolean isSupportMessage;

    private String coachName;
    private String coachLogin;
    private String forceReplyToCoach;
    private String OriginalMessageSubject;
    private String OriginalMessageNote;

    private String supportCategoryString = "Select...";

    private AlertDialog chooseToDlg;
    private AlertDialog chooseCategoryDlg;
    private int selectedToIndex;
    private int selectedCategoryIndex;
    private String[] toFieldValues;
    private String[] categoryValues;
    private List<String> categoryPickerValues = new ArrayList<>();
    private List<String> categoryDescriptions = new ArrayList<>();

    private Typeface avenirNextMediumFont;
    private Typeface avenirRomanFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_compose);

        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        avenirRomanFont = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Roman.ttf");

        selectedMessageID = getIntent().getStringExtra("selectedMessageID");
        isSupportMessage = getIntent().getBooleanExtra("isSupportMessage", false);

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        scrollViewCompose = (ScrollView) findViewById(R.id.scrollViewCompose);

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
        View mActionBar = HTActionBar.getActionBar(this, "New Message", "leftArrow", "deleteMark"); // actually, message compose button

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

        // right message compose button
        ((ImageView) mActionBar.findViewById(R.id.rightCheckAction)).setImageResource(R.drawable.ht_icon_send_message);
        mActionBar.findViewById(R.id.rightCheckAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageAction();
            }
        });

        if (login == null || pw == null || login.equals("") || pw.equals("")) {
            Intent intent = new Intent(HTInboxComposeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {

            generalMsgHeader = (LinearLayout) findViewById(R.id.generalMsgHeader);
            toLabelView = (TextView) findViewById(R.id.toLabelView);
            destLabelView = (TextView) findViewById(R.id.destLabelView);
            subjectEditView = (EditText) findViewById(R.id.subjectEdit);

            toLabelView.setTypeface(avenirNextMediumFont);
            destLabelView.setTypeface(avenirNextMediumFont);
            subjectEditView.setTypeface(avenirNextMediumFont);

            supportMsgHeader = (LinearLayout) findViewById(R.id.supportMsgHeader);
            supportHeaderLabel = (TextView) findViewById(R.id.supportHeaderLabel);
            categoryView = (TextView) findViewById(R.id.categoryView);
            categoryDescView = (TextView) findViewById(R.id.categoryDescView);

            messageEditView = (EditText) findViewById(R.id.messageEditView);
            subjectEditView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            messageEditView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

            supportHeaderLabel.setTypeface(avenirNextMediumFont);
            categoryView.setTypeface(avenirNextMediumFont);
            categoryDescView.setTypeface(avenirRomanFont);
            messageEditView.setTypeface(avenirRomanFont);

            if(isSupportMessage) {
                supportMsgHeader.setVisibility(View.VISIBLE);
                generalMsgHeader.setVisibility(View.GONE);
            } else {
                generalMsgHeader.setVisibility(View.VISIBLE);
                supportMsgHeader.setVisibility(View.GONE);
            }

            getMessages();
        }
    }

    private void getMessages() {

        coachName = "";
        coachLogin = "";
        forceReplyToCoach = "";
        OriginalMessageSubject = "";
        OriginalMessageNote = "";

        categoryPickerValues.clear();
        categoryDescriptions.clear();

        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_message_template"));
                    nameValuePairs.add(new BasicNameValuePair("messageid", selectedMessageID));
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
                    NodeList nodes = doc.getElementsByTagName("messages");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTInboxComposeActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString;

                                if (XMLFunctions.tagExists(e2, "coach_name")) {
                                    tempString = XMLFunctions.getValue(e2, "coach_name");
                                    coachName = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "coach_login")) {
                                    tempString = XMLFunctions.getValue(e2, "coach_login");
                                    coachLogin = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "force_reply_to_coach")) {
                                    forceReplyToCoach = XMLFunctions.getValue(e2, "force_reply_to_coach");
                                }

                                if (XMLFunctions.tagExists(e2, "message_subject")) {
                                    tempString = XMLFunctions.getValue(e2, "message_subject");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    OriginalMessageSubject = tempString.replace("<br>", " ").replace("<br />", " ").replace("<br/>", " ");
                                }

                                if (XMLFunctions.tagExists(e2, "message_note")) {
                                    tempString = XMLFunctions.getValue(e2, "message_note");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    OriginalMessageNote = tempString.replace("<br>", "\n").replace("<br />", "\n").replace("<br/>", "\n");
                                }

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "support_category_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "support_category_" + i);
                                    categoryPickerValues.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "support_description_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "support_description_" + i);
                                    categoryDescriptions.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                showMessage();
                            }
                        });
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(HTInboxComposeActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(HTInboxComposeActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showMessage() {
        scrollViewCompose.setVisibility(View.VISIBLE);
        if(isSupportMessage) {
            categoryView.setText(supportCategoryString);
            categoryValues = new String[categoryPickerValues.size() + 1];
            categoryValues[0] = supportCategoryString;
            for (int i = 0 ; i < categoryPickerValues.size(); i++) {
                categoryValues[i+1] = categoryPickerValues.get(i);
            }

            categoryDescriptions.add(0, "");

            chooseCategoryDlg = new AlertDialog.Builder(HTInboxComposeActivity.this)
                    .setTitle("Category")
                    .setSingleChoiceItems(categoryValues, selectedCategoryIndex, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            categoryView.setText(categoryValues[which]);
                            categoryDescView.setText(categoryDescriptions.get(which));
                            selectedCategoryIndex = which;
                            dialog.dismiss();

                        }
                    }).create();

            categoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    chooseCategoryDlg.show();
                }
            });

        } else {
            if (!"".equals(coachName) && !"".equals(coachLogin) && !"1".equals(forceReplyToCoach)) { // there's a coach, and not a forced reply
                toFieldValues = new String[]{"My Practice", coachName};
                chooseToDlg = new AlertDialog.Builder(HTInboxComposeActivity.this)
                        .setTitle("To:")
                        .setSingleChoiceItems(toFieldValues, selectedToIndex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                destLabelView.setText(toFieldValues[which]);
                                selectedToIndex = which;
                                dialog.dismiss();

                            }
                        }).create();
                destLabelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseToDlg.show();
                    }
                });

            } else if (!"".equals(coachName) && !"".equals(coachLogin) && "1".equals(forceReplyToCoach)) { // there's a coach, and a forced reply {

                destLabelView.setText(coachName);
            }

            if (!"".equals(OriginalMessageSubject)) {
                subjectEditView.setText(OriginalMessageSubject);
            }

            if (!"".equals(OriginalMessageNote)) {
                messageEditView.setText("\n\n" + OriginalMessageNote);
                messageEditView.requestFocus();
                messageEditView.setSelection(0,0);
            }
        }
    }

    private void sendMessageAction(){
        String subjectStr = subjectEditView.getText().toString().trim();
        String messageStr = messageEditView.getText().toString().trim();

        if ("".equals(messageStr)) {
            new AlertDialog.Builder(this)
                    .setTitle("No Message")
                    .setMessage("Please enter your message")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            messageEditView.requestFocus();
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        }
                    }).create().show();
        } else if ("".equals(subjectStr) && !isSupportMessage) {
            new AlertDialog.Builder(this)
                    .setTitle("No Subject")
                    .setMessage("Please enter your subject")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            subjectEditView.requestFocus();
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        }
                    }).create().show();
        } else if ("Select...".equals(categoryView.getText()) && isSupportMessage) {
            imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
            new AlertDialog.Builder(this)
                    .setTitle("No Category")
                    .setMessage("Please select your category")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Send Message")
                    .setMessage("Send this message?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendMessage();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
                    .create().show();
        }
    }

    private void sendMessage() {
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {
                String toStr = "";
                String subjectStr = "";
                String messageStr = "";
                String supportMessage = "";

                toStr = "practice";

                if (!"".equals(coachName) && !"".equals(coachLogin) && (selectedToIndex == 1 || "1".equals(forceReplyToCoach))) {
                    toStr = coachLogin;
                }

                subjectStr = HTGlobals.getInstance().cleanStringBeforeSending(subjectEditView.getText().toString());
                messageStr = HTGlobals.getInstance().cleanStringBeforeSending(messageEditView.getText().toString());

                if (isSupportMessage) {
                    supportMessage = "1";
                    subjectStr = HTGlobals.getInstance().cleanStringBeforeSending(categoryView.getText().toString());
                }

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "send_message"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("to", toStr));
                    nameValuePairs.add(new BasicNameValuePair("message", messageStr));
                    nameValuePairs.add(new BasicNameValuePair("subject", subjectStr));
                    nameValuePairs.add(new BasicNameValuePair("message_replied_to", selectedMessageID));
                    nameValuePairs.add(new BasicNameValuePair("support_message", supportMessage));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("messages");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTInboxComposeActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
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

//                            toast = HTToast.showToast(HTInboxComposeActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(HTInboxComposeActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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
