package com.sph.healthtrac.more.inbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * Created by rcr on 11/05/15.
 */
public class MessageDetailActivity extends Activity {

    private static RelativeLayout relativeLayoutInbox;
    private TextView subjectTextView;
    private LinkEnabledTextView noteTextView;

    private static InputMethodManager imm;
    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;
    Calendar calendar;
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

    private String messageID;
    private String messageAcked;
    private String messageSubject;
    private String messageNote;
    private String messageDate;

    Typeface subjectFont;
    Typeface noteFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        messageID = getIntent().getStringExtra("message_id");
        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        relativeLayoutInbox = (RelativeLayout) findViewById(R.id.inboxContainer);
        Typeface typeCurrentGoalLabels = Typeface.createFromAsset(this.getAssets(), "fonts/AvenirNext-Regular.ttf");

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
        View mActionBar = HTActionBar.getActionBar(this, "", "leftArrow", "deleteMark");

        relativeLayoutInbox.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // Cancel button
        mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(relativeLayoutInbox.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });

        //delete button
        ((ImageView) mActionBar.findViewById(R.id.rightCheckAction)).setImageResource(R.drawable.ht_message_compose);
        mActionBar.findViewById(R.id.rightCheckAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageDetailActivity.this, HTInboxComposeActivity.class);
                intent.putExtra("selectedMessageID", messageID);
                intent.putExtra("isSupportMessage", false);
                startActivity(intent);
//                AlertDialog.Builder builder = null;
//                builder = new AlertDialog.Builder(MessageDetailActivity.this);
//                AlertDialog alert;
//                builder.setTitle(R.string.delete_message_title)
//                        .setMessage(getResources()
//                                .getString(R.string.delete_message_content))
//                        .setCancelable(false)
//                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                deleteMessages();
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                alert = builder.create();
//                alert.show();
            }
        });

        subjectFont = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Medium.ttf");
        noteFont = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Roman.ttf");

        subjectTextView = (TextView) findViewById(R.id.subjectTextView);
        noteTextView = (LinkEnabledTextView) findViewById(R.id.noteTextView);

        subjectTextView.setTypeface(subjectFont);
        noteTextView.setTypeface(noteFont);
        subjectTextView.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        noteTextView.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        noteTextView.setLinkTextColor(getResources().getColor(R.color.ht_blue));
        noteTextView.setLinksClickable(true);
        noteTextView.setOnTextLinkClickListener(new TextLinkClickListener() {
            @Override
            public void onTextLinkClick(View textView, String clickedString) {
                Intent intent = new Intent(MessageDetailActivity.this, HTBrowserActivity.class);
                intent.putExtra("title", "");
                intent.putExtra("open_link", clickedString);
                startActivity(intent);
            }
        });

        subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        noteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        if (login.equals("") || pw.equals("")) {

            Intent intent = new Intent(MessageDetailActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {
            getMessages();
        }

    }


    private void getMessages() {

        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_messages"));
                    nameValuePairs.add(new BasicNameValuePair("messageid", messageID));
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

                                toast = HTToast.showToast(MessageDetailActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        int i = 1;
                        messageAcked = XMLFunctions.getValue(e2, "message_" + i + "_acked");
                        messageSubject = XMLFunctions.getValue(e2, "message_" + i + "_subject");
                        messageSubject = messageSubject.replace("|*|lt|*|", "<").replace("|*|gt|*|", ">")
                                .replace("|*|and|*|", "&").replace("<br>", " ").replace("<br />", " ")
                                .replace("<br/>", " ").replace("&#39;", "'");
                        messageNote = XMLFunctions.getValue(e2, "message_" + i + "_note");
                        messageNote = messageNote.replace("|*|lt|*|", "<").replace("|*|gt|*|", ">")
                                .replace("|*|and|*|", "&").replace("<br>", "\n").replace("<br />", "\n")
                                .replace("<br/>", "\n").replace("&#39;", "'");
                        messageDate = XMLFunctions.getValue(e2, "message_" + i + "_created");
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(MessageDetailActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(MessageDetailActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                        }
                    });

                } finally {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            subjectTextView.setText(messageSubject);
                            noteTextView.gatherLinksForText(messageNote);
                            MovementMethod m = noteTextView.getMovementMethod();
                            if ((m == null) || !(m instanceof LinkMovementMethod)) {
                                if (noteTextView.getLinksClickable()) {
                                    noteTextView.setMovementMethod(LinkMovementMethod.getInstance());
                                }
                            }
                        }
                    });
                }
            }
        };

        myThread.start();
    }

//    private void deleteMessages() {
//        progressDialog.show();
//        myThread = new Thread() {
//
//            boolean isSuccess = false;
//
//            @Override
//            public void run() {
//
//                try {
//                    post = new HttpPost(getString(R.string.web_service_url));
//
//                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
//                    nameValuePairs.add(new BasicNameValuePair("action", "delete_message"));
//                    nameValuePairs.add(new BasicNameValuePair("messageid", messageID));
//                    nameValuePairs.add(new BasicNameValuePair("userid", login));
//                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
//                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
//                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
//                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
//
//                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                    client = new DefaultHttpClient();
//                    response = client.execute(post);
//                    entity = response.getEntity();
//
//                    String responseText = EntityUtils.toString(entity);
//
//                    Document doc = XMLFunctions.XMLfromString(responseText);
//
//                    NodeList error = doc.getElementsByTagName("error");
//                    NodeList nodes = doc.getElementsByTagName("messages");
//
//                    Element e1 = (Element) error.item(0);
//                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");
//
//                    if (!errorMessage.equals("")) {
//
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//
//                                toast = HTToast.showToast(MessageDetailActivity.this, errorMessage, Toast.LENGTH_LONG);
//                            }
//                        });
//                    } else {
//                        isSuccess = true;
//                    }
//
//                } catch (Exception e) {
//
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//
//                            toast = HTToast.showToast(MessageDetailActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
//                        }
//                    });
//
//                } finally {
//
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            progressDialog.dismiss();
//                            if (isSuccess)
//                                MessageDetailActivity.this.finish();
//                        }
//                    });
//                }
//            }
//        };
//
//        myThread.start();
//    }
}
