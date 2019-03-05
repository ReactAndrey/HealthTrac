package com.sph.healthtrac.more.inbox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTSearchEditBar;
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
 * Created by rcr on 10/05/15.
 */
public class InboxActivity extends Activity {

    private List<String> messageIDs = new ArrayList<>();
    private List<String> messageAckeds = new ArrayList<>();
    private List<String> messageSubjects = new ArrayList<>();
    private List<String> messageNotes = new ArrayList<>();
    private List<String> messageDates = new ArrayList<>();
    private static RelativeLayout relativeLayoutInbox;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

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
        View mActionBar = HTActionBar.getActionBar(this, "Messages", "leftArrow", "deleteMark"); // actually, message compose button

        relativeLayoutInbox.addView(mActionBar);

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

                imm.hideSoftInputFromWindow(relativeLayoutInbox.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });

        // right message compose button
        ((ImageView) mActionBar.findViewById(R.id.rightCheckAction)).setImageResource(R.drawable.ht_message_compose);
        mActionBar.findViewById(R.id.rightCheckAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InboxActivity.this, HTInboxComposeActivity.class);
                intent.putExtra("selectedMessageID", "0");
                intent.putExtra("isSupportMessage", false);
                startActivity(intent);
            }
        });

        View searchBarView = HTSearchEditBar.getActionBar(this);
        searchEditText = (EditText) searchBarView.findViewById(R.id.editText);
        relativeLayoutInbox.addView(searchBarView);
        params = (RelativeLayout.LayoutParams) searchBarView.getLayoutParams();
        params.height = topBarHeight;
        params.setMargins(0, topBarHeight, 0, 0);  // left, top, right, bottom
        searchBarView.setLayoutParams(params);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getMessages();
            }
        });

        listViewInbox = (ListView) relativeLayoutInbox.findViewById(R.id.listViewInbox);
        adapter = new HTInboxListView(this, messageIDs, messageAckeds, messageSubjects, messageNotes, messageDates);
        listViewInbox.setAdapter(adapter);
//        listViewInbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(InboxActivity.this, MessageDetailActivity.class);
//                intent.putExtra("message_id", messageIDs.get(position));
//                startActivity(intent);
//            }
//        });


        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(InboxActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getMessages();
    }


    private void getMessages() {

        progressDialog.show();

        messageIDs.clear();
        messageAckeds.clear();
        messageSubjects.clear();
        messageNotes.clear();
        messageDates.clear();
        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    String searchString = searchEditText.getText().toString();
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(7);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_messages"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("search", searchString));

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

                                toast = HTToast.showToast(InboxActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        String tempString;
                        final Element e2 = (Element) nodes.item(0);
                        int i = 1;
                        while (XMLFunctions.tagExists(e2, "message_" + i + "_id")) {
                            tempString = XMLFunctions.getValue(e2, "message_" + i + "_id");
                            messageIDs.add(tempString);
                            tempString = XMLFunctions.getValue(e2, "message_" + i + "_acked");
                            messageAckeds.add(tempString);
                            tempString = XMLFunctions.getValue(e2, "message_" + i + "_subject");
                            tempString = tempString.replace("|*|lt|*|", "<").replace("|*|gt|*|", ">")
                                    .replace("|*|and|*|", "&").replace("<br>", " ").replace("<br />", " ")
                                    .replace("<br/>", " ").replace("&#39;", "'");
                            messageSubjects.add(tempString);
                            tempString = XMLFunctions.getValue(e2, "message_" + i + "_note");
                            tempString = tempString.replace("|*|lt|*|", "<").replace("|*|gt|*|", ">")
                                    .replace("|*|and|*|", "&").replace("<br>", " ").replace("<br />", " ")
                                    .replace("<br/>", " ").replace("&#39;", "'");
                            messageNotes.add(tempString);
                            tempString = XMLFunctions.getValue(e2, "message_" + i + "_created");
                            messageDates.add(tempString);
                            i++;
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(InboxActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(InboxActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    public void deleteMessages(final String messageID) {
        progressDialog.show();
        myThread = new Thread() {

            boolean isSuccess = false;

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "delete_message"));
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

                                toast = HTToast.showToast(InboxActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        isSuccess = true;
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(InboxActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(InboxActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                        }
                    });

                } finally {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (isSuccess)
                                getMessages();
                        }
                    });
                }
            }
        };

        myThread.start();
    }

}
